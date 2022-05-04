package com.example.testbro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityBorrow extends AppCompatActivity implements AdapterView.OnItemSelectedListener, MyBorrowAdapter.OnNoteListener {

    // declaration
    Spinner spinner;
    TextView textViewSelectedClub;
    DatabaseReference referenceClubs, referenceUsers, referenceItems;
    ArrayList<String> clubList;
    String clubName, currentClub, authUserID;
    ArrayAdapter arrayAdapter;
    Bundle extras;

    RecyclerView recyclerViewBorrow;
    MyBorrowAdapter myBorrowAdapter;
    ArrayList<ItemClass> itemList;
    FirebaseAuth mAuth;

    ParserClass parserClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);

        // initialize

        // get current club from previous activity
        extras = getIntent().getExtras();
        if(extras!= null){
            currentClub = extras.getString("currentClub");
        }

        mAuth = FirebaseAuth.getInstance();
        authUserID = mAuth.getCurrentUser().getUid();

        clubList = new ArrayList<>();
        itemList = new ArrayList<>();

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        textViewSelectedClub = findViewById(R.id.tvClub);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, clubList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        referenceClubs = FirebaseDatabase.getInstance().getReference("Clubs");
        referenceUsers = FirebaseDatabase.getInstance().getReference("Users");

        recyclerViewBorrow = findViewById(R.id.recyclerViewBorrow);
        recyclerViewBorrow.setHasFixedSize(true);
        recyclerViewBorrow.setLayoutManager(new LinearLayoutManager(this));
        myBorrowAdapter = new MyBorrowAdapter(this, itemList, this);
        recyclerViewBorrow.setAdapter(myBorrowAdapter);


        // get all existing clubs and store to clubList
        referenceClubs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clubList.clear();
                arrayAdapter.notifyDataSetChanged();
                for (DataSnapshot club : snapshot.getChildren()){
                    clubName = club.child("clubName").getValue().toString();
                    // remove current club from club list
                    if(! currentClub.equals(clubName)) {
                        clubList.add(clubName);
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // show selected text to tvClub
        textViewSelectedClub.setText(spinner.getSelectedItem().toString());
        String clubRef = spinner.getSelectedItem().toString().trim().toLowerCase().replaceAll("\\s", "");
        refreshRecycler(clubRef);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onNoteClick(int position) {
        ItemClass itemClass = itemList.get(position);
        // pass clicked item class to next activity
        Intent i = new Intent(ActivityBorrow.this, ActivityBooking.class);
        i.putExtra("ItemClass", itemClass);
        startActivity(i);
    }

    public void refreshRecycler(String clubRef){
        itemList.clear();
        myBorrowAdapter.notifyDataSetChanged();
        // show inventory of selected club
        referenceItems = FirebaseDatabase.getInstance().getReference("Clubs").child(clubRef).child("items");
        referenceItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                myBorrowAdapter.notifyDataSetChanged();
                for(DataSnapshot item : snapshot.getChildren()){
                    ItemClass itemClass = item.getValue(ItemClass.class);
                    Log.d("item name", itemClass.getName());
                    itemList.add(new ItemClass(itemClass));
                }
                myBorrowAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}