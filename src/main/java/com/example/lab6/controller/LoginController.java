package com.example.lab6.controller;

import com.example.lab6.model.PageDTO;
import com.example.lab6.model.User;
import com.example.lab6.service.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    public CheckBox hidePasswordCheckbox;
    public TextField passwordShowTextField;
    Stage stage;
    boolean isHiddenPass = true;

    public Label loginLabel;
    public AnchorPane anchorPanePurple;
    public ImageView logo;
    public ImageView emailIcon;
    public ImageView passwordIcon;
    public TextField emailTextField;
    public TextField passwordTextField;

    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    FriendRequestService friendRequestService;
    EventService eventService;
    PostService postService;

    private String email;

    @FXML
    private TextField loginField;

    public void setServices(UserService service, FriendshipService fservice, MessageService messageService, FriendRequestService friendRequestService, EventService eventService, PostService postService, Stage stage) {

        this.userService = service;
        this.friendshipService = fservice;
        this.messageService = messageService;
        this.friendRequestService = friendRequestService;
        this.stage = stage;
        this.eventService = eventService;
        this.postService = postService;
    }

    public void initialize() {
        logo.setImage(new Image("/images/mountain.png"));
        emailIcon.setImage(new Image("/images/user.png"));
        passwordIcon.setImage(new Image("/images/padlock.png"));
        passwordShowTextField.setVisible(false);
    }

    public void showUserDialog(String email) {
        // create a new stage for the popup dialog.
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/homeView.fxml"));

            AnchorPane root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setMaximized(true);
            PageDTO page = new PageDTO(userService.exists(email), userService.exists(email).getFriendsList());
            HomeController userViewController = loader.getController();
            userViewController.setServices(userService, friendshipService, friendRequestService, messageService, eventService, postService, dialogStage, email, page);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showCreateAccountDialog(User user) {
        try {

            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/createAccountView.fxml"));

            AnchorPane root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create Account");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            dialogStage.setMaximized(true);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AccountController accountViewController = loader.getController();
            accountViewController.setService(userService, friendRequestService, dialogStage);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCreateAccountDialog(MouseEvent ev) {
        showCreateAccountDialog(null);
    }

    public void loginLabel(InputMethodEvent inputMethodEvent) {
    }

    public void setServices(UserService service, FriendshipService fservice, MessageService messageService, FriendRequestService friendRequestService, Stage stage) {

        this.userService = service;
        this.friendshipService = fservice;
        this.messageService = messageService;
        this.friendRequestService = friendRequestService;
        this.stage = stage;
    }

    @FXML
    void viewpass() {
        if (hidePasswordCheckbox.isSelected()) {
            passwordTextField.setVisible(false);
            passwordShowTextField.setVisible(true);
            passwordShowTextField.setText(passwordTextField.getText());
        } else {
            passwordTextField.setVisible(true);
            passwordShowTextField.setVisible(false);
            passwordTextField.setText(passwordShowTextField.getText());
        }
    }

    public void onHandleLogin(ActionEvent actionEvent) {
        email = String.valueOf(emailTextField.getText());
        if (userService.exists(email) == null)
            MessageAlert.showErrorMessage(null, "The account do not exists");
        if (!userService.checkPassword(passwordTextField.getText(), email))
            MessageAlert.showErrorMessage(null, "The password is incorrect!");
        else {
            showUserDialog(email);
        }
        //test
//        try {
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("/views/messages-view.fxml"));
//
//            AnchorPane root = loader.load();
//
//            // Create the dialog Stage.
//            Stage dialogStage = new Stage();
//            dialogStage.setTitle("My messages");
//            Scene scene = new Scene(root);
//            dialogStage.setScene(scene);
//
//            MessagesController messagesController = loader.getController();
//            messagesController.setServices(messageService, friendshipService, userService,dialogStage, email);
//
//            dialogStage.show();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}