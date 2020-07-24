package model;

import controller.Database;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class Comment implements BaseModel {
    private String user;
    private String product;
    private String text;
    private String title;
    private LocalDate time;

    private enum CommentStatus {WaitingForConfirmation, Confirmed, RejectedByManager}

    CommentStatus status = CommentStatus.WaitingForConfirmation;
    private boolean bought;
    private String id;
    private String parent = "";
    private int depth = 0;
    private ArrayList<String> childern = new ArrayList<>(); //id

    public Comment(Comment parent, User user, Product product, String title, String text) {
        this.user = user.getId();
        this.product = product.getId();
        this.title = title;
        this.text = text;
        this.id = UUID.randomUUID().toString();
        this.time = LocalDate.now();
        this.bought = user instanceof Customer && ((Customer) user).hasBoughtProduct(product);
        if (parent != null) {
            this.parent = parent.getId();
            depth = parent.depth + 1;
        }
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return Database.getUserById(user);
    }

    public Product getProduct() throws Exception {
        return Database.getProductById(product);
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }


    public boolean hasBought() {
        return bought;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status.toString();
    }

    public void setStatus(String status) {
        this.status = CommentStatus.valueOf(status);
    }

    public LocalDate getDate() {
        return time;
    }

    public void addChild(Comment comment) throws Exception {
        childern.add(comment.getId());
        Database.update(this, getId());
    }

    public Comment getParent() {
        return Database.getCommentById(parent);
    }

    public int getDepth() {
        return depth;
    }

    public ArrayList<Comment> getChildren() {
        ArrayList<Comment> childrenList = new ArrayList<>();
        for (String child : childern) {
            childrenList.add(Database.getCommentById(child));
        }
        return childrenList;
    }

    @Override
    public String toString() {
        return "Title: "+ this.title + "\n" + getUser().getFullName() + " said:\n" + text + "\n" +
                "has bought this product: " +
                (bought ? "yes" : "no");
    }
}
