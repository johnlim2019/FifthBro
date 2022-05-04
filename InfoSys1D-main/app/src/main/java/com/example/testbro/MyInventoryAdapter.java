package com.example.testbro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MyInventoryAdapter extends RecyclerView.Adapter<MyInventoryAdapter.MyViewHolder> {

    Context context;
    ArrayList<BookingObj> bookings;

    MyInventoryAdapter(Context ctx, ArrayList<BookingObj> bookings) {
        this.context = ctx;
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inventory_row, parent, false);
        return new MyInventoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyInventoryAdapter.MyViewHolder holder, int position) {
        BookingObj booking = bookings.get(position);
        holder.bookingUserName.setText(booking.getUserName());
        holder.bookingStartTime.setText(new SimpleDateFormat("EEE d MMM HH:mm", Locale.getDefault()).format(booking.start));
        holder.bookingEndTime.setText(new SimpleDateFormat("EEE d MMM HH:mm", Locale.getDefault()).format(booking.end));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView bookingUserName, bookingStartTime, bookingEndTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            bookingUserName = itemView.findViewById(R.id.displayUserName);
            bookingStartTime = itemView.findViewById(R.id.displayStartTime);
            bookingEndTime = itemView.findViewById(R.id.displayEndTime);
        }
    }
}
