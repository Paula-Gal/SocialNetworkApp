package com.example.lab6.utils.observer;

import com.example.lab6.utils.events.EventObs;

public interface Observer<E extends EventObs> {
    void update(E e);
}