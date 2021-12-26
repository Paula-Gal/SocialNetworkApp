package com.example.lab6.utils.events;

import com.example.lab6.model.FriendRequest;

public class FriendRequestChangeEvent implements Event {
    FriendRequest olddata, data;
    ChangeEventType type;

    public FriendRequestChangeEvent(ChangeEventType type, FriendRequest olddata, FriendRequest data) {
        this.olddata = olddata;
        this.type = type;
        this.data = data;
    }

    public FriendRequestChangeEvent(ChangeEventType type, FriendRequest data) {
        this.type = type;
        this.data = data;
    }



    public ChangeEventType getType() {
        return type;
    }

    public FriendRequest getOlddata() {
        return olddata;
    }

    public FriendRequest getData() {
        return data;
    }
}
