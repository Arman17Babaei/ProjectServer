package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Chat implements BaseModel {
    private final String id;
    boolean isPublic = false;
    private final ArrayList<String> usernames = new ArrayList<>();          // saves usernames
    private final ArrayList<ChatMessage> messages = new ArrayList<>();

    public Chat() {
        this.id = UUID.randomUUID().toString();
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void addUser(String username) {
        usernames.add(username);
    }

    public boolean hasUser(String userId) {
        return isPublic || usernames.contains(userId);
    }

    public void addMessage(ChatMessage message) {
        if (hasUser(message.getSenderId()))
            messages.add(message);
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getUsers() {
        return usernames;
    }
}
