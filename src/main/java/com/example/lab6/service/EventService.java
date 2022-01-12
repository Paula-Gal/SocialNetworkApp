package com.example.lab6.service;

import com.example.lab6.model.Event;
import com.example.lab6.model.User;
import com.example.lab6.model.validators.EventValidator;
import com.example.lab6.model.validators.ValidationException;
import com.example.lab6.repository.EventRepository;
import com.example.lab6.repository.UserRepository;
import com.example.lab6.utils.events.EventChangeEvent;
import com.example.lab6.utils.observer.Observable;
import com.example.lab6.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventService implements Observable<EventChangeEvent> {

   EventRepository<Long, Event> repoEvent;
    UserRepository<Long, User> repoUser;
    EventValidator eventValidator;

    public EventService(EventRepository<Long, Event> repoEvent, UserRepository<Long, User> repoUser, EventValidator eventValidator) {
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

    public List<Event> getAllEvents(Long id) {
        Iterable<Event> list = repoEvent.findAll();

        List<Event> events = new ArrayList<>();

        list.forEach(events::add);

        Predicate<Event> event = x -> !x.getSubscribers().contains(id);

        events = events.stream().filter(event).collect(Collectors.toList());

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

    public void removeEvent(Long id) {
        Event event = repoEvent.findOne(id);
        repoEvent.remove(event);
    }

    private final List<Observer<EventChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<EventChangeEvent> e) {

    }

    @Override
    public void removeObserver(Observer<EventChangeEvent> e) {

    }

    @Override
    public void notifyObservers(EventChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }

    public void unsubscribe(Long idEvent, Long idUser) {
        Event event = repoEvent.findOne(idEvent);

        repoEvent.delete(event, idUser);
    }

    public void subscribe(Long idEvent, Long myId) {
        Event event = repoEvent.findOne(idEvent);
        List<Long> subs;
        List<Long> subscribers = new ArrayList<>();
        subs = event.getSubscribers();
        subscribers.add(myId);

    /*    if (subs != null) {
            subs.forEach(x -> {
                subscribers.add(x);
            });
        }*/
        event.setSubscribers(subscribers);

        repoEvent.update(event);
    }

    public void saveNotificationDate(Long user, Long event) {
      repoEvent.saveLastNotificationDate(event, user);
    }

    public LocalDateTime getLastNotificationDate(Long user, Long event) {
        return repoEvent.getLastNotificationDate(event, user);
    }
}
