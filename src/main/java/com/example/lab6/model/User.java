package com.example.lab6.model;

import java.util.ArrayList;
import java.util.List;

public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    public ArrayList<User> friendsList;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.friendsList = new ArrayList<>() {
        };
    }

    /**
     *
     * @return the firstName of a user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @return the lastName of a user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @return the list of friends of a user
     */
    public List<User> getFriendsList() {
        return friendsList;
    }

    /**
     *
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @param friend is added to the friendlist
     */
    public void addFriend(User friend){
           this.friendsList.add(friend);
    }

    @Override
    public String toString() {
        return "id= " + id +
                ", firstName = " + firstName +
                ", lastName = " + lastName +
                ", friendsList = " + friendsList.size();
    }
}
