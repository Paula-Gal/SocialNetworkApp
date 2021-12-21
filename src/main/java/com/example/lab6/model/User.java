package com.example.lab6.model;

import java.util.ArrayList;
import java.util.List;

public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    public ArrayList<User> friendsList;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.friendsList = new ArrayList<>() {
        };
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
