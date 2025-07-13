package com.fidespn.model;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String messageId;
    private String chatId;
    private String senderId;
    private String senderUsername;
    private String content;
    private Date timestamp;

    public ChatMessage(String messageId, String chatId, String senderId, String senderUsername, String content) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.content = content;
        this.timestamp = new Date();
    }

    public String getMessageId() {
        return messageId;
    }

    public String getChatId() {
        return chatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
               "messageId='" + messageId + '\'' +
               ", chatId='" + chatId + '\'' +
               ", senderId='" + senderId + '\'' +
               ", senderUsername='" + senderUsername + '\'' +
               ", content='" + content + '\'' +
               ", timestamp=" + timestamp +
               '}';
    }
}