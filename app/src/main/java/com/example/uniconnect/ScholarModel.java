package com.example.uniconnect;

public class ScholarModel {
    private String title;
    private String titleLink;
    private String displayedLink;
    private String snippet;

    public ScholarModel(String title, String titleLink, String displayedLink, String snippet) {
        this.title = title;
        this.titleLink = titleLink;
        this.displayedLink = displayedLink;
        this.snippet = snippet;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleLink() {
        return titleLink;
    }

    public String getDisplayedLink() {
        return displayedLink;
    }

    public String getSnippet() {
        return snippet;
    }
}
