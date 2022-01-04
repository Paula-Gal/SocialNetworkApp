package com.example.lab6.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Event extends Entity<Long> {

    private String name;
    private LocalDate date;
    private LocalTime start;
    private List<Long> participants;
    private String description;
    private String location;
    private Long admin;
    private List<Long> subscribers;

    public Event(String name, LocalDate date, LocalTime start, List<Long> participants, String description, String location, Long admin, List<Long> subscribers) {
        this.name = name;
        this.date = date;
        this.start = start;
        this.participants = participants;
        this.description = description;
        this.location = location;
        this.admin = admin;
        this.subscribers = subscribers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public void setParticipants(List<Long> participants) {
        this.participants = participants;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStart() {
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
