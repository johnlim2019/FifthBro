package com.example.testbro;

import androidx.appcompat.app.AppCompatActivity;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import androidx.annotation.NonNull;
import android.view.View;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ActivityQRScanner extends AppCompatActivity {
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        Bundle extras = getIntent().getExtras();
        BookingObj bookingobj = (BookingObj) extras.getSerializable("BookingObj");
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(ActivityQRScanner.this, result.getText(), Toast.LENGTH_SHORT).show();
                        if (result.getText().equals(bookingobj.getItemId())) {
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            String userId = mAuth.getCurrentUser().getUid();
                            if (userId.equals(bookingobj.getUserId())) {
                                //Toast.makeText(ActivityQRScanner.this, new SimpleDateFormat("EEE d MMM HH:mm").format(new Date(System.currentTimeMillis() +( 3600 * 1000))), Toast.LENGTH_LONG).show();
                                    Date start = new Date(bookingobj.start);
                                    Date end = new Date(bookingobj.end);
                                    //Toast.makeText(ActivityQRScanner.this, start.toString(), Toast.LENGTH_LONG).show();
                                    DatabaseReference dbbooking = FirebaseDatabase.getInstance().getReference("Bookings").child(bookingobj.getBookingId());
                                    DatabaseReference dbitem = FirebaseDatabase.getInstance().getReference("Clubs").child(bookingobj.getClubId()).child("items").child(bookingobj.getItemId());
                                    //checking in item
                                    if (bookingobj.isCheckOut) {
                                        dbbooking.child("isCheckOut").setValue(false);
                                        dbbooking.child("isCheckIn").setValue(true);
                                        dbitem.child("availability").setValue(true);
                                        Toast.makeText(ActivityQRScanner.this, "Check in successful", Toast.LENGTH_LONG).show();
                                    }
                                    //checking out item
                                    else {
                                        if ((new Date(System.currentTimeMillis() +( 3600 * 1000)).compareTo(start) >= 0 && new Date(System.currentTimeMillis()).compareTo(end) <= 0) ||(new Date(System.currentTimeMillis()).compareTo(start) >= 0 && new Date(System.currentTimeMillis()).compareTo(end) <= 0)) {
                                            dbbooking.child("isCheckOut").setValue(true);
                                            dbitem.child("availability").setValue(false);
                                            dbbooking.child("isCheckIn").setValue(false);
                                            Toast.makeText(ActivityQRScanner.this, "Check out successful", Toast.LENGTH_LONG).show();
                                        } else {
                                            //Toast.makeText(ActivityQRScanner.this, "Not in booking slot", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    finish();

                            }
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}