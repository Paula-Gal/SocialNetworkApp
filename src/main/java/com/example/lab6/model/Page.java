package com.example.lab6.model;

import java.util.List;

public class Page {

    private User admin;
    private List<User> friends;
    private List<MessageDTO> receivedMessages;
    private List<FriendRequest> myReceivedRequests;

    public Page(User admin, List<User> friends, List<MessageDTO> receivedMessages, List<FriendRequest> myReceivedRequests) {
        this.admin = admin;
        this.friends = friends;
        this.receivedMessages = receivedMessages;
        this.myReceivedRequests = myReceivedRequests;

    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public List<MessageDTO> getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(List<MessageDTO> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    public List<FriendRequest> getMyReceivedRequests() {
        return myReceivedRequests;
    }

    public void setMyReceivedRequests(List<FriendRequest> myReceivedRequests) {
        this.myReceivedRequests = myReceivedRequests;
    }

}