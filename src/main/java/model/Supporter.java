package model;

public class Supporter extends User {
    public Supporter(String username, String name, String surname, String email, String phoneNumber, String password, long credit) {
        super(username, name, surname, email, phoneNumber, password, credit);
    }

    @Override
    public String getType() {
        return "supporter";
    }
}
