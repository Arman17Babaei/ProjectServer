import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Socket;
import java.util.Enumeration;

public class createBankAccount extends HttpServlet {
    private final int bankPort = 2222;
    private final String IP = "127.0.0.1";
    private DataInputStream input;
    private DataOutputStream output;
    private Socket bankSocket;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

    private void handleGetObject(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Enumeration<String> parameterNames = request.getParameterNames();
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        //connecting to bank server
        getSocket();
        String message = "create_account " + firstName + " " + lastName + " " + username
                + " " + password + " " + password;
        try {
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
