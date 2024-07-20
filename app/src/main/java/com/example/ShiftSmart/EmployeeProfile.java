package com.example.ShiftSmart;

public class EmployeeProfile {
    private final String id, firstName, lastName, nickname, email, phoneNumber;
    private final int trainedOpening, trainedClosed, isActive;
    private final int[] availabilities;
    private boolean expandable;

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public EmployeeProfile(String id, String firstName, String lastName, String nickname,
                           String email, String phoneNumber,
                           int trainedOpening, int trainedClosed,
                           int[] availabilities, int isActive) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.trainedOpening = trainedOpening;
        this.trainedClosed = trainedClosed;
        this.availabilities = availabilities;
        this.isActive = isActive;
        this.expandable = false;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public String getNickname() {
        return nickname;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public int isTrainedOpening() {
        return trainedOpening;
    }

    public int isTrainedClosing() {
        return trainedClosed;
    }

    public int[] getAvailabilities(){return availabilities;}
    public int getActive(){return isActive;}

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", Trained for Opening='" + trainedOpening + '\'' +
                ", Trained for Closing='" + trainedClosed + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
