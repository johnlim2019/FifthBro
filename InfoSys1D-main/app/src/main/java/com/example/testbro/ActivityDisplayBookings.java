package com.example.testbro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityDisplayBookings extends AppCompatActivity implements MyBookingsAdapter.OnNoteListener {

    // declaration
    RecyclerView recyclerView;
    MyBookingsAdapter myBookingsAdapter;
    DatabaseReference referenceCurrentUser, referenceBookings, referenceTimePeriod, referenceItem;
    ArrayList<BookingObj> myBookings;
    ArrayList<String> myBookingIDs;
    String userID, clubID, itemID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_bookings);

        // initialize
        recyclerView = findViewById(R.id.recyclerViewBookings);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myBookings = new ArrayList<>();
        myBookingIDs = new ArrayList<>();

        myBookingsAdapter = new MyBookingsAdapter(this, myBookings, this);
        recyclerView.setAdapter(myBookingsAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        // set references
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        referenceCurrentUser = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        referenceBookings = FirebaseDatabase.getInstance().getReference("Bookings");

        // get list of bookingIDs from current user, store into myBookingIDs
        referenceCurrentUser.child("bookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot bookingIDs : snapshot.getChildren()){
                    myBookingIDs.add(bookingIDs.getValue().toString());
                }

                referenceBookings.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myBookings.clear();
                        myBookingsAdapter.notifyDataSetChanged();
                        for(DataSnapshot bookingIDs : snapshot.getChildren()){
                            if(myBookingIDs.contains(bookingIDs.getKey())){
                                BookingObj bookingObj = bookingIDs.getValue(BookingObj.class);
                                myBookings.add(bookingObj);
                            }
                        }
                        myBookingsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }// myBookingIDs cannot leave here

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onNoteClick(int position) {
        BookingObj bookingObj = myBookings.get(position);
        Intent i = new Intent(ActivityDisplayBookings.this, ActivityQRScanner.class);
        i.putExtra("BookingObj", bookingObj);
        startActivity(i);
    }


    // swipe to delete
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            BookingObj selectedBooking = myBookings.get(viewHolder.getBindingAdapterPosition());
            AlertDialog.Builder adb = new AlertDialog.Builder(viewHolder.itemView.getContext());
            adb.setTitle("Delete Booking");
            adb.setMessage("Are you sure you want to delete this booking?");
            adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    myBookingsAdapter.notifyDataSetChanged();
                }
            });
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    deleteBooking(selectedBooking);
                }
            });
            adb.create();
            adb.show();
        }
    };

    public void deleteBooking(BookingObj bookingObj){
        // get bookingID
        final String bookingID = bookingObj.getBookingId();
        // remove from userlog
        referenceCurrentUser.child("bookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot bookingIDs : snapshot.getChildren()){
                    if(bookingIDs.getValue().toString().equals(bookingID)){
                        Log.d("bookingid.getkey", bookingIDs.getKey());
                        Log.d("bookingid.getvalue", bookingIDs.getValue().toString());
                        referenceCurrentUser.child("bookings").child(bookingIDs.getKey().toString()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // remove from itemlog
        // get clubid from bookingobj
        clubID = bookingObj.getClubId();
        itemID = bookingObj.getItemId();
        referenceItem = FirebaseDatabase.getInstance().getReference("Clubs").child(clubID).child("items").child(itemID).child("log");
        referenceItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot bookingIDs : snapshot.getChildren()){
                    if(bookingIDs.getValue().toString().equals(bookingID)){
                        referenceItem.child(bookingIDs.getKey().toString()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // remove from timeperiod
        referenceTimePeriod = FirebaseDatabase.getInstance().getReference("TimePeriods");
        referenceTimePeriod.child(bookingID).removeValue();

        // remove from bookings
        referenceBookings.child(bookingID).removeValue();
    }
}