package com.example.lab6.controller;

import com.example.lab6.model.Post;
import com.example.lab6.model.Tuple;
import com.example.lab6.model.User;
import com.example.lab6.service.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class FriendProfileController {

    public ImageView profilePhoto;
    public Label nameLabel;
    public Button addButton;
    public Button removeButton;
    public Button sentButton;
    public ScrollPane scroller;
    public VBox vboxPosts;
    public TextArea nameText;
    public Button acceptButton;
    public Label noPostsLabel;
    public AnchorPane nopostAnchor;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private PostService postService;
    private int numberOfPost;
    private int leftLimitPosts = 0;
    private int postOnPage = 2;


    Stage stage;
    private Long to;
    private String email;
    private String email_friend;
    private Long friendID;
    private Long myID;

    @FXML
    public void initialize() {
        sentButton.setVisible(false);
        addButton.setVisible(false);
        removeButton.setVisible(false);
        acceptButton.setVisible(false);
        scroller.setVisible(false);
        nopostAnchor.setVisible(false);


  }

    public void setServices(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService,PostService postService, Stage stage, String email, String email_friend) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.postService = postService;
        this.stage = stage;
        this.email = email;
        this.email_friend = email_friend;
        this.friendID = userService.exists(email_friend).getId();
        this.myID = userService.exists(email).getId();

        if(userService.findPhoto(email_friend) != null)
            profilePhoto.setImage(new Image(userService.findPhoto(email_friend)));
        else
            profilePhoto.setImage(new Image("/images/profile.png"));
        profilePhoto.setFitHeight(110);
        profilePhoto.setFitWidth(110);

        nameLabel.setText(userService.exists(email_friend).getFirstName() + " " + userService.exists(email_friend).getLastName() + "!");
        setProfilePicture();

        if(friendshipService.exists(myID, friendID) != null)
            removeButton.setVisible(true);
        else {
            if(friendRequestService.existsFriendRequests(myID, friendID) != null)
            {
                if(friendRequestService.existsFriendRequests(myID, friendID).getFrom().equals(myID))
                    sentButton.setVisible(true);
                else
                    acceptButton.setVisible(true);
            }
            else
                addButton.setVisible(true);

        }
        List<Post> posts = this.postService.getFriendPosts(friendID);
        if(posts.size() == 0) {
            nopostAnchor.setVisible(true);
        }
        else{
            scroller.setVisible(true);
            initializePost();
        }


    }

    private void setProfilePicture() {
        if(userService.findPhoto(email_friend) != null)
            profilePhoto.setImage(new Image(userService.findPhoto(email_friend)));

    }
    public void onAddButton(ActionEvent actionEvent) {
        friendRequestService.sendFriendRequest(userService.exists(email).getId(), userService.exists(email_friend).getId());
        addButton.setVisible(false);
        sentButton.setVisible(true);
    }

    public void onRemoveButton(ActionEvent actionEvent) {
        friendshipService.removeFriendship(myID, friendID);
        removeButton.setVisible(false);
        addButton.setVisible(true);
    }

    private void initializePost(){
        vboxPosts.getChildren().clear();

        numberOfPost = postService.getFriendPosts(friendID).size();

        int nr = leftLimitPosts+postOnPage;
        if(nr > numberOfPost)
            nr = numberOfPost;
        List<Post> postList = postService.getMyPostsOnPage(leftLimitPosts, nr-leftLimitPosts, friendID);

        postList.forEach(x->{
            VBox box = new VBox();
            box.getStyleClass().add("vbox-post");
            HBox hbox = new HBox();
            box.setPrefWidth(380);
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
            vboxPosts.getChildren().add(box);

        });


        vboxPosts.setSpacing(20);
        scroller.setContent(vboxPosts);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setVvalue(0.5);
        scroller.setHvalue(0.5);


    }

    public void onScroll(ScrollEvent scrollEvent) {
        if(scrollEvent.getDeltaY()<0 && (leftLimitPosts+postOnPage)<numberOfPost){ //scroll up
            leftLimitPosts ++;
            initializePost();
        }
        if(scrollEvent.getDeltaY()>0 && leftLimitPosts > 0){ //scroll down and there are messages left
            leftLimitPosts--;
            initializePost();
        }

    }


    public void onAcceptButton(ActionEvent actionEvent) {
        friendRequestService.acceptFriendRequest(friendID, myID);
        removeButton.setVisible(true);
        acceptButton.setVisible(false);
    }
}
