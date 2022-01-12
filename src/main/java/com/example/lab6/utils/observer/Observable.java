package com.example.lab6.utils.observer;

import com.example.lab6.utils.events.EventObs;

public interface Observable<E extends EventObs> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
