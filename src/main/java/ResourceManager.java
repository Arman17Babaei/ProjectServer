import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import controller.Database;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
        String token = convertedObject.get("token").getAsString();
        User user = TokenMap.getUser(token);

        JsonObject object = convertedObject.get("object").getAsJsonObject();
        Object modelObject = new Gson().fromJson(object, Class.forName("model." + className));
        ObjectChecker.checkObjectPOST(modelObject, user);
        String fileName = "Database/" + className + "/" + objectId + ".json";
        FileWriter writer;
        writer = new FileWriter(fileName);
        new GsonBuilder().setPrettyPrinting().create().toJson(object, writer);
        writer.close();
        response.getWriter().println("{\n" +
            "\"ok\": true,\n" +
            "\"token\":\"" + TokenMap.renewToken(token) + "\"\n" +
            "}");
    }

    private void handleGetObject(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PrintWriter out = response.getWriter();
        Enumeration<String> parameterNames = request.getParameterNames();
        String token = null;
        String className = null;
        String objectId = null;
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length > 1) {
                throw new Exception("you should enter a unique value for each parameter");
            }
            switch (paramName) {
                case "token":
                    token = paramValues[0];
                    break;
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
        } else {
            file = new File("Database/" + className + "/" + objectId + ".json");
        }
        Scanner sc = new Scanner(file);
        // we just need to use \\Z as delimiter
        sc.useDelimiter("\\Z");
        String objectString = sc.next();
        response.setContentType("application/json");
        response.getWriter().println("{\n" +
            "\"ok\": true,\n" +
            "\"token\":\"" + TokenMap.renewToken(token) + "\",\n" +
            "\"className\":\"" + className + "\",\n" +
            "\"object\":" + objectString +  "\n" +
            "}");
    }
}
