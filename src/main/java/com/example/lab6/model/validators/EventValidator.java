package com.example.lab6.model.validators;

import com.example.lab6.model.Event;

public class EventValidator implements Validator<Event>{


    @Override
    public void validate(Event entity) throws ValidationException {
        if(entity.getName().length() == 0)
            throw new ValidationException("Event description cannot be null");

        if(entity.getName().length() > 500)
            throw new ValidationException("Event description should have maximum 500 characters");

        if(entity.getLocation().length() == 0)
            throw new ValidationException("Event location cannot be null");
    }
}
