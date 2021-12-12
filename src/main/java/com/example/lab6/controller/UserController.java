package com.example.lab6.controller;

import com.example.lab6.model.User;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class UserController {
    private UserService userService;
    private FriendshipService friendshipService;
    Stage stage;
    private Long id;
    ObservableList<User> model = FXCollections.observableArrayList();


    @FXML
    private Label userLabel;

    public void initialize(){
        IdColumnForm.setCellValueFactory(new PropertyValueFactory<User, String>("id"));
        NameColumnFrom.setCellValueFactory(new PropertyValueFactory<User, String>("name"));
        tableView.setItems(model);
        //userLabel.setText(userService.exists(id).getFirstName() + " " + userService.exists(id).getLastName());
    }

    public void setServices(UserService userService, FriendshipService friendshipService, Stage stage, Long id){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.stage = stage;
        this.id = id;
        setUserLabel(id);
        setFriendsView(id);
    }

    private void initModel() {
        Iterable<User> messages = userService.getAll();
        List<MessageTask> messageTaskList = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(messageTaskList);
    }


    public void setUserLabel(Long id){
        userLabel.setText(userService.exists(id).getFirstName() + " " + userService.exists(id).getLastName());
    }

    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User,String> IdColumnForm;
    @FXML
    TableColumn<User,String> NameColumnFrom;

    public void setFriendsView(Long id){

    }

    public void handleAddFriend(ActionEvent actionEvent) {
    }

    public void handleRemoveFriend(ActionEvent actionEvent) {
    }

    public void handleSearchButton(ActionEvent actionEvent) {
    }
}
