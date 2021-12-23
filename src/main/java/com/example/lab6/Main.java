package com.example.lab6;

import com.example.lab6.controller.LoginController;
import com.example.lab6.model.*;
import com.example.lab6.model.validators.FriendshipValidator;
import com.example.lab6.model.validators.UserValidator;
import com.example.lab6.model.validators.Validator;
import com.example.lab6.repository.Repository;
import com.example.lab6.repository.UserRepository;
import com.example.lab6.repository.db.FriendRequestDbRepository;
import com.example.lab6.repository.db.FriendshipDbRepository;
import com.example.lab6.repository.db.MessageDbRepository;
import com.example.lab6.repository.db.UserDbRepository;
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
    Repository<Tuple<Long, Long>, Friendship> repoDbf;
    Repository<Long, MessageDTO> messageDb;
    Repository<Tuple<Long, Long>, FriendRequest> frRequestDb;

    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    FriendRequestService friendRequestService;

    @Override
    public void start(Stage stage) throws IOException {
        repoDb = new UserDbRepository("jdbc:postgresql://localhost:5432/userApp", "postgres","qwaszx12", new UserValidator());
        repoDbf = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/userApp", "postgres","qwaszx12", new FriendshipValidator());
        messageDb = new MessageDbRepository("jdbc:postgresql://localhost:5432/userApp", "postgres", "qwaszx12");
        frRequestDb = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/userApp", "postgres", "qwaszx12");



        userService = new UserService(repoDb, repoDbf, new UserValidator());
        friendshipService = new FriendshipService(repoDb, repoDbf);
        messageService = new MessageService(messageDb, repoDb, repoDbf);
        friendRequestService = new FriendRequestService(frRequestDb, repoDb, repoDbf);
        //                //stage.setFullScreen(trueice(frRequestDb, repoDb, repoDbf);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("UserApp");
        stage.setScene(scene);
        LoginController helloController = fxmlLoader.getController();
        helloController.setServices(userService, friendshipService, messageService, friendRequestService, stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}