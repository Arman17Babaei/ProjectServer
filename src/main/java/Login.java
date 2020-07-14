import com.google.gson.Gson;
import com.google.gson.JsonObject;
import controller.Database;
import model.User;
import org.omg.PortableServer.THREAD_POLICY_ID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class Login extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            handleLogin(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\n" +
                "\"ok\": false,\n" +
                "\"error\": \"" + e.getMessage() + "\"\n" +
                "}");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PrintWriter out = response.getWriter();
        Enumeration<String> parameterNames = request.getParameterNames();
        String username = null;
        String password = null;
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length > 1) {
                throw new Exception("You should enter only one value in each field");
            }
            if (paramName.equals("username")) {
                username = paramValues[0];
            }
            if (paramName.equals("password")) {
                password = paramValues[0];
            }
        }
        User user = Database.getUserByUsername(username);
        if (user == null) {
            throw new Exception("user not found");
        }
        if (user.getPassword().equals(password)) {
            String token = TokenMap.getToken(user);
            response.getWriter().println("{\n" +
                "\"ok\": true,\n" +
                "\"token\":\"" + token + "\"\n" +
                "}");
        } else {
            throw new Exception("password not correct");
        }
    }
}
