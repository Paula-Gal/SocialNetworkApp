package com.example.lab6.model.validators;

import com.example.lab6.model.User;
import com.example.lab6.repository.Repository;


public class UserValidator implements Validator<User>{

    @Override
    public void validate(User entity) throws ValidationException {

        if(entity.getFirstName().length() == 0)
            throw new ValidationException("FirstName is invalid");

        if(entity.getLastName().length() == 0)
            throw new ValidationException("Lastname is invalid");

        if(entity.getId() == null)
            throw  new ValidationException("Id invalid");
    }


}
