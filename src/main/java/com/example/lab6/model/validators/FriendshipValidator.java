package com.example.lab6.model.validators;

import com.example.lab6.model.Friendship;

public class FriendshipValidator implements Validator<Friendship> {

    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getE2() == null || entity.getE1() == null)
            throw new ValidationException("Invalid");
    }
}
