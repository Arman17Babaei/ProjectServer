import model.Category;

public class ClassMap {
    public static Class getClass(String c) {
        if (c.equals("Category")) {
            return Category.class;
        }
        return null;
    }
}
