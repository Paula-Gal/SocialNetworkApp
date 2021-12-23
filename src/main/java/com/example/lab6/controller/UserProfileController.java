package com.example.lab6.controller;

import com.example.lab6.model.FriendshipDTO;
import com.example.lab6.model.Message;
import com.example.lab6.model.User;
import com.example.lab6.model.UserDTO;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.MessageService;
import com.example.lab6.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class UserProfileController {

    public Label nameLabel;
    public ImageView profilePhoto;
    public ListView<UserDTO> chatView;
    public Label messagesLabel;
    public AnchorPane conversation;
    public Label conversationLabel;
    public ListView conversationList;
    public TextField writeMessageField;

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private MessageService messageService;
    private Long to;



    Stage stage;
    private String email;
    ObservableList<String> modelUser = FXCollections.observableArrayList();
    ObservableList<UserDTO> model = FXCollections.observableArrayList();
    ObservableList<String> modelMessage = FXCollections.observableArrayList();
    ObservableList<FriendshipDTO> modelUserFriends = FXCollections.observableArrayList();


    @FXML
    public void initialize() {

        profilePhoto.setImage(new Image("/images/profile.png"));
        messagesLabel.setText("Messages");
        chatView.setItems(model);
        conversation.setVisible(false);
    }

    public void setServices(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService,MessageService messageService, Stage stage, String email) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.messageService = messageService;
        this.stage = stage;
        this.email = email;
        nameLabel.setText(userService.exists(email).getFirstName() + " " + userService.exists(email).getLastName());
        setChatView();

        setProfilePicture();

    }

    private void setChatView() {
        setModelUserforFriends();
        chatView.setItems(model);
        chatView.setCellFactory(param -> new ListCell<UserDTO>() {
            private final ImageView imageView = new ImageView();
            @Override
            public void updateItem(UserDTO userDTO, boolean empty) {
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setImage(new Image(userDTO.getUrlPhoto()));
                    imageView.setFitHeight(40);
                    imageView.setFitWidth(40);
                    setGraphic(imageView);
                    setText("   " + userDTO.getNume());
                }
            }
        });

    }

    private void setProfilePicture() {
        if(userService.findPhoto(email) != null)
            profilePhoto.setImage(new Image(userService.findPhoto(email)));

    }

    public void setModelUserforFriends() {
        try {
            List<FriendshipDTO> friends = friendshipService.getFriendships(userService.exists(email).getId());
            List<UserDTO> users = new ArrayList<>();
            friends.forEach(x -> {
                if(userService.findPhoto(x.getUser().getEmail()) == null) {
                    UserDTO userDto = new UserDTO(x.getUser());
                    userDto.setUrlPhoto("/images/profile.png");
                    userDto.setEmailDTO(x.getUser().getEmail());
                    users.add(userDto);
                }
                else
                {
                    UserDTO userDto = new UserDTO(x.getUser());
                    userDto.setUrlPhoto(userService.findPhoto(x.getUser().getEmail()));
                    userDto.setEmailDTO(x.getUser().getEmail());
                    users.add(userDto);
                }

            });
            model.setAll(users);

        } catch (Exception ex) {
            MessageAlert.showErrorMessage(null, "The user doesn't exist!");
        }

    }

    public void onEditLabelClicked(MouseEvent mouseEvent) {

        FileChooser fileChooser = new FileChooser();

        String userDirectory = System.getProperty("user.home");
        File userD = new File(userDirectory);
        if(!userD.canRead())
            userD = new File("c:/");
        fileChooser.setInitialDirectory(userD);
        File path = fileChooser.showOpenDialog(stage);

        profilePhoto.setImage(new Image(path.toURI().toString()));
        if(userService.findPhoto(email) != null)
            userService.updatePictre(email, path.toURI().toString());
        else
            userService.savePictre(email, path.toURI().toString());

    }

    @FXML
    public void onChatClicked(MouseEvent mouseEvent) {


            int poz = chatView.getSelectionModel().getSelectedIndex() + 1;
            List<FriendshipDTO> friends = friendshipService.getFriendships(userService.exists(email).getId());
            User user = friends.get(poz).getUser();
            conversationLabel.setText("Your conversation with " + user.getFirstName() + " " + user.getLastName());
            conversation.setVisible(true);
            setConversation(user);
            conversationList.setItems(modelMessage);
            this.to = user.getId();

    }

    public void setConversation(User user){
            List<Message> messages = messageService.getConversation(userService.exists(email).getId(), user.getId());
            List<String> mess = new ArrayList<>();
            messages.forEach(x -> {
                mess.add(x.getMessage());
            });
            modelMessage.setAll(mess);

    }

    public void onSendMessage(ActionEvent actionEvent) {
        String message = writeMessageField.getText();
        List<Long> tos =  new ArrayList<>();
        tos.add(to);
        messageService.sendMessage(userService.exists(email).getId(), tos, message);
    }
}
