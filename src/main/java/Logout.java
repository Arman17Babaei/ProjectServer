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

public class Logout extends HttpServlet {
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!RequestManager.isAllowed(request)) {
            response.setStatus(429);
            return;
        }
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            handleLogout(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().println("{\n" +
                "\"ok\": false,\n" +
                "\"error\": \"" + e.getMessage() + "\"\n" +
                "}");
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        //User user = TokenMap.getUser(token);

        TokenMap.removeToken(token);
        response.getWriter().println("{\n" +
            "\"ok\": true\n" +
            "}");
    }
}
