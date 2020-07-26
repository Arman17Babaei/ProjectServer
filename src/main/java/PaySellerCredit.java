import controller.Database;
import model.Customer;
import model.Seller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class PaySellerCredit extends HttpServlet {
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
        Seller seller = (Seller) Database.getUserById(request.getParameter("destId"));
        Customer customer = (Customer) Database.getUserById(request.getParameter("sourceId"));
        long money = Long.parseLong(request.getParameter("money"));
        assert customer != null;
        long newMoney = (long) (Math.floor(((double) (100 - Database.getWage()) / 100) * money));
        //adding wage money to shop account
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "deposit");
        params.put("username", Database.getShopAccountId());
        params.put("password", Database.getShopPassword());
        params.put("money", String.valueOf(money - newMoney));
        params.put("description", "wageDeposited");
        //----------------------------------
        assert seller != null;
        customer.setCredit(customer.getCredit() - money);
        seller.setCredit(seller.getCredit() + newMoney);
        Database.update(seller, seller.getId());
        Database.update(customer, customer.getId());
        response.getWriter().println("{\n" +
                "\"ok\": true, \n" +
                "\"reply\": \"" + "Done successfully" + "\"\n" +
                "}");
    }
}
