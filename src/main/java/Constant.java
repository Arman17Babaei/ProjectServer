import com.google.gson.Gson;
import com.google.gson.JsonObject;
import controller.Constants;
import controller.Database;
import io.jsonwebtoken.Claims;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

public class Constant extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!RequestManager.isAllowed(request)) {
            response.setStatus(429);
            return;
        }
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            handleGet(request, response);
        } catch (Exception e) {
            RequestManager.setBadRequest(request);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\n" +
                "\"ok\": false,\n" +
                "\"error\": \"" + e.getMessage() + "\"\n" +
                "}");
        }
    }

    private void handleGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        String wage = null;
        String minimumAmount = null;
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length > 1) {
                throw new Exception("You should enter only one value in each field");
            }
            if (paramName.equals("wage")) {
                wage = paramValues[0];
            }
            if (paramName.equals("minimumCredit")) {
                minimumAmount = paramValues[0];
            }
        }
        System.out.println();
        User user = TokenMap.getUser(token);

        Database.setConstants(new Constants(wage, minimumAmount));

        token = Token.createJWT(TokenMap.renewToken(token), user.getUsername(), "AuthToken:D", 30 * 1000);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ok", "ture");
        jsonObject.addProperty("token", token);

        System.out.println(Database.getWage());
        System.out.println(Database.getMinimumCredit());

        response.getWriter().println(new Gson().toJson(jsonObject));
    }
}
