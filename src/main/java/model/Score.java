package model;

import controller.Database;

import java.util.UUID;

public class Score implements BaseModel {
    private String user;
    private int score;
    private String product;
    private String id;

    public Score(User user, int score, Product product) {
        this.user = user.getId();
        this.score = score;
        this.product = product.getId();
        this.id = UUID.randomUUID().toString();
    }

    public User getUser() {
        return Database.getUserById(user);
    }

    public int getScore() {
        return score;
    }

    public Product getProduct() throws Exception{
        return Database.getProductById(product);
    }

    public String getId() {
        return id;
    }
}
