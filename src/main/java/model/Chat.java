package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Chat {
    private final String id;
    boolean isPublic = false;
    private final ArrayList<String> users = new ArrayList<>();
    private final ArrayList<ChatMessage> messages = new ArrayList<>();

    public Chat() {
        this.id = UUID.randomUUID().toString();
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void addUser(String userId) {
        users.add(userId);
    }

    private boolean hasUser(String userId) {
        return isPublic || users.contains(userId);
    }

    public void addMessage(ChatMessage message) {
        if (hasUser(message.getSenderId()))
            messages.add(message);
    }

    public List<ChatMessage> getMessages(String userId) {
        if (!hasUser(userId))
            return new ArrayList<>();

        return messages;
    }

    public List<ChatMessage> getMessages(String userId, int from) {
        if (!hasUser(userId))
            return new ArrayList<>();

        if (from >= messages.size())
            from = 0;
        return messages.subList(from, messages.size());
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getUsers() {
        return users;
    }
}
