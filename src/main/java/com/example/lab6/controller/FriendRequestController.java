package com.example.lab6.controller;

import com.example.lab6.model.*;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestController {
    private UserService userService;
    FriendRequestService friendRequestService;
    Stage stage;
    private Long id;
    ObservableList<FriendRequestDTO> modelRequest = FXCollections.observableArrayList();

    public void setServices(UserService service, FriendRequestService friendRequestService, Stage stage, Long id){

        this.userService = service;
        this.friendRequestService = friendRequestService;
        this.stage = stage;
        this.id = id;
        initModel();
    }


    @FXML
    TableView<FriendRequestDTO> tableRequestView;
    @FXML
    TableColumn<FriendRequestDTO, String> fromTableColumn;
    @FXML
    TableColumn<FriendRequestDTO, String> toTableColumn;
    @FXML
    TableColumn<FriendRequestDTO, Status> statusTableColumn;
    @FXML
    TableColumn<FriendRequestDTO, LocalDateTime> dateColumnTable;


    @FXML
    public void initialize(){
        fromTableColumn.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("from"));
        toTableColumn.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, String>("to"));
        statusTableColumn.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, Status>("status"));
        dateColumnTable.setCellValueFactory(new PropertyValueFactory<FriendRequestDTO, LocalDateTime>("date"));
        tableRequestView.setItems(modelRequest);
    }

    private void initModel() {
        try {
            List<FriendRequestDTO> request = friendRequestService.getFriendRequest(id);
            List<FriendRequestDTO> requestDTOS = StreamSupport.stream(request.spliterator(), false)
                    .collect(Collectors.toList());
            modelRequest.setAll(requestDTOS);
        }
        catch (Exception ex) {
            MessageAlert.showErrorMessage(null, "!!");
        }
    }
    public void handleAcceptButton(ActionEvent actionEvent) {
        int poz = tableRequestView.getSelectionModel().getSelectedIndex();
        List<FriendRequestDTO> friendRequestDTOS= friendRequestService.getFriendRequest(id);
        if(friendRequestDTOS.get(poz).getIdF().equals(id))
        MessageAlert.showErrorMessage(null, "The only operation allowed is 'Delete'");
        else {
            friendRequestService.acceptFriendRequest(friendRequestDTOS.get(poz).getIdF(), id);
            initModel();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Accept a friend request", "The request has been accepted");
        }
    }

    public void handleDeleteButton(ActionEvent actionEvent) {
    }

    public void handleRejectButton(ActionEvent actionEvent) {
    }

    public void handleBackButton(ActionEvent actionEvent) {
    }
}
