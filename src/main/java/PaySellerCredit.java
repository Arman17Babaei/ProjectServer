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
        if (customer.getCredit() < money) {
            response.getWriter().println("{\n" +
                    "\"ok\": false, \n" +
                    "\"error\": \"" + "Not enough credit" + "\"\n" +
                    "}");
        }else {
            customer.setCredit(customer.getCredit() - money);
            money = (long) (Math.floor((double) (100 - Database.getWage()) / 100) * money);
            assert seller != null;
            seller.setCredit(seller.getCredit() + money);
            Database.update(seller, seller.getId());
            Database.update(customer, customer.getId());
            response.getWriter().println("{\n" +
                    "\"ok\": true, \n" +
                    "\"reply\": \"" + "Done successfully" + "\"\n" +
                    "}");
        }
    }
}
