package com.example.lab6.model.validators;

import com.example.lab6.model.User;

import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    public void validateEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        StringBuffer errors = new StringBuffer("");
        if (!matcher.matches()) errors.append("Invalid Email" + "\n");
        if(errors.length() > 0) throw new ValidationException(errors.toString());
    }

}
