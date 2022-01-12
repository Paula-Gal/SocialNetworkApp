package com.example.lab6.model;

import java.time.LocalDateTime;
import java.util.List;

public class Event extends Entity<Long> {

    private String title;
    private LocalDateTime creationDate;
    private LocalDateTime start;
    private LocalDateTime end;
    private List<Long> participants;
    private String description;
    private String location;
    private Long admin;
    private List<Long> subscribers;
    private LocalDateTime lastNotificationDate;

    public Event(String title, LocalDateTime creationDate, LocalDateTime start, LocalDateTime end, String description, String location, Long admin) {
        this.title = title;
        this.creationDate = creationDate;
        this.end = end;
        this.start = start;
        this.description = description;
        this.location = location;
        this.admin = admin;
    }

    public Event(String title, LocalDateTime creationDate, LocalDateTime start, LocalDateTime end, List<Long> participants, String description, String location, Long admin, List<Long> subscribers) {
        this.title = title;
        this.creationDate = creationDate;
        this.end = end;
        this.start = start;
        this.participants = participants;
        this.description = description;
        this.location = location;
        this.admin = admin;
        this.subscribers = subscribers;
    }

    public Event(String title, LocalDateTime start, LocalDateTime end, List<Long> participants, String description, String location, Long admin, List<Long> subscribers) {
        this.title = title;
        this.end = end;
        this.start = start;
        this.participants = participants;
        this.description = description;
        this.location = location;
        this.admin = admin;
        this.subscribers = subscribers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastNotificationDate() {
        return lastNotificationDate;
    }

    public void setLastNotificationDate(LocalDateTime lastNotificationDate) {
        this.lastNotificationDate = lastNotificationDate;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getName() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }

    public void setDate(LocalDateTime date) {
        this.creationDate = date;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setParticipants(List<Long> participants) {
        this.participants = participants;
    }

    public LocalDateTime getDate() {
        return creationDate;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public List<Long> getParticipants() {
        return participants;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Long getAdmin() {
        return admin;
    }

    public List<Long> getSubscribers() {
        return subscribers;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAdmin(Long admin) {
        this.admin = admin;
    }

    public void setSubscribers(List<Long> subscribers) {
        this.subscribers = subscribers;
    }
}
