package com.example.testbro;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MyBookingsAdapter extends RecyclerView.Adapter<MyBookingsAdapter.MyViewHolder> {

    Context context;
    ArrayList<BookingObj> myBookings;
    OnNoteListener myOnNoteListener;

    MyBookingsAdapter(Context ctx, ArrayList<BookingObj> bookings, OnNoteListener monl){
        this.context = ctx;
        this.myBookings = bookings;
        this.myOnNoteListener = monl;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bookings_row, parent, false);
        return new MyViewHolder(view, myOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BookingObj bookingObj = myBookings.get(position);
        holder.itemName.setText(bookingObj.getItemName());
        holder.itemStartTime.setText(new SimpleDateFormat("EEE d MMM HH:mm", Locale.getDefault()).format(new Date(bookingObj.start)));
        holder.itemEndTime.setText(new SimpleDateFormat("EEE d MMM HH:mm", Locale.getDefault()).format(new Date(bookingObj.end)));
    }

    @Override
    public int getItemCount() {
        return myBookings.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView itemName, itemStartTime, itemEndTime;
        OnNoteListener onNoteListener;
        public MyViewHolder(@NonNull View itemView, OnNoteListener onl) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName);
            itemStartTime = itemView.findViewById(R.id.itemStartTime);
            itemEndTime = itemView.findViewById(R.id.itemEndTime);

            this.onNoteListener = onl;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getBindingAdapterPosition());
        }
    }
    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
