package com.example.lab6.model.validators;

public interface Validator<T> {

    /**
     * @param entity
     * @throws ValidationException if the entity is not valid
     */
    void validate(T entity) throws ValidationException;
}