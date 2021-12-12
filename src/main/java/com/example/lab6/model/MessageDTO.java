package com.example.lab6.model;

import java.time.LocalDateTime;
import java.util.List;

public class MessageDTO extends Entity<Long>{
    private Long from;
    private List<Long> to;
    private String message;
    private LocalDateTime date;
    private Long reply;

    public Long getId() {
        return id;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    public List<Long> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Long getReply() {
        return reply;
    }

    public MessageDTO(Long from, List<Long> to, String message, LocalDateTime date, Long reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply = reply;
    }
}
