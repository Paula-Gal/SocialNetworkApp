package com.example.lab6.controller;

import com.example.lab6.model.FriendshipDTO;
import com.example.lab6.model.User;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController {

    public Label welcomeText;
    public ImageView xImage;
    public TextField searchField;
    public Label searchLabel;
    public ListView listView;
    public ListView friendsView;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;

    Stage stage;
    private String email;
    ObservableList<String> modelUser = FXCollections.observableArrayList();
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
    public void initialize() {

        searchField.setVisible(false);
        xImage.setVisible(false);
        listView.setVisible(false);
        friendsView.setVisible(false);
        //tableColumnFriendFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        // tableColumnFriendLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        //tableColumnFriendDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        //tableViewFriends.setItems(modelUserFriends);
        //welcomeText.setText("Welcome, " + userService.exists(email).getFirstName() + " " + userService.exists(email).getLastName() + "!");
    }

    public void setServices(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, Stage stage, String email) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.stage = stage;
        this.email = email;
        welcomeText.setText("Welcome, " + userService.exists(email).getFirstName() + " " + userService.exists(email).getLastName() + "!");

        //setUserLabel(email);
        //getFriends();
    }

    private void getFriends() {
        try {
            List<FriendshipDTO> friends = friendshipService.getFriendships(userService.exists(email).getId());
            List<FriendshipDTO> friendshipDTOS = StreamSupport.stream(friends.spliterator(), false)
                    .collect(Collectors.toList());
            modelUserFriends.setAll(friendshipDTOS);
        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, "The user doesn't exist!");
        }
    }

    public void setUserLabel(String email) {
        userLabel.setText("Welcome " + userService.exists(email).getFirstName() + " " + userService.exists(email).getLastName());
    }

    public void handleAddFriend(ActionEvent actionEvent) {
        showFriendsDialog(email);
    }

    public void showFriendsDialog(String id) {
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
            //addFriendController.setServices(userService, friendRequestService, dialogStage, id);


            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void handleRemoveFriend(ActionEvent actionEvent) {
//        int poz = tableViewFriends.getSelectionModel().getSelectedIndex();
//        List<FriendshipDTO> friendshipDTOS = friendshipService.getFriendships(id);
//        friendshipService.removeFriendship(id, friendshipDTOS.get(poz).getUser().getId());
//        if (friendshipService.getFriendships(id).size() == 0)
//            tableViewFriends.getItems().clear();
//        else
//            getFriends();
//        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Remove a friend", "The friend has been removed");
//
//    }

//    @Override
//    public void update(UserChangeEvent userChangeEvent) {
//
//    }
//
//    public void handleUpdateFriends(ActionEvent actionEvent) {
//        getFriends();
//    }
//
//    public void handleShowRequests(ActionEvent actionEvent) {
//        showFriendsRequestDialog(id);
//    }
//
//    public void showFriendsRequestDialog(Long id) {
//        // create a new stage for the popup dialog.
//        try {
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("/views/friendsRequestsView.fxml"));
//
//            AnchorPane root = loader.load();
//
//            // Create the dialog Stage.
//            Stage dialogStage = new Stage();
//            dialogStage.setTitle("Friend requests");
//            dialogStage.initModality(Modality.WINDOW_MODAL);
//            //dialogStage.initOwner(primaryStage);
//            Scene scene = new Scene(root);
//            dialogStage.setScene(scene);
//
//            FriendRequestController friendRequestController = loader.getController();
//            friendRequestController.setServices(userService, friendRequestService, dialogStage, id);
//
//            dialogStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void handleMessages(ActionEvent actionEvent) {
    }

    public void onHandleBack(ActionEvent actionEvent) {
        stage.close();
    }


    public void handleRemoveFriend(ActionEvent actionEvent) {
    }

    public void handleUpdateFriends(ActionEvent actionEvent) {
    }

    public void handleShowRequests(ActionEvent actionEvent) {
    }

    public void onSearchLabel(MouseEvent mouseEvent) {
        searchField.setVisible(true);
        xImage.setVisible(true);
    }

    public void setModelUser() {
        if (!searchField.getText().isEmpty()) {
            List<User> userList = userService.filter1(userService.exists(email).getId(), searchField.getText().toString());
            List<String> users = new ArrayList<>();
            userList.forEach(x -> {
                users.add(x.getFirstName() + " " + x.getLastName());
                modelUser.setAll(users);
            });
        } else
            listView.setVisible(false);

    }

    public void setModelUserforFriends() {
        try {
            List<FriendshipDTO> friends = friendshipService.getFriendships(userService.exists(email).getId());
            List<String> users = new ArrayList<>();
            friends.forEach(x -> {
                users.add(x.getFirstName() + " " + x.getLastName());
            });

            modelUser.setAll(users);
        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, "The user doesn't exist!");
        }

    }

    public void onSearchField(KeyEvent keyEvent) {
        listView.setVisible(true);
        setModelUser();
        listView.setItems(null);
        //listView.setPrefHeight(26*modelUser.size());
        listView.setItems(modelUser);

    }

    public void onXClicked(MouseEvent mouseEvent) {
        searchField.setVisible(false);
        xImage.setVisible(false);
        listView.setVisible(false);
    }

    public void onFriendsClicked(MouseEvent mouseEvent) {
        setModelUserforFriends();
        friendsView.setItems(modelUser);
        friendsView.setPrefHeight(26 * modelUser.size());
        friendsView.setVisible(true);

    }
}
