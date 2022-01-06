package com.example.lab6;

import com.example.lab6.controller.SplashScreenController;
import com.example.lab6.model.*;
import com.example.lab6.model.validators.FriendshipValidator;
import com.example.lab6.model.validators.UserValidator;
import com.example.lab6.repository.Repository;
import com.example.lab6.repository.UserRepository;
import com.example.lab6.repository.db.*;
import com.example.lab6.repository.paging.PagingRepository;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.MessageService;
import com.example.lab6.service.UserService;
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
    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    FriendRequestService friendRequestService;

    @Override
    public void start(Stage stage) throws IOException {
        repoDb = new UserDbRepository("jdbc:postgresql://localhost:5432/socialnetworkapp", "postgres", "paula123", new UserValidator());
        repoDbf = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/socialnetworkapp", "postgres","paula123", new FriendshipValidator());
        messageDb = new MessageDbRepository("jdbc:postgresql://localhost:5432/socialnetworkapp", "postgres", "paula123");
        frRequestDb = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/socialnetworkapp", "postgres", "paula123");
        repoDbGroup = new GroupDbRepository("jdbc:postgresql://localhost:5432/socialnetworkapp", "postgres", "paula123");


        userService = new UserService(repoDb, repoDbf, new UserValidator());
        friendshipService = new FriendshipService(repoDb, repoDbf);
        messageService = new MessageService(messageDb, repoDb, repoDbf, repoDbGroup);
        friendRequestService = new FriendRequestService(frRequestDb, repoDb, repoDbf);
        //                //stage.setFullScreen(trueice(frRequestDb, repoDb, repoDbf);
//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/login-view.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/splash-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("BeSocial");
        stage.setScene(scene);
        //LoginController helloController = fxmlLoader.getController();
        SplashScreenController splashScreenController = fxmlLoader.getController();
        stage.setMaximized(true);
        splashScreenController.setServices(userService, friendshipService, messageService, friendRequestService, stage);
        stage.show();
        splashScreenController.run();

    }


    public static void main(String[] args) {
        launch();
    }
}