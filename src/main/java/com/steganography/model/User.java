package com.steganography.model;


public class User {

    private final int userId;
    private final String firstName;
    private final String lastName;
    private final String username;

    public User(int userId, String firstName, String lastName, String username) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    public int getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + username + ")";
    }
}
