package com.example.lab6.service;

import com.example.lab6.model.Event;
import com.example.lab6.model.User;
import com.example.lab6.model.validators.EventValidator;
import com.example.lab6.model.validators.ValidationException;
import com.example.lab6.repository.UserRepository;
import com.example.lab6.repository.paging.PagingRepository;
import com.example.lab6.utils.events.EventChangeEvent;
import com.example.lab6.utils.observer.Observable;
import com.example.lab6.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventService implements Observable<EventChangeEvent> {

    PagingRepository<Long, Event> repoEvent;
    UserRepository<Long, User> repoUser;
    EventValidator eventValidator;

    public EventService(PagingRepository<Long, Event> repoEvent, UserRepository<Long, User> repoUser, EventValidator eventValidator) {
        this.repoEvent = repoEvent;
        this.repoUser = repoUser;
        this.eventValidator = eventValidator;
    }

    public void addEvent(Event event) {
        try {
            eventValidator.validate(event);
            repoEvent.save(event);
        } catch (ValidationException ex) {
            throw new ValidationException(ex.getMessage());
        }
    }

    public List<Event> getAllEvents() {
        Iterable<Event> list = repoEvent.findAll();
        List<Event> events = new ArrayList<>();
        list.forEach(events::add);

        return events;
    }

    public List<Event> getMyEvents(Long id) {
        Iterable<Event> subscribedEvents = repoEvent.findAll();

        List<Event> myEvents = new ArrayList<>();

        subscribedEvents.forEach(myEvents::add);

        Predicate<Event> event = x -> x.getSubscribers().contains(id);

        myEvents = myEvents.stream().filter(event).collect(Collectors.toList());

        return myEvents;
    }

    public List<Event> getCreatedByMeEvents(Long id) {
        Iterable<Event> list = repoEvent.findAll();
        List<Event> myEvents = new ArrayList<>();
        List<Event> myEventsList;

        Predicate<Event> event = x -> x.getAdmin().equals(id);
        list.forEach(myEvents::add);

        myEventsList = myEvents.stream().filter(event).collect(Collectors.toList());
        return myEventsList;
    }

    @Override
    public void addObserver(Observer<EventChangeEvent> e) {

    }

    @Override
    public void removeObserver(Observer<EventChangeEvent> e) {

    }

    @Override
    public void notifyObservers(EventChangeEvent t) {

    }
}
