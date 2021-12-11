package com.example.lab6.model;

import java.util.Objects;

public class Tuple<E1, E2> {
    private E1 e1;
    private E2 e2;

    public Tuple(E1 e1,E2 e2){
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     *
     * @return the first element from tuple
     */
    public E1 getE1(){
        return e1;
    }

    /**
     *
     * @return the second element from tuple
     */
    public E2 getE2(){
        return e2;
    }

    /**
     *
     * @param e1 set the first element from tuple
     */
    public void setE1(E1 e1) {
        this.e1 = e1;
    }

    /**
     *
     * @param e2 set the second element from tuple
     */
    public void setE2(E2 e2) {
        this.e2 = e2;
    }

    @Override
    public String toString(){
        return "" + e1 + "," + e2;
    }

    @Override
    public boolean equals(Object obj){
        return this.e1.equals(((Tuple) obj).e1) && this.e2.equals(((Tuple) obj).e2);
    }

    @Override
    public int hashCode(){
        return Objects.hash(e1,e2);
    }
}
