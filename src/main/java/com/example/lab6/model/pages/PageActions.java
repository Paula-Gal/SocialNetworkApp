package com.example.lab6.model.pages;

import com.example.lab6.model.*;
import com.example.lab6.model.validators.ValidationException;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.MessageService;
import com.example.lab6.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PageActions {

    private final PageObject pageObject;

    public PageActions(PageObject pageObject) {
        this.pageObject=pageObject;
    }


    public UserService getUserService(){
        return pageObject.getUserService();
    }

    public FriendshipService getFriendshipService(){
        return pageObject.getFriendshipService();
    }

    public FriendRequestService getFriendRequestService(){
        return pageObject.getFriendRequestService();
    }
    public MessageService getMessageSercice(){
        return pageObject.getMessageService();
    }



    public void sendFriendRequest(Long to) {
        pageObject.getFriendRequestService().sendFriendRequest(pageObject.getLoggedUser().getId(), to);

    }

    public void removeFriend(Long to){
        pageObject.getFriendshipService().removeFriendship(pageObject.getLoggedUser().getId(), to);
    }

    public List<FriendRequestDTO> getFriendRequest(){
        return pageObject.getFriendRequestService().getFriendRequest(pageObject.getLoggedUser().getId());
    }

    public void deleteFriendRequest(Long to){
        pageObject.getFriendRequestService().deleteFriendRequest(pageObject.getLoggedUser().getId(), to);
    }

    public List<Message> getConversation(Long to){
        return pageObject.getMessageService().getConversation(pageObject.getLoggedUser().getId(), to);
    }

}
