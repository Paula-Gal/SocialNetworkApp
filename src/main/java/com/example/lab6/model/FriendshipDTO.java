package com.example.lab6.model;


import java.time.LocalDateTime;

public class FriendshipDTO {
    private User user;
    private LocalDateTime date;

    public FriendshipDTO(User user, LocalDateTime date) {

        this.user = user;
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId(){
        return user.getId().toString();
    }

    public String getFirstName(){
        return user.getFirstName();
    }

    public String getLastName(){
        return user.getLastName();
    }

}
