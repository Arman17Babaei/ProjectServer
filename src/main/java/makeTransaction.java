import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashMap;

public class makeTransaction {
    private static final int bankPort = 2222;
    private static final String IP = "127.0.0.1";
    private static DataInputStream input;
    private static DataOutputStream output;
    private static Socket bankSocket;



    public static String doTransaction(HashMap<String, String> parameters) throws IOException {
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
        bankSocket.close();
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
