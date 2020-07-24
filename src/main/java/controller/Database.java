package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Database {

    private static final String shopAccountId = "1000";
    private static final String shopUsername = "onlineShop";
    private static final String shopPassword = "1234";
    private static final ArrayList<User> allUsers = new ArrayList<>();
    private static final ArrayList<Product> allProducts = new ArrayList<>();
    private static final ArrayList<JsonObject> allRequests = new ArrayList<>();
    private static final ArrayList<Discount> allDiscountCodes = new ArrayList<>();
    private static final ArrayList<Category> allCategories = new ArrayList<>();
    private static final ArrayList<Comment> allComments = new ArrayList<>();
    private static final ArrayList<Property> allProperties = new ArrayList<>();
    private static final ArrayList<Score> allScores = new ArrayList<>();
    private static final ArrayList<PurchaseLog> allPurchaseLogs = new ArrayList<>();
    private static final ArrayList<SellLog> allSellLogs = new ArrayList<>();
    private static final ArrayList<Off> allOffs = new ArrayList<>();
    private static final ArrayList<Product> allProductAds = new ArrayList<>();
    private static final ArrayList<String> allPossibleManagers = new ArrayList<>();
    private static final ArrayList<PossibleSupporter> allPossibleSupporters = new ArrayList<>();
    private static int wage = 5;

    static {
        Database.loadAllData();
    }

    public static void loadAllData() {
        makeDirectories();
        loadLists();
    }

    private static void makeDirectories() {
        makeDirectory(Manager.class);
        makeDirectory(Supporter.class);
        makeDirectory(Seller.class);
        makeDirectory(Customer.class);
        makeDirectory(Product.class);
        makeDirectory(JsonObject.class);
        makeDirectory(Discount.class);
        makeDirectory(Category.class);
        makeDirectory(Comment.class);
        makeDirectory(Property.class);
        makeDirectory(Score.class);
        makeDirectory(PurchaseLog.class);
        makeDirectory(SellLog.class);
        makeDirectory(Off.class);
        makeDirectory("ProductAd");
        makeDirectory(String.class);
        makeDirectory(PossibleSupporter.class);
    }

    private static void loadLists() {
        loadList(allUsers, Manager.class);
        loadList(allUsers, Supporter.class);
        loadList(allUsers, Seller.class);
        loadList(allUsers, Customer.class);
        loadList(allProducts, Product.class);
        loadList(allRequests, JsonObject.class);
        loadList(allDiscountCodes, Discount.class);
        loadList(allCategories, Category.class);
        loadList(allComments, Comment.class);
        loadList(allProperties, Property.class);
        loadList(allScores, Score.class);
        loadList(allPurchaseLogs, PurchaseLog.class);
        loadList(allSellLogs, SellLog.class);
        loadList(allOffs, Off.class);
        loadList(allProductAds, Product.class, "ProductAd");
        loadList(allPossibleManagers, String.class);
        loadList(allPossibleSupporters, PossibleSupporter.class);
    }

    private static <T> String getPath(String folderName) {
        return "Database\\" + folderName + "\\";
    }

    private static String getPath(Object object) {
        return "Database\\" + object.getClass().getSimpleName() + "\\";
    }

    private static <T> void makeDirectory(Class<T> cls) {
        makeDirectory(cls.getSimpleName());
    }

    private static <T> void makeDirectory(String folderName) {
        new File(getPath(folderName)).mkdirs();
    }

    private static <T> void loadList(ArrayList<T> list, Class<? extends T> cls) {
        loadList(list, cls, cls.getSimpleName());
    }

    private static <T> void loadList(ArrayList<T> list, Class<? extends T> cls, String folderName) {
        for (final File fileEntry : new File(getPath(folderName)).listFiles()) {
            try {
                FileReader fileReader = new FileReader(fileEntry);
                Object object = new Gson().fromJson(fileReader, cls);
                list.add(cls.cast(object));
                fileReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void writeObject(Object object, String id) {
        writeObject(object, id, object.getClass().getSimpleName());
    }

    static void writeObject(Object object, String id, String folderName) {
        String fileName = getPath(folderName) + id + ".json";
        FileWriter writer;
        try {
            writer = new FileWriter(fileName);
            new GsonBuilder().setPrettyPrinting().create().toJson(object, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteObject(Object object, String id) {
        deleteObject(id, object.getClass().getSimpleName());
    }

    private static void deleteObject(String id, String folderName) {
        try {
            File file = new File(getPath(folderName) + id + ".json");
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addPossibleManager(String username) {
        if (!allPossibleManagers.contains(username)) {
            allPossibleManagers.add(username);
        }
        writeObject(username, username);
    }

    public static void add(Object o) {

        if (o instanceof Manager)
            add((Manager) o);

        if (o instanceof Supporter)
            add((Supporter) o);

        if (o instanceof Seller)
            add((Seller) o);

        if (o instanceof Customer)
            add((Customer) o);

        if (o instanceof Product)
            add((Product) o);

        if (o instanceof JsonObject)
            add((JsonObject) o);

        if (o instanceof Discount)
            add((Discount) o);

        if (o instanceof Category)
            add((Category) o);

        if (o instanceof Comment)
            add((Comment) o);

        if (o instanceof Property)
            add((Property) o);

        if (o instanceof Score)
            add((Score) o);

        if (o instanceof Supporter)
            add((Supporter) o);

        if (o instanceof PurchaseLog)
            add((PurchaseLog) o);

        if (o instanceof SellLog)
            add((SellLog) o);

        if (o instanceof Off)
            add((Off) o);

        if (o instanceof PossibleSupporter)
            add((PossibleSupporter) o);

//        (allPossibleManagers, String.class);
//        (allPossibleSupporters, String.class, "PossibleSupporter");
    }


    public static void add(PossibleSupporter supporter) {
        allPossibleSupporters.add(supporter);
        writeObject(supporter, supporter.getUsername());
    }

    public static void add(User user) {
        allUsers.add(user);
        writeObject(user, user.getId());
    }

    public static void add(Product product) {
        for (Product productIn : allProducts) {
            if (productIn.equals(product.getId())) {
                allProducts.remove(productIn);
            }
        }
        allProducts.add(product);
        writeObject(product, product.getId());
    }

    public static void add(JsonObject request) {
        allRequests.add(request);
        writeObject(request, request.getAsJsonObject().get("id").getAsString());
    }

    public static void add(Discount discount) {
        allDiscountCodes.add(discount);
        writeObject(discount, discount.getId());
    }

    public static void add(Category category) {
        allCategories.add(category);
        writeObject(category, category.getId());
    }

    public static void add(Comment comment) {
        allComments.add(comment);
        writeObject(comment, comment.getId());
    }

    public static void add(Property property) {
        allProperties.add(property);
        writeObject(property, property.getId());
    }

    public static void add(Score score) {
        allScores.add(score);
        writeObject(score, score.getId());
    }

    public static void add(PurchaseLog log) {
        allPurchaseLogs.add(log);
        writeObject(log, log.getId());
    }

    public static void add(SellLog log) {
        allSellLogs.add(log);
        writeObject(log, log.getId());
    }

    public static void add(Off off) {
        allOffs.add(off);
        try {
            off.putInDuty();
        } catch (Exception ignored) {
        }
        writeObject(off, off.getId());
    }

    public static void addProductToAds(Product product) {
        allProductAds.add(product);
        writeObject(product, product.getId(), "ProductAd");
    }

    public static void remove(User user) {
        allUsers.remove(user);
        deleteObject(user, user.getId());
    }

    public static void remove(Product product) throws Exception {
        product = getProductById(product.getId());
        allProducts.remove(product);
        deleteObject(product, product.getId());
    }

    public static void remove(JsonElement jsonElement) {
        allRequests.remove(jsonElement);
        deleteObject(jsonElement, jsonElement.getAsJsonObject().get("id").getAsString());
    }

    public static void removePossibleManager(String username) {
        allPossibleManagers.remove(username);
        deleteObject(username, username);
    }

    public static void removeProductFromAds(Product product) {
        allProductAds.remove(product);
        deleteObject(product.getId(), "ProductAd");
    }

    public static Product getProductById(String id) throws Exception {
        for (Product product : allProducts) {
            if (product.getId().equals(id))
                return product;
        }
        throw new Exception("product id not found");
    }

    public static User getUserById(String id) {
        for (User user : allUsers) {
            if (user.getId().equals(id))
                return user;
        }
        return null;
    }

    public static JsonElement getRequestById(String id) {
        for (JsonElement jsonElement : allRequests) {
            if (jsonElement.getAsJsonObject().get("id").getAsString().equals(id)) {
                return jsonElement;
            }
        }
        return null;
    }

    public static Discount getDiscountById(String id) {
        for (Discount discountCode : allDiscountCodes) {
            if (discountCode.getId().equals(id))
                return discountCode;
        }
        return null;
    }

    public static Category getCategoryById(String id) {
        for (Category category : allCategories) {
            if (category.getId().equals(id))
                return category;
        }
        return null;
    }

    public static Comment getCommentById(String id) {
        for (Comment comment : allComments) {
            if (comment.getId().equals(id))
                return comment;
        }
        return null;
    }

    public static Property getPropertyById(String id) {
        for (Property property : allProperties) {
            if (property.getId().equals(id))
                return property;
        }
        return null;
    }

    public static Score getScoreById(String id) {
        for (Score score : allScores) {
            if (score.getId().equals(id))
                return score;
        }
        return null;
    }

    public static Off getOffById(String id) {
        for (Off off : allOffs) {
            if (off.getId().equals(id))
                return off;
        }
        return null;
    }

    public static PurchaseLog getPurchaseLogById(String id) {
        for (PurchaseLog log : allPurchaseLogs) {
            if (log.getId().equals(id))
                return log;
        }
        return null;
    }

    public static SellLog getSellLogById(String id) {
        for (SellLog log : allSellLogs) {
            if (log.getId().equals(id))
                return log;
        }
        return null;
    }

    public static User getUserByUsername(String username) {
        for (User user : allUsers) {
            if (user.getUsername().equals(username))
                return user;
        }
        return null;
    }

    public static Category getCategoryByName(String name) {
        for (Category category : allCategories) {
            if (category.getName().equals(name))
                return category;
        }
        return null;
    }

    public static Discount getDiscountByCode(String code) {
        for (Discount discountCode : allDiscountCodes) {
            if (discountCode.getCode().equals(code))
                return discountCode;
        }
        return null;
    }

    public static Property getPropertyByName(String name) {
        for (Property property : allProperties) {
            if (property.getName().equals(name))
                return property;
        }
        return null;
    }

    public static ArrayList<String> getAllPossibleManagers() {
        return allPossibleManagers;
    }

    public static ArrayList<Category> getAllCategories() {
        return allCategories;
    }

    public static ArrayList<Product> getAllProducts() {
        return allProducts;
    }

    public static ArrayList<Product> getAllProductAds() {
        return allProductAds;
    }

    public static ArrayList<User> getAllUsers() {
        return allUsers;
    }

    public static ArrayList<JsonObject> getAllRequests() {
        return allRequests;
    }

    public static ArrayList<Discount> getAllDiscountCodes() {
        return allDiscountCodes;
    }

    public static ArrayList<Comment> getAllComments() {
        return allComments;
    }

    public static ArrayList<Property> getAllProperties() {
        return allProperties;
    }

    public static ArrayList<Score> getAllScores() {
        return allScores;
    }

    public static ArrayList<PurchaseLog> getAllPurchaseLogs() {
        return allPurchaseLogs;
    }

    public static ArrayList<SellLog> getAllSellLogs() {
        return allSellLogs;
    }

    public static ArrayList<Off> getAllOffs() {
        return allOffs;
    }

    public static String getShopAccountId() {
        return shopAccountId;
    }

    public static String getShopUsername() {
        return shopUsername;
    }

    public static String getShopPassword() {
        return shopPassword;
    }

    public static int getWage() {
        return wage;
    }

    public static void update(Object object, String id) {
        writeObject(object, id);
    }

    public static void remove(Category category) {
        if (category.getParentCategory() != null) {
            getCategoryById(category.getParentCategory()).getSubCategories().remove(category.getId());
        }
        allCategories.remove(category);
        deleteObject(category, category.getId());
    }
}
