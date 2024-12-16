package com.example.uniconnect;

public class Group {
    private String groupName;
    private String ownerName;

    public Group(String groupName, String ownerName) {
        this.groupName = groupName;
        this.ownerName = ownerName;

    }

    public String getGroupName() {
        return groupName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getGroupId() {
        return groupName;
    }
}
