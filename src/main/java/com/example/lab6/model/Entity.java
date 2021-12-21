package com.example.lab6.model;

import java.io.Serializable;
import java.util.Objects;

public class Entity<ID> implements Serializable {

    // private static final long serialVersionUID = 7331115341259248461L;
    protected ID id;

    public ID getId() {
        return id;
    }

//    private String email;

    public void setId(ID id) {
        this.id = id;
    }

//    public String getEmail() {
//        return email;
//    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}