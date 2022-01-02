package com.example.lab6.model.pages;

import com.example.lab6.model.User;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.MessageService;
import com.example.lab6.service.UserService;

public class PageObject {
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private MessageService  messageService;

    private final User loggedUser;

    public PageObject(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, MessageService  messageService, User loggedUser) {
        this.userService = userService;
        this.friendRequestService = friendRequestService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.loggedUser = loggedUser;
    }

    public UserService getUserService() {
        return userService;
    }

    public FriendshipService getFriendshipService() {
        return friendshipService;
    }

    public FriendRequestService getFriendRequestService() {
        return friendRequestService;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public User getLoggedUser() {
        return loggedUser;
    }
}
