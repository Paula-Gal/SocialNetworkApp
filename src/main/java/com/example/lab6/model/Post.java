package com.example.lab6.model;

import java.time.LocalDateTime;

public class Post extends Entity<Long> {

    private String description;
    private LocalDateTime date;
    private Long admin;
    private String url;

    public Post(Long admin, String url, String description, LocalDateTime date) {
        this.description = description;
        this.date = date;
        this.admin = admin;
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getAdmin() {
        return admin;
    }

    public void setAdmin(Long admin) {
        this.admin = admin;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
