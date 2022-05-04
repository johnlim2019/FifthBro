package com.example.testbro;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class BookingObj implements Serializable {
    public String bookingId;
    private String itemId;
    private String userId;
    private String userName;
    private String itemName;
    private String clubId;
//    public TimePeriod timing;
    public long start;
    public long end;
    boolean isLate = false;
    boolean isCheckOut = false;
    boolean isCheckIn = false;
    boolean isComplete = false;

    BookingObj(){}

    BookingObj(String itemId, String userId,String userName, String itemName, TimePeriod timing, String clubID){
        this.bookingId = UUID.randomUUID().toString();
        this.itemId = itemId;
        this.userId = userId;
//        this.timing = timing;
        this.start = timing.start;
        this.end = timing.end;
        this.userName = userName;
        this.clubId = clubID;
        this.itemName = itemName;
    }

    public String getBookingId() {
        return bookingId;
    }
//    public TimePeriod getTiming() {
//        return timing;
//    }
    public String getClubId(){return clubId;}

    public String getItemName() {
        return itemName;
    }

    public String getUserName() {
        return userName;
    }

//    public void setTiming(TimePeriod timing) {
//        this.timing = timing;
//    }

    public String getItemId() {
        return itemId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean getIsComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete){
        isComplete = complete;
    }
    public void forceUncomplete(){
        isComplete = false;
    }

    public boolean getIsLate() {
        return isLate;
    }

    public void setLate(boolean late) {
        isLate = late;
    }

    public boolean getIsCheckIn() {
        return isCheckIn;
    }

    public boolean getIsCheckOut() {
        return isCheckOut;
    }

    public void setIsCheckIn(boolean checkIn) {
        isCheckIn = checkIn;
    }

    public void setIsCheckOut(boolean checkOut) {
        isCheckOut = checkOut;
    }

    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE d MMM HH:mm", Locale.getDefault());
        return simpleDateFormat.format(new Date(start)) + " - "
                +simpleDateFormat.format(new Date(end)) +" by \n"
                +this.getUserName();
    }
}
