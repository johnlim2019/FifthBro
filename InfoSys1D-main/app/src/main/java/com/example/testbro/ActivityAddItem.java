package com.example.testbro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;

public class ActivityAddItem extends AppCompatActivity implements View.OnClickListener{

    // declaration
    String club;
    DatabaseReference referenceItem;
    EditText etItemName;
    Button btnAddItem;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // initialize

        // get club reference from previous activity
        extras = getIntent().getExtras();
        if(extras != null){
            club = extras.getString("club");
        }

        etItemName = findViewById(R.id.etItemName);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(this);

        referenceItem = FirebaseDatabase.getInstance().getReference("Clubs").child(club).child("items");

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnAddItem:
                // add item
                addItem();
                // go back to previous activity after adding 1 item
                finish();
                break;
        }
    }

    private void addItem() {
        // get name of item from input
        String itemName = etItemName.getText().toString().trim();
        // create new item class using name
        ItemClass itemClass = new ItemClass(itemName, club);
        // add item class into database, under current club
        referenceItem.child(itemClass.getItemID()).setValue(itemClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ActivityAddItem.this, "Item added", Toast.LENGTH_LONG).show();
                }
            }
        });
//        HashMap<String, BookingObj> log = new HashMap<>();
//        BookingObj obj = new BookingObj("itemId", "userId","userName","itemName", new TimePeriod(new Date(), new Date()));
//        referenceItem.child(itemClass.getItemID()).child("log").child(obj.getBookingId()).setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(ActivityAddItem.this, "Log added", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }
}