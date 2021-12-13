package com.example.lab6;

import com.example.lab6.controller.HelloController;
import com.example.lab6.model.*;
import com.example.lab6.model.validators.FriendshipValidator;
import com.example.lab6.model.validators.UserValidator;
import com.example.lab6.repository.Repository;
import com.example.lab6.repository.db.FriendRequestDbRepository;
import com.example.lab6.repository.db.FriendshipDbRepository;
import com.example.lab6.repository.db.MessageDbRepository;
import com.example.lab6.repository.db.UtilizatorDbRepository;
import com.example.lab6.service.FriendRequestService;
import com.example.lab6.service.FriendshipService;
import com.example.lab6.service.MessageService;
import com.example.lab6.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    Repository<Long, User> repoDb;
    Repository<Tuple<Long, Long>, Friendship> repoDbf;
    Repository<Long, MessageDTO> messageDb;
    Repository<Tuple<Long, Long>, FriendRequest> frRequestDb;

    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    FriendRequestService friendRequestService;

    @Override
    public void start(Stage stage) throws IOException {
        repoDb = new UtilizatorDbRepository("jdbc:postgresql://localhost:5432/socialnetworkapp", "postgres","paula123", new UserValidator());
        repoDbf = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/socialnetworkapp", "postgres","paula123", new FriendshipValidator());
        messageDb = new MessageDbRepository("jdbc:postgresql://localhost:5432/socialnetworkapp", "postgres", "paula123");
        frRequestDb = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/socialnetworkapp", "postgres", "paula123");



        userService = new UserService(repoDb, repoDbf);
        friendshipService = new FriendshipService(repoDb, repoDbf);
        messageService = new MessageService(messageDb, repoDb, repoDbf);
        friendRequestService = new FriendRequestService(frRequestDb, repoDb, repoDbf);
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/views/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 240);
        stage.setTitle("UserApp");
        stage.setScene(scene);
        HelloController helloController = fxmlLoader.getController();
        helloController.setServices(userService, friendshipService, messageService, friendRequestService);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}