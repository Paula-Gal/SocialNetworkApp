package com.example.lab6.utils.events;

import com.example.lab6.model.User;

public class UserChangeEvent implements EventObs {
    private ChangeEventType type;
    private User data;
    private User oldData;

    public UserChangeEvent( ChangeEventType type, User data ) {
        this.type = type;
        this.data = data;
    }

    public UserChangeEvent( ChangeEventType type, User data, User oldData ) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public User getData() {
        return data;
    }

    public User getOldData() {
        return oldData;
    }
}
