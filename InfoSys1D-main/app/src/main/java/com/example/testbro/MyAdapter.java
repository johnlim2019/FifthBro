package com.example.testbro;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    OnNoteListener mOnNoteListener;
    ArrayList<ItemClass> items;

    public MyAdapter(Context ctx, ArrayList<ItemClass> item, OnNoteListener mOnNoteListener){
        context = ctx;
        items = item;
        this.mOnNoteListener = mOnNoteListener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
        return new MyViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemClass itemClass = items.get(position);
        holder.itemAvail.setText(itemClass.retAvail());
        holder.itemName.setText(itemClass.getName());
        holder.item = itemClass;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemAvail, itemName;
        OnNoteListener onNoteListener;
        ItemClass item;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            itemAvail = itemView.findViewById(R.id.avail);
            itemName = itemView.findViewById(R.id.name);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);

            itemView.findViewById(R.id.generateQr).setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Log.d("item name", item.getItemID());
                    Intent i =  new Intent(v.getContext(), ActivityQRGen.class);
                    i.putExtra("item", item.getItemID());
                    v.getContext().startActivity(i);
                }
            });
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
