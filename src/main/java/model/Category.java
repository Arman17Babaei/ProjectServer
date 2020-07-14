package model;

import controller.Database;

import java.util.ArrayList;
import java.util.UUID;

public class Category {
    private String name;
    private ArrayList<String> specialProperties = new ArrayList<>();
    private ArrayList<String> products = new ArrayList<>();
    private ArrayList<String> subCategories = new ArrayList<>(); //id
    private String parentCategory; //id
    private String id;

    public Category() {
        id = UUID.randomUUID().toString();
    }

    public Category(String name) {
        this.name = name;
        products = new ArrayList<>();
        specialProperties = new ArrayList<>();
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = new ArrayList<>();
        for (Product product : products) {
            this.products.add(product.getId());
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<String> subCategories) {
        this.subCategories = subCategories;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    public ArrayList<Property> getSpecialProperties() {
        ArrayList<Property> finalList = new ArrayList<>();
        for (String property : specialProperties) {
            finalList.add(Database.getPropertyById(property));
        }
        if (parentCategory != null) {
            finalList.addAll(Database.getCategoryById(parentCategory).getSpecialProperties());
        }
        return finalList;
    }

    public ArrayList<Property> getOnlyThisProperties() {
        ArrayList<Property> finalList = new ArrayList<>();
        for (String property : specialProperties) {
            finalList.add(Database.getPropertyById(property));
        }
        return finalList;
    }

    public ArrayList<Product> getProducts() {
        ArrayList<Product> finalList = new ArrayList<>();
        for (String product : products) {
            try {
                finalList.add(Database.getProductById(product));
            } catch (Exception ignored) {}
        }
        return finalList;
    }

    public void addProduct(Product product) {
        this.products.add(product.getId());
    }

    public void addProperty(Property property) {
        this.specialProperties.add(property.getId());
    }

    public void addSubCategory(Category category) {
        this.subCategories.add(category.getId());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Category)
            return ((Category) object).getName().equals(this.name);
        else if (object instanceof String)
            return object.equals(this.name);
        return false;
    }

    public void removeProperty(Property property) {
        this.specialProperties.remove(property.getId());
    }

    @Override
    public String toString() {
        return name;
    }
}
