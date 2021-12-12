package com.example.lab6.controller;

import com.example.lab6.model.User;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


public class AddFriendController {

    private UserService userService;
    private FriendshipService friendshipService;

    Stage stage;
    private Long id;

    ObservableList<User> modelUsers = FXCollections.observableArrayList();

    @FXML
    private Label userLabel;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;
    @FXML
    TableView<User> tableViewUsers;
    @FXML
    TableColumn<User, String> tableColumnFirstName;
    @FXML
    TableColumn<User, String> tableColumnLastName;
    @FXML
    TableColumn<User, String> tableColumnAction;

    private final Button addButton = new Button("Add");

    public void addFriend() {

    }

    @FXML
    public void initialize(){
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableViewUsers.setItems(modelUsers);
    }



    public void setServices(UserService userService, FriendshipService friendshipService, Stage stage, Long id){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.stage = stage;
        this.id = id;
    }

    public void onHandleSearch(ActionEvent actionEvent) {

    }

}
