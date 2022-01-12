package com.example.lab6.utils.events;

import com.example.lab6.model.Event;

public class EventChangeEvent implements EventObs {

    ChangeEventType type;
    Event olddata, data;

    public EventChangeEvent(ChangeEventType type, Event olddata, Event data) {
        this.type =type;
        this.olddata = olddata;
        this.data = data;
    }

    public EventChangeEvent(ChangeEventType type, Event data) {
        this.type =type;
        this.data = data;
    }

    public Event getOlddata() {
        return olddata;
    }

    public Event getData() {
        return data;
    }
}
