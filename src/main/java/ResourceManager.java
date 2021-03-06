import com.google.gson.Gson;
import com.google.gson.JsonObject;
import controller.Database;
import io.jsonwebtoken.Claims;
import model.Supporter;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.Scanner;

public class ResourceManager extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!RequestManager.isAllowed(request)) {
            response.setStatus(429);
            return;
        }
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            handleGetObject(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\n" +
                "\"ok\": false,\n" +
                "\"error\": \"" + e.getMessage() + "\"\n" +
                "}");
        }
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
            handlePostObject(request, response);
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

    private void handlePostObject(HttpServletRequest request, HttpServletResponse response) throws Exception {
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

        String className = convertedObject.get("className").getAsString();
        String objectId = convertedObject.get("objectId").getAsString();
        User user = TokenMap.getUser(token);

        JsonObject object = convertedObject.get("object").getAsJsonObject();
        Object modelObject = new Gson().fromJson(object, Class.forName("model." + className));
        ObjectChecker.checkObjectPOST(modelObject, user);
        String fileName = "Database/" + className + "/" + objectId + ".json";
        Database.add(modelObject);
/*        FileWriter writer;
        writer = new FileWriter(fileName);
        new GsonBuilder().setPrettyPrinting().create().toJson(object, writer);
        writer.close();*/
        token = Token.createJWT(TokenMap.renewToken(token), user.getUsername(), "AuthToken:D", 30 * 1000);
        response.getWriter().println("{\n" +
            "\"ok\": true,\n" +
            "\"token\":\"" + token + "\"\n" +
            "}");
    }

    private void handleGetObject(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        String objectId = null;
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length > 1) {
                throw new Exception("you should enter a unique value for each parameter");
            }
            switch (paramName) {
                case "className":
                    className = paramValues[0];
                    break;
                case "objectId":
                    objectId = paramValues[0];
                    break;
                default:
                    throw new Exception("unexpected parameter " + paramName);
            }
        }
        User user = TokenMap.getUser(token);
        String cwd = System.getProperty("user.dir");
        System.out.println("Current working directory : " + cwd);
        File file = null;
        if (className.equals("User")) {
            if (new File("Database/" + "Customer" + "/" + objectId + ".json").exists()) {
                file = new File("Database/" + "Customer" + "/" + objectId + ".json");
                className = "Customer";
            }
            if (new File("Database/" + "Manager" + "/" + objectId + ".json").exists()) {
                file = new File("Database/" + "Manager" + "/" + objectId + ".json");
                className = "Manager";
            }
            if (new File("Database/" + "Seller" + "/" + objectId + ".json").exists()) {
                file = new File("Database/" + "Seller" + "/" + objectId + ".json");
                className = "Seller";
            }
            if (new File("Database/" + "Supporter" + "/" + objectId + ".json").exists()) {
                file = new File("Database/" + "Supporter" + "/" + objectId + ".json");
                className = "Supporter";
            }
        } else {
            file = new File("Database/" + className + "/" + objectId + ".json");
        }
        Scanner sc = new Scanner(file);
        // we just need to use \\Z as delimiter
        sc.useDelimiter("\\Z");
        String objectString = sc.next();
        sc.close();
        response.setContentType("application/json");
        token = Token.createJWT(TokenMap.renewToken(token), user.getUsername(), "AuthToken:D", 30 * 1000);
        response.getWriter().println("{\n" +
            "\"ok\": true,\n" +
            "\"token\":\"" + token + "\",\n" +
            "\"className\":\"" + className + "\",\n" +
            "\"object\":" + objectString +  "\n" +
            "}");
    }
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!RequestManager.isAllowed(request)) {
            response.setStatus(429);
            return;
        }
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            handleDelete(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().println("{\n" +
                "\"ok\": false,\n" +
                "\"error\": \"" + e.getMessage() + "\"\n" +
                "}");
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        String id = null;
        String folderName = null;
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length > 1) {
                throw new Exception("You should enter only one value in each field");
            }
            if (paramName.equals("id")) {
                id = paramValues[0];
            } else if (paramName.equals("folderName")) {
                folderName = paramValues[0];
            } else {
                throw new Exception("too many parameters");
            }
        }

        User user = TokenMap.getUser(token);

        System.out.println("Database/" + folderName + "/" + id + ".json");
        File file = new File("Database/" + folderName + "/" + id + ".json");
        if (!file.exists()) {
            throw new Exception("file not found!");
        }
        file.delete();

        Database.loadLists();

        token = Token.createJWT(TokenMap.renewToken(token), user.getUsername(), "AuthToken:D", 30 * 1000);
        response.getWriter().println("{\n" +
            "\"ok\": true,\n" +
            "\"token\":\"" + token + "\"\n" +
            "}");
    }
}
