package com.example.lab6;

import com.example.lab6.controller.SplashScreenController;
import com.example.lab6.model.*;
import com.example.lab6.model.validators.EventValidator;
import com.example.lab6.model.validators.FriendshipValidator;
import com.example.lab6.model.validators.PostValidator;
import com.example.lab6.model.validators.UserValidator;
import com.example.lab6.repository.Repository;
import com.example.lab6.repository.UserRepository;
import com.example.lab6.repository.db.*;
import com.example.lab6.repository.paging.PagingRepository;
import com.example.lab6.service.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    UserRepository<Long, User> repoDb;
    PagingRepository<Tuple<Long, Long>, Friendship> repoDbf;
    PagingRepository<Long, MessageDTO> messageDb;
    Repository<Tuple<Long, Long>, FriendRequest> frRequestDb;
    Repository<Long, Group> repoDbGroup;
    PagingRepository<Long, Post> repoPost;

    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    FriendRequestService friendRequestService;
    EventDbRepository repoEvents;
    EventService eventService;
    PostService postService;

    @Override
    public void start(Stage stage) throws IOException {
        repoDb = new UserDbRepository("jdbc:postgresql://localhost:5432/userApp", "postgres", "qwaszx12", new UserValidator());
        repoDbf = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/userApp", "postgres", "qwaszx12", new FriendshipValidator());
        messageDb = new MessageDbRepository("jdbc:postgresql://localhost:5432/userApp", "postgres", "qwaszx12");
        frRequestDb = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/userApp", "postgres", "qwaszx12");
        repoDbGroup = new GroupDbRepository("jdbc:postgresql://localhost:5432/userApp", "postgres", "qwaszx12");
        repoEvents = new EventDbRepository("jdbc:postgresql://localhost:5432/userApp", "postgres", "qwaszx12");
        repoPost = new PostDbRepository("jdbc:postgresql://localhost:5432/userApp", "postgres", "qwaszx12");

        eventService =  new EventService(repoEvents, repoDb,new EventValidator());
        userService = new UserService(repoDb, repoDbf, new UserValidator());
        friendshipService = new FriendshipService(repoDb, repoDbf);
        messageService = new MessageService(messageDb, repoDb, repoDbf, repoDbGroup);
        friendRequestService = new FriendRequestService(frRequestDb, repoDb, repoDbf);
        postService = new PostService(repoPost, repoDb, new PostValidator());

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/splash-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("BeSocial");
        stage.setScene(scene);

        SplashScreenController splashScreenController = fxmlLoader.getController();
        stage.setMaximized(true);
        splashScreenController.setServices(userService, friendshipService, messageService, friendRequestService, eventService, postService, stage);
        stage.show();
        splashScreenController.run();

    }

    public static void main(String[] args) {
        launch();
    }
}