package com.example.lab6.controller;

import com.example.lab6.model.User;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.MessageService;
import com.example.lab6.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    FriendRequestService friendRequestService;
    private Long id;
    @FXML
    private Label welcomeText;

    @FXML
    private TextField loginField;

    @FXML
    public void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public void initialize(){

    }

    @FXML
    public void handleLogin(){
        id = Long.valueOf(loginField.getText());
        if(userService.exists(id) == null)
            MessageAlert.showErrorMessage(null, "The account do not exists");
        else
            showUserDialog(id);
    }

    public void showUserDialog(Long id){
        // create a new stage for the popup dialog.
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/userView.fxml"));

            AnchorPane root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            UserController userViewController  = loader.getController();
            userViewController.setServices(userService, friendshipService, friendRequestService,dialogStage, id);


            dialogStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
    public void showCreateAccountDialog(User user){
        try {

            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/createAccountView.fxml"));

            AnchorPane root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Message");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AccountController accountViewController  = loader.getController();
            accountViewController.setService(userService, friendRequestService, dialogStage);

            dialogStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }


    }

    @FXML
    public void handleCreateAccountDialog(ActionEvent ev){
        showCreateAccountDialog(null);

    }
    public void loginLabel(InputMethodEvent inputMethodEvent) {
    }

    public void setServices(UserService service, FriendshipService fservice, MessageService messageService, FriendRequestService friendRequestService){

        this.userService=service;
        this.friendshipService = fservice;
        this.messageService = messageService;
        this.friendRequestService = friendRequestService;
    }
}