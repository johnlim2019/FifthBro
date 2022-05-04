package com.example.testbro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class ActivityItemCheckOut extends AppCompatActivity {

    Bundle extras;
    BookingObj bookingObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_check_out);

        extras = getIntent().getExtras();
        if(extras != null){
            bookingObj = (BookingObj) extras.getSerializable("BookingObj");
        }

        Log.d("Tag", bookingObj.getBookingId());

    }
}