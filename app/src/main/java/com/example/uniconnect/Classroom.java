package com.example.uniconnect;

public class Classroom {
    private String className;
    private String subject;
    private String room;
    private String teacher;
    private String classroomKey;
    private String date;
    private int numPresence;

    public Classroom() {
        // Default constructor required for calls to DataSnapshot.getValue(Classroom.class)
    }

    public Classroom(String className, String date, String classroomKey) {
        this.className = className;
        this.date = date;

        this.classroomKey = classroomKey;
    }

    // Getters and setters
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getClassroomKey() {
        return classroomKey;
    }

    public void setClassroomKey(String classroomKey) {
        this.classroomKey = classroomKey;
    }
}
