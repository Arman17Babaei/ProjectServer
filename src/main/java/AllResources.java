import com.google.gson.Gson;
import com.google.gson.JsonObject;
import controller.Database;
import io.jsonwebtoken.Claims;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.SplittableRandom;

public class AllResources extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!RequestManager.isAllowed(request)) {
            response.setStatus(429);
            return;
        }
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            handleGetAll(request, response);
        } catch (Exception e) {
            RequestManager.setBadRequest(request);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\n" +
                "\"ok\": false,\n" +
                "\"error\": \"" + e.getMessage() + "\"\n" +
                "}");
        }
    }

    private void handleGetAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        String className = null;
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length > 1) {
                throw new Exception("You should enter only one value in each field");
            }
            if (paramName.equals("className")) {
                className = paramValues[0];
            }
        }
        User user = TokenMap.getUser(token);

        ArrayList<String> res = new ArrayList<>();

        if ("User".equals(className)) {
            res.addAll(listFilesForFolder("Customer"));
            res.addAll(listFilesForFolder("Seller"));
            res.addAll(listFilesForFolder("Manager"));
        } else {
            res.addAll(listFilesForFolder(className));
        }

        token = Token.createJWT(TokenMap.renewToken(token), user.getUsername(), "AuthToken:D", 30 * 1000);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ok", "ture");
        jsonObject.add("list", new Gson().toJsonTree(res));
        jsonObject.addProperty("token", token);

        response.getWriter().println(new Gson().toJson(jsonObject));
    }

    public ArrayList<String> listFilesForFolder(String folder_path) {
        ArrayList<String> res = new ArrayList<>();
        final File folder = new File("Database/" + folder_path);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().contains(".json")) {
                    res.add(file.getName().replace(".json", ""));
                }
            }
        }
        return res;
    }
}
