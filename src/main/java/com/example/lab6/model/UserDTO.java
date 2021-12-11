package com.example.lab6.model;

public class UserDTO {
    private String nume;

    public UserDTO(User user) {

        this.nume = user.getFirstName() + " " + user.getLastName();
    }
    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }
}
