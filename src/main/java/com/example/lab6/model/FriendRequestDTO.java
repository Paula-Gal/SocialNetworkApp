package com.example.lab6.model;

import java.time.LocalDateTime;

public class FriendRequestDTO extends Entity<Tuple<Long,Long>> {
    private User from;
    private User to;
    private Status status;
    private LocalDateTime lastUpdatedDate;

    public FriendRequestDTO(User from, User to, Status status, LocalDateTime lastUpdatedDate) {
        this.from = from;
        this.to = to;
        this.status = status;
        this.lastUpdatedDate = lastUpdatedDate;
    }

    /**
     * Get method for from user
     * @return the name of a user
     */
    public String getFrom() {

        return from.getFirstName() + " " + from.getLastName();
    }

    public User getUserFrom(){
        return from;
    }

    public User getUserTo(){
        return to;
    }
    public Long getIdF(){
        return from.getId();
    }

    public Long getIdT(){
        return to.getId();
    }

    /**
     * Set method for from user
     * @param from
     */
    public void setFrom(User from) {
        this.from = from;
    }

    /**
     * Get method for to user
     * @return the nameof to user
     */
    public String getTo() {
        return to.getFirstName() + " " + to.getLastName();
    }

    public void setTo(User to) {
        this.to = to;
    }

    /**
     * Get method for status
     * @return the status of the request
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set method for status
     * @param status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Get method for lastUpdatedDate
     * @return lastUpdateDate for a request
     */
    public LocalDateTime getDate() {
        return lastUpdatedDate;
    }

    /**
     * Set method for lastUpdateDate
     * @param lastUpdatedDate
     */
    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
