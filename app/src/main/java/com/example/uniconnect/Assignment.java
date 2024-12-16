package com.example.uniconnect;

public class Assignment {
    private String assignmentName;
    private String description;
    private String deadline;
    private String marks;
    private String fileLink;
    private String assignid;

    public Assignment() {}

    public Assignment(String assignmentName, String description, String deadline, String marks, String fileLink,String assignid) {
        this.assignmentName = assignmentName;
        this.description = description;
        this.deadline = deadline;
        this.marks = marks;
        this.fileLink = fileLink;
this.assignid=assignid;
    }



    public void setAssignid(String assignid) {
        this.assignid = assignid;
    }

    public String getAssignid() {
        return assignid;
    }

    public String getTitle() {
        return assignmentName;
    }

    public void setTitle(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
    }





    public String getDescription() {
        return description;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getMarks() {
        return marks;
    }

    public String getFileLink() {
        return fileLink;
    }




}

