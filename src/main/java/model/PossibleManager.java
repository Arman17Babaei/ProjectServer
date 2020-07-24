package model;

import java.util.UUID;

public class PossibleManager implements BaseModel {
    private String id;
    private String username;

    public PossibleManager() {
        this.id = UUID.randomUUID().toString();
    }

    public PossibleManager(String username) {
        this.username = username;
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
