package com.example.testbro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemClass implements Serializable {
    private String name, itemID, clubID;
    public boolean availability;
    private ArrayList<String> log;

    public ItemClass(){

    }
    public ItemClass(ItemClass itemClass) {
        this.clubID = itemClass.getClubID();
        this.name = itemClass.getName();
        this.availability = itemClass.getAvailability();
        this.itemID = itemClass.itemID;
        this.log = itemClass.getLog();
    }

    public ItemClass(String name, String clubID){
        this.clubID = clubID;
        this.name = name;
        this.availability = true;
        this.itemID = UUID.randomUUID().toString();
        this.log = new ArrayList<String>();
    }

    public boolean getAvailability(){
        return this.availability;
    }
    public void setAvailability(boolean av) {this.availability = av;}

    public String retAvail() {
        if (this.availability) {
            return "Available";
        } else {
            return "Unavailable";
        }
    }


    public String getName() {
        return name;
    }
    public String getItemID(){
        return itemID;
    }
    public String getClubID(){
        return clubID;
    }
    public void addToLog(String logID) {
        this.log.add(logID);
    }
    public ArrayList<String> getLog() {
        return log;
    }
}
