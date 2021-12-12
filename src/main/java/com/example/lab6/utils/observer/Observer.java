package com.example.lab6.utils.observer;

import com.example.lab6.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}