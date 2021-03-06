import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

public class RequestManager {
    private static final String[] HEADERS_TO_TRY = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR" };

    private static final int MAX_REQUESTS = 1000;
    private static final int MAX_UNHANDLED_REQUESTS = 2000;
    private static final int MAX_BAD_REQUESTS = 200;

    private static HashMap<String, Deque<Long>> time = new HashMap<>();
    private static HashMap<String, Deque<Long>> badTime = new HashMap<>();

    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    private static boolean isAllowed(String ipAddress) {
        System.out.println(ipAddress);
        if (!time.containsKey(ipAddress)) {
            time.put(ipAddress, new LinkedList<>());
        }
        Deque<Long> timestamp = time.get(ipAddress);
        timestamp.addLast(System.currentTimeMillis());
        while ((!timestamp.isEmpty() && timestamp.getFirst() + 60 * 1000 < System.currentTimeMillis()) || timestamp.size() > MAX_UNHANDLED_REQUESTS) {
            timestamp.removeFirst();
        }
        if (timestamp.size() > MAX_REQUESTS) return false;

        if (!badTime.containsKey(ipAddress)) {
            badTime.put(ipAddress, new LinkedList<>());
        }
        timestamp = badTime.get(ipAddress);
        while ((!timestamp.isEmpty() && timestamp.getFirst() + 60 * 1000 < System.currentTimeMillis()) || timestamp.size() > MAX_UNHANDLED_REQUESTS) {
            timestamp.removeFirst();
        }
        return timestamp.size() < MAX_BAD_REQUESTS;
    }

    private static void setBadRequest(String ipAddress) {
        if (!badTime.containsKey(ipAddress)) {
            badTime.put(ipAddress, new LinkedList<>());
        }
        Deque<Long> timestamp = badTime.get(ipAddress);
        timestamp.addLast(System.currentTimeMillis());
    }

    public static void setBadRequest(HttpServletRequest request) {
        setBadRequest(getClientIpAddress(request));
    }

    public static boolean isAllowed(HttpServletRequest request) {
        return isAllowed(getClientIpAddress(request));
    }
}
