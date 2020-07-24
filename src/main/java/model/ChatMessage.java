package model;

import java.time.LocalDateTime;

public class ChatMessage {
    private final String senderId;
    private final LocalDateTime time;
    private final String text;

    public ChatMessage(String senderId, String text) {
        this.senderId = senderId;
        this.text = text;
        this.time = LocalDateTime.now();
    }

    public String getSenderId() {
        return senderId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getText() {
        return text;
    }
}
