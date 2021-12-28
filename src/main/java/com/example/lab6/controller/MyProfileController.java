package com.example.lab6.controller;

import com.example.lab6.model.FriendRequestDTO;
import com.example.lab6.model.User;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.MessageService;
import com.example.lab6.service.UserService;
import com.example.lab6.utils.events.FriendRequestChangeEvent;
import com.example.lab6.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MyProfileController implements Observer<FriendRequestChangeEvent> {

    public Label nameLabel;
    public ImageView profilePhoto;
    public ListView friendsRequestsView;
    public GridPane hamburgerMenu;
    private Boolean ByMe = false;


    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private MessageService messageService;
    private Long to;
    private Long from;


    Stage stage;
    private String email;

    ObservableList<User> modelRequests = FXCollections.observableArrayList();


    @FXML
    public void initialize() {

        profilePhoto.setImage(new Image("/images/profile.png"));
        friendsRequestsView.setVisible(false);
        hamburgerMenu.setVisible(false);
    }

    public void setModelRequests() {

        List<FriendRequestDTO> requests = friendRequestService.getFriendRequest(from);
        List<User> users = new ArrayList<>();
        requests.forEach(x -> {
            users.add(x.getUserFrom());

        });
        modelRequests.setAll(users);
    }

    public void setModelRequestsByMe() {

        List<FriendRequestDTO> requests = friendRequestService.getMyFriendsRequestes(from);
        List<User> users = new ArrayList<>();
        requests.forEach(x -> {
            users.add(x.getUserTo());

        });
        modelRequests.setAll(users);
    }

    public void setServices(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, MessageService messageService, Stage stage, String email) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.messageService = messageService;
        this.stage = stage;
        this.email = email;
        nameLabel.setText(userService.exists(email).getFirstName() + " " + userService.exists(email).getLastName());
        setProfilePicture();
        this.from = userService.exists(email).getId();
        friendRequestService.addObserver(this);

    }


    private void setProfilePicture() {
        if (userService.findPhoto(email) != null)
            profilePhoto.setImage(new Image(userService.findPhoto(email)));

    }


    public void onEditLabelClicked(MouseEvent mouseEvent) {

        FileChooser fileChooser = new FileChooser();

        String userDirectory = System.getProperty("user.home");
        File userD = new File(userDirectory);
        if (!userD.canRead())
            userD = new File("c:/");
        fileChooser.setInitialDirectory(userD);
        File path = fileChooser.showOpenDialog(stage);

        profilePhoto.setImage(new Image(path.toURI().toString()));
        if (userService.findPhoto(email) != null)
            userService.updatePictre(email, path.toURI().toString());
        else
            userService.savePictre(email, path.toURI().toString());

    }


    public void onMenuButton(ActionEvent actionEvent) {
        hamburgerMenu.setVisible(true);
    }


    public void setFriendsRequestsView() {
        friendsRequestsView.setItems(modelRequests);
        friendsRequestsView.setCellFactory(param -> new ListCell<User>() {
            private final Button acceptBtn = new Button("Accept");
            private final Button rejectBtn = new Button("Reject");
            private final Label label = new Label();

            @Override
            public void updateItem(User user, boolean empty) {
                if (empty)
                    setGraphic(null);
                else {

                    label.setText(user.getFirstName() + " " + user.getLastName());
                    label.setPrefWidth(90);
                    GridPane pane = new GridPane();

                    pane.getStyleClass().add("gridpane");
                    pane.add(label, 0, 0);
                    pane.add(acceptBtn, 1, 0);
                    pane.add(rejectBtn, 2, 0);
                    setGraphic(pane);

                    acceptBtn.setOnAction(event -> {
                        friendRequestService.acceptFriendRequest(user.getId(), from);
                    });

                    rejectBtn.setOnAction(event -> {
                        friendRequestService.rejectFriendRequest(user.getId(), from);
                    });

                }
            }
        });
    }

    public void setFriendsRequestsViewByMe() {
        friendsRequestsView.setItems(modelRequests);
        friendsRequestsView.setCellFactory(param -> new ListCell<User>() {
            private final Button deleteBtn = new Button("Delete");
            private final Label label = new Label();

            @Override
            public void updateItem(User user, boolean empty) {
                if (empty)
                    setGraphic(null);
                else {

                    label.setText(user.getFirstName() + " " + user.getLastName());
                    label.setPrefWidth(90);
                    GridPane pane = new GridPane();

                    pane.getStyleClass().add("gridpane");
                    pane.add(label, 0, 0);
                    pane.add(deleteBtn, 1, 0);

                    setGraphic(pane);

                    deleteBtn.setOnAction(event -> {
                        friendRequestService.deleteFriendRequest(from, user.getId());
                    });

                }
            }
        });
    }


    public void onMyFriendsRequests(MouseEvent mouseEvent) {
        ByMe = false;
        setModelRequests();
        setFriendsRequestsView();
        friendsRequestsView.setVisible(true);

    }

    public void onSendByMe(MouseEvent mouseEvent) {
        ByMe = true;
        setModelRequestsByMe();
        setFriendsRequestsViewByMe();
        friendsRequestsView.setVisible(true);
    }

    @Override
    public void update(FriendRequestChangeEvent friendRequestChangeEvent) {
        if (ByMe) {
            setModelRequestsByMe();
            setModelRequestsByMe();
        } else {
            setModelRequests();
            setFriendsRequestsView();
        }
    }
}
