package com.example.lab6.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Friendship extends Entity<Tuple<Long,Long>> implements List<String> {
    LocalDate date;
    private Tuple<Long,Long> ship;

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setShip(Tuple<Long, Long> ship) {
        this.ship = ship;
    }

    /**
     * Constructor
     * @param ship
     */
    public Friendship(Tuple<Long, Long> ship) {
        this.ship = ship;
    }

    /**
     *
     * @return the tuple
     */
    public Tuple<Long, Long> getShip() {
        return ship;
    }


    /**
     *
     * @return the first element from tuple
     */
    public Long getE1(){
        return ship.getE1();
    }

    /**
     *
     * @return the second element from tuple
     */
    public  Long getE2(){
        return ship.getE2();
    }


    public LocalDate getDate(){return date;}

    @Override
    public String toString() {
        return "id= " + id +
                " e1 = " + ship.getE1() +
                ", e2 = " + ship.getE2();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<String> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(String s) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public String get(int index) {
        return null;
    }

    @Override
    public String set(int index, String element) {
        return null;
    }

    @Override
    public void add(int index, String element) {

    }

    @Override
    public String remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<String> listIterator() {
        return null;
    }

    @Override
    public ListIterator<String> listIterator(int index) {
        return null;
    }

    @Override
    public List<String> subList(int fromIndex, int toIndex) {
        return null;
    }


}
