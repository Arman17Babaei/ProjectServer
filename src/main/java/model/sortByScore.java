package model;

import java.util.Comparator;

public class sortByScore implements Comparator<Product> {

    @Override
    public int compare(Product o1, Product o2) {
        return Double.compare(o1.getAverageScore(), o2.getAverageScore());
    }

}