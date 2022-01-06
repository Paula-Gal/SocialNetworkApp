package com.example.lab6.controller;

import com.example.lab6.model.FriendshipDTO;
import com.example.lab6.model.UserDTO;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.MessageService;
import com.example.lab6.service.UserService;
import com.example.lab6.utils.events.MessageChangeEvent;
import com.example.lab6.utils.observer.Observer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.ceil;

public class MessagesController implements Observer<MessageChangeEvent> {

    public TextField searchField;
    public ImageView userImage;
    public Label userName;
    public Button createGroupButton;
    public TextField conversationField;
    public ImageView addImage;
    public ImageView imageSend;
    public AnchorPane anchorPagination;
    private MessageService messageService;
    private FriendshipService friendshipService;
    private UserService userService;
    Stage stage;
    private String email;

    @FXML
    Pagination pagination = new Pagination();

    public void initialize() {
        searchField.setPromptText("Search a friend here...");
        createGroupButton.setVisible(false);
        addImage.setVisible(false);
        conversationField.setVisible(false);
        imageSend.setVisible(false);
        userName.setVisible(false);
    }

    public void setServices(MessageService messageService, FriendshipService friendshipService, UserService userService, Stage dialogStage, String email) {
        this.messageService = messageService;
        this.stage = dialogStage;
        this.email = email;
        this.friendshipService = friendshipService;
        this.userService = userService;

        messageService.addObserver(this);
        start();
        anchorPagination.getChildren().add(pagination);
    }

    @Override
    public void update(MessageChangeEvent messageChangeEvent) {

    }

    public void start() {
        pagination.getStyleClass().add("/style/pagination");
        pagination.getStyleClass().add("/style/pagination-control");
        pagination.getStyleClass().add("/style/bullet-button");
        pagination.getStyleClass().add("/style/toggle-button");
        pagination.getStyleClass().add("/style/button");
        pagination.setStyle("/style/pagination");

        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                return createPage(pageIndex);
            }
        });

        int nr_friends = friendshipService.getFriendships(userService.exists(email).getId()).size();
        double nr = (double) (nr_friends) / (double) itemsPerPage();

        pagination.setPageCount((int) ceil(nr));
    }

    public int itemsPerPage() {
        return 2;
    }

    public VBox createPage(int pageIndex) {
        VBox box = new VBox();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double t = bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) * 0.3;
        double y = bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) * 0.7;

        box.setLayoutX(t);
        box.setLayoutY(y);

        box.setAlignment(Pos.CENTER);
        int page = pageIndex * itemsPerPage();

        List<FriendshipDTO> friendshipDTOS = new ArrayList<>();
        friendshipDTOS.addAll(friendshipService.getMyFriendsOnPage((pageIndex) * itemsPerPage(), itemsPerPage(), userService.exists(email).getId()));
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
            label.setPrefWidth(90);
            GridPane pane = new GridPane();
            pane.getStyleClass().add("gridpane");
            pane.add(imageView, 0, 0);
            pane.add(label, 1, 0);
            pane.add(button, 3, 0);

            box.getChildren().add(pane);
            label.setOnMouseClicked(event -> {
                conversationField.setVisible(false);
                imageSend.setVisible(false);
                userName.setVisible(false);
                userName.setText("Your conversation with " + users.get(index).getNume());
//                conversation.setVisible(true);
                //  conversationList.scrollTo(modelMessages.size() - 1);
                //to = users.get(index).getIdUser();
                //setConversation(to);
                // emailTo = user.getEmail();
                //setConversationList();
            });
            nr++;
        }
        return box;
    }


    public void onCreateGroup(ActionEvent actionEvent) {

    }

    public void openGroups(ActionEvent actionEvent) {
        createGroupButton.setVisible(true);
        addImage.setVisible(true);
    }

    public void openPrivateMessages(ActionEvent actionEvent) {
        userName.setVisible(true);
        conversationField.setVisible(true);
        imageSend.setVisible(true);
    }
}
