package com.example.lab6.repository;

import com.example.lab6.model.Entity;
import com.example.lab6.model.validators.ValidationException;

public interface UserRepository<ID, E extends Entity<ID>> {

    /**
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
     * @throws IllegalArgumentException if id is null.
     */
    E findOne(ID id);

    E findOneByEmail(String email);

    /**
     * @return all entities
     */
    Iterable<E> findAll();

    /**
     * @param entity entity must be not null
     * @return null- if the given entity is saved
     * otherwise returns the entity (id already exists)
     * @throws ValidationException      if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null.     *
     */
    E save(E entity);

    /**
     * @param entity entity must be not null
     * @return the entity the entity is removed
     * @throws IllegalArgumentException if the given entity is null.     *
     */
    E remove(E entity);

    /**
     * @param entity entity must not be null
     * @return null - if the entity is updated,
     * otherwise  returns the entity  - (e.g id does not exist).
     * @throws IllegalArgumentException if the given entity is null.
     * @throws ValidationException      if the entity is not valid.
     */
    E update(E entity);

}
