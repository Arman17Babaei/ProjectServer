import com.google.gson.Gson;
import com.google.gson.JsonObject;
import controller.Database;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
        PrintWriter out = response.getWriter();
        Enumeration<String> parameterNames = request.getParameterNames();
        String className = null;
        String token = null;
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length > 1) {
                throw new Exception("You should enter only one value in each field");
            }
            if (paramName.equals("className")) {
                className = paramValues[0];
            }
            if (paramName.equals("token")) {
                token = paramValues[0];
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

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ok", "ture");
        jsonObject.add("list", new Gson().toJsonTree(res));
        jsonObject.addProperty("token", TokenMap.renewToken(token));

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
