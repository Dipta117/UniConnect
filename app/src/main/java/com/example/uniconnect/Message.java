package com.example.uniconnect;

public class Message {
    private String id;
    private String sender;
    private String content;

    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String id, String sender, String content) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        //this.image=image;
//
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }
}
