package com.example.lab6.controller;

import com.example.lab6.model.*;
import com.example.lab6.service.*;
import com.example.lab6.utils.events.FriendRequestChangeEvent;
import com.example.lab6.utils.observer.Observer;
import javafx.event.ActionEvent;
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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.ceil;


public class MyProfileController implements Observer<FriendRequestChangeEvent> {

    public Label nameLabel;
    public ImageView profilePhoto;

    public GridPane hamburgerMenu;


    public TextField searchField;

    public Pagination pagination;
    public ImageView ImageX;
    public VBox raport1VBox;
    public DatePicker endDatePicker;
    public DatePicker startDatePicker;
    public Label activityRaportLabel;
    public ImageView ImageXRaport;
    public Label activityRaportLabel1;
    public DatePicker startDatePicker1;
    public DatePicker endDatePicker1;
    public Pagination paginationSearch;
    public VBox raport2VBox;
    public ImageView uploadImage;
    public ImageView toUploadImage;
    public String toUploadImageURL;
    public TextField postField;
    public ScrollPane scrollerPost;
    public VBox vBoxPost;
    private Boolean ByMe = false;
    public int leftLimitPosts = 0;
    public int numberOfPost;
    public int postOnPage = 2;


    private LocalDateTime start;
    private LocalDateTime end;

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private MessageService messageService;
    private PostService postService;
    private Long to;
    private Long from;
    private Long fromUser;

    Stage stage;
    private String email;
    private Long myId;


    @FXML
    public void initialize() {

        profilePhoto.setImage(new Image("/images/profile.png"));

        hamburgerMenu.setVisible(false);
        pagination.setVisible(false);

        ImageX.setVisible(false);
        raport1VBox.setVisible(false);
        activityRaportLabel.setVisible(false);
        ImageXRaport.setVisible(false);
        raport2VBox.setVisible(false);
        toUploadImage.setVisible(false);
    }

    public void setServices(UserService userService, FriendshipService friendshipService, FriendRequestService friendRequestService, MessageService messageService,  PostService postService, Stage stage, String email) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.friendRequestService = friendRequestService;
        this.messageService = messageService;
        this.postService = postService;
        this.stage = stage;
        this.email = email;
        this.myId = userService.exists(email).getId();
        nameLabel.setText(userService.exists(email).getFirstName() + " " + userService.exists(email).getLastName());
        setProfilePicture();
        this.from = userService.exists(email).getId();
        friendRequestService.addObserver(this);
        initializePost();
    }


    public int itemsPerPage() {
        return 10;
    }

    public void listofFriendRequests() {

        pagination.getStyleClass().add("/style/pagination");
        pagination.getStyleClass().add("/style/pagination-control");
        pagination.getStyleClass().add("/style/bullet-button");
        pagination.getStyleClass().add("/style/toggle-button");
        pagination.getStyleClass().add("/style/button");
        pagination.getStyleClass().add("/style/control-box");
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                return createPageForListOfFriendRequests(pageIndex);
            }
        });

        int nr_friends = friendRequestService.getFriendRequest(myId).size();

        if(nr_friends == 0)
            pagination.setPageCount(1);
        else{
            double nr = (double) (nr_friends) / (double) itemsPerPage();
            pagination.setPageCount((int) ceil(nr));
        }


    }

    public VBox createPageForListOfFriendRequests(int pageIndex) {
        VBox box = new VBox();

        int page = pageIndex * itemsPerPage();

        List<FriendRequestDTO> friendRequestDTOS = friendRequestService.getFriendRequestsOnPage((pageIndex) * itemsPerPage(), itemsPerPage(), myId);



        int nr = 0;
        for (int i = page; i < page + friendRequestDTOS.size(); i++) {
            HBox row = new HBox();
            int index = nr;
            Button acceptBtn = new Button("Accept");
            Button rejectBtn = new Button("Reject");
            Label label = new Label();

            acceptBtn.getStyleClass().add("/style/button-accept-reject");
            acceptBtn.setStyle("-fx-background-color:linear-gradient(to left, #d53369, #cbad6d);-fx-background-radius: 60;-fx-text-fill: black;");
            //acceptBtn.setStyle("-fx-background-radius: 60");

            rejectBtn.setStyle("-fx-background-color:linear-gradient(to left, #d53369, #cbad6d);-fx-background-radius: 60;-fx-text-fill: black;");

            acceptBtn.setPrefWidth(90);
            rejectBtn.setPrefWidth(90);
            label.setText(friendRequestDTOS.get(index).getFrom());
         
            GridPane pane = new GridPane();

            pane.getStyleClass().add("gridpane");
            pane.add(label, 1, 0);
            pane.add(acceptBtn, 3, 0);
            pane.add(rejectBtn, 4, 0);
            pane.setHgap(5);
            pane.setAlignment(Pos.CENTER_LEFT);

            box.getChildren().add(pane);


            acceptBtn.setOnAction(event -> {
                friendRequestService.acceptFriendRequest(friendRequestDTOS.get(index).getUserFrom().getId(), myId);
            });

            rejectBtn.setOnAction(event -> {
                friendRequestService.rejectFriendRequest(friendRequestDTOS.get(index).getUserFrom().getId(), myId);
            });

            nr++;
        }

        return box;
    }

    public void listofFriendRequestsByMe() {

        pagination.getStyleClass().add("/style/pagination");
        pagination.getStyleClass().add("/style/pagination-control");
        pagination.getStyleClass().add("/style/bullet-button");
        pagination.getStyleClass().add("/style/toggle-button");
        pagination.getStyleClass().add("/style/button");
        pagination.getStyleClass().add("/style/control-box");
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                return createPageForListOfFriendRequestsByMe(pageIndex);
            }
        });

        int nr_friends = friendRequestService.getMyFriendsRequestes(myId).size();
        if(nr_friends == 0)
            pagination.setPageCount(1);
        { double nr = (double) (nr_friends) / (double) itemsPerPage();

        pagination.setPageCount((int)(nr));}

    }

    public VBox createPageForListOfFriendRequestsByMe(int pageIndex) {
        VBox box = new VBox();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double t = bounds.getMinX() + (bounds.getWidth() - stage.getWidth()) * 0.3;
        double y = bounds.getMinY() + (bounds.getHeight() - stage.getHeight()) * 0.7;

        box.setLayoutX(t);
        box.setLayoutY(y);
        box.setAlignment(Pos.CENTER);
        int page = pageIndex * itemsPerPage();

        List<FriendRequestDTO> friendRequestDTOS = friendRequestService.getFriendRequestsByMeOnPage((pageIndex) * itemsPerPage(), itemsPerPage(), myId);



        int nr = 0;
        for (int i = page; i < page + friendRequestDTOS.size(); i++) {

            int index = nr;
            Button deleteBtn = new Button("Accept");
            Label label = new Label();


            label.setText(friendRequestDTOS.get(index).getFrom());
            label.setPrefWidth(120);
            deleteBtn.setPrefWidth(90);
            deleteBtn.setStyle("-fx-background-color:linear-gradient(to left, #d53369, #cbad6d);-fx-background-radius: 60;-fx-text-fill: black;");

            GridPane pane = new GridPane();

            pane.getStyleClass().add("gridpane");
            pane.add(label, 1, 0);
            pane.add(deleteBtn, 3, 0);
            box.getChildren().add(pane);




            deleteBtn.setOnAction(event -> {
                friendRequestService.deleteFriendRequest(myId, friendRequestDTOS.get(index).getUserFrom().getId());
            });

            nr++;
        }
        return box;
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
            userService.updatePicture(email, path.toURI().toString());
        else
            userService.savePicture(email, path.toURI().toString());
    }

    public void onMenuButton(ActionEvent actionEvent) {
        if (hamburgerMenu.isVisible())
            hamburgerMenu.setVisible(false);
        else
            hamburgerMenu.setVisible(true);
    }

    public void onMyFriendsRequests(MouseEvent mouseEvent) {
        raport1VBox.setVisible(false);
        raport2VBox.setVisible(false);
        ImageXRaport.setVisible(false);
        pagination.setVisible(true);
        listofFriendRequests();
        ImageX.setVisible(true);
        ByMe = false;

    }

    public void onSendByMe(MouseEvent mouseEvent) {
        raport1VBox.setVisible(false);
        raport2VBox.setVisible(false);
        ImageXRaport.setVisible(false);
        pagination.setVisible(true);
        ImageX.setVisible(true);
        listofFriendRequestsByMe();
        ByMe = true;
    }

    public void onActivityReport(MouseEvent mouseEvent) {
        pagination.setVisible(false);
        ImageX.setVisible(false);
        start = null;
        end = null;
        raport1VBox.setVisible(true);
        ImageXRaport.setVisible(true);
        raport2VBox.setVisible(false);

    }

    public void onMessagesReport(MouseEvent mouseEvent) {
        ImageX.setVisible(false);
        pagination.setVisible(false);
        start = null;
        end = null;
        raport2VBox.setVisible(true);
        ImageXRaport.setVisible(true);
        raport1VBox.setVisible(false);

    }



    private void activityReport() throws IOException {
        List<FriendshipDTO> friendships = userService.getFriendshipsByDate(start, end, userService.exists(email).getId());

        friendships.forEach(x -> {
            System.out.println(x.getFirstName() + " " + x.getLastName() + " " + x.getDate());
        });

        List<MessageDTO> messages = messageService.getMessagesByDate(start, end, userService.exists(email).getId());

        messages.forEach(x -> {
            System.out.println(x.getMessage() + " " + x.getDate());
        });

        //Creating PDF document object
        PDDocument document = new PDDocument();

        PDPage pdPage = new PDPage();
        // Add the page to the document and save the document to a desired file.
        document.addPage(pdPage);

        PDPageContentStream contentStream = new PDPageContentStream(document, pdPage);

        //Begin the Content stream
        contentStream.beginText();

        //Setting the font to the Content stream
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);

        //Setting the position for the line
        contentStream.newLineAtOffset(25, 500);

        contentStream.setLeading(14.5f);

        //The content
        contentStream.showText("The friendships between " + start.toLocalDate() + " " + end.toLocalDate() + " : ");
        contentStream.newLine();
        friendships.forEach(x -> {
            try {
                contentStream.showText(x.getFirstName() + " " + x.getLastName() + " " + "friendship date: " + x.getDate().toLocalDate() + " ");
                contentStream.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        contentStream.newLine();
        contentStream.showText("The messages between " + start.toLocalDate() + " and " + end.toLocalDate());
        contentStream.newLine();
        messages.forEach(x -> {
            try {
                contentStream.showText(x.getMessage() + " " + x.getDate().toLocalDate());
                contentStream.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //Ending the content stream
        contentStream.endText();

        //Closing the content stream
        contentStream.close();

        //Saving the document
        document.save("C:/testPDF/my_activities.pdf");

        //Closing the document
        document.close();
    }

    private void messagesReport() throws IOException {
        List<MessageDTO> messages = messageService.getMessagesFromAFriend(start, end, userService.exists(email).getId(), fromUser);

        messages.forEach(x -> {
            System.out.println(x.getMessage() + " " + x.getDate());
        });

        //Creating PDF document object
        PDDocument document = new PDDocument();

        PDPage pdPage = new PDPage();
        // Add the page to the document and save the document to a desired file.
        document.addPage(pdPage);

        PDPageContentStream contentStream = new PDPageContentStream(document, pdPage);

        //Begin the Content stream
        contentStream.beginText();

        //Setting the font to the Content stream
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);

        //Setting the position for the line
        contentStream.newLineAtOffset(25, 500);

        contentStream.setLeading(14.5f);

        //The content
        contentStream.showText("The messages between " + start.toLocalDate() + " " + end.toLocalDate() + " from " + userService.getUserByID(fromUser).getFirstName() + " " + userService.getUserByID(fromUser).getLastName());
        contentStream.newLine();
        messages.forEach(x -> {
            try {
                contentStream.showText(x.getMessage() + " " + x.getDate().toLocalDate() + " ");
                contentStream.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //Ending the content stream
        contentStream.endText();

        //Closing the content stream
        contentStream.close();

        //Saving the document
        document.save("C:/testPDF/my_messages.pdf");

        //Closing the document
        document.close();
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

    public int itemsPerPageForRaport() {
        return 3;
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

        List<User> userList = userService.getSearchOnPage((pageIndex) * itemsPerPageForRaport(), itemsPerPageForRaport(),myId, searchField.getText().toString());

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

                fromUser = userList.get(index).getId();
                searchField.setText(userList.get(index).getFirstName() +  " " + userList.get(index).getFirstName());

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





    @Override
    public void update(FriendRequestChangeEvent friendRequestChangeEvent) {
        if (ByMe) {
            listofFriendRequests();
        } else {
            listofFriendRequestsByMe();
        }
    }

    public void onXPaginationClicked(MouseEvent mouseEvent) {
        pagination.setVisible(false);
        ImageX.setVisible(false);
    }

    public void onGenerateBtn(ActionEvent actionEvent) throws IOException {
        if(startDatePicker.getValue() != null && endDatePicker.getValue() != null){
            if(startDatePicker.getValue().isBefore(endDatePicker.getValue())) {
                start = startDatePicker.getValue().atStartOfDay();
                end = endDatePicker.getValue().atTime(LocalTime.MAX);
                activityReport();
                activityRaportLabel.setVisible(true);
                activityRaportLabel.setText("The PDF was generated");
                startDatePicker.getEditor().clear();
                endDatePicker.getEditor().clear();
            }
            else{
                activityRaportLabel.setVisible(true);
                activityRaportLabel.setText("The dates are incorrect");
            }
        }
        else{
            activityRaportLabel.setVisible(true);
            activityRaportLabel.setText("You need to pick both dates!!");
        }

    }

    public void onXRaport1Clicked(MouseEvent mouseEvent) {
        raport1VBox.setVisible(false);
        raport2VBox.setVisible(false);
        ImageXRaport.setVisible(false);
    }

    public void onGenerateBtn1(ActionEvent actionEvent) throws IOException {
        if(startDatePicker1.getValue() != null && endDatePicker1.getValue() != null && !searchField.getText().isEmpty()){
            if(startDatePicker1.getValue().isBefore(endDatePicker1.getValue())) {
                start = startDatePicker1.getValue().atStartOfDay();
                end = endDatePicker1.getValue().atTime(LocalTime.MAX);
                messagesReport();
                activityRaportLabel1.setVisible(true);
                activityRaportLabel1.setText("The PDF was generated");
                startDatePicker1.getEditor().clear();
                endDatePicker1.getEditor().clear();
            }
            else{
                activityRaportLabel1.setVisible(true);
                activityRaportLabel1.setText("The dates are incorrect");
            }
        }
        else{
            activityRaportLabel1.setVisible(true);
            activityRaportLabel1.setText("You need to pick both dates!!");
        }
    }

    public void onSearchField(KeyEvent keyEvent) {
        listofSearching();
    }


    public void onPostButton(ActionEvent actionEvent) {
        if(!postField.getText().isEmpty() || !toUploadImageURL.isEmpty()){
            Post post = new Post(myId, toUploadImageURL, postField.getText(), LocalDateTime.now());
            postService.addPost(post);
            toUploadImage.setVisible(false);
            postField.clear();
            toUploadImageURL = "";
            initializePost();
        }
    }

    public void onUploadImage(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();

        String userDirectory = System.getProperty("user.home");
        File userD = new File(userDirectory);
        if (!userD.canRead())
            userD = new File("c:/");
        fileChooser.setInitialDirectory(userD);
        File path = fileChooser.showOpenDialog(stage);

        toUploadImage.setVisible(true);
        toUploadImage.setImage(new Image(path.toURI().toString()));
        toUploadImageURL = toUploadImage.getImage().getUrl();

    }

    private void initializePost(){
        vBoxPost.getChildren().clear();
        numberOfPost = postService.getMyPosts(myId).size();

        int nr = leftLimitPosts+postOnPage;
        if(nr > numberOfPost)
            nr = numberOfPost;
        List<Post> postList = postService.getMyPostsOnPage(leftLimitPosts, nr-leftLimitPosts, myId);

        postList.forEach(x->{
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
            vBoxPost.getChildren().add(box);

        });


        vBoxPost.setSpacing(20);
        scrollerPost.setContent(vBoxPost);
        scrollerPost.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollerPost.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollerPost.setVvalue(0.5);
        scrollerPost.setHvalue(0.5);


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
}
