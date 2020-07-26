package controller;

public class Constants {
    int wage;
    long minimumCredit;

    public Constants (String wage, String minimumCredit) {
        this.wage = Integer.parseInt(wage);
        this.minimumCredit = Long.parseLong(minimumCredit);
    }
}
