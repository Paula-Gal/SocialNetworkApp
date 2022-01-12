package com.example.lab6.controller;

import com.example.lab6.model.Event;
import com.example.lab6.service.EventService;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class AddEventController {

    public VBox vboxAddEvent;
    public Label addEventLabel;
    public Label titleLabel;
    public TextField textFieldTitle;
    public Label startDateLabel;
    public Button addEventButton;
    public TextField locationField;
    public Label locationLabel;
    public TextField descField;
    public Label descriptions;
    public Label endDateLabel;
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    private EventService eventService;
    private Stage stage;
    private Long id;

    public void setServices(EventService eventService, Stage stage, Long id) {
        this.eventService = eventService;
        this.stage = stage;
        this.id = id;
    }

    public void initialize() {
    }

    public void addEvent(ActionEvent actionEvent) {

        String error = "All the fields are mandatory!";

        if (textFieldTitle.getText().isEmpty() || descField.getText().isEmpty() || locationField.getText().isEmpty() || startDatePicker.getValue() == null || endDatePicker.getValue() == null || locationField.getText().isEmpty())
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "", error);

        else {

            String title = textFieldTitle.getText();
            String desc = descField.getText();
            String location = locationField.getText();
            LocalDateTime start = startDatePicker.getValue().atStartOfDay();
            LocalDateTime end = endDatePicker.getValue().atStartOfDay();
            Event event = new Event(title, LocalDateTime.now(), start, end, desc, location, id);

            eventService.addEvent(event);

            stage.close();
        }
    }
}
