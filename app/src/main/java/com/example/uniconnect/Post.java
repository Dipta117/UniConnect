package com.example.uniconnect;

import java.util.ArrayList;

public class Post {
    private String postId;
    private String text;
    private String userName;
    private long timestamp;
    private ArrayList<String> comments;

    // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    public Post() {
    }

    public Post(String postId, String text, String userName, long timestamp, ArrayList<String> comments) {
        this.postId = postId;
        this.text = text;
        this.userName = userName;
        this.timestamp = timestamp;
        this.comments = comments;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }
}
