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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.EventListener;
import java.util.ResourceBundle;

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
    private String email;

    @FXML
    private TextField loginField;

    public void initialize() {
        logo.setImage(new Image("/images/icons/social.png"));
        emailIcon.setImage(new Image("/images/user.png"));
        passwordIcon.setImage(new Image("/images/padlock.png"));
        passwordShowTextField.setVisible(false);
    }

    public void showUserDialog(String id) {
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

            UserController userViewController = loader.getController();
            userViewController.setServices(userService, friendshipService, friendRequestService, messageService, dialogStage, id);

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
            dialogStage.setTitle("Edit Message");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
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

    public void onHandleUsers() {
        showAllUsers();
    }

    private void showAllUsers() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/allUsersView.fxml"));

            AnchorPane root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("All users");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AllUsersController allUsersController = loader.getController();
            allUsersController.setService(userService, dialogStage);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void viewpass() {
        if(hidePasswordCheckbox.isSelected()) {
            passwordTextField.setVisible(false);
            passwordShowTextField.setVisible(true);
            passwordShowTextField.setText(passwordTextField.getText());
        }
        else {
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
            stage.close();
        }
    }
}