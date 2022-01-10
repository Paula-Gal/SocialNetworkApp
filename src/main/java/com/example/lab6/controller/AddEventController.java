package com.example.lab6.controller;

import com.example.lab6.service.EventService;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddEventController {

    public VBox vboxAddEvent;
    private EventService eventService;
    private Stage stage;
    private String id;

    public void setServices(EventService userService, Stage stage, String id) {
       this.eventService = eventService;
       this.stage = stage;
       this.id = id;
    }

    public void initialize() {

    }


}
