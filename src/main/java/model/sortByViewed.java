package model;

import java.util.Comparator;

public class sortByViewed implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        return o1.getViewed() - o2.getViewed();
    }

}