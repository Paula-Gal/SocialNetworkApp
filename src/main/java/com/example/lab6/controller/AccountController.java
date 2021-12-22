package com.example.lab6.controller;

import com.example.lab6.model.User;
import com.example.lab6.model.validators.ValidationException;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.UserService;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class AccountController {

    public TextField emailTextField;
    public TextField passwordTextField;
    private UserService userService;
    FriendRequestService friendRequestService;
    Stage stage;

    public void setService(UserService service, FriendRequestService friendRequestService, Stage stage) {

        this.userService = service;
        this.friendRequestService = friendRequestService;
        this.stage = stage;
    }

    public void initialize() {
    }

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    public static String getSecurePassword(String password) {

        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            //  md.update(salt);
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    @FXML
    public void handleCreate() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailTextField.getText();
        String password = passwordTextField.getText();
        String hashedPass = getSecurePassword(password);
        User user = new User(firstName, lastName, email, hashedPass);

        saveUser(user);
    }

    private void saveUser(User user) {
        try {
            User user1 = this.userService.add(user);
            if (user1 == null)
                stage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Creating account", "The account have been created");
            stage.close();
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

}
