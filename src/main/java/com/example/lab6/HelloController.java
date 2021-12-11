package com.example.lab6;

import com.example.lab6.controller.AccountController;
import com.example.lab6.model.User;
import com.example.lab6.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    UserService userService;
    private Long id;
    @FXML
    private Label welcomeText;

    @FXML
    private Label loginLabel;

    @FXML
    public void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public void initialize(){

    }

    @FXML
    public void onLoginButtonClick(){
        id = Long.valueOf(loginLabel.getText());
    }

    public void onCreateAccountButton(User user){
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
            accountViewController.setService(userService, dialogStage);

            dialogStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }


    }

    @FXML
    public void handleCreateAccountDialog(ActionEvent ev){
        onCreateAccountButton(null);

    }
    public void loginLabel(InputMethodEvent inputMethodEvent) {
    }

}