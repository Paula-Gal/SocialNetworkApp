package com.example.lab6.model;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    private Long id ;
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime date;
    private Message reply;

    /**
     * Constructor
     * @param id the id of a message
     * @param from the User who sent the message
     * @param to the receiver
     * @param message the message
     * @param date the date when the message has been sent
     */
    public Message(Long id, User from, List<User> to, String message, LocalDateTime date, Message reply) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply = reply;
    }



    public Message(Long id, User from, List<User> to, String message, LocalDateTime date) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply = null;
    }

    /**
     * Getter of reply message
     * @return message
     */
    public Message getReply() {
        return reply;
    }

    /**
     * Setter of reply
     * @param reply reply message
     */
    public void setReply(Message reply) {
        this.reply = reply;
    }

    /**
     * Getter of id
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter of id
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter of User
     * @return a User
     */
    public User getFrom() {
        return from;
    }

    /**
     * Setter of User
     * @param from
     */
    public void setFrom(User from) {
        this.from = from;
    }

    /**
     * Getter of a list with Users
     * @return a list with User
     */
    public List<User> getTo() {
        return to;
    }

    /**
     * Setter of a list with Users
     * @param to
     */
    public void setTo(List<User> to) {
        this.to = to;
    }

    /**
     * Getter of message
     * @return a String
     */
    public String getMessage() {
        return message;
    }
    /**
     * Setter of message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter of date
     * @return a LocalDateTime
     */
    public LocalDateTime getDate() {
        return date;
    }
    /**
     * Setter of date
     * @return
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
