package com.mv.ibird;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CurrentObservvationRecyclerAdapter extends RecyclerView.Adapter<CurrentObservvationRecyclerAdapter.CurrentObservationRecyclerViewHolder>{


    private final List<String> birdNames;
    private final List<Long> time;
    private final List<Integer> visibility;
    private final LayoutInflater mInflater;
    private CurrentObservvationRecyclerAdapter.ItemClickListener mClickListener;

    // data is passed into the constructor
    CurrentObservvationRecyclerAdapter(Context context, List<String> birdNames, List<Long> time, List<Integer> visibility) {
        this.mInflater = LayoutInflater.from(context);
        this.birdNames = birdNames;
        this.visibility = visibility;
        this.time = time;
    }

    // inflates the row layout from xml when needed
    @Override
    public CurrentObservvationRecyclerAdapter.CurrentObservationRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.currentobservation_recyclerrow, parent, false);
        return new CurrentObservvationRecyclerAdapter.CurrentObservationRecyclerViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(CurrentObservvationRecyclerAdapter.CurrentObservationRecyclerViewHolder holder, int position) {
        String animal = birdNames.get(position);
        holder.birdTextView.setText(animal);
        Long selectedTime = time.get(position);
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(selectedTime), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss   dd-MM-yyyy");
        holder.timeTextView.setText(date.format(formatter));
        if(visibility.get(position) == 1){
            holder.visibilityImageView.setImageResource(R.drawable.ic_baseline_visibility_24);
        }
        else if(visibility.get(position) == 2){
            holder.visibilityImageView.setImageResource(R.drawable.ic_baseline_volume_up_24);
        }
        else if(visibility.get(position) == 3){
            holder.visibilityImageView.setImageResource(R.drawable.ic_baseline_home_24);
        }
        else{
            holder.visibilityImageView.setImageResource(R.drawable.ic_baseline_question_mark_24);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return birdNames.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class CurrentObservationRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView birdTextView;
        TextView timeTextView;
        ImageView visibilityImageView;

        CurrentObservationRecyclerViewHolder(View itemView) {
            super(itemView);
            birdTextView = itemView.findViewById(R.id.currentObservationRecyclerBirdName);
            timeTextView = itemView.findViewById(R.id.currentObservationRecyclerTime);
            visibilityImageView = itemView.findViewById(R.id.currentObservationRecyclerVisibility);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onMyItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return birdNames.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(CurrentObservvationRecyclerAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onMyItemClick(View view, int position);
    }
    
}
