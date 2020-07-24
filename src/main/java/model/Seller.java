package model;

import controller.Database;

import java.util.ArrayList;

public class Seller extends User {
    private String companyName;
    private String companyInfo;
    private ArrayList<String> salesHistory = new ArrayList<>();
    private ArrayList<String> productsToSell;
    private ArrayList<String> allOffs;

    public Seller(String username, String name, String surname, String email, String phoneNumber, String password, long credit, String companyName, String companyInfo) {
        super(username, name, surname, email, phoneNumber, password, credit);
        this.companyName = companyName;
        this.companyInfo = companyInfo;
        try {
            this.bankAccountId = createBankAccount();
        } catch (Exception ignored) {
        }
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyInfo() {
        return companyInfo;
    }

    public ArrayList<SellLog> getSalesHistory() {
        ArrayList<SellLog> result = new ArrayList<>();
        for (String log : this.salesHistory) {
            result.add(Database.getSellLogById(log));
        }
        return result;
    }

    public ArrayList<Product> getProductsToSell () throws Exception {
        ArrayList<Product> result = new ArrayList<>();
        for (String product : productsToSell) {
            result.add(Database.getProductById(product));
        }
        return result;
    }

    public void addSellLog (SellLog log) {
        this.salesHistory.add(log.getId());
    }

    @Override
    public String getType() {
        return "seller";
    }

    @Override
    public String toString() {
        return getType() + "{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", credit=" + credit +
                ", id='" + id + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyInfo='" + companyInfo + '\'' +
                '}';
    }
}
