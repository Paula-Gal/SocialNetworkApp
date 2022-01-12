package com.example.lab6.model.validators;


import com.example.lab6.model.Post;

public class PostValidator implements Validator<Post>{


    @Override
    public void validate(Post entity) throws ValidationException {
        if(entity.getUrl() == null && entity.getDescription() == null)
            throw new ValidationException("The descripton and url can not be both null");

        if(entity.getDescription().length() > 200)
            throw new ValidationException("Event description should have maximum 200 characters");

    }
}

