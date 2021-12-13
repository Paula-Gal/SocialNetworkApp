package com.example.lab6.controller;

import com.example.lab6.model.FriendshipDTO;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.UserService;
import com.example.lab6.utils.events.UserChangeEvent;
import com.example.lab6.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<UserChangeEvent> {

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;

    Stage stage;
    private Long id;

    ObservableList<FriendshipDTO> modelUserFriends = FXCollections.observableArrayList();

    @FXML
    private Label userLabel;

    //user friends
    @FXML
    Button buttonShowFriends;
    @FXML
    TableView<FriendshipDTO> tableViewFriends;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnFriendFirstName;
    @FXML
    TableColumn<FriendshipDTO, String> tableColumnFriendLastName;
    @FXML
    TableColumn<FriendshipDTO, LocalDateTime> tableColumnFriendDate;

    @FXML
    public void initialize(){
        tableColumnFriendFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnFriendLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnFriendDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableViewFriends.setItems(modelUserFriends);
    }

    public void setServices(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, Stage stage, Long id){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.stage = stage;
        this.id = id;
        setUserLabel(id);
        getFriends();
    }

    private void getFriends() {
        try {
            List<FriendshipDTO> friends = friendshipService.getFriendships(id);
            List<FriendshipDTO> friendshipDTOS = StreamSupport.stream(friends.spliterator(), false)
                    .collect(Collectors.toList());
            modelUserFriends.setAll(friendshipDTOS);
        }
        catch (Exception ex) {
            MessageAlert.showErrorMessage(null, "The user doesn't exist!");
        }
    }

    public void setUserLabel(Long id){
        userLabel.setText("Welcome " + userService.exists(id).getFirstName() + " " + userService.exists(id).getLastName());
    }

    public void handleAddFriend(ActionEvent actionEvent) {
        showFriendsDialog(id);
    }

    public void showFriendsDialog(Long id) {
        // create a new stage for the popup dialog.
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/addFriendView.fxml"));

            AnchorPane root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add a friend");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AddFriendController addFriendController = loader.getController();
            addFriendController.setServices(userService, friendRequestService, dialogStage, id);


            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleRemoveFriend(ActionEvent actionEvent) {
        int poz = tableViewFriends.getSelectionModel().getSelectedIndex();
        List<FriendshipDTO> friendshipDTOS = friendshipService.getFriendships(id);
        friendshipService.removeFriendship(id, friendshipDTOS.get(poz).getUser().getId());
        if(friendshipService.getFriendships(id).size() == 0)
            tableViewFriends.getItems().clear();
        else
             getFriends();
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Remove a friend", "The friend has been removed");

    }

    @Override
    public void update(UserChangeEvent userChangeEvent) {

    }

    public void handleShowFriends(ActionEvent actionEvent) {
        }

    public void handleShowRequests(ActionEvent actionEvent) {
        showFriendsRequestDialog(id);
    }

    public void showFriendsRequestDialog(Long id){
        // create a new stage for the popup dialog.
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/friendsRequestsView.fxml"));

            AnchorPane root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friend requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            FriendRequestController friendRequestController = loader.getController();
            friendRequestController.setServices(userService, friendRequestService, dialogStage, id);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleMessages(ActionEvent actionEvent) {
    }
}
