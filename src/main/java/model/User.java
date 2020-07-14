package model;

import java.io.File;
import java.util.UUID;

public abstract class User {
    protected String username;
    protected String name;
    protected String surname;
    protected String email;
    protected String phoneNumber;
    protected String password;
    protected long credit;
    protected String id;
    protected String birthDate = "2007-12-03";
    protected String gender = "Prefer not to say";
    protected String Address;

    protected String profilePictureAddress = new File("src/main/resources/images/account-icon.png").toURI().toString(); //TODO ye jaii bayad dorost she vali kasi nemifahme

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getProfilePictureAddress() {
        return profilePictureAddress;
    }

    public void setProfilePictureAddress(String profilePictureAddress) {
        this.profilePictureAddress = profilePictureAddress;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public User(String username, String name, String surname, String email, String phoneNumber, String password, long credit) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.credit = credit;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getType() {
        return null;
    }

    public String getFullName() {
        return name + " " + surname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCredit(long credit) {
        this.credit = credit;
    }

    public void addToCredit (long credit) {
        this.credit += credit;
    }

    public Long getCredit() {
        return credit;
    }

    public boolean validatePassword (String password) {
        return this.password.equals(password);
    }

    @Override
    public String toString() {
        return getType() + "{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", credit=" + credit +
                ", id='" + id + '\'' +
                '}';
    }

    public String getSurname() {
        return surname;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return name;
    }
}
