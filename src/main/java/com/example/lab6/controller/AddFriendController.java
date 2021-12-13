package com.example.lab6.controller;

import com.example.lab6.model.User;
import com.example.lab6.model.validators.ValidationException;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.UserService;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
    private FriendRequestService friendRequestService;

    Stage stage;
    private Long id;

    ObservableList<User> modelUsers = FXCollections.observableArrayList();

    @FXML
    private Label userLabel;

    @FXML
    private TextField searchField;


    @FXML
    TableView<User> tableViewUsers;
    @FXML
    TableColumn<User, String> tableColumnFirstName;
    @FXML
    TableColumn<User, String> tableColumnLastName;
    @FXML
    TableColumn<User, User> tableColumnAction = new TableColumn<>("Action");

    public void addFriend() {

    }

    @FXML
    public void initialize() {
    }
    public void setTableFriendsView(){
        tableViewUsers.getItems().clear();
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnAction.setMaxWidth(120);
        tableColumnAction.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        tableColumnAction.setCellFactory(param -> new TableCell<User, User>() {
            private final Button sendRequest = new Button("send request");

            @Override
            protected void updateItem(User person, boolean empty) {
                super.updateItem(person, empty);

                if (person == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(sendRequest);
                sendRequest.setOnAction(
                        event -> {
                            try {
                                friendRequestService.sendFriendRequest(id, person.getId());
                                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Send request", "The request has been sent");
                            } catch (ValidationException ex) {
                                MessageAlert.showErrorMessage(null, ex.getMessage());
                            }
                        }
                );
            }
        });
        tableViewUsers.setItems(modelUsers);
        if(tableViewUsers.getColumns().size() != 3)
            tableViewUsers.getColumns().add(tableColumnAction);
        tableViewUsers.setItems(modelUsers);
    }



    public void setServices(UserService userService, FriendRequestService friendRequestService, Stage stage, Long id){
        this.userService = userService;
        this.friendRequestService = friendRequestService;
        this.stage = stage;
        this.id = id;
    }

    public void onHandleSearch(ActionEvent actionEvent) {
        if(searchField.getText().isEmpty())
            MessageAlert.showErrorMessage(null,"Type first");
        else {
            setTable(searchField.getText());
        }
    }

    public void setTable(String str){
        setTableFriendsView();
        modelUsers.setAll(userService.filter1(id, str));
    }

    public void onSearchFieldAction(ActionEvent actionEvent) {
        if(searchField.getText().isEmpty())
            tableViewUsers.getItems().removeAll();
    }

    public void onHandleBack(ActionEvent actionEvent) {
        stage.close();
    }
}
