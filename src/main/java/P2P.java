import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Claims;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;

public class P2P extends HttpServlet {
    private static HashMap<String, ConnectionInfo> sellers = new HashMap<String, ConnectionInfo>();

    public static void addSeller(String userId, String ip, String port) {
        sellers.put(userId, new ConnectionInfo(ip, port));
    }

    public static ConnectionInfo getSeller(String userId) {
        return sellers.get(userId);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!RequestManager.isAllowed(request)) {
            response.setStatus(429);
            return;
        }
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            hostSeller(request, response);
        } catch (Exception e) {
            RequestManager.setBadRequest(request);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace();
            response.getWriter().println("{\n" +
                "\"ok\": false,\n" +
                "\"error\": \"" + e.getMessage() + "\"\n" +
                "}");
        }
    }

    private void hostSeller(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Claims claims = Token.decodeJWT(request.getHeader("AuthToken"));
        String token = claims.getId();
        String issuer = claims.getIssuer();
        System.out.println(issuer);
        System.out.println(TokenMap.getUser(token).getUsername());
        if (!issuer.equals(TokenMap.getUser(token).getUsername()) || claims.getExpiration().before(new Date(System.currentTimeMillis()))) {
            throw new Exception("Not Authorized");
        }

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                if (line.length() > 10000 || jb.length() > 20000) {
                    RequestManager.setBadRequest(request);
                    response.getWriter().println("{\n" +
                        "\"ok\": false,\n" +
                        "\"error\": \"request too large\"\n" +
                        "}");
                    return;
                }
                jb.append(line);
            }
        } catch (Exception e) { /*report an error*/ }

        String json = jb.toString();
        JsonObject convertedObject = new Gson().fromJson(json, JsonObject.class);

        String port = convertedObject.get("port").getAsString();
        String ip = RequestManager.getClientIpAddress(request);
        User user = TokenMap.getUser(token);

        sellers.put(user.getUsername(), new ConnectionInfo(ip, port));

        token = Token.createJWT(TokenMap.renewToken(token), user.getUsername(), "AuthToken:D", 30 * 1000);
        response.getWriter().println("{\n" +
            "\"ok\": true,\n" +
            "\"token\":\"" + token + "\"\n" +
            "}");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!RequestManager.isAllowed(request)) {
            response.setStatus(429);
            return;
        }
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            getCorrespondingSeller(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\n" +
                "\"ok\": false,\n" +
                "\"error\": \"" + e.getMessage() + "\"\n" +
                "}");
        }
    }

    private void getCorrespondingSeller(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String token;
        System.out.println(request.getHeader("AuthToken"));
        if (!request.getHeader("AuthToken").equals("random-customer")) {
            Claims claims = Token.decodeJWT(request.getHeader("AuthToken"));
            token = claims.getId();
            String issuer = claims.getIssuer();
            if (!issuer.equals(TokenMap.getUser(token).getUsername()) || claims.getExpiration().before(new Date(System.currentTimeMillis()))) {
                System.out.println(issuer);
                System.out.println(TokenMap.getUser(token).getUsername());
                System.out.println(claims.getExpiration());
                System.out.println(new Date(System.currentTimeMillis()));
                throw new Exception("Not Authorized");
            }
        } else {
            token = "random-customer";
        }

        PrintWriter out = response.getWriter();
        Enumeration<String> parameterNames = request.getParameterNames();
        String seller = null;
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length > 1) {
                throw new Exception("you should enter a unique value for each parameter");
            }
            switch (paramName) {
                case "seller":
                    seller = paramValues[0];
                    break;
                default:
                    throw new Exception("unexpected parameter " + paramName);
            }
        }
        User user = TokenMap.getUser(token);

        // we just need to use \\Z as delimiter
        response.setContentType("application/json");

        ConnectionInfo sellerInfo = sellers.get(seller);
        token = Token.createJWT(TokenMap.renewToken(token), user.getUsername(), "AuthToken:D", 30 * 1000);
        response.getWriter().println("{\n" +
            "\"ok\": true,\n" +
            "\"ip\":\"" + sellerInfo.ip + "\",\n" +
            "\"port\":\"" + sellerInfo.port + "\",\n" +
            "\"token\":" + token + ",\n" +
            "\"sellerToken\":" + Token.createJWT("whatever", user.getUsername(), "getFile", 30 * 1000) + "\n" +
            "}");
    }
}

class ConnectionInfo {
    String ip;
    String port;

    public ConnectionInfo(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }
}
