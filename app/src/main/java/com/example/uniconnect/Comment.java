package com.example.uniconnect;

public class Comment {
    private String commenterName;
    private String commentText;
    private long timestamp; // Optional: If you want to store the time of the comment

    public Comment() {
        // Default constructor for Firebase
    }

    public Comment(String commenterName, String commentText, long timestamp) {
        this.commenterName = commenterName;
        this.commentText = commentText;
        this.timestamp = timestamp;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
