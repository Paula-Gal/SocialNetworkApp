package com.example.lab6.model;

public class UserDTO {
    private String nume;
    private String urlPhoto;
    private String email;
    private Long id;

    public String getEmailDTO() {
        return email;
    }

    public void setEmailDTO(String email) {
        this.email = email;
    }

    public UserDTO(User user) {

        this.nume = user.getFirstName() + " " + user.getLastName();
        this.id = user.getId();
    }
    public Long getIdUser() {
        return id;
    }
    public String getNume() {
        return nume;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }


    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }
}
