package com.example.uniconnect;

public class Submission {
    private String id;
    private String content;
    private String marks;

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public Submission(String id, String content,String marks) {
        this.id = id;
        this.content = content;
        this.marks=marks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
