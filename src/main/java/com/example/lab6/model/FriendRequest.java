package com.example.lab6.model;

import java.time.LocalDateTime;

public class FriendRequest extends Entity<Tuple<Long,Long>> {
    private Long from;
    private Long to;
    private Status status;
    private LocalDateTime lastUpdatedDate;

    public FriendRequest(Long from, Long to, Status status, LocalDateTime lastUpdatedDate) {
        this.from = from;
        this.to = to;
        this.status = status;
        this.lastUpdatedDate = lastUpdatedDate;
    }

    /**
     * Get method for from user
     * @return the if of from user
     */
    public Long getFrom() {
        return from;
    }

    /**
     * Set method for from user
     * @param from
     */
    public void setFrom(Long from) {
        this.from = from;
    }

    /**
     * Get method for to user
     * @return the id of to user
     */
    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
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
    public LocalDateTime getLastUpdatedDate() {
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
