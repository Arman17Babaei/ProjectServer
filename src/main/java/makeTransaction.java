import controller.Database;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashMap;

public class makeTransaction extends HttpServlet {
    private static final int bankPort = 2222;
    private static final String IP = "127.0.0.1";
    private static DataInputStream input;
    private static DataOutputStream output;
    private static Socket bankSocket;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String reply = "";
        try {
            String type = request.getParameter("type");
            String username = request.getParameter("username");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            HashMap<String, String> fields = new HashMap<>();
            fields.put("username", request.getParameter("username"));
            fields.put("password", request.getParameter("password"));
            fields.put("type", request.getParameter("type"));
            fields.put("money", request.getParameter("money"));
            fields.put("sourceId", request.getParameter("sourceId"));
            fields.put("destId", request.getParameter("destId"));
            fields.put("description", request.getParameter("description"));
            if (type.equals("deposit")) {
                try {
                    checkCredit(request.getParameter("username"), request.getParameter("money"));
                }catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("{\n" +
                            "\"ok\": false,\n" +
                            "\"error\": \"" + e.getMessage() + "\"\n" +
                            "}");
                    return;
                }
            }
            reply = doTransaction(fields);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\n" +
                    "\"ok\": false,\n" +
                    "\"error\": \"" + e.getMessage() + "\"\n" +
                    "}");
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().println("{\n" +
                "\"ok\": true, \n" +
                "\"reply\": \"" + reply + "\"\n" +
                "}");
    }

    private void checkCredit(String username, String moneyString) throws Exception {
        long money = Long.parseLong(moneyString);
        User user = Database.getUserByUsername(username);
        assert user != null;
        if (user.getCredit() - money < Database.getMinimumCredit())
            throw new Exception("Not Enough Credit");
    }


    public static String doTransaction(HashMap<String, String> parameters) throws Exception {
        String username = parameters.get("username");
        String password = parameters.get("password");
        String type = parameters.get("type");
        String money = parameters.get("money");
        String sourceId = parameters.get("sourceId");
        String destId = parameters.get("destId");
        String description = parameters.get("description");

        getSocket();

        String token = null;
        String message = null;
        token = getToken(username, password);
        message = "create_receipt " + token + " " + type + " " + money + " " + sourceId
                + " " + destId + " " + description;
        sendMessage(message);
        String receiptId = input.readUTF();
        message = "pay " + receiptId;
        sendMessage(message);
        String reply = input.readUTF();
        sendMessage("exit");
        bankSocket.close();
        if (reply.contains("error"))
            throw new Exception(reply);
        return reply;
    }

    private static String getToken(String username, String password) throws IOException {
        String message = "get_token " + username + " " + password;
        sendMessage(message);
        return input.readUTF();
    }

    private static void sendMessage(String message) throws IOException {
        output.writeUTF(message);
        output.flush();
    }

    private static void getSocket() throws IOException {
        bankSocket = new Socket(IP, bankPort);
        output = new DataOutputStream(new BufferedOutputStream(bankSocket.getOutputStream()));
        input = new DataInputStream(new BufferedInputStream(bankSocket.getInputStream()));
    }
}
