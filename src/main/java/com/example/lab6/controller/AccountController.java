package com.example.lab6.controller;

import com.example.lab6.model.User;
import com.example.lab6.model.validators.ValidationException;
import com.example.lab6.service.UserService;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccountController {

    private UserService userService;
    Stage stage;

    public void setService(UserService service, Stage stage){

        this.userService = service;
        this.stage = stage;
    }

    public void initialize(){

    }

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    public void handleCreate(){
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        User user = new User(firstName, lastName);
        saveUser(user);
    }

    private void saveUser(User user){
        try{
            User user1 = this.userService.add(user);
            if(user1 == null)
                stage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Creating account","The account have been created");
            stage.close();
        }
        catch (ValidationException e){
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
    }

}
