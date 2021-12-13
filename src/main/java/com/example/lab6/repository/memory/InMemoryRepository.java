package com.example.lab6.repository.memory;

import com.example.lab6.model.Entity;
import com.example.lab6.model.validators.ValidationException;
import com.example.lab6.model.validators.Validator;
import com.example.lab6.repository.Repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    private Map<ID, E> entities;
    private Validator<E> validator;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<>();
    }

    @Override
    public E findOne(ID id) {
        if(id == null)
            throw new IllegalArgumentException("ID must not be null!");
        return entities.get(id);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public E save(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("Entity must not be null!");
        try {
            validator.validate(entity);
            if(entities.get(entity.getId()) != null)
                return entity;
            entities.put(entity.getId(), entity);
            return null;
        }
        catch (ValidationException e)
        {
            System.out.println(e.toString());
        }

        return entity;
    }

    @Override
    public E remove(E entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity must not be null!");
        E elem = entities.get(entity.getId());
        entities.remove(entity.getId());
        return elem;
    }

    @Override
    public E update(E entity) {
        return null;
    }




}
