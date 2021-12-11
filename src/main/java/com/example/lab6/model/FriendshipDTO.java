package com.example.lab6.model;

import java.time.LocalDate;

public class FriendshipDTO {
    private User user;
    private LocalDate date;

    public FriendshipDTO(User user, LocalDate date) {

        this.user = user;
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
