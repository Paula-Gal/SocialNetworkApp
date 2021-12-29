package com.example.lab6.controller;

import com.example.lab6.model.FriendRequestDTO;
import com.example.lab6.model.FriendshipDTO;
import com.example.lab6.model.MessageDTO;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class MyProfileController implements Observer<FriendRequestChangeEvent> {

    public Label nameLabel;
    public ImageView profilePhoto;
    public ListView friendsRequestsView;
    public GridPane hamburgerMenu;
    public DatePicker datePicker;
    public ImageView dateImage;
    public TextField searchField;
    public ListView listView;
    public Label labelDateSaved;
    public Label labelPdfGenerated;
    public Label userSavedLabel;
    public ImageView xImage;
    private Boolean ByMe = false;
    private int selected_report = 1;

    private LocalDateTime start;
    private LocalDateTime end;

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private MessageService messageService;
    private Long to;
    private Long from;
    private Long fromUser;

    Stage stage;
    private String email;

    ObservableList<User> modelRequests = FXCollections.observableArrayList();
    ObservableList<String> modelUser = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        profilePhoto.setImage(new Image("/images/profile.png"));
        friendsRequestsView.setVisible(false);
        hamburgerMenu.setVisible(false);

        labelDateSaved.setVisible(false);
        labelPdfGenerated.setVisible(false);
        userSavedLabel.setVisible(false);
        datePicker.setVisible(false);
        searchField.setVisible(false);
        dateImage.setVisible(false);
        listView.setVisible(false);
        xImage.setVisible(false);
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
        datePicker.setVisible(false);
        dateImage.setVisible(false);
        labelDateSaved.setVisible(false);
        labelPdfGenerated.setVisible(false);
    }

    public void onSendByMe(MouseEvent mouseEvent) {
        ByMe = true;
        setModelRequestsByMe();
        setFriendsRequestsViewByMe();
        friendsRequestsView.setVisible(true);
        datePicker.setVisible(false);
        dateImage.setVisible(false);
        labelDateSaved.setVisible(false);
        labelPdfGenerated.setVisible(false);
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

    public void onActivityReport(MouseEvent mouseEvent) {
        start = null;
        end = null;
        selected_report = 1;
        datePicker.setVisible(true);
        dateImage.setVisible(true);
        datePicker.setPromptText("Start date");
    }

    public void onMessagesReport(MouseEvent mouseEvent) {
        start = null;
        end = null;
        selected_report = 2;
        labelDateSaved.setVisible(false);
        labelPdfGenerated.setVisible(false);
        datePicker.setVisible(true);
        dateImage.setVisible(true);
        datePicker.setPromptText("Start date");
    }

    public void onResetDatePicker(MouseEvent mouseEvent) {
    }

    public void onReady(MouseEvent mouseEvent) throws IOException {
        if (start != null) {
            datePicker.setPromptText("End date");
            end = datePicker.getValue().atTime(LocalTime.MAX);
            datePicker.getEditor().clear();
            if (selected_report == 1) {
                labelDateSaved.setVisible(true);
                activityReport();
                labelPdfGenerated.setVisible(true);
            } else {
                labelDateSaved.setVisible(true);
                searchField.setVisible(true);
                xImage.setVisible(true);
            }
        } else {
            datePicker.setPromptText("Start Date");
            start = datePicker.getValue().atStartOfDay();
            datePicker.getEditor().clear();
        }
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

    public void onSearchedClicked(MouseEvent mouseEvent) throws IOException {
        int poz = listView.getSelectionModel().getSelectedIndex();
        List<User> userList = userService.filter1(userService.exists(email).getId(), searchField.getText().toString());

        userSavedLabel.setVisible(true);
        fromUser = userList.get(poz).getId();
        messagesReport();
        labelPdfGenerated.setVisible(true);
    }

    public void onXClicked(MouseEvent mouseEvent) {
        searchField.setVisible(false);
        xImage.setVisible(false);
        listView.setVisible(false);
        userSavedLabel.setVisible(false);
    }
}
