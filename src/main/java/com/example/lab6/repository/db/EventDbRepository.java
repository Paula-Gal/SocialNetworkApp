package com.example.lab6.repository.db;

import com.example.lab6.model.Event;
import com.example.lab6.repository.paging.Page;
import com.example.lab6.repository.paging.Pageable;
import com.example.lab6.repository.paging.PagingRepository;

public class EventDbRepository implements PagingRepository<Long, Event> {
    @Override
    public Event findOne(Long aLong) {
        return null;
    }

    @Override
    public Iterable<Event> findAll() {
        return null;
    }

    @Override
    public Event save(Event entity) {
        return null;
    }

    @Override
    public Event remove(Event entity) {
        return null;
    }

    @Override
    public Event update(Event entity) {
        return null;
    }

    @Override
    public Page<Event> findAll(Pageable pageable) {
        return null;
    }
}
