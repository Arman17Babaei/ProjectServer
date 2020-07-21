import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Socket;
import java.util.Enumeration;

public class makeTransaction extends HttpServlet {
    private final int bankPort = 2222;
    private final String IP = "127.0.0.1";
    private DataInputStream input;
    private DataOutputStream output;
    private Socket bankSocket;

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
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String type = request.getParameter("type");
        String money = request.getParameter("money");
        String sourceId = request.getParameter("sourceId");
        String destId = request.getParameter("destId");
        String description = request.getParameter("description");

        getSocket();

        String token = null;
        String message = null;
        try {
            token = getToken(username, password);
            message = "create_receipt " + token + " " + type + " " + money + " " + sourceId
                    + " " + destId + " " + description;
            sendMessage(message);
            String receiptId = input.readUTF();
            message = "pay " + receiptId;
            sendMessage(message);
        }catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\n" +
                    "\"ok\": false,\n" +
                    "\"error\": \"" + e.getMessage() + "\"\n" +
                    "}");
        }
        String reply = input.readUTF();
        response.getWriter().println("{\n" +
                "\"ok\": true, \n" +
                "\"reply\": \"" + reply + "\"\n" +
                "}");
        bankSocket.close();
    }

    private String getToken(String username, String password) throws IOException {
        String message = "get_token " + username + " " + password;
        sendMessage(message);
        return input.readUTF();
    }

    private void sendMessage(String message) throws IOException {
        output.writeUTF(message);
        output.flush();
    }

    private void getSocket() throws IOException {
        bankSocket = new Socket(IP, bankPort);
        output = new DataOutputStream(new BufferedOutputStream(bankSocket.getOutputStream()));
        input = new DataInputStream(new BufferedInputStream(bankSocket.getInputStream()));
    }
}
