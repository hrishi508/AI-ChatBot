package com.example.aichatbot.chat;

public class ChatMsgModal {
    // variable for our string and int for a type of message.
    // type is to use weather the message in our recycler
    // view is from user or from FIrebase ML kit.
    private String message;
    private int type;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ChatMsgModal(String message, int type) {
        this.message = message;
        this.type = type;
    }
}

