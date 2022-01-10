package com.example.lab6.controller;

import com.example.lab6.model.*;
import com.example.lab6.service.*;
import com.example.lab6.utils.EventListType;
import com.example.lab6.utils.events.MessageChangeEvent;
import com.example.lab6.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.ceil;

public class HomeController implements Observer<MessageChangeEvent> {

    public Label welcomeText;
    public ImageView xImage;
    public TextField searchField;
    public Label searchLabel;


  //  public ListView<UserDTO> chatView;
    public Label conversationLabel;
    public ListView conversationList;
    public TextField writeMessageField;

    public AnchorPane conversation;
    //public TextField addingFriendsToConversation;
    //public ListView groupView;
    public ImageView setGroupImage;
    //public ListView groupsView;
    public CheckBox friendsGroupsCheckBox;
    public ImageView imageName;

    public Label messagesLabel;
    public Pagination pagination;
    public Pagination paginationSearch;
    public VBox eventsBox;
    public ScrollPane scroller;
    public ImageView closeEventsImage;
    public Label createConvLabel;
    public StackPane stackpane;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private MessageService messageService;
    private EventService eventService;

    private EventListType eventListType = EventListType.AllEvents;

    Stage stage;
    private Long myId;
    private Long to;
    private final Group groupFinal = new Group();
    private final List<Long> groupConversation = new ArrayList<>();
    private String email;
    private PageDTO pageDTO;

    ObservableList<Message> modelMessages = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        imageName.setImage(new Image("/images/bsocial.png"));
        imageName.setFitHeight(60);
        imageName.setFitWidth(180);
        searchField.setVisible(false);
        xImage.setVisible(false);
        paginationSearch.setVisible(false);
        conversation.setVisible(false);

        addingFriendsToConversation.setVisible(false);
        setGroupImage.setVisible(false);
        eventsBox.setVisible(false);
        scroller.setVisible(false);
        closeEventsImage.setVisible(false);

        groupsView.setVisible(false);
        listView.setVisible(false);
        conversationLabel.setVisible(false);
        friendsGroupsCheckBox.setVisible(false);
        chatView.setVisible(false);
        groupsView.setVisible(false);
        createConvLabel.setVisible(false);
    }

    public void setServices(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, MessageService messageService, Stage stage, String email, PageDTO page) {
    public void setServices(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, MessageService messageService, EventService eventService, Stage stage, String email) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.stage = stage;
        this.email = email;
        this.eventService = eventService;
        this.messageService = messageService;
        this.myId = userService.exists(email).getId();
        this.pageDTO = page;
        welcomeText.setText("Welcome, " + pageDTO.getAdmin().getFirstName() + " " + pageDTO.getAdmin().getLastName() + "!");
        listofFriends();
        welcomeText.setText("Welcome, " + userService.exists(email).getFirstName() + " " + userService.exists(email).getLastName() + "!");
        //setFriendsList();
        messageService.addObserver(this);
        // start();
        //anchorPagination.getChildren().add(pagination);


    }


    public int itemsPerPage() {
        return 10;
    }

    public void listofFriends() {

        pagination.getStyleClass().add("/style/pagination");
        pagination.getStyleClass().add("/style/pagination-control");
        pagination.getStyleClass().add("/style/bullet-button");
        pagination.getStyleClass().add("/style/toggle-button");
        pagination.getStyleClass().add("/style/button");
        pagination.getStyleClass().add("/style/control-box");
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                return createPageForListOfFriends(pageIndex);
            }
        });

        int nr_friends = friendshipService.getFriendships(myId).size();
        double nr = (double) (nr_friends) / (double) itemsPerPage();

        pagination.setPageCount((int) ceil(nr));

    }

    public VBox createPageForListOfFriends(int pageIndex) {
        VBox box = new VBox();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double t = bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) * 0.3;
        double y = bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) * 0.7;

        box.setLayoutX(t);
        box.setLayoutY(y);
        box.setAlignment(Pos.CENTER);
        int page = pageIndex * itemsPerPage();

        List<FriendshipDTO> friendshipDTOS = new ArrayList<>();
        friendshipDTOS.addAll(friendshipService.getMyFriendsOnPage((pageIndex) * itemsPerPage(), itemsPerPage(), myId));
       // friendshipDTOS.addAll(messageService.getMyConversationPage((pageIndex) * itemsPerPage(), itemsPerPage(), userService.exists(email).getId()));
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
                conversationLabel.setText("Your conversation with " + users.get(index).getNume());
                conversation.setVisible(true);
                conversationList.scrollTo(modelMessages.size() - 1);
                to = users.get(index).getIdUser();
                setConversation(to);
                setConversationList();
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

    public void listofGroups() {

        pagination.getStyleClass().add("/style/pagination");
        pagination.getStyleClass().add("/style/pagination-control");
        pagination.getStyleClass().add("/style/bullet-button");
        pagination.getStyleClass().add("/style/toggle-button");
        pagination.getStyleClass().add("/style/button");
        pagination.getStyleClass().add("/style/control-box");
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                return createPageForListOfGroups(pageIndex);
            }
        });

        int nr_groups = messageService.myGroups(myId).size();
        double nr = (double) (nr_groups) / (double) itemsPerPage();

        pagination.setPageCount((int) ceil(nr));

    }

    public VBox createPageForListOfGroups(int pageIndex) {
        VBox box = new VBox();

        int page = pageIndex * itemsPerPage();

        List<Group> groups = new ArrayList<>();
        groups.addAll(messageService.getGroupsOnPage((pageIndex) * itemsPerPage(), itemsPerPage(), myId));


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
                conversationLabel.setText("Your conversation with " + groups.get(index).getName());
                conversation.setVisible(true);
                conversationList.scrollTo(modelMessages.size() - 1);
                groupFinal.setId(groups.get(index).getId());
                groupFinal.setMembers(groups.get(index).getMembers());
                groupFinal.setMessages(groups.get(index).getMessages());
                setModelMessages(groups.get(index));

                setConversationListGroup();
            });

            nr++;
        }
        return box;
    }

    public void setModelUser() {
        if (!searchField.getText().isEmpty()) {
            List<User> userList = userService.filter1(userService.exists(email).getId(), searchField.getText());
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
        listView.setPrefHeight(26 * modelUser.size());
        listView.setItems(modelUser);
    }

    public void setModel() {
        try {
            List<FriendshipDTO> friends = friendshipService.getFriendships(userService.exists(email).getId());
            List<UserDTO> users = new ArrayList<>();
            friends.forEach(x -> {
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


    public void listofSearching() {

        paginationSearch.getStyleClass().add("/style/pagination");
        paginationSearch.getStyleClass().add("/style/pagination-control");
        paginationSearch.getStyleClass().add("/style/bullet-button");
        paginationSearch.getStyleClass().add("/style/toggle-button");
        paginationSearch.getStyleClass().add("/style/button");
        paginationSearch.getStyleClass().add("/style/control-box");
        paginationSearch.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                return createPageForListOfSearching(pageIndex);
            }
        });

        int nr_searchs = userService.filter1(myId, searchField.getText()).size();
        double nr = (double) (nr_searchs) / (double) itemsPerPage();

        paginationSearch.setPageCount((int) ceil(nr));

    }

    public VBox createPageForListOfSearching(int pageIndex) {
        VBox box = new VBox();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double t = bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) * 0.3;
        double y = bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) * 0.7;

        box.setLayoutX(t);
        box.setLayoutY(y);
        box.setAlignment(Pos.CENTER);
        int page = pageIndex * itemsPerPage();

        List<User> userList = userService.getSearchOnPage((pageIndex) * itemsPerPage(), itemsPerPage(),myId, searchField.getText().toString());

        List<UserDTO> users = new ArrayList<>();
        userList.forEach(x -> {
            if (userService.findPhoto(x.getEmail()) == null) {
                UserDTO userDto = new UserDTO(x);
                userDto.setUrlPhoto("/images/profile.png");
                userDto.setEmailDTO(x.getEmail());
                users.add(userDto);
            } else {
                UserDTO userDto = new UserDTO(x);
                userDto.setUrlPhoto(userService.findPhoto(x.getEmail()));
                userDto.setEmailDTO(x.getEmail());
                users.add(userDto);
            }

        });
        int nr = 0;
        for (int i = page; i < page + userList.size(); i++) {
            VBox row = new VBox();
            int index = nr;

            Label label = new Label();
            ImageView imageView = new ImageView();


            imageView.setImage(new Image(users.get(index).getUrlPhoto()));
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);

            label.setText("  " + userList.get(index).getLastName() + " " + userList.get(index).getFirstName());
            label.setPrefWidth(160);

            GridPane pane = new GridPane();
            pane.getStyleClass().add("gridpane");
            pane.add(imageView, 0, 0);
            pane.add(label, 1, 0);

            box.getChildren().add(pane);
            label.setOnMouseClicked(event -> {
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
                    friendProfileController.setServices(userService, friendshipService, friendRequestService, dialogStage, email, userList.get(index).getEmail());

                    dialogStage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            nr++;
        }
        return box;
    }


    public void onHandleBack(ActionEvent actionEvent) {
        stage.close();
    }

    public void onSearchLabel(MouseEvent mouseEvent) {
        searchField.setVisible(true);
        xImage.setVisible(true);
    }


    public void onSearchField(KeyEvent keyEvent) {
        if(searchField.getText().isEmpty())
        {
            paginationSearch.setVisible(false);
        }
        else{
            paginationSearch.setVisible(true);
            listofSearching();
        }

    }


    public void setConversationList() {
        conversationList.setItems(modelMessages);
        conversationList.scrollTo(modelMessages.size() - 1);
        conversationList.setCellFactory(
                param -> new ListCell<Message>() {
                    protected final Label label = new Label();

                    @Override
                    public void updateItem(Message message, boolean empty) {
                        if (empty) {
                            setGraphic(null);

                        } else {
                            label.setText(message.getMessage());

                            if (message.getFrom().getId().equals(myId)) {
                                // label.setTextAlignment(TextAlignment.RIGHT);
                                // getStyleClass().add("background-mymessage");
                                label.getStyleClass().add("background-mymessage");
                                setAlignment(Pos.CENTER_RIGHT);
                                //setText(message.getMessage());

                            } else {
                                label.getStyleClass().add("background-message");
                            }
                            setGraphic(label);
                        }
                    }

                });
    }

    private void setModelMessages(Group group) {
        if (group.getMessages() != null) {
            List<Message> messages = messageService.convertMessages(group.getMessages());
            modelMessages.setAll(messages);
        }
    }

    public void setConversationListGroup() {
        conversationList.setItems(modelMessages);
        conversationList.setCellFactory(
                param -> new ListCell<Message>() {
                    protected final Label label = new Label();

                    @Override
                    public void updateItem(Message message, boolean empty) {
                        if (empty) {
                            setGraphic(null);

                        } else {

                            if (message.getFrom().getId().equals(myId)) {
                                // label.setTextAlignment(TextAlignment.RIGHT);
                                // getStyleClass().add("background-mymessage");
                                label.setText(message.getMessage());
                                label.getStyleClass().add("background-mymessage");
                                setAlignment(Pos.CENTER_RIGHT);
                                //setText(message.getMessage());

                            } else {
                                label.setText(message.getFrom().getFirstName() + ": " + message.getMessage());

                                label.getStyleClass().add("background-message");
                            }
                            setGraphic(label);
                        }
                    }

                });
    }




    public void setConversation(Long to) {
        List<Message> messages = messageService.getConversation(myId, to);

        modelMessages.setAll(messages);
    }

    public void onSendMessage(MouseEvent actionEvent) {
        String message = writeMessageField.getText();
        if (friendsGroupsCheckBox.isSelected()) {
            List<Long> recipients = new ArrayList<>();
            groupFinal.getMembers().forEach(x -> {
                if (!x.equals(userService.exists(email).getId()))
                    recipients.add(x);
            });
            MessageDTO messageDTO = new MessageDTO(myId, recipients, message, LocalDateTime.now(), null);
            messageService.sendMessageGroup(groupFinal, messageDTO);
            writeMessageField.setText("");
        } else {
            List<Long> tos = new ArrayList<>();
            tos.add(to);
            messageService.sendMessage(myId, tos, message);
            writeMessageField.setText("");
        }
    }

    public void onXClicked(MouseEvent mouseEvent) {
        searchField.setVisible(false);
        xImage.setVisible(false);
        paginationSearch.setVisible(false);
    }

    public void myProfileClicked(MouseEvent mouseEvent) {
        // create a new stage for the popup dialog.
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/myProfile.fxml"));

            AnchorPane root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("My profile");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setMaximized(true);

            MyProfileController userProfileController = loader.getController();
            userProfileController.setServices(userService, friendshipService, friendRequestService, messageService, dialogStage, email);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSearchedClicked(MouseEvent mouseEvent) {
        int poz = listView.getSelectionModel().getSelectedIndex();
        List<User> userList = userService.filter1(userService.exists(email).getId(), searchField.getText());

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
            friendProfileController.setServices(userService, friendshipService, friendRequestService, dialogStage, email, user.getEmail());

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        if (friendsGroupsCheckBox.isSelected()) {
            setModelMessages(groupFinal);
            setConversationListGroup();
        } else {
            setConversation(to);
            setConversationList();
        }

    }

    public void onCloseConversation(MouseEvent mouseEvent) {
        conversation.setVisible(false);
    }


    public void friendsOrGroups(ActionEvent actionEvent) {
        if (friendsGroupsCheckBox.isSelected()) {
            listofGroups();

        } else {
            listofFriends();
        }
    }

    public void openMessagesPage(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/messages-view.fxml"));

            AnchorPane root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("My messages");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            MessagesController messagesController = loader.getController();
            messagesController.setServices(messageService,friendshipService, friendRequestService, userService, dialogStage, email);
            messagesController.setServices(messageService, friendshipService, userService, dialogStage, email);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openAddEventPage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/add-event-view.fxml"));

            AnchorPane root = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add new event");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AddEventController addEventController  = new AddEventController();
            addEventController.setServices(eventService,dialogStage,email);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout(MouseEvent mouseEvent) {
        stage.close();
    }

    public void openEvents() {

        List<Event> events = new ArrayList<>();

        switch (eventListType) {
            case AllEvents -> events = eventService.getAllEvents();
            case MyEvents -> events = eventService.getMyEvents(userService.exists(email).getId());
            case CreatedByMeEvents -> events = eventService.getCreatedByMeEvents(userService.exists(email).getId());
        }
        eventsBox.setVisible(true);
        scroller.setVisible(true);
        closeEventsImage.setVisible(true);
        eventsBox.getChildren().clear();

        HBox filteredEvents = new HBox();
        filteredEvents.getStyleClass().add("back-color-category");
        Label allEvents = new Label("All Events");
        allEvents.getStyleClass().add("background-event-category");
        filteredEvents.getChildren().add(allEvents);

        Label myEvents = new Label("My Events");
        myEvents.getStyleClass().add("background-event-category");
        filteredEvents.getChildren().add(myEvents);

        Label createdByMe = new Label("Created by me");
        createdByMe.getStyleClass().add("background-event-category");
        filteredEvents.getChildren().add(createdByMe);

        filteredEvents.setSpacing(30);

        eventsBox.getChildren().add(filteredEvents);

        myEvents.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                eventListType = EventListType.MyEvents;
                stackpane.setVisible(false);
                openEvents();
            }
        });

        allEvents.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                eventListType = EventListType.AllEvents;
                stackpane.setVisible(false);
                openEvents();
            }
        });

        createdByMe.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                eventListType = EventListType.CreatedByMeEvents;
                ImageView imageView = new ImageView();
                imageView.getStyleClass().add("image-add-event");
                imageView.setFitWidth(55);
                imageView.setFitHeight(55);
                imageView.setVisible(true);
                stackpane.setAlignment(Pos.BOTTOM_RIGHT);

                stackpane.getChildren().add(imageView);
                stackpane.setVisible(true);

                imageView.setOnMouseClicked(e -> {
                    openAddEventPage();
                });
                openEvents();
            }
        });

        eventsBox.setSpacing(20);
        events.forEach(x -> {
            HBox row = new HBox();
            VBox elem = new VBox();
            VBox imageEventBox = new VBox();

            ImageView eventImage = new ImageView();
            Label title = new Label();
            Label description = new Label();
            Label startDate = new Label();
            Label endDate = new Label();
            Label location = new Label();

            title.setText(x.getName());
            description.setText(x.getDescription());
            startDate.setText(x.getStart().toString());
            endDate.setText(x.getEnd().toString());
            location.setText(x.getLocation());

            eventImage.setImage(new Image("/images/event-image.png"));
            eventImage.getStyleClass().add("event-image");
            row.getStyleClass().add("events-background");
            scroller.getStyleClass().add("scroll-background");
            scroller.getStyleClass().add("rounded-scroll-pane");

            eventImage.setFitHeight(50);
            eventImage.setFitWidth(50);

            imageEventBox.getChildren().add(eventImage);
            elem.getChildren().add(title);
            elem.getChildren().add(description);
            elem.getChildren().add(location);
            elem.getChildren().add(startDate);
            elem.getChildren().add(endDate);

            title.setAlignment(Pos.CENTER);
            description.setAlignment(Pos.CENTER);

            row.getChildren().add(imageEventBox);
            row.setSpacing(20);
            row.getChildren().add(elem);
            eventsBox.getChildren().add(row);
        });

        scroller.setContent(eventsBox);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        //scroller.setFitToWidth(eventsBox.isFillWidth());
        scroller.setVvalue(0);
        scroller.setHvalue(0);
        //   }
    }

    public void openPosts(MouseEvent mouseEvent) {
//        if (eventsBox.isVisible() && scroller.isVisible()) {
//            eventsBox.setVisible(false);
//            scroller.setVisible(false);
//        } else {
//            eventsBox.getChildren().clear();
//            eventsBox.setVisible(true);
//            scroller.setVisible(true);
//        }
    }

    public void closeEvents(MouseEvent mouseEvent) {
        scroller.setVisible(false);
        eventsBox.setVisible(false);
        closeEventsImage.setVisible(false);
        groupsView.setVisible(false);
        listView.setVisible(false);
        conversationLabel.setVisible(false);
        friendsGroupsCheckBox.setVisible(false);
    }
}
