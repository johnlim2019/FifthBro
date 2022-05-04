package com.example.testbro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public class ActivityBooking extends AppCompatActivity {

    ItemClass itemInstance;
    UserClass userInstance;
    ClubClass clubInstance;
    Bundle extras;
    Button startTimeBtn, endTimeBtn, bookBtn, finishBtn;
    TextView bookingListingsTitle, confirmStartTiming, confirmEndTiming;
    ListView bookingListings;
    Date start, end;
    SimpleDateFormat simpleDateFormat;
    String itemName, itemId, userName, userId, clubName, clubID;
    ArrayList<String> itemLog, userLog, clubLog;
    ArrayAdapter<BookingObj> arr;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference referenceUsers, referenceClub, referenceItem, referenceBookings, referenceTimeperiod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // declare your objects before anything else
        bookingListingsTitle = findViewById(R.id.CurrentBookingsTitle);
        bookingListings = findViewById(R.id.CurrentBookings);
        startTimeBtn = findViewById(R.id.SelStartTiming);
        endTimeBtn = findViewById(R.id.SelEndTiming);
        confirmStartTiming = findViewById(R.id.PrintTiming);
        confirmEndTiming = findViewById(R.id.PrintTiming2);
        bookBtn = findViewById(R.id.bookNow);
        finishBtn = findViewById(R.id.doneBooking);
        this.simpleDateFormat = new SimpleDateFormat("EEE d MMM HH:mm", Locale.getDefault());
        db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        referenceUsers = db.getReference("Users");
        referenceClub = db.getReference("Clubs");
        referenceBookings = db.getReference("Bookings");
        referenceTimeperiod = db.getReference("TimePeriods");
        arr = new ArrayAdapter<BookingObj>(this, R.layout.custom_list_view, new ArrayList<>());
        bookingListings.setAdapter(arr);



        extras = getIntent().getExtras();
        if (extras != null) {
            itemInstance = new ItemClass((ItemClass) extras.getSerializable("ItemClass"));
            itemId = itemInstance.getItemID();
            itemName = itemInstance.getName();
            itemLog = itemInstance.getLog();
            clubID = itemInstance.getClubID();
//            Toast.makeText(ActivityBooking.this, itemId, Toast.LENGTH_LONG).show();
        }
        referenceItem = db.getReference("Clubs").child(clubID).child("items");
        referenceUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserClass userInstance = snapshot.getValue(UserClass.class);
                userName = userInstance.getName();
                userLog = userInstance.getBookings();
                referenceItem = db.getReference("Clubs").child(clubID).child("items");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        printBookings();
        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectBooking(confirmStartTiming, true);
            }
        });
        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectBooking(confirmEndTiming, false);
            }
        });
        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeBooking();
                finish();
            }
        });
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //delete function

        /*bookingListings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // remeber to cast the bookingobj so we can get an addressable instance
                BookingObj booking = (BookingObj) bookingListings.getItemAtPosition(position);
                if (booking.getUserId() == userId) {
                    //we can delete it
                    AlertDialog.Builder adb = new AlertDialog.Builder(ActivityBooking.this);
                    adb.setTitle("Delete?");
                    adb.setMessage("Are you sure you want to delete " + booking.toString());
                    final int positionToRemove = position;
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // delete the bookings for both the logs
                            itemLog.remove(booking.getBookingId());
                            userLog.remove(booking.getBookingId());
                            referenceUsers.child(userId).child("bookings").setValue(userLog).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ActivityBooking.this, "Successful", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            referenceClub.child(clubID).child("items").child(itemId).setValue(itemLog).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ActivityBooking.this, "Successful", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            printBookings();
                            Snackbar.make(findViewById(R.id.LinearLayout), "Deleted " + booking.toString(), Snackbar.LENGTH_SHORT).show();
                            // once we are done update our instances
                            storeAttributes();
                        }
                    });
                    adb.show();
                } else {
                    Snackbar.make(findViewById(R.id.LinearLayout), "You do not own " + booking.toString(), Snackbar.LENGTH_LONG).show();
                }

            }
        });*/
    }

    public void selectBooking(TextView target, Boolean isStart) {
        final Date now;
        Date temp;
        if (isStart == true) {
            temp = new Date();
        } else {
            if (start != null) {
                temp = start;
            } else {
                temp = new Date(new Date().getTime() + TimeUnit.HOURS.toMillis(1));
            }
        }
        String title;
        if (isStart) {
            title = "Start Time";
        } else {
            title = "End Time";
        }

        now = new Date(temp.getTime() + TimeUnit.HOURS.toMillis(1));
        final Calendar calendarMin = Calendar.getInstance();
        final Calendar calendarMax = Calendar.getInstance();
        calendarMin.setTime(now);
        calendarMax.setTime(new Date(now.getTime() + TimeUnit.DAYS.toMillis(31)));

        final Date minTime = calendarMin.getTime();
        final Date maxTime = calendarMax.getTime();

        SingleDateAndTimePickerDialog.Builder singleBuilder = new SingleDateAndTimePickerDialog.Builder(this)
                .setTimeZone(TimeZone.getDefault())
                .bottomSheet()
                .curved()
                .displayMinutes(false)
                .defaultDate(now)
                //.backgroundColor(Color.BLACK)
                .mainColor(Color.BLACK)
                .minDateRange(minTime)
                .maxDateRange(maxTime)
                .title(title)
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        // set minutes to 0
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.set(Calendar.MINUTE, 0);
                        date = calendar.getTime();
                        Log.d("booking slot", date.toString());
                        target.setText(simpleDateFormat.format(date));
                        if (isStart == true) {
                            start = date;
                        } else {
                            end = date;
                        }
                    }
                });
        singleBuilder.display();
    }

    public void completeBooking() {
        // check for the correct values
        if (start == null || end == null) {
            Snackbar.make(findViewById(R.id.LinearLayout), "Missing Time", Snackbar.LENGTH_SHORT).show();
        } else if (start.getTime() > end.getTime()) {
            Snackbar.make(findViewById(R.id.LinearLayout), "End is before Start", Snackbar.LENGTH_SHORT).show();
        } else {
            // create new booking object
            TimePeriod timing = new TimePeriod(start, end);
            BookingObj booking = new BookingObj(itemId, userId, userName, itemName, timing, clubID);
            // check if booking is not overlapping
            referenceUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserClass userInstance = snapshot.getValue(UserClass.class);
                    userLog = userInstance.getBookings();
                    if (userLog == null) {
                        userLog = new ArrayList<>();
                    }
                    userLog.add(booking.getBookingId());
                    referenceUsers.child(userId).child("bookings").setValue(userLog).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                Toast.makeText(ActivityBooking.this,"Saved to userlog", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            referenceBookings.child(booking.getBookingId()).setValue(booking).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
//                        Toast.makeText(ActivityBooking.this, "Added booking", Toast.LENGTH_LONG).show();
                    }
                }
            });
            referenceTimeperiod.child(booking.getBookingId()).setValue(timing).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
//                        Toast.makeText(ActivityBooking.this, "Added TP", Toast.LENGTH_LONG).show();
                    }
                }
            });

            referenceItem.child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ItemClass itemInstance = snapshot.getValue(ItemClass.class);
                    itemLog = itemInstance.getLog();
                    if (itemLog == null) {
                        itemLog = new ArrayList<>();
                        itemLog.add(booking.getBookingId());
                        Snackbar.make(findViewById(R.id.LinearLayout), "Booked", Snackbar.LENGTH_SHORT).show();
                        referenceClub.child(clubID).child("items").child(itemId).child("log").setValue(itemLog).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
//                                    Toast.makeText(ActivityBooking.this,"Saved to itemlog", Toast.LENGTH_LONG).show();
//                                    printBookings();
                                }
                            }
                        });
                        printBookings();

                    } else {

                        itemLog.add(booking.getBookingId());
                        storeAttributes();
                        for (String i : itemLog) {
                            if (i.equals(booking.getBookingId())){
                                continue;
                            }
                            referenceBookings.child(i).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        BookingObj currBooking = snapshot.getValue(BookingObj.class);
                                        String currBookingUserName = currBooking.getUserName();
                                        referenceTimeperiod.child(i).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            TimePeriod j = snapshot.getValue(TimePeriod.class);
                                            if (j.overlap(timing)) {

                                                Snackbar.make(findViewById(R.id.LinearLayout), "Booking overlaps "
                                                                + currBookingUserName +  "'s " +
                                                                simpleDateFormat.format(j.retStart()) + " to " +
                                                                simpleDateFormat.format(j.retEnd())
                                                        , Snackbar.LENGTH_LONG).show();
                                                referenceTimeperiod.child(booking.getBookingId()).removeValue();
                                                referenceBookings.child(booking.getBookingId()).removeValue();
                                                referenceUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        userLog.remove(booking.getBookingId());
                                                        storeAttributes();

                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                                itemLog.remove(booking.getBookingId());
                                                storeAttributes();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                        printBookings();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        printBookings();
    }

    //
    public void printBookings() {
        // print title
        bookingListingsTitle.setText(itemName);
        // first we want to sort the bookings by start time
        ArrayList<String> ids = itemInstance.getLog();
        if(ids == null){
            return;
        }
        Log.d("ids",ids.toString());
        referenceBookings.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String, Long> startTimeHashM = new HashMap<>();
                    HashMap<String, BookingObj> log = new HashMap<>();
                    for(DataSnapshot item : snapshot.getChildren()){
                        BookingObj bookingObj = item.getValue(BookingObj.class);
                        log.put(bookingObj.getBookingId(),bookingObj);
                    }
                    Log.d("HASHMAP", String.valueOf(log.size()));
                    // now we have the bookings in a log list
                    for (Map.Entry<String,BookingObj> i : log.entrySet()) {
                        Log.d("Time Start",i.getValue().toString());
                        Long time = null;
                        try {
                            time = i.getValue().start;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (time != null && ids.contains(i.getKey()))
                        startTimeHashM.put(i.getKey(),time);
                    }
                    // Create a list from elements of HashMap
                    List<Map.Entry<String, Long> > list =
                            new LinkedList<Map.Entry<String, Long> >(startTimeHashM.entrySet());

                    // Sort the list
                    Collections.sort(list, new Comparator<Map.Entry<String, Long> >() {
                        public int compare(Map.Entry<String, Long> o1,
                                           Map.Entry<String, Long> o2)
                        {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });

                    // now we have the sorted list of key
                    ArrayList<BookingObj> listBookedTime = new ArrayList<>();
                    for (Map.Entry<String, Long> i : list) {
                        Log.d("sorted", i.getKey());
                        listBookedTime.add(log.get(i.getKey()));
                    }
                    // booking listing list view adapter.
//                    ArrayAdapter<BookingObj> arr;
//                    arr = new ArrayAdapter<BookingObj>(getApplicationContext(), android.R.layout.simple_list_item_1, listBookedTime);
//                    bookingListings.setAdapter(arr);
                    arr.clear();
                    arr.addAll(listBookedTime);
                    arr.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        /*
        // now we have the sorted list of key
        for (int i = 0; i < entryList.size(); i++) {
            Map.Entry<String, Long> entry = entryList.get(i);
            String key = entry.getKey();
            db.getReference("Bookings").child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    BookingObj bookingInstance = snapshot.getValue(BookingObj.class);
                    if (bookingInstance.isComplete == false) {
                        listBookedTime.add(bookingInstance);
                    }
//                    Log.d("currentBooking", simpleDateFormat.format(bookingInstance.getTiming().getStart()));
//                    Log.d("currentBooking", simpleDateFormat.format(bookingInstance.getTiming().getEnd()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
//            BookingObj booking = itemLog.get(key);
//            if (booking.isComplete == false) {
//                listBookedTime.add(booking);
//            }
//            Log.d("currentBooking", simpleDateFormat.format(booking.getTiming().getStart()));
//            Log.d("currentBooking", simpleDateFormat.format(booking.getTiming().getEnd()));

        }*/

    }


    private void storeAttributes(){
        referenceUsers.child(userId).child("bookings").setValue(userLog).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(ActivityBooking.this,"Saved to userlog", Toast.LENGTH_LONG).show();
                }
            }
        });

        referenceClub.child(clubID).child("items").child(itemId).child("log").setValue(itemLog).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(ActivityBooking.this,"Saved to itemlog", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
