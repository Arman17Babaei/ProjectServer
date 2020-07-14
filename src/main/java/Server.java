import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import static javax.print.attribute.standard.ReferenceUriSchemesSupported.HTTP;

public class Server extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            for (String paramValue : paramValues) {
                out.write("t" + paramValue);
                out.write("n");
            }

        }
        response.setContentType("application/json");
        response.getWriter().println("{\n" +
            "\"ok\": false,\n" +
            "\"error\": \"invalid request method\"\n" +
            "}");
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
        IOException, ServletException {
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                if (line.length() > 1000 || jb.length() > 2000) {
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


        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            System.out.println(jb);
            response.getWriter().println(jb);
        } catch (Exception e) {
            // crash and burn
            throw new IOException("Error parsing JSON request string");
        }

        // DataStore.getInstance().putPerson(new Person(name, about, birthYear, password));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }
}


