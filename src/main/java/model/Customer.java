package model;

import controller.Database;

import java.util.ArrayList;
import java.util.HashMap;

public class Customer extends User {
    private HashMap<String, String> address;
    private HashMap<String, Integer> cart = new HashMap<>(); //<productId, Number>
    private Discount discountUsed;
    private ArrayList<String> purchaseHistory = new ArrayList<>();
    private ArrayList<String> discountCodes = new ArrayList<>();

    public Customer(String username, String name, String surname, String email, String phoneNumber, String password, long credit) {
        super(username, name, surname, email, phoneNumber, password, credit);
    }

    @Override
    public String getType() {
        return "customer";
    }

    public Discount getDiscountUsed() {
        return discountUsed;
    }

    public void deleteDiscount() {
        this.discountUsed = null;
    }

    public void setAddress(HashMap<String, String> address) {
        this.address = address;
    }

    public boolean hasDiscount(Discount discount) {
        return discountCodes.contains(discount.getId());
    }

    public void addToPurchaseHistory(PurchaseLog purchaseLog) {
        purchaseHistory.add(purchaseLog.getId());
    }

    public void addToCart(String productId) {
        cart.put(productId, 1);
    }

    public boolean addToCart(Product product, int quantity) {
        cart.put(product.getId(), quantity);
        System.out.println(cart);
        return true;
    }

    public void increaseNumberInCart(String productId) {
        try {
            if (isProductInCart(productId))
                if (cart.get(productId) < Database.getProductById(productId).getQuantity())
                    cart.put(productId, cart.get(productId) + 1);
        } catch (Exception ignored) {}
    }

    public int getProductQuantityInCart(String productId) {
        return cart.get(productId);
    }

    public void useDiscount(Discount discount) throws Exception {
        discount.useCode(this);
        discountUsed = discount;
    }

    public void undoUseDiscount () {
        discountUsed.undoUseCode(this);
        discountUsed = null;
    }

    public int decreaseNumberInCart(String productId) {
        if (isProductInCart(productId)) {
            if (cart.get(productId) <= 1)
                return cart.get(productId);
            cart.put(productId, cart.get(productId) - 1);
        }
        return cart.get(productId);
    }

    public void removeFromCart(String productId) {
        if (isProductInCart(productId)) {
            cart.remove(productId);
        }
    }

    public void emptyCart() {
        this.cart = new HashMap<>();
    }

    public HashMap<Product, Integer> getCart() {
        HashMap<Product, Integer> finalCart = new HashMap<>();
        for (String item : cart.keySet()) {
            try {
                finalCart.put(Database.getProductById(item), cart.get(item));
            } catch (Exception ignored) {
            }
        }
        return finalCart;
    }

    public void setQuantityInCart(String productId, int quantity) {
        cart.replace(productId, quantity);
    }

    public boolean isProductInCart(String productId) {
        return this.cart.containsKey(productId);
    }

    public int getProductInCart(String productId) {
        if (cart.containsKey(productId))
            return cart.get(productId);
        return 0;
    }

    public void payCredit(long cost) throws Exception {
        if (this.credit >= cost)
            this.setCredit(this.getCredit() - cost);
        else
            throw new Exception("Not enough credit");
    }

    public ArrayList<PurchaseLog> getPurchaseHistory() {
        ArrayList<PurchaseLog> purchaseLogs = new ArrayList<>();
        for (String log : this.purchaseHistory) {
            purchaseLogs.add(Database.getPurchaseLogById(log));
        }
        return purchaseLogs;
    }

    public ArrayList<Discount> getDiscountCodes() {
        ArrayList<Discount> toReturn = new ArrayList<>();
        for (String code : discountCodes) {
            toReturn.add(Database.getDiscountById(code));
        }
        return toReturn;
    }

    public boolean hasBoughtProduct(Product product) {
        return true;
//        for (String log : purchaseHistory) {
//            if (controller.Database.getPurchaseLogById(log).getProducts().containsKey(product))
//                return true;
//        }
//        return false;
    }

    public void setDiscountUsed(Discount discountUsed) {
        this.discountUsed = discountUsed;
    }

    @Override
    public String toString() {
        return  username;
    }
}
