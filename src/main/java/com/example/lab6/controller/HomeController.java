package com.example.lab6.controller;

import com.example.lab6.model.*;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.MessageService;
import com.example.lab6.service.UserService;
import com.example.lab6.utils.events.MessageChangeEvent;
import com.example.lab6.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserController implements Observer<MessageChangeEvent> {

    public Label welcomeText;
    public ImageView xImage;
    public TextField searchField;
    public Label searchLabel;
    public ListView listView;

    public ListView<UserDTO> chatView;
    public Label conversationLabel;
    public ListView conversationList;
    public TextField writeMessageField;

    public AnchorPane conversation;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private MessageService messageService;



    Stage stage;
    private Long to;
    private String email;
    private String emailTo;
    ObservableList<String> modelUser = FXCollections.observableArrayList();
    ObservableList<UserDTO> model = FXCollections.observableArrayList();
    ObservableList<FriendshipDTO> modelUserFriends = FXCollections.observableArrayList();
    ObservableList<String> modelMessage = FXCollections.observableArrayList();
    ObservableList<Message> modelMessages = FXCollections.observableArrayList();

    @FXML
    private Label userLabel;


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

        chatView.setItems(model);
        conversation.setVisible(false);

       }

    public void setServices(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService,MessageService messageService,  Stage stage, String email) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.stage = stage;
        this.email = email;
        this.messageService = messageService;
        welcomeText.setText("Welcome, " + userService.exists(email).getFirstName() + " " + userService.exists(email).getLastName() + "!");
        setFriendsList();
        messageService.addObserver(this);

    }



    public void onHandleBack(ActionEvent actionEvent) {
        stage.close();
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


    public void onSearchField(KeyEvent keyEvent) {
        listView.setVisible(true);
        setModelUser();
        listView.setItems(null);
        //listView.setPrefHeight(26*modelUser.size());
        listView.setItems(modelUser);

    }

    private void setFriendsList() {
        setModel();
        chatView.setItems(model);
        chatView.setCellFactory(param -> new ListCell<UserDTO>() {
            private final ImageView imageView = new ImageView();
            private final Button button = new Button();
            protected final Label label = new Label();

            @Override
            public void updateItem(UserDTO userDTO, boolean empty) {
                if (empty) {
                    setText(null);
                    setGraphic(null);
                    setLineSpacing(0);
                    setGraphic(null);
                } else {
                    imageView.setImage(new Image(userDTO.getUrlPhoto()));
                    imageView.setFitHeight(40);
                    imageView.setFitWidth(40);
                    ImageView img = new ImageView(new Image("/images/seeProfile.png"));
                    img.setFitWidth(20);
                    img.setFitHeight(20);
                    button.setGraphic(img);
                    button.setStyle("-fx-background-color: transparent");
                    label.setText("  " + userDTO.getNume());
                    label.setPrefWidth(90);
                    GridPane pane = new GridPane();
                    pane.getStyleClass().add("gridpane");
                    pane.add(imageView, 0, 0);
                    pane.add(label, 1, 0);
                    pane.add(button, 3, 0);
                    setGraphic(pane);
                    label.setOnMouseClicked(event -> {
                        conversationLabel.setText("Your conversation with " + userDTO.getNume());
                        conversation.setVisible(true);
                        to = userDTO.getIdUser();
                        setConversation(to);
                        // emailTo = user.getEmail();
                        setConversationList();
                    });
                    button.setOnAction(event ->{
                        try {
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(getClass().getResource("/views/friendProfile.fxml"));
                            AnchorPane root = loader.load();

                            // Create the dialog Stage.
                            Stage dialogStage = new Stage();
                            dialogStage.setTitle("My profile");
                            dialogStage.initModality(Modality.WINDOW_MODAL);
                            //dialogStage.initOwner(primaryStage);
                            Scene scene = new Scene(root);
                            dialogStage.setScene(scene);

                            FriendProfileController friendProfileController = loader.getController();
                            friendProfileController.setServices(userService, friendshipService, friendRequestService, dialogStage, email,userDTO.getEmailDTO());

                            dialogStage.show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                }
            }
        });



        chatView.setPrefHeight(50*model.size());

    }

    @FXML
    public void onFriendClicked(MouseEvent mouseEvent) {

            /*int poz = chatView.getSelectionModel().getSelectedIndex();
            List<FriendshipDTO> friends = friendshipService.getFriendships(userService.exists(email).getId());
            User user = friends.get(poz).getUser();
            conversationLabel.setText("Your conversation with " + user.getFirstName() + " " + user.getLastName());
            conversation.setVisible(true);
            to =user.getId();
            setConversation(to);
            // emailTo = user.getEmail();
            setConversationList();
*/

    }

    public void setConversationList(){
        conversationList.setItems(modelMessages);
        conversationList.setCellFactory(
                param -> new ListCell<Message>() {
                    protected final Label label = new Label();

                    @Override
                    public void updateItem(Message message, boolean empty) {
                        if (empty) {
                            setGraphic(null);

                        } else {
                         setText(message.getMessage());
                            if (message.getFrom().getId().equals(userService.exists(email).getId())) {
                               // label.setTextAlignment(TextAlignment.RIGHT);
                               getStyleClass().add("background-mymessage");

                                setAlignment(Pos.CENTER_RIGHT);
                                //setText(message.getMessage());

                            } else {
                                getStyleClass().add("background-message");
                            }
                           setGraphic(label);
                        }
                    }


                });
    }

    public void setConversation(Long to){
        List<Message> messages = messageService.getConversation(userService.exists(email).getId(), to);
       /* List<String> mess = new ArrayList<>();
        messages.forEach(x -> {
            mess.add(x.getMessage());
        });

        */

        modelMessages.setAll(messages);

    }

    public void onSendMessage(ActionEvent actionEvent) {
        String message = writeMessageField.getText();
        List<Long> tos =  new ArrayList<>();
        tos.add(to);
        messageService.sendMessage(userService.exists(email).getId(), tos, message);
    }
    public void setModel() {
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

    public void onXClicked(MouseEvent mouseEvent) {
        searchField.setVisible(false);
        xImage.setVisible(false);
        listView.setVisible(false);
    }



    public void myProfileClicked(MouseEvent mouseEvent) {
        // create a new stage for the popup dialog.
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/userProfile.fxml"));

            AnchorPane root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("My profile");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            UserProfileController userProfileController = loader.getController();
            userProfileController.setServices(userService, friendshipService, friendRequestService, messageService, dialogStage, email);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSearchedClicked(MouseEvent mouseEvent) {
        int poz = listView.getSelectionModel().getSelectedIndex();
        List<User> userList = userService.filter1(userService.exists(email).getId(), searchField.getText().toString());

        User user = userList.get(poz);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/friendProfile.fxml"));
            AnchorPane root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("My profile");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            FriendProfileController friendProfileController = loader.getController();
            friendProfileController.setServices(userService, friendshipService, friendRequestService, dialogStage, email,user.getEmail());

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        setConversation(to);
        setConversationList();
    }
}
