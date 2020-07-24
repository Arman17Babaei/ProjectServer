package model;

import controller.Database;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Off implements BaseModel {
    private List<String> products;
    private String sellerId;
    private OffStatus offStatus;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private int discountAmount;
    private int percentage;
    private int maxAmount;
    private String id;

    public Off() {
        this.id = UUID.randomUUID().toString();
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public Off(List<String> products, String offStatus, String startTime, String finishTime, String discountAmount, String seller) {
        //Done
        //LocalDateTime.parse("2007-12-03T10:15:30");
        this.products.addAll(products);
        this.offStatus = (offStatus.equals("A") ? OffStatus.A : OffStatus.B);
        this.startTime = LocalDateTime.parse(startTime);
        this.finishTime = LocalDateTime.parse(finishTime);
        this.discountAmount = Integer.parseInt(discountAmount);
        this.sellerId = seller;
        this.id = UUID.randomUUID().toString();
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    //Ask Arman in case of ambiguity

    public void putInDuty() throws Exception {
        for (String productId : products) {
            Product product = Database.getProductById(productId);
            product.setOff(id);
            Database.remove(product);
            Database.add(product);
            System.out.println(product.getOff());
        }
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getProducts() {
        return products;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public OffStatus getOffStatus() {
        return offStatus;
    }

    public void setOffStatus(String offStatus) {
        this.offStatus = (offStatus.equals("A") ? OffStatus.A : OffStatus.B);
    }

    @Override
    public String toString() {
        return "off id " + id + "\n" +
                "products:\t" + products.toString() + "\n" +
                "offStatus:\t" + offStatus.toString() + "\n" +
                "time:\t" + startTime + " to " + finishTime + "\n" +
                "Amount" + discountAmount;
    }

    private enum OffStatus {A, B}
}
