package model;


import java.util.ArrayList;
import java.util.UUID;

public class Filter implements BaseModel {
    private ArrayList<Property> properties = new ArrayList<>();
    //some other properties are:
    //category, name, inStock(number property), brand, maxPrice, minPrice
    private String id;

    @Override
    public String getId() {
        return id;
    }

    public Filter() {
        this.id = UUID.randomUUID().toString();
    }

    public boolean isValid(Product product) {
        for (Property property : properties) {
            if (!product.hasProperty(property))
                return false;
        }
        return true;
    }

    public ArrayList<Product> apply (ArrayList<Product> products) {
        ArrayList<Product> result = new ArrayList<>();
        for (Product product : products) {
            if (isValid(product))
                result.add(product);
        }
        return result;
    }

    public void addRestriction (Property property) {
        this.properties.add(property);
    }

    public void removeRestriction (String property) {
        properties.removeIf(thisProperty -> thisProperty.getName().equals(property));
    }

    public ArrayList<Property> getProperties() {
        return properties;
    }
}
