import controller.Database;
import io.jsonwebtoken.Claims;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

public class Login extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!RequestManager.isAllowed(request)) {
            response.setStatus(429);
            return;
        }
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            handleLogin(request, response);
        } catch (Exception e) {
            RequestManager.setBadRequest(request);
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
        System.out.println(username);
        User user = Database.getUserByUsername(username);
        if (user == null) {
            throw new Exception("user not found");
        }
        if (user.getPassword().equals(password)) {
            String token = TokenMap.getToken(user);
            token = Token.createJWT(token, username, "AuthToken:D", 30 * 1000);
            response.getWriter().println("{\n" +
                "\"ok\": true,\n" +
                "\"token\":\"" + token + "\"\n" +
                "}");
        } else {
            throw new Exception("password not correct");
        }
    }
}
