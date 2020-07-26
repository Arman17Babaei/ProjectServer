import controller.Database;
import model.Seller;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class PaySellerByBankAccount extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

    private void handleGetObject(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String reply = null;
        String sellerId = request.getParameter("destId");
        long money = Long.parseLong(request.getParameter("money"));

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("username", request.getParameter("username"));
        parameters.put("password", request.getParameter("password"));
        parameters.put("type", "move");
        parameters.put("money", String.valueOf(money));
        parameters.put("sourceId", request.getParameter("sourceId"));
        parameters.put("destId", Database.getShopAccountId());
        parameters.put("description", request.getParameter("description"));

        try {
            reply = makeTransaction.doTransaction(parameters);
            long newMoney = (long) (Math.floor(((double) (100 - Database.getWage()) / 100) * money));
            Seller seller = (Seller) Database.getUserById(sellerId);
            assert seller != null;
            seller.setCredit(seller.getCredit() + newMoney);
            Database.update(seller, sellerId);
            //Withdrawing the money from shop account
            parameters.replace("username", Database.getShopUsername());
            parameters.replace("password", Database.getShopPassword());
            parameters.replace("money", String.valueOf(newMoney));
            parameters.replace("type", "withdraw");
            parameters.replace("sourceId", Database.getShopAccountId());
            parameters.replace("destId", "-1");
            reply = makeTransaction.doTransaction(parameters);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\n" +
                    "\"ok\": false,\n" +
                    "\"error\": \"" + e.getMessage() + "\"\n" +
                    "}");
            return;
        }
        response.getWriter().println("{\n" +
                "\"ok\": true, \n" +
                "\"reply\": \"" + reply + "\"\n" +
                "}");
    }
}
