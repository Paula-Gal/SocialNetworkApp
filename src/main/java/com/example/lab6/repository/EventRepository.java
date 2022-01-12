package com.example.lab6.repository;

import com.example.lab6.model.Entity;
import com.example.lab6.model.validators.ValidationException;

import java.time.LocalDateTime;

public interface EventRepository<ID, E extends Entity<ID>> {
    E findOne(ID id);

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
     *
     * @param entity
     *          entity must not be null
     * @return null - if the entity is updated,
     *                otherwise  returns the entity  - (e.g id does not exist).
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws ValidationException
     *             if the entity is not valid.
     */
    E update(E entity);

    E delete(E entity, Long userID);

    E saveLastNotificationDate(Long event, Long user);

    LocalDateTime getLastNotificationDate(Long eventID, Long userID);

}
