package com.example.testbro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityShowInventory extends AppCompatActivity implements View.OnClickListener, MyAdapter.OnNoteListener {

    // declaration
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    DatabaseReference referenceItems;
    ArrayList<ItemClass> items;
    Bundle extras;
    String club;
    Button btnAddItem;
    Button showLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_inventory);

        // initialize

        // get club reference from previous activity
        extras = getIntent().getExtras();
        if(extras != null){
            club = extras.getString("club");
        }

        btnAddItem = findViewById(R.id.addItem);
        btnAddItem.setOnClickListener(this);

        recyclerView = findViewById(R.id.recyclerView);
        referenceItems = FirebaseDatabase.getInstance().getReference("Clubs").child(club).child("items");

        items =  new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAdapter = new MyAdapter(this, items, this);
        recyclerView.setAdapter(myAdapter);

        referenceItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clear recycler view before loading data
                items.clear();
                myAdapter.notifyDataSetChanged();
                // get all items under current club from database,
                // store them in items list
                for(DataSnapshot item : snapshot.getChildren()){
                    ItemClass itemClass = item.getValue(ItemClass.class);
                    items.add(itemClass);
                }
                // display all items
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.addItem:
                // pass club reference to add item activity
                Intent i = new Intent(ActivityShowInventory.this, ActivityAddItem.class);
                i.putExtra("club", club);
                startActivity(i);
        }
    }

    @Override
    public void onNoteClick(int position) {
        // try catch here
        ItemClass item = items.get(position);
        if (item.getLog()==null) {
            return;
        }
        Intent i2 = new Intent(ActivityShowInventory.this, ActivityShowInventoryLog.class);
        i2.putExtra("item", item);
        startActivity(i2);
    }
}