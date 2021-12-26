package com.example.lab6.utils.events;

import com.example.lab6.model.Message;
import com.example.lab6.model.MessageDTO;

public class MessageChangeEvent implements Event{
    ChangeEventType type;
    MessageDTO olddata, data;

    public MessageChangeEvent(ChangeEventType type, MessageDTO olddata, MessageDTO data) {
        this.type =type;
        this.olddata = olddata;
        this.data = data;
    }

    public MessageChangeEvent(ChangeEventType type, MessageDTO data) {
        this.type =type;
        this.data = data;
    }

    public MessageDTO getOlddata() {
        return olddata;
    }

    public MessageDTO getData() {
        return data;
    }
}
