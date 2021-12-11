package com.example.lab6;

import com.example.lab6.controller.HelloController;
import com.example.lab6.model.Friendship;
import com.example.lab6.model.Tuple;
import com.example.lab6.model.User;
import com.example.lab6.model.validators.FriendshipValidator;
import com.example.lab6.model.validators.UserValidator;
import com.example.lab6.repository.Repository;
import com.example.lab6.repository.db.FriendshipDbRepository;
import com.example.lab6.repository.db.UtilizatorDbRepository;
import com.example.lab6.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class HelloApplication extends Application {
    Repository<Long, User> repoDb;
    Repository<Tuple<Long, Long>, Friendship> repoDbf;

    UserService userService;
    @Override
    public void start(Stage stage) throws IOException {
        repoDb = new UtilizatorDbRepository("jdbc:postgresql://localhost:5432/userApp", "postgres","qwaszx12", new UserValidator());
        repoDbf = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/userApp", "postgres","qwaszx12", new FriendshipValidator());

        userService = new UserService(repoDb, repoDbf);
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/views/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 240);
        stage.setTitle("UserApp");
        stage.setScene(scene);
        HelloController helloController = fxmlLoader.getController();
        helloController.setUserService(userService);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}