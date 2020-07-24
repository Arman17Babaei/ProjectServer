import model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ObjectChecker {

    private static HashMap<String, ArrayList<String>> availableTypes;

    {
        availableTypes = new HashMap<>();
        availableTypes.put("manager", new ArrayList<>(Arrays.asList("Category", "Comment", "Customer", "Discount", "Filter", "Manager", "Off", "Product", "Property", "PurchaseLog", "Score", "Seller", "SellLog")));
        availableTypes.put("customer", new ArrayList<>(Arrays.asList("Category", "Comment", "Customer", "Discount", "Filter", "Manager", "Off", "Product", "Property", "PurchaseLog", "Score", "Seller", "SellLog")));
        availableTypes.put("seller", new ArrayList<>(Arrays.asList("Category", "Comment", "Customer", "Discount", "Filter", "Manager", "Off", "Product", "Property", "PurchaseLog", "Score", "Seller", "SellLog")));
    }

    public static void checkObject(Object modelObject) {}

    public static void checkObjectPOST(Object modelObject, User user) throws Exception {
        return;
/*        if (user == null) {
            throw new Exception("You can't post data without logging in");
        }
        System.out.println(user.getType());
        if (!availableTypes.get(user.getType()).contains(modelObject.getClass().getSimpleName())) {
            throw new Exception("You are not allowed to post such data type");
        }*/
    }
}
