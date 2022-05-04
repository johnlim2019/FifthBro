package com.example.testbro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class UserClass implements Serializable {

    private String name, email, clubID, userID, phone, clubName;
    private ArrayList<String> bookings;
    public UserClass(){

    }
    public UserClass(String club, String name, String email, String phone, String clubName){
        this.clubID = club;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.clubName = clubName;
        this.userID = phone+name;
        this.bookings = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getClubID() {
        return clubID;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone(){
        return phone;
    }

    public String getUserID(){
        return userID;
    }
    public String getClubName(){
        return clubName;
    }

    public ArrayList<String> getBookings(){
        return bookings;
    }
    public void addToBookings(String bookingID) {
        this.bookings.add(bookingID);
    }
}
