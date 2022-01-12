package com.example.lab6.controller;

import com.example.lab6.service.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SplashScreenController extends Thread {
    public ImageView logoImage;

    Stage stage;

    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    FriendRequestService friendRequestService;
    EventService eventService;
    PostService postService;

    public void initialize() {
        logoImage.setVisible(true);
        logoImage.setImage(new Image("/images/bsocial.png"));
    }


    public void setServices(UserService userService, FriendshipService friendshipService, MessageService messageService, FriendRequestService friendRequestService, EventService eventService, PostService postService, Stage stage) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.friendRequestService = friendRequestService;
        this.stage = stage;
        this.eventService = eventService;
        this.postService = postService;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
            stage.close();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/login-view.fxml"));

            AnchorPane root = null;
            try {
                root = loader.load();
                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setMaximized(true);
                dialogStage.setTitle("Login");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                //dialogStage.initOwner(primaryStage);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                LoginController loginController = loader.getController();
                loginController.setServices(userService, friendshipService, messageService, friendRequestService, eventService, postService, dialogStage);

                dialogStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
