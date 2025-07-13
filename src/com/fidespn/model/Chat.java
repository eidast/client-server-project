package com.fidespn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Chat implements Serializable {
    private static final long serialVersionUID = 1L;
    private String chatId;
    private String matchId;
    private List<ChatMessage> messages;

    public Chat(String chatId, String matchId) {
        this.chatId = chatId;
        this.matchId = matchId;
        this.messages = new ArrayList<>();
    }

    public String getChatId() {
        return chatId;
    }

    public String getMatchId() {
        return matchId;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void addMessage(ChatMessage message) {
        this.messages.add(message);
    }

    @Override
    public String toString() {
        return "Chat{" +
               "chatId='" + chatId + '\'' +
               ", matchId='" + matchId + '\'' +
               ", messageCount=" + messages.size() +
               '}';
    }
}