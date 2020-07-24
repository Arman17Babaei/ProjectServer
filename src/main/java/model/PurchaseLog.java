package model;

import controller.Database;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

public class PurchaseLog implements BaseModel {
    private LocalDateTime date;
    private long amountPaid;
    private String discount;
    private HashMap<String, Integer> products;//<Product, Number>

    private enum deliveryStatus {InStock, Sent, Delivered}

    deliveryStatus status = deliveryStatus.InStock;
    private String id;

    public PurchaseLog() {
        this.products = new HashMap<>();
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public long getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(long amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Discount getDiscount() {
        return Database.getDiscountById(discount);
    }

    public void setDiscount(Discount discount) {
        this.discount = discount.getId();
    }

    public HashMap<Product, Integer> getProducts() {
        HashMap<Product, Integer> toReturn = new HashMap<>();
        for (String product : products.keySet()) {
            try {
                toReturn.put(Database.getProductById(product), products.get(product) );
            } catch (Exception ignored) {}
        }
        return toReturn;
    }

    public void setProducts(HashMap<Product, Integer> products) {
        this.products = new HashMap<>();
        for (Product product : products.keySet()) {
            this.products.put(product.getId(), products.get(product));
        }
    }

    public String getStatus() {
        return status.toString();
    }

    public void setStatus(String status) {
        this.status = deliveryStatus.valueOf(status);
    }

    @Override
    public String toString() {
        return date + "\t" + amountPaid;
    }
}
