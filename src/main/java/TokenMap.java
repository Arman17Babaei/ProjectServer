import model.User;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TokenMap {
    private final static int tokenLength = 100;

    private static HashMap<String, User> userMap = new HashMap<>();

    public static String getToken(User user) {
        int n = tokenLength;
        byte[] array = new byte[256];
        new Random().nextBytes(array);

        String randomString
            = new String(array, StandardCharsets.UTF_8);

        // Create a StringBuffer to store the result
        StringBuffer r = new StringBuffer();

        // Append first 20 alphanumeric characters
        // from the generated random String into the result
        for (int k = 0; k < randomString.length(); k++) {

            char ch = randomString.charAt(k);

            if (((ch >= 'a' && ch <= 'z')
                || (ch >= 'A' && ch <= 'Z')
                || (ch >= '0' && ch <= '9'))
                && (n > 0)) {

                r.append(ch);
                n--;
            }
        }

        // return the resultant string
        if (userMap.containsKey(r.toString())) {
            return getToken(user);
        }
        userMap.put(r.toString(), user);
        return r.toString();
    }

    public static User getUser(String token) throws Exception {
        if (userMap.containsKey(token)) {
            return userMap.get(token);
        } else {
            throw new Exception("invalid token");
        }
    }

    public static void removeToken(String token) {
        userMap.remove(token);
    }
}
