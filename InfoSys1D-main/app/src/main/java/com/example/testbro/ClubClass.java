package com.example.testbro;

import android.content.ClipData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ClubClass implements Serializable {
    private ArrayList<UserClass> users;
    private String clubName, clubID;
    private ArrayList<ItemClass> items;
    private HashMap<String, BookingObj> log;

    public ClubClass(){

    }
    public ClubClass(String clubName, String clubID){
        this.clubName = clubName;
        this.clubID = clubID;
        this.users = new ArrayList<>();
        this.items = new ArrayList<>();
        this.log = new HashMap<>();
    }

    public String getClubName() {
        return clubName;
    }
    public String getClubID(){
        return clubID;
    }
    public ArrayList<UserClass> getUsers(){
        return users;
    }
    public ArrayList<ItemClass> getItems(){
        return items;
    }
    public void addUser(UserClass userClass){
        this.users.add(userClass);
    }
    public void setLog(HashMap<String, BookingObj> log){
        this.log.putAll(log);
    }
    public HashMap<String, BookingObj> getLog(){
        return log;
    }
}
