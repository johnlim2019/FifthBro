package com.example.testbro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyBorrowAdapter extends RecyclerView.Adapter<MyBorrowAdapter.MyViewHolder> {

    Context context;
    ArrayList<ItemClass> items;
    OnNoteListener mOnNoteListener;

    public MyBorrowAdapter(Context ctx, ArrayList<ItemClass> item, OnNoteListener onl){
        this.context = ctx;
        this.items = item;
        this.mOnNoteListener = onl;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.borrow_item_row, parent, false);
        return new MyViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemClass itemClass = items.get(position);
        holder.itemAvail.setText(itemClass.retAvail());
        holder.itemName.setText(itemClass.getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView itemAvail, itemName;
        OnNoteListener onNoteListener;


        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            itemName = itemView.findViewById(R.id.borrowItemNameFb);
            itemAvail = itemView.findViewById(R.id.borrowItemAvailFb);
            this.onNoteListener = onNoteListener;
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
