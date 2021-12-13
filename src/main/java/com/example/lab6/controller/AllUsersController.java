package com.example.lab6.controller;

import com.example.lab6.model.Status;
import com.example.lab6.model.User;
import com.example.lab6.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class AllUsersController {

    private UserService userService;
    Stage stage;
    ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, Long> idColumn;
    @FXML
    TableColumn<User, String> firstName;
    @FXML
    TableColumn<User, Status> lastName;

    @FXML
    public void initialize(){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableView.setItems(model);
    }

    private void initModel() {
        try {
            List<User> users = userService.getUsers();
            model.setAll(users);
        }
        catch (Exception ex) {
            MessageAlert.showErrorMessage(null, "!!");
        }
    }

    public void setService(UserService service,Stage stage){
        this.userService = service;
        this.stage = stage;
        initModel();
    }

    public void onHandleBack(ActionEvent actionEvent) {
        stage.close();
    }
}
