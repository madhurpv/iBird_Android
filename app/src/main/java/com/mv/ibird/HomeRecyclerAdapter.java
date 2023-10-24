package com.mv.ibird;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.HomeRecyclerViewHolder> {


    private List<String> titles;
    private List<Long> times;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    HomeRecyclerAdapter(Context context, List<String> titles, List<Long> times) {
        this.mInflater = LayoutInflater.from(context);
        this.titles = titles;
        this.times = times;
    }

    // inflates the row layout from xml when needed
    @Override
    public HomeRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.home_recyclerrow, parent, false);
        return new HomeRecyclerViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(HomeRecyclerViewHolder holder, int position) {
        String animal = titles.get(position);
        holder.myTextView.setText(animal);
        Long selectedTime = times.get(position);
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(selectedTime), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        holder.recyclerRowDate.setText(date.format(formatter));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return titles.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class HomeRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        TextView recyclerRowDate;

        HomeRecyclerViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.recyclerRowTitle);
            recyclerRowDate = itemView.findViewById(R.id.recyclerRowDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return titles.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
