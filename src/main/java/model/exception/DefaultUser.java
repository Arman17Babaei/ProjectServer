package model.exception;

import model.Customer;

public class DefaultUser extends Customer {

    public DefaultUser() {
            super("","AnonymousUser","",
                "username@example.com","+98xxxxxxxxxx","",0);
        setUsername(getId());
    }
}
