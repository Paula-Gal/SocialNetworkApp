package com.example.lab6.utils.events;

import com.example.lab6.model.FriendshipDTO;

public class UserChangeEvent implements Event {
    private ChangeEventType type;
    private FriendshipDTO data;
    private FriendshipDTO oldData;

    public UserChangeEvent( ChangeEventType type, FriendshipDTO data ) {
        this.type = type;
        this.data = data;
    }

    public UserChangeEvent( ChangeEventType type, FriendshipDTO data, FriendshipDTO oldData ) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public FriendshipDTO getData() {
        return data;
    }

    public FriendshipDTO getOldData() {
        return oldData;
    }
}
