package com.example.lab6.controller;

import com.example.lab6.model.*;
import com.example.lab6.service.*;
import com.example.lab6.utils.EventListType;
import com.example.lab6.utils.NotificationType;
import com.example.lab6.utils.events.MessageChangeEvent;
import com.example.lab6.utils.observer.Observer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.ceil;

public class HomeController implements Observer<MessageChangeEvent> {

    public Label welcomeText;
    public ImageView xImage;
    public TextField searchField;
    public Label searchLabel;

    public Label conversationLabel;
    public VBox conversationList;
    public TextArea writeMessageField;

    public AnchorPane conversation;
    public CheckBox friendsGroupsCheckBox;
    public ImageView imageName;

    public Label messagesLabel;
    public Pagination pagination;
    public Pagination paginationSearch;
    public VBox eventsBox;
    public ScrollPane scroller;
    public ImageView closeEventsImage;
    public StackPane stackpane;
    public AnchorPane notif;
    public ScrollPane scrollerPosts;
    public VBox vBoxPosts;
    public ScrollPane scrollerMessages;
    public StackPane stackPaneSubs;
    public ScrollPane scrollerSubs;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private MessageService messageService;
    private EventService eventService;
    private PostService postService;


    private EventListType eventListType = EventListType.AllEvents;

    Stage stage;
    private Long myId;
    private Long to;
    private final Group groupFinal = new Group();
    private String email;
    private PageDTO pageDTO;
    public int leftLimitPosts = 0;
    public int numberOfPost;
    public int postOnPage = 2;
    private boolean is_open_subs = false;


    @FXML
    public void initialize() {

        imageName.setImage(new Image("/images/bsocial.png"));
        imageName.setFitHeight(60);
        imageName.setFitWidth(180);
        searchField.setVisible(false);
        xImage.setVisible(false);
        paginationSearch.setVisible(false);
        conversation.setVisible(false);

        eventsBox.setVisible(false);
        scroller.setVisible(false);
        closeEventsImage.setVisible(false);

        conversationLabel.setVisible(false);

        stackpane.setVisible(true);
        notif.setVisible(false);

        stackPaneSubs.setVisible(false);
    }

    public void showNotification(NotificationType notificationType, Event event) {

        Notifications notifications = Notifications.create();
//        notifications.owner(notif);
//
//        notifications.graphic(notif);

        switch (notificationType) {
            case AWeekBefore -> {
                notifications.text("7 days reminder");
                notifications.title(event.getTitle());
                notifications.hideAfter(Duration.seconds(10));
            }
            case ADayBefore -> {
                notifications.text("1 day reminder");
                notifications.title(event.getTitle());
                notifications.hideAfter(Duration.seconds(10));
            }
            case Today -> {
                notifications.text("Today reminder");
                notifications.title(event.getTitle());
                notifications.hideAfter(Duration.seconds(10));
            }
        }
        notifications.show();
    }

    public void setServices(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, MessageService messageService, EventService eventService, PostService postService, Stage stage, String email, PageDTO page) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.stage = stage;
        this.email = email;
        this.eventService = eventService;
        this.messageService = messageService;
        this.postService = postService;
        this.myId = userService.exists(email).getId();
        this.pageDTO = page;

        welcomeText.setText("Welcome, " + pageDTO.getAdmin().getFirstName() + " " + pageDTO.getAdmin().getLastName() + "!");
        listofFriends();
        welcomeText.setText("Welcome, " + userService.exists(email).getFirstName() + " " + userService.exists(email).getLastName() + "!");
        //setFriendsList();
        messageService.addObserver(this);
        initializePost();
        // start();
        //anchorPagination.getChildren().add(pagination);

        Thread thread = new Thread("Notification Thread") {
            public void run() {
                System.out.println("run by: " + getName());
                Iterable<Event> eventsList = eventService.getMyEvents(myId);
                LocalDate today = LocalDateTime.now().toLocalDate();

                eventsList.forEach(x -> {
                    LocalDateTime notificationDate = eventService.getLastNotificationDate(myId, x.getId());
                    LocalDateTime eventDateStart = x.getStart();

                    LocalDate dateEvent = eventDateStart.toLocalDate();
                    LocalDate lastNotificationDate;

                    if (notificationDate == null)
                        lastNotificationDate = LocalDate.now();
                    else
                        lastNotificationDate = notificationDate.toLocalDate();

                    long noOfDaysBetween = ChronoUnit.DAYS.between(today, dateEvent);
                    System.out.println(ChronoUnit.DAYS.between(lastNotificationDate, today));

                    //lastNotificationDate != today.minusDays(7)
                    if (noOfDaysBetween == 7 && ((ChronoUnit.DAYS.between(lastNotificationDate, today) > 0) || notificationDate == null)) {
                        showNotification(NotificationType.AWeekBefore, x);
                        eventService.saveNotificationDate(myId, x.getId());
                    } else if (noOfDaysBetween == 1 && lastNotificationDate != today.minusDays(1) && (((ChronoUnit.DAYS.between(lastNotificationDate, today) > 0) || notificationDate == null))) {
                        showNotification(NotificationType.ADayBefore, x);
                        eventService.saveNotificationDate(myId, x.getId());
                    } else if (noOfDaysBetween == 0 && ((ChronoUnit.DAYS.between(lastNotificationDate, today) > 0) || notificationDate == null)) {
                        showNotification(NotificationType.Today, x);
                        eventService.saveNotificationDate(myId, x.getId());
                    }
                });
            }
        };

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                thread.run();
                System.out.println(thread.getName());
            }
        });
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
        if (nr_friends == 0)
            pagination.setPageCount(1);
        else {
            double nr = (double) (nr_friends) / (double) itemsPerPage();

            pagination.setPageCount((int) ceil(nr));
        }
    }

    public VBox createPageForListOfFriends(int pageIndex) {
        VBox box = new VBox();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double t = bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) * 0.3;
        double y = bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) * 0.7;

        pagination.setPadding(new Insets(8));
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
                conversationLabel.setText(users.get(index).getNume());
                conversationLabel.setVisible(true);
                conversation.setVisible(true);
                to = users.get(index).getIdUser();
                setConversation(to);
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
                    friendProfileController.setServices(userService, friendshipService, friendRequestService, postService, dialogStage, email, users.get(index).getEmailDTO());

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

            int index = nr;
            ImageView imageView = new ImageView();
            Button button = new Button();
            Label label = new Label();
            imageView.setImage(new Image("/images/people.png"));
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);

            label.setText("  " + groups.get(index).getName());
            label.setPrefWidth(160);
            HBox row = new HBox();
            row.getChildren().add(imageView);
            row.getChildren().add(label);
            row.setSpacing(10);

            box.getChildren().add(row);
            label.setOnMouseClicked(event -> {

                conversation.setVisible(true);
                conversationLabel.setText(groups.get(index).getName());
                conversationLabel.setVisible(true);

                groupFinal.setId(groups.get(index).getId());
                groupFinal.setMembers(groups.get(index).getMembers());
                groupFinal.setMessages(groups.get(index).getMessages());
                setConversationGroup(groupFinal);
            });

            nr++;
        }
        return box;
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

        List<User> userList = userService.getSearchOnPage((pageIndex) * itemsPerPage(), itemsPerPage(), myId, searchField.getText().toString());

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
                    dialogStage.setTitle("User profile");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    //dialogStage.initOwner(primaryStage);
                    Scene scene = new Scene(root);
                    dialogStage.setScene(scene);

                    FriendProfileController friendProfileController = loader.getController();
                    friendProfileController.setServices(userService, friendshipService, friendRequestService, postService, dialogStage, email, userList.get(index).getEmail());

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
        if (searchField.getText().isEmpty()) {
            paginationSearch.setVisible(false);
        } else {
            paginationSearch.setVisible(true);
            listofSearching();
        }
    }

    private void setConversation(Long friendId) {

        conversationList.getChildren().clear();

        conversationList.setFillWidth(true);
        List<Message> messages = messageService.getConversation(myId, friendId);
        Collections.reverse(messages);
        messages.forEach(x -> {
            HBox row = new HBox();
            ImageView profilePhoto = new ImageView();
            Label text = new Label();


            if (userService.findPhoto(x.getFrom().getEmail()) != null)
                profilePhoto.setImage(new Image(userService.findPhoto(x.getFrom().getEmail())));
            else
                profilePhoto.setImage(new Image("/images/profile.png"));

            ImageView replyImg = new ImageView();
            replyImg.setImage(new Image("/images/reply.png"));
            replyImg.setFitWidth(15);
            replyImg.setFitHeight(15);

            Label date_sent = new Label();

            VBox reply_date = new VBox();
            reply_date.setSpacing(3);
            reply_date.setVisible(false);

            profilePhoto.setFitWidth(30);
            profilePhoto.setFitHeight(30);
            text.setText(x.getMessage());
            text.setWrapText(true);
            row.setSpacing(2);
            if (x.getFrom().getId().equals(myId)) {
                text.getStyleClass().add("background-mymessage");
                text.setAlignment(Pos.CENTER_LEFT);
                date_sent.setText("Sent on " + " " + x.getDate().format(DateTimeFormatter.ofPattern(" HH:mm yyyy-MM-dd")));
                date_sent.getStyleClass().add("date-message-start");

                reply_date.setAlignment(Pos.CENTER_RIGHT);
                reply_date.getChildren().add(replyImg);
                reply_date.getChildren().add(date_sent);

                row.getChildren().add(reply_date);
                row.getChildren().add(text);
                row.getChildren().add(profilePhoto);
                row.setAlignment(Pos.CENTER_RIGHT);


            } else {
                text.getStyleClass().add("background-message");
                text.setAlignment(Pos.CENTER_RIGHT);
                date_sent.setText("Sent on " + " " + x.getDate().format(DateTimeFormatter.ofPattern(" HH:mm yyyy-MM-dd")));
                date_sent.getStyleClass().add("date-message-start");

                reply_date.setAlignment(Pos.CENTER_LEFT);
                reply_date.getChildren().add(replyImg);
                reply_date.getChildren().add(date_sent);

                row.getChildren().add(profilePhoto);
                row.getChildren().add(text);
                row.getChildren().add(reply_date);
                row.setAlignment(Pos.CENTER_LEFT);


            }

            text.setOnMouseEntered(y->{
                reply_date.setVisible(true);
            });
            row.setOnMouseExited(y->{
                reply_date.setVisible(false);
            });

            conversationList.getChildren().add(row);
            replyImg.setOnMouseClicked(z->{
                writeMessageField.setText("Replied to:  " + x.getMessage() + "\n");
            });

        });

        conversationList.setSpacing(5);
        scrollerMessages.setContent(conversationList);
        scrollerMessages.setFitToWidth(conversationList.isFillWidth());
        scrollerMessages.setVvalue(1.0);
        scrollerMessages.setHvalue(0.5);

    }

    private void setConversationGroup(Group groupFinal) {
        conversationList.getChildren().clear();

        List<Message> messages = messageService.convertMessages(groupFinal.getMessages());

       // Collections.reverse(messages);
        messages.forEach(x -> {
            HBox row = new HBox();
            row.setPrefHeight(100);
            ImageView profilePhoto = new ImageView();
            Label text = new Label();
            row.setSpacing(2);
            if (userService.findPhoto(x.getFrom().getEmail()) != null)
                profilePhoto.setImage(new Image(userService.findPhoto(x.getFrom().getEmail())));
            else
                profilePhoto.setImage(new Image("/images/profile.png"));
            profilePhoto.setFitWidth(45);
            profilePhoto.setFitHeight(45);

            ImageView replyImg = new ImageView();
            replyImg.setImage(new Image("/images/reply.png"));
            replyImg.setFitWidth(20);
            replyImg.setFitHeight(20);

            Label date_sent = new Label();

            VBox reply_date = new VBox();
            reply_date.setSpacing(3);
            reply_date.setVisible(false);

            text.setWrapText(true);
            text.setMaxWidth(400);

            if (x.getFrom().getId().equals(myId)) {
                text.setText(x.getMessage());
                text.getStyleClass().add("background-mymessage");
                text.setAlignment(Pos.CENTER_RIGHT);
                date_sent.setText("Sent on " + " " + x.getDate().format(DateTimeFormatter.ofPattern(" HH:mm yyyy-MM-dd")));
                date_sent.getStyleClass().add("date-message-start");

                reply_date.setAlignment(Pos.CENTER_RIGHT);
                reply_date.getChildren().add(replyImg);
                reply_date.getChildren().add(date_sent);

                row.getChildren().add(reply_date);
                row.getChildren().add(text);
                row.getChildren().add(profilePhoto);
                row.setAlignment(Pos.CENTER_RIGHT);

            } else {
                text.setText(x.getFrom().getLastName() + " " + x.getFrom().getFirstName() + ":" + x.getMessage());
                text.getStyleClass().add("background-message");
                date_sent.getStyleClass().add("date-message-start");

                reply_date.setAlignment(Pos.CENTER_LEFT);
                reply_date.getChildren().add(replyImg);
                reply_date.getChildren().add(date_sent);

                row.getChildren().add(profilePhoto);
                row.getChildren().add(text);
                row.getChildren().add(reply_date);
                row.setAlignment(Pos.CENTER_LEFT);

            }

            text.setOnMouseEntered(y->{
                reply_date.setVisible(true);
            });
            row.setOnMouseExited(y->{
                reply_date.setVisible(false);
            });

            conversationList.getChildren().add(row);
            replyImg.setOnMouseClicked(z->{
                writeMessageField.setText("Replied to:  " + x.getMessage() + "\n");
            });
        });

        conversationList.setSpacing(5);

        scrollerMessages.setContent(conversationList);
        scrollerMessages.setFitToWidth(conversationList.isFillWidth());
        scrollerMessages.setVvalue(0.0);
        scrollerMessages.setHvalue(0.5);
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
            userProfileController.setServices(userService, friendshipService, friendRequestService, messageService, postService, dialogStage, email);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        if (friendsGroupsCheckBox.isSelected()) {

           setConversationGroup(groupFinal);
        } else {
            setConversation(to);
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
            messagesController.setServices(messageService, friendshipService, friendRequestService, userService,postService, dialogStage, email);

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

            AddEventController addEventController = loader.getController();
            addEventController.setServices(eventService, dialogStage, userService.exists(email).getId());

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshList() {
        eventListType = EventListType.CreatedByMeEvents;
        openEvents();
    }

    public void logout(MouseEvent mouseEvent) {
        stage.close();
    }

    public void openEvents() {

        scrollerPosts.setVisible(false);
        List<Event> events = new ArrayList<>();

        switch (eventListType) {
            case AllEvents -> events = eventService.getAllEvents(userService.exists(email).getId());
            case MyEvents -> events = eventService.getMyEvents(userService.exists(email).getId());
            case CreatedByMeEvents -> events = eventService.getCreatedByMeEvents(userService.exists(email).getId());
        }
        eventsBox.getStyleClass().add("vbox-event");
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

        filteredEvents.setSpacing(43);

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
                    eventListType = EventListType.CreatedByMeEvents;
                    openEvents();
                });
                openEvents();
            }
        });

        eventsBox.setSpacing(20);
        events.forEach(x -> {
            HBox row = new HBox();
            VBox elem = new VBox();
            VBox removeImageBox = new VBox();
            VBox subscribeBox = new VBox();
            VBox imageEventBox = new VBox();

            ImageView eventImage = new ImageView();
            ImageView removeImage = new ImageView();
            ImageView imageMore = new ImageView();
            ImageView locationImage = new ImageView();
            ImageView dateImage = new ImageView();

            Label title = new Label();
            Label description = new Label();
            Label startDate = new Label();
            Label endDate = new Label();
            Label location = new Label();

            //location boc
            HBox locationBox = new HBox();
            locationImage.getStyleClass().add("location-image");

            //date box
            HBox dateBox = new HBox();
            Label line = new Label();
            line.setText("-");
            dateImage.getStyleClass().add("date-image");

            locationImage.setFitHeight(15);
            locationImage.setFitWidth(15);

            dateImage.setFitWidth(15);
            dateImage.setFitHeight(15);

            locationBox.getChildren().add(locationImage);
            locationBox.getChildren().add(location);

            dateBox.setSpacing(2);

            dateBox.getChildren().add(dateImage);
            dateBox.getChildren().add(startDate);
            dateBox.getChildren().add(line);
            dateBox.getChildren().add(endDate);

            title.setText(x.getName());
            title.getStyleClass().add("bold-text");
            description.setText(x.getDescription());
            startDate.setText(x.getStart().toLocalDate().toString());
            endDate.setText(x.getEnd().toLocalDate().toString());
            location.setText(x.getLocation());
            location.setWrapText(true);
            location.setMaxWidth(250);

            eventImage.setImage(new Image("/images/event-image.png"));
            eventImage.getStyleClass().add("event-image");
            row.getStyleClass().add("events-background");

//            scroller.getStyleClass().add("scroll-background");
            // scroller.getStyleClass().add("rounded-scroll-pane");

            eventImage.setFitHeight(45);
            eventImage.setFitWidth(45);

            imageEventBox.getChildren().add(eventImage);
            elem.setSpacing(7);

            elem.getChildren().add(title);
            elem.getChildren().add(description);
            elem.getChildren().add(dateBox);
            elem.getChildren().add(locationBox);
            //  elem.getChildren().add(endDate);

            //list of subs
//            scrollerSubs.setVisible(true);
//            stackPaneSubs.setVisible(true);

            switch (eventListType) {
                case AllEvents -> {
                    removeImage.setImage(new Image("/images/subs-v2.png"));
                    removeImage.getStyleClass().add("image-ripple");
                    removeImage.setFitHeight(60);
                    removeImage.setFitWidth(60);
//                    scrollerSubs.setVisible(false);
//                    is_open_subs = false;
                }
                case MyEvents -> {
                    removeImage.setImage(new Image("/images/uns-v1.png"));
                    removeImage.setFitHeight(65);
                    removeImage.setFitWidth(65);
//                    scrollerSubs.setVisible(false);
//                    is_open_subs = false;
                }
                case CreatedByMeEvents -> {
                    removeImage.setImage(new Image("/images/remove.png"));
                    removeImage.setFitHeight(35);
                    removeImage.setFitWidth(35);
//                    scrollerSubs.setVisible(false);
//                    is_open_subs = false;

                    imageMore.getStyleClass().add("image-see-subs");
                    imageMore.setFitHeight(20);
                    imageMore.setFitWidth(20);

                    imageMore.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if (is_open_subs) {
                                stackPaneSubs.setVisible(false);
                                is_open_subs = false;
                            } else {
                                is_open_subs = true;
                                stackPaneSubs.setVisible(true);
                                scrollerSubs.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                                scrollerSubs.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

                                VBox rowSubs = new VBox();
                                stackPaneSubs.getStyleClass().add("vbox-event");
                                rowSubs.setSpacing(3);
                                HBox titleBox = new HBox();
                                Label titleEv = new Label();
                                titleEv.setText(x.getTitle());
                                titleEv.getStyleClass().add("bold-text");
                                titleBox.getChildren().add(titleEv);
                                rowSubs.getChildren().add(titleBox);

                                List<Long> subscribers = x.getSubscribers();
                                if(subscribers.size() == 0) {
                                    HBox sub = new HBox();
                                    Label name = new Label();
                                    name.setText("No subscribers yet!");
                                    sub.getChildren().add(name);
                                    rowSubs.getChildren().add(sub);
                                }
                                else {
                                    subscribers.forEach(k -> {
                                        HBox sub = new HBox();
                                        Label name = new Label();
                                        name.setText(userService.getUserByID(k).getFirstName() + " " + userService.getUserByID(k).getLastName());
                                        sub.getChildren().add(name);
                                        rowSubs.getChildren().add(sub);
                                    });
                                }

                                scrollerSubs.setContent(rowSubs);
                            }
                        }
                    });
                }
            }

            subscribeBox.setVisible(eventListType.equals(EventListType.AllEvents));
            removeImageBox.setSpacing(20);
            removeImageBox.getChildren().add(removeImage);
            removeImageBox.getChildren().add(imageMore);

            removeImage.setOnMouseClicked(y -> {

                switch (eventListType) {
                    case AllEvents -> {
                        eventService.subscribe(x.getId(), myId);
                        if (ChronoUnit.DAYS.between(x.getStart().toLocalDate(), LocalDateTime.now().toLocalDate()) == 0) {
                            showNotification(NotificationType.Today, x);
                            eventService.saveNotificationDate(myId, x.getId());
                        }
                        eventListType = EventListType.AllEvents;
                        openEvents();
                    }

                    case MyEvents -> {
                        eventService.unsubscribe(x.getId(), myId);
                        eventListType = EventListType.MyEvents;
                        openEvents();
                    }

                    case CreatedByMeEvents -> {
                        eventService.removeEvent(x.getId());
                        eventListType = EventListType.CreatedByMeEvents;
                        openEvents();
                    }
                }
            });

            title.setAlignment(Pos.CENTER);
            description.setAlignment(Pos.CENTER);

            row.getChildren().add(imageEventBox);
            row.setSpacing(20);
            row.getChildren().add(elem);
            row.getChildren().add(removeImageBox);
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
        scroller.setVisible(false);
        scrollerPosts.setVisible(true);
        initializePost();
    }

    public void closeEvents(MouseEvent mouseEvent) {
        scrollerPosts.setVisible(true);
        scroller.setVisible(false);
        eventsBox.setVisible(false);
        closeEventsImage.setVisible(false);
        conversationLabel.setVisible(false);
    }

    public void initializePost() {
        vBoxPosts.getChildren().clear();
        vBoxPosts.setStyle("-fx-border-radius: 20");
        numberOfPost = postService.getAllPosts().size();

        int nr = leftLimitPosts + postOnPage;
        if (nr > numberOfPost)
            nr = numberOfPost;

        List<FriendshipDTO> friendshipDTOS = friendshipService.getFriendships(myId);
        List<Post> postList = postService.getHomePostsOnPage(leftLimitPosts, nr-leftLimitPosts, friendshipDTOS);

        postList.forEach(x -> {
            VBox box = new VBox();
            box.getStyleClass().add("vbox-post");
            HBox hbox = new HBox();
            box.setPrefWidth(400);
            box.setFillWidth(true);
            ImageView profile = new ImageView();
            if (userService.findPhoto(userService.getUserByID(x.getAdmin()).getEmail()) == null)
                profile.setImage(new Image("/images/profile.png"));
            else
                profile.setImage(new Image(userService.findPhoto(userService.getUserByID(x.getAdmin()).getEmail())));

            profile.setFitHeight(50);
            profile.setFitWidth(50);

            VBox data = new VBox();
            Label name = new Label();
            name.setText(userService.getUserByID(x.getAdmin()).getFirstName() + " " + userService.getUserByID(x.getAdmin()).getLastName());
            name.getStyleClass().add("label-name-post");
            data.getChildren().add(name);
            Label date = new Label("Posted on " + x.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            date.getStyleClass().add("label-date-post");
            data.getChildren().add(date);

            hbox.getChildren().add(profile);
            hbox.getChildren().add(data);
            hbox.setSpacing(20);
            hbox.setAlignment(Pos.CENTER_LEFT);
            box.getChildren().add(hbox);

            if(x.getDescription() != null){

                Label description = new Label(x.getDescription());
                description.setAlignment(Pos.CENTER);

                description.getStyleClass().add("label-description-post");
                box.getChildren().add(description);
            }

            if(x.getUrl() != null){
                ImageView img = new ImageView();
                img.setImage(new Image(x.getUrl()));
                HBox rowImage = new HBox();
                img.setFitWidth(160);
                img.setFitHeight(160);
                rowImage.getChildren().add(img);
                rowImage.setAlignment(Pos.CENTER);
                box.getChildren().add(rowImage);
            }

            box.setSpacing(10);
            box.getStyleClass().add("vbox-post");
            vBoxPosts.getChildren().add(box);

        });

        vBoxPosts.setSpacing(20);
        scrollerPosts.setContent(vBoxPosts);
        scrollerPosts.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollerPosts.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollerPosts.setVvalue(0.5);
        scrollerPosts.setHvalue(0.5);
    }

    public void onScrollerPosts(ScrollEvent scrollEvent) {
        if (scrollEvent.getDeltaY() < 0 && (leftLimitPosts + postOnPage) < numberOfPost) { //scroll up
            leftLimitPosts++;
            initializePost();
        }
        if (scrollEvent.getDeltaY() > 0 && leftLimitPosts > 0) { //scroll down and there are messages left
            leftLimitPosts--;
            initializePost();
        }
    }
}
