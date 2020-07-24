package model;

import controller.Database;

import java.time.LocalDateTime;
import java.util.UUID;

public class SellLog implements BaseModel {
    private LocalDateTime date;
    private long amountReceived;
    private long amountReduced;
    private String soldProduct;
    private String customer;

    private enum shippingStatus {inStock, readyToPost, Posted}

    shippingStatus status = shippingStatus.inStock;
    private String id;

    public SellLog() {
        this.id = UUID.randomUUID().toString();
    }
    public String getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public long getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(long amountReceived) {
        this.amountReceived = amountReceived;
    }

    public long getAmountReduced() {
        return amountReduced;
    }

    public void setAmountReduced(long amountReduced) {
        this.amountReduced = amountReduced;
    }

    public Product getSoldProduct() throws Exception{
        return Database.getProductById(soldProduct);
    }

    public void setSoldProduct(Product product) {
        this.soldProduct = product.getId();
    }

    public Customer getCustomer() {
        return (Customer) Database.getUserById(customer);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer.getId();
    }

    public void setStatus(String status) {
        this.status = shippingStatus.valueOf(status);
    }

    public String getStatus() {
        return status.toString();
    }

    @Override
    public String toString() {
        return date + "\t" + Database.getUserById(customer) .getUsername() + "\t" + amountReceived;
    }
}
