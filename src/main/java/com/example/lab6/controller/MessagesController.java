package com.example.lab6.controller;

import com.example.lab6.model.*;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.MessageService;
import com.example.lab6.service.UserService;
import com.example.lab6.utils.events.MessageChangeEvent;
import com.example.lab6.utils.observer.Observer;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Math.ceil;


public class MessagesController implements Observer<MessageChangeEvent> {

    public TextField searchField;
    public ImageView userImage;
    public Label userName;
    public Button createGroupButton;
    public TextField conversationField;

    public ImageView imageSend;
    public AnchorPane anchorPagination;
    public Button openGroupsButton;
    public Label addMembersToGroupLabel;
    public ImageView plusGroupImage;
    public TextField nameGroupField;
    public ScrollPane scrollerMembers;
    public VBox members;
    public ImageView menuImage;

    private int leftLimit = 0;

    private AtomicBoolean friendsBool = new AtomicBoolean();

    public ScrollPane scroller;
    public VBox chat;
    private MessageService messageService;
    private FriendshipService friendshipService;
    private UserService userService;
    private FriendRequestService friendRequestService;
    Stage stage;
    private String email;
    private Long myId;
    private Long friendId;
    private Group groupFinal = new Group();
    private int numberOfMessages;

    private List<Long> newMembersForGroup = new ArrayList<>();

    private List<FriendshipDTO> model = new ArrayList<>();


    public Pagination pagination;

    public void initialize() {
        searchField.setPromptText("Search a friend here...");
        createGroupButton.setVisible(false);

        userName.setVisible(false);
        friendsBool.set(true);
        nameGroupField.setVisible(false);
        addMembersToGroupLabel.setVisible(false);
        plusGroupImage.setVisible(false);
        menuImage.setVisible(false);
        scrollerMembers.setVisible(false);

    }

    public void setServices(MessageService messageService, FriendshipService friendshipService, FriendRequestService friendRequestService, UserService userService, Stage dialogStage, String email) {
        this.messageService = messageService;
        this.stage = dialogStage;
        this.email = email;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.userService = userService;
        messageService.addObserver(this);
        friendsChat();
        this.myId = this.userService.exists(email).getId();
        friendsBool.set(true);
    }

    public int itemsPerPage() {
        return 2;
    }

    public int messagesPerPage() {
        return 5;
    }

    @Override
    public void update(MessageChangeEvent messageChangeEvent) {

        if (friendsBool.get()) {
            setConversation1(friendId);
        } else {
            setConversationGroup1(groupFinal);

        }
    }

    public void friendsChat() {

        pagination.getStyleClass().add("/style/pagination");
        pagination.getStyleClass().add("/style/pagination-control");
        pagination.getStyleClass().add("/style/bullet-button");
        pagination.getStyleClass().add("/style/toggle-button");
        pagination.getStyleClass().add("/style/button");
        pagination.getStyleClass().add("/style/control-box");
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                return createPageForFriendsChat(pageIndex);
            }
        });

        int nr_friends = messageService.getMyFriendsWithMessages(userService.exists(email).getId()).size();


        if (nr_friends == 0)
            pagination.setPageCount(1);
        else {
            double nr = (double) (nr_friends) / (double) itemsPerPage();
            pagination.setPageCount((int) ceil(nr));
        }


    }

    public VBox createPageForFriendsChat(int pageIndex) {
        VBox box = new VBox();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double t = bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) * 0.3;
        double y = bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) * 0.7;

        box.setLayoutX(t);
        box.setLayoutY(y);
        box.setAlignment(Pos.CENTER);
        int page = pageIndex * itemsPerPage();

        List<FriendshipDTO> friendshipDTOS = new ArrayList<>();
        friendshipDTOS.addAll(messageService.getMyConversationPage((pageIndex) * itemsPerPage(), itemsPerPage(), userService.exists(email).getId()));
        List<UserDTO> users = new ArrayList<>();
        friendshipDTOS.forEach(x -> {
            if (userService.findPhoto(x.getUser().getEmail()) == null) {
                UserDTO userDto = new UserDTO(x.getUser());
                userDto.setUrlPhoto("/images/profile.png");
                userDto.setEmailDTO(x.getUser().getEmail());
                users.add(userDto);
            } else {
                UserDTO userDto = new UserDTO(x.getUser());
                userDto.setUrlPhoto(userService.findPhoto(x.getUser().getEmail()));
                userDto.setEmailDTO(x.getUser().getEmail());
                users.add(userDto);
            }

        });

        int nr = 0;
        for (int i = page; i < page + friendshipDTOS.size(); i++) {
            VBox row = new VBox();
            int index = nr;
            ImageView imageView = new ImageView();
            Button button = new Button();
            Label label = new Label();

            imageView.setImage(new Image(users.get(index).getUrlPhoto()));
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);
            ImageView img = new ImageView(new Image("/images/seeProfile.png"));
            img.setFitWidth(20);
            img.setFitHeight(20);

            button.setGraphic(img);
            button.setStyle("-fx-background-color: transparent");
            label.setText("  " + users.get(index).getNume());
            label.setPrefWidth(160);

            GridPane pane = new GridPane();
            pane.getStyleClass().add("gridpane");
            pane.add(imageView, 0, 0);
            pane.add(label, 1, 0);
            pane.add(button, 3, 0);

            box.getChildren().add(pane);
            label.setOnMouseClicked(event -> {
                userImage.setVisible(true);
                if (users.get(index).getUrlPhoto() != null)
                    userImage.setImage(new Image(users.get(index).getUrlPhoto()));
                else
                    userImage.setImage(new Image("/images/profile.png"));
                userName.setVisible(true);
                userName.setText("Your conversation with " + users.get(index).getNume());


                friendId = users.get(index).getIdUser();
                numberOfMessages = messageService.getConversation(myId, friendId).size();
                setConversation1(friendId);
                leftLimit = 0;
                // emailTo = user.getEmail();
                //setConversationList();
            });
            button.setOnAction(event -> {
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
                    friendProfileController.setServices(userService, friendshipService, friendRequestService, dialogStage, email, users.get(index).getEmailDTO());

                    dialogStage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

            nr++;
        }
        return box;
    }

//    private void setConversation(Long friendId) {
//
//        chat.getChildren().clear();
//        List<Message> messages = messageService.getConversation(myId, friendId);
//
//            messages.forEach(x -> {
//                HBox row = new HBox();
//                ImageView profilePhoto = new ImageView();
//                Label text = new Label();
//                if (userService.findPhoto(x.getFrom().getEmail()) != null)
//                    profilePhoto.setImage(new Image(userService.findPhoto(x.getFrom().getEmail())));
//                else
//                    profilePhoto.setImage(new Image("/images/profile.png"));
//
//                profilePhoto.setFitWidth(50);
//                profilePhoto.setFitHeight(50);
//                text.setText(x.getMessage());
//                if (x.getFrom().getId().equals(myId)) {
//                    text.getStyleClass().add("background-mymessages");
//                    text.setAlignment(Pos.CENTER_RIGHT);
//
//                    row.getChildren().add(text);
//                    row.getChildren().add(profilePhoto);
//                    row.setAlignment(Pos.CENTER_RIGHT);
//                    row.setPrefHeight(100);
//
//                } else {
//                    text.getStyleClass().add("background-messages");
//                    row.getChildren().add(profilePhoto);
//                    row.getChildren().add(text);
//                    row.setAlignment(Pos.CENTER_LEFT);
//                    row.setPrefHeight(100);
//
//                }
//
//                chat.getChildren().add(row);
//
//            });
//            chat.setSpacing(5);
//
//      scroller.setContent(chat);
//      scroller.setFitToWidth(chat.isFillWidth());
//      scroller.setVvalue(1.0);
//      scroller.setHvalue(1.0);
//      scroller.setPannable(true);
//    }

    private void setConversation1(Long friendId) {

        chat.getChildren().clear();
        numberOfMessages = messageService.getConversation(myId, friendId).size();

        int nr = leftLimit + messagesPerPage();
        if (nr > numberOfMessages)
            nr = numberOfMessages;

        chat.setFillWidth(true);
        List<Message> messages = messageService.getMyMessagesOnPage(leftLimit, nr - leftLimit, myId, friendId);
        Collections.reverse(messages);
        messages.forEach(x -> {
            HBox row = new HBox();
            ImageView profilePhoto = new ImageView();
            Label text = new Label();
            if (userService.findPhoto(x.getFrom().getEmail()) != null)
                profilePhoto.setImage(new Image(userService.findPhoto(x.getFrom().getEmail())));
            else
                profilePhoto.setImage(new Image("/images/profile.png"));

            profilePhoto.setFitWidth(45);
            profilePhoto.setFitHeight(45);
            text.setText(x.getMessage());
            if (x.getFrom().getId().equals(myId)) {
                text.getStyleClass().add("background-mymessages");
                text.setAlignment(Pos.CENTER_RIGHT);

                row.getChildren().add(text);
                row.getChildren().add(profilePhoto);
                row.setAlignment(Pos.CENTER_RIGHT);
                row.setPrefHeight(100);

            } else {
                text.getStyleClass().add("background-messages");
                row.getChildren().add(profilePhoto);
                row.getChildren().add(text);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPrefHeight(100);

            }

            chat.getChildren().add(row);

        });

        chat.setSpacing(5);
        scroller.setContent(chat);
        scroller.setFitToWidth(chat.isFillWidth());
        scroller.setVvalue(0.5);
        scroller.setHvalue(0.5);

    }


    public void groupsChat() {
        pagination.getStyleClass().add("/style/pagination");
        pagination.getStyleClass().add("/style/pagination-control");
        pagination.getStyleClass().add("/style/bullet-button");
        pagination.getStyleClass().add("/style/toggle-button");
        pagination.getStyleClass().add("/style/button");
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                return createPageForGroupsChat(pageIndex);
            }
        });


        int nr_groups = messageService.myGroups(myId).size();
        if (nr_groups == 0)
            pagination.setPageCount(1);
        else {
            double nr = (double) (nr_groups) / (double) itemsPerPage();

            pagination.setPageCount((int) (nr));
        }
    }

    public VBox createPageForGroupsChat(int pageIndex) {
        VBox box = new VBox();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double t = bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) * 0.3;
        double y = bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) * 0.7;

        box.setLayoutX(t);
        box.setLayoutY(y);

        box.setAlignment(Pos.CENTER);
        int page = pageIndex * itemsPerPage();


        List<Group> groups = new ArrayList<>();
        groups.addAll(messageService.getGroupsOnPage((pageIndex) * itemsPerPage(), itemsPerPage(), userService.exists(email).getId()));


        int nr = 0;
        for (int i = page; i < page + groups.size(); i++) {
            VBox row = new VBox();
            int index = nr;
            ImageView imageView = new ImageView();
            Button button = new Button();
            Label label = new Label();
            imageView.setImage(new Image("/images/people.png"));
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);

            label.setText("  " + groups.get(index).getName());
            label.setPrefWidth(160);
            GridPane pane = new GridPane();
            pane.getStyleClass().add("gridpane");
            pane.add(imageView, 0, 0);
            pane.add(label, 1, 0);
            box.getChildren().add(pane);
            label.setOnMouseClicked(event -> {
                userImage.setVisible(true);
                userImage.setImage(new Image("/images/people.png"));
                userName.setText("   " + groups.get(index).getName());
                menuImage.setVisible(true);
                groupFinal.setId(groups.get(index).getId());
                groupFinal.setMembers(groups.get(index).getMembers());
                groupFinal.setMessages(groups.get(index).getMessages());
                leftLimit = 0;
                setConversationGroup1(groupFinal);
            });

            nr++;
        }
        return box;
    }

//    private void setConversationGroup(Group groupFinal) {
//        chat.getChildren().clear();
//        List<Message> messages = messageService.convertMessages(groupFinal.getMessages());
//
//        messages.forEach(x -> {
//            HBox row = new HBox();
//            ImageView profilePhoto = new ImageView();
//            Label text = new Label();
//            if (userService.findPhoto(x.getFrom().getEmail()) != null)
//                profilePhoto.setImage(new Image(userService.findPhoto(x.getFrom().getEmail())));
//            else
//                profilePhoto.setImage(new Image("/images/profile.png"));
//            profilePhoto.setFitWidth(40);
//            profilePhoto.setFitHeight(40);
//            if (x.getFrom().getId().equals(myId)) {
//                text.setText(x.getMessage());
//                text.getStyleClass().add("background-mymessage");
//                text.setAlignment(Pos.CENTER_RIGHT);
//
//                row.getChildren().add(text);
//                row.getChildren().add(profilePhoto);
//                row.setAlignment(Pos.CENTER_RIGHT);
//
//            } else {
//                text.setText(x.getFrom().getLastName() + " " + x.getFrom().getFirstName() + ":" + x.getMessage());
//                text.getStyleClass().add("background-message");
//                row.getChildren().add(profilePhoto);
//                row.getChildren().add(text);
//                row.setAlignment(Pos.CENTER_LEFT);
//
//            }
//
//            chat.getChildren().add(row);
//        });
//        chat.setSpacing(5);
//
//        scroller.setContent(chat);
//        scroller.setFitToWidth(chat.isFillWidth());
//        scroller.setVvalue(1.0f);
//        scroller.setHvalue(1.0);
//    }

    private void setConversationGroup1(Group groupFinal) {
        chat.getChildren().clear();
        numberOfMessages = messageService.convertMessages(groupFinal.getMessages()).size();
        List<Message> messagesList = messageService.convertMessages(groupFinal.getMessages());

        int nr = leftLimit + messagesPerPage();
        if (nr > numberOfMessages)
            nr = numberOfMessages;

        List<Message> messages = messageService.getGroupMessagesOnPage(leftLimit, nr - leftLimit, messagesList);

        messages.forEach(x -> {
            HBox row = new HBox();
            row.setPrefHeight(100);
            ImageView profilePhoto = new ImageView();
            Label text = new Label();
            if (userService.findPhoto(x.getFrom().getEmail()) != null)
                profilePhoto.setImage(new Image(userService.findPhoto(x.getFrom().getEmail())));
            else
                profilePhoto.setImage(new Image("/images/profile.png"));
            profilePhoto.setFitWidth(45);
            profilePhoto.setFitHeight(45);
            if (x.getFrom().getId().equals(myId)) {
                text.setText(x.getMessage());
                text.getStyleClass().add("background-mymessages");
                text.setAlignment(Pos.CENTER_RIGHT);

                row.getChildren().add(text);
                row.getChildren().add(profilePhoto);
                row.setAlignment(Pos.CENTER_RIGHT);

            } else {
                text.setText(x.getFrom().getLastName() + " " + x.getFrom().getFirstName() + ":" + x.getMessage());
                text.getStyleClass().add("background-messages");
                row.getChildren().add(profilePhoto);
                row.getChildren().add(text);
                row.setAlignment(Pos.CENTER_LEFT);

            }

            chat.getChildren().add(row);
        });
        chat.setSpacing(5);

        scroller.setContent(chat);
        scroller.setFitToWidth(chat.isFillWidth());
        scroller.setVvalue(1.0f);
        scroller.setHvalue(1.0);
    }

    public void onCreateGroup(ActionEvent actionEvent) {
        addMembersToGroupLabel.setVisible(true);
        searchFriendsForGroup();
        createGroupButton.setVisible(false);
        plusGroupImage.setVisible(true);
        nameGroupField.setVisible(true);
        newMembersForGroup.add(myId);
    }

    private void searchFriendsForGroup() {
        pagination.getStyleClass().add("/style/pagination");
        pagination.getStyleClass().add("/style/pagination-control");
        pagination.getStyleClass().add("/style/bullet-button");
        pagination.getStyleClass().add("/style/toggle-button");
        pagination.getStyleClass().add("/style/button");
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                return createPageForFriendsForGroupChat(pageIndex);
            }
        });

        int nr_groups = userService.friends(myId, searchField.getText()).size();
        double nr = (double) (nr_groups) / (double) itemsPerPage();

        pagination.setPageCount((int) ceil(nr));
    }

    public VBox createPageForFriendsForGroupChat(int pageIndex) {
        VBox box = new VBox();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double t = bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) * 0.3;
        double y = bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) * 0.7;

        box.setLayoutX(t);
        box.setLayoutY(y);
        box.setAlignment(Pos.CENTER);
        int page = pageIndex * itemsPerPage();

        List<User> users = new ArrayList<>();
        users.addAll(userService.searchingFriends((pageIndex) * itemsPerPage(), itemsPerPage(), myId, searchField.getText()));

        List<UserDTO> usersDTO = new ArrayList<>();
        users.forEach(x -> {
            if (userService.findPhoto(x.getEmail()) == null) {
                UserDTO userDto = new UserDTO(x);
                userDto.setUrlPhoto("/images/profile.png");
                userDto.setEmailDTO(x.getEmail());
                usersDTO.add(userDto);
            } else {
                UserDTO userDto = new UserDTO(x);
                userDto.setUrlPhoto(userService.findPhoto(x.getEmail()));
                userDto.setEmailDTO(x.getEmail());
                usersDTO.add(userDto);
            }

        });

        int nr = 0;
        for (int i = page; i < page + users.size(); i++) {
            VBox row = new VBox();
            int index = nr;
            ImageView imageView = new ImageView();
            Button button = new Button();
            Button buttonPlus = new Button();
            Label label = new Label();

            imageView.setImage(new Image(usersDTO.get(index).getUrlPhoto()));
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);


            ImageView img = new ImageView(new Image("/images/seeProfile.png"));
            img.setFitWidth(20);
            img.setFitHeight(20);
            button.setGraphic(img);
            button.setStyle("-fx-background-color: transparent");

            label.setText("  " + usersDTO.get(index).getNume());
            label.setPrefWidth(160);
            ImageView img1 = new ImageView(new Image("/images/plus.png"));
            img1.setFitWidth(20);
            img1.setFitHeight(20);
            buttonPlus.setGraphic(img1);
            buttonPlus.setStyle("-fx-background-color: transparent");

            GridPane pane = new GridPane();
            pane.add(imageView, 0, 0);
            pane.add(label, 1, 0);
            pane.add(button, 3, 0);
            pane.add(buttonPlus, 2, 0);

            box.getChildren().add(pane);
            label.setOnMouseClicked(event -> {
                userImage.setVisible(true);
                if (usersDTO.get(index).getUrlPhoto() != null)
                    userImage.setImage(new Image(usersDTO.get(index).getUrlPhoto()));
                else
                    userImage.setImage(new Image("/images/profile.png"));
                userName.setVisible(true);
                userName.setText("Your conversation with " + usersDTO.get(index).getNume());
                friendId = usersDTO.get(index).getIdUser();
                setConversation1(friendId);

            });
            buttonPlus.setOnAction(event -> {
                newMembersForGroup.add(usersDTO.get(index).getIdUser());
                buttonPlus.setVisible(false);

            });
            nr++;
        }
        return box;
    }


    public void openGroups(ActionEvent actionEvent) {
        createGroupButton.setVisible(true);

        friendsBool.set(false);
        userName.setVisible(true);
        groupsChat();
    }

    public void openPrivateMessages(ActionEvent actionEvent) {
        userName.setVisible(true);
        conversationField.setVisible(true);
        imageSend.setVisible(true);
        friendsBool.set(true);
        friendsChat();
        createGroupButton.setVisible(false);
        plusGroupImage.setVisible(false);
        addMembersToGroupLabel.setVisible(false);
        nameGroupField.setVisible(false);

    }

    public void onSearchField(KeyEvent keyEvent) {
        if (friendsBool.get()) {
            searchFriends();
        } else {
            if (addMembersToGroupLabel.isVisible())
                searchFriendsForGroup();
            else
                searchGroups();
        }
    }

    public void searchFriends() {
        pagination.getStyleClass().add("/style/pagination");
        pagination.getStyleClass().add("/style/pagination-control");
        pagination.getStyleClass().add("/style/bullet-button");
        pagination.getStyleClass().add("/style/toggle-button");
        pagination.getStyleClass().add("/style/button");
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                return createPageForSearchingFriends(pageIndex);
            }
        });

        int friends = messageService.getMyFriendsWithMessages(myId).size();
        double nr = (double) (friends) / (double) itemsPerPage();

        pagination.setPageCount((int) ceil(nr));
    }


    public VBox createPageForSearchingFriends(int pageIndex) {
        VBox box = new VBox();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double t = bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) * 0.3;
        double y = bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) * 0.7;

        box.setLayoutX(t);
        box.setLayoutY(y);
        box.setAlignment(Pos.CENTER);
        int page = pageIndex * itemsPerPage();

        List<User> users = new ArrayList<>();
        users.addAll(userService.searchingFriends((pageIndex) * itemsPerPage(), itemsPerPage(), myId, searchField.getText()));

        List<UserDTO> usersDTO = new ArrayList<>();
        users.forEach(x -> {
            if (userService.findPhoto(x.getEmail()) == null) {
                UserDTO userDto = new UserDTO(x);
                userDto.setUrlPhoto("/images/profile.png");
                userDto.setEmailDTO(x.getEmail());
                usersDTO.add(userDto);
            } else {
                UserDTO userDto = new UserDTO(x);
                userDto.setUrlPhoto(userService.findPhoto(x.getEmail()));
                userDto.setEmailDTO(x.getEmail());
                usersDTO.add(userDto);
            }

        });

        int nr = 0;
        for (int i = page; i < page + users.size(); i++) {
            VBox row = new VBox();
            int index = nr;
            ImageView imageView = new ImageView();
            Button button = new Button();
            Label label = new Label();

            imageView.setImage(new Image(usersDTO.get(index).getUrlPhoto()));
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);
            ImageView img = new ImageView(new Image("/images/seeProfile.png"));
            img.setFitWidth(20);
            img.setFitHeight(20);

            button.setGraphic(img);
            button.setStyle("-fx-background-color: transparent");
            label.setText("  " + usersDTO.get(index).getNume());
            label.setPrefWidth(160);

            GridPane pane = new GridPane();
            pane.getStyleClass().add("gridpane");
            pane.add(imageView, 0, 0);
            pane.add(label, 1, 0);
            pane.add(button, 3, 0);

            box.getChildren().add(pane);
            label.setOnMouseClicked(event -> {
                userImage.setVisible(true);
                if (usersDTO.get(index).getUrlPhoto() != null)
                    userImage.setImage(new Image(usersDTO.get(index).getUrlPhoto()));
                else
                    userImage.setImage(new Image("/images/profile.png"));
                userName.setVisible(true);
                userName.setText("Your conversation with " + usersDTO.get(index).getNume());
                friendId = usersDTO.get(index).getIdUser();
                setConversation1(friendId);

            });
            button.setOnAction(event -> {
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
                    friendProfileController.setServices(userService, friendshipService, friendRequestService, dialogStage, email, usersDTO.get(index).getEmailDTO());

                    dialogStage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            nr++;
        }
        return box;
    }

    public void searchGroups() {
        pagination.getStyleClass().add("/style/pagination");
        pagination.getStyleClass().add("/style/pagination-control");
        pagination.getStyleClass().add("/style/bullet-button");
        pagination.getStyleClass().add("/style/toggle-button");
        pagination.getStyleClass().add("/style/button");
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                return createPageForSearchingGroups(pageIndex);
            }
        });

        int nr_friends = friendshipService.getFriendships(userService.exists(email).getId()).size();
        double nr = (double) (nr_friends) / (double) itemsPerPage();

        pagination.setPageCount((int) ceil(nr));
    }

    public VBox createPageForSearchingGroups(int pageIndex) {
        VBox box = new VBox();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double t = bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) * 0.3;
        double y = bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) * 0.7;

        box.setLayoutX(t);
        box.setLayoutY(y);

        int page = pageIndex * itemsPerPage();


        List<Group> groups = new ArrayList<>();
        groups.addAll(messageService.getSearchingGroupsOnPage((pageIndex) * itemsPerPage(), itemsPerPage(), userService.exists(email).getId(), searchField.getText()));


        int nr = 0;
        for (int i = page; i < page + groups.size(); i++) {
            VBox row = new VBox();
            int index = nr;
            ImageView imageView = new ImageView();
            Button button = new Button();
            Label label = new Label();
            imageView.setImage(new Image("/images/people.png"));
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);

            label.setText("  " + groups.get(index).getName());
            label.setPrefWidth(160);
            GridPane pane = new GridPane();
            pane.getStyleClass().add("gridpane");
            pane.add(imageView, 0, 0);
            pane.add(label, 1, 0);
            box.getChildren().add(pane);
            label.setOnMouseClicked(event -> {
                userImage.setVisible(true);
                userImage.setImage(new Image("/images/people.png"));
                userName.setText("Your conversation with " + groups.get(index).getName());
                groupFinal.setId(groups.get(index).getId());
                groupFinal.setMembers(groups.get(index).getMembers());
                groupFinal.setMessages(groups.get(index).getMessages());

                setConversationGroup1(groupFinal);
            });

            nr++;
        }
        return box;
    }


    public void onSend(MouseEvent mouseEvent) {
        String message = conversationField.getText();
        if (friendsBool.get()) {
            List<Long> tos = new ArrayList<>();
            tos.add(friendId);
            messageService.sendMessage(userService.exists(email).getId(), tos, message);
            conversationField.setText("");

        } else {
            List<Long> recipients = new ArrayList<>();
            groupFinal.getMembers().forEach(x -> {
                if (!x.equals(userService.exists(email).getId()))
                    recipients.add(x);
            });
            MessageDTO messageDTO = new MessageDTO(userService.exists(email).getId(), recipients, message, LocalDateTime.now(), null);
            messageService.sendMessageGroup(groupFinal, messageDTO);
            conversationField.setText("");
        }
    }


    public void onPlusGroupImage(MouseEvent mouseEvent) {
        if (nameGroupField.getText() != null) {
            Group gr = new Group(nameGroupField.getText(), new ArrayList<>());
            gr.setMembers(newMembersForGroup);
            groupFinal.setId(gr.getId());
            groupFinal.setMembers(gr.getMembers());
            groupFinal.setMessages(gr.getMessages());
            messageService.saveGroup(gr);
            groupsChat();
            plusGroupImage.setVisible(false);
            createGroupButton.setVisible(true);
            nameGroupField.setVisible(false);
            addMembersToGroupLabel.setVisible(false);
        }
    }

    public void onMenuDots(MouseEvent mouseEvent) {
        if (scrollerMembers.isVisible())
            scrollerMembers.setVisible(false);
        else {
            scrollerMembers.setVisible(true);
            members.getChildren().clear();
            List<Long> membersIds = new ArrayList<>();
            membersIds.addAll(groupFinal.getMembers());
            List<User> membersList = new ArrayList<>();
            membersIds.forEach(x -> membersList.add(userService.getUserByID(x)));

            membersList.forEach(x -> {
                VBox row = new VBox();
                ImageView imageView = new ImageView();
                Label label = new Label();
                if (userService.findPhoto(x.getEmail()) == null)
                    imageView.setImage(new Image("/images/profile.png"));
                else
                    imageView.setImage(new Image(userService.findPhoto(x.getEmail())));
                imageView.setFitHeight(40);
                imageView.setFitWidth(40);

                label.setText("  " + x.getFirstName() + " " + x.getLastName());
                GridPane pane = new GridPane();
                pane.getStyleClass().add("gridpane");
                pane.add(imageView, 0, 0);
                pane.add(label, 1, 0);
                members.getChildren().add(pane);
                label.setOnMouseClicked(event -> {
                    try {
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/views/friendProfile.fxml"));
                        AnchorPane root = loader.load();
                        Stage dialogStage = new Stage();
                        dialogStage.setTitle("My profile");
                        dialogStage.initModality(Modality.WINDOW_MODAL);

                        Scene scene = new Scene(root);
                        dialogStage.setScene(scene);

                        FriendProfileController friendProfileController = loader.getController();
                        friendProfileController.setServices(userService, friendshipService, friendRequestService, dialogStage, email, x.getEmail());

                        dialogStage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            });

            scroller.setContent(chat);
            scroller.setFitToWidth(chat.isFillWidth());
            scroller.setVvalue(1.0);
            scroller.setHvalue(1.0);

        }
    }

    public void handleScroll(ScrollEvent scrollEvent) {
        if (friendsBool.get()) {
            if (scrollEvent.getDeltaY() > 0 && (leftLimit + messagesPerPage()) < numberOfMessages) { //scroll up
                leftLimit++;
                setConversation1(friendId);
            }
            if (scrollEvent.getDeltaY() < 0 && leftLimit > 0) { //scroll down and there are messages left
                leftLimit--;
                setConversation1(friendId);
            }
        } else {
            if (scrollEvent.getDeltaY() > 0 && (leftLimit + messagesPerPage()) < numberOfMessages) { //scroll up
                leftLimit++;
                setConversationGroup1(groupFinal);
            }
            if (scrollEvent.getDeltaY() < 0 && leftLimit > 0) { //scroll down and there are messages left
                leftLimit--;
                setConversationGroup1(groupFinal);
            }
        }
    }

    public void onKeyPressed(KeyEvent keyEvent) {
//        if(keyEvent.getCode().equals(KeyCode.UP)&& (rightLimit-messagesPerPage())>0){ //scroll up
//            rightLimit--;
//        }
//        if(keyEvent.getCode().equals(KeyCode.DOWN) && rightLimit!=numberOfMessages){ //scroll down and there are messages left
//            rightLimit++;
//        }
//        setConversation1(friendId);

    }
}
