package com.example.lab6.controller;

import com.example.lab6.model.Tuple;
import com.example.lab6.model.User;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.MessageService;
import com.example.lab6.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class FriendProfileController {

    public ImageView profilePhoto;
    public Label nameLabel;
    public Button addButton;
    public Button removeButton;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;


    Stage stage;
    private Long to;
    private String email;
    private String email_friend;

    @FXML
    public void initialize() {
        profilePhoto.setImage(new Image("/images/profile.png"));
  }

    public void setServices(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, Stage stage, String email, String email_friend) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.stage = stage;
        this.email = email;
        this.email_friend = email_friend;

        nameLabel.setText(userService.exists(email_friend).getFirstName() + " " + userService.exists(email_friend).getLastName() + "!");
        setProfilePicture();
        if(friendshipService.exists(userService.exists(email).getId(), userService.exists(email_friend).getId()) != null) {
            addButton.setVisible(false);
            removeButton.setVisible(true);
        }
        else
        {removeButton.setVisible(false);
            addButton.setVisible(true);
        }

    }

    private void setProfilePicture() {
        if(userService.findPhoto(email_friend) != null)
            profilePhoto.setImage(new Image(userService.findPhoto(email_friend)));

    }
    public void onAddButton(ActionEvent actionEvent) {
        friendRequestService.sendFriendRequest(userService.exists(email).getId(), userService.exists(email_friend).getId());
    }

    public void onRemoveButton(ActionEvent actionEvent) {
        userService.remove(userService.exists(email_friend).getId());
    }


}
