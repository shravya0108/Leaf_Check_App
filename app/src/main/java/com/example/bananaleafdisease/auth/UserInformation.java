package com.example.bananaleafdisease.auth;

public class UserInformation {
    public String firstName;
    public String lastName;
    public String phoneNumber;

    public UserInformation() {
        // Default constructor required for calls to DataSnapshot.getValue(UserInformation.class)
    }

    public UserInformation(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
