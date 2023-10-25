package com.mv.ibird;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements HomeRecyclerAdapter.ItemClickListener {

    public static HomeRecyclerAdapter adapter; // Updated from CurrentObservation.java also
    FloatingActionButton addNewObservation;

    public static ArrayList<String> titles;    // Updated from CurrentObservation.java also
    public static ArrayList<Long> times;       // Updated from CurrentObservation.java also

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("allObservationsClass", "");
        AllObservationsClass allObservationsClassObj = gson.fromJson(json, AllObservationsClass.class);
        titles = allObservationsClassObj.getObservationsArray();
        times = allObservationsClassObj.getObservationsTimesArray();


        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HomeRecyclerAdapter(this, titles, times);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        addNewObservation = findViewById(R.id.floatingActionButton);
        addNewObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                // Create a new dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

                // Set the title and the message of the dialog
                builder.setTitle("Start New Observation");
                builder.setMessage("Enter the name of observation : ");

                // Create an input field and add it to the dialog
                EditText inputField = new EditText(HomeActivity.this);
                builder.setView(inputField);

                // Create a positive button for starting the observation
                builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the input text from the field
                        String inputText = inputField.getText().toString();

                        // Do something with the input text, such as starting an observation
                        //startObservation(inputText);
                        CurrentObservationClass currentObservationClass = new CurrentObservationClass(inputText);


                        String json = sharedPreferences.getString("allObservationsClass", "");
                        AllObservationsClass allObservationsClassObj = gson.fromJson(json, AllObservationsClass.class);
                        allObservationsClassObj.addObservation(currentObservationClass);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        String allObservationsClassJson = gson.toJson(allObservationsClassObj);
                        myEdit.putString("allObservationsClass", allObservationsClassJson);
                        myEdit.apply();


                        Intent intent = new Intent(HomeActivity.this, CurrentObservation.class);
                        intent.putExtra("Position", allObservationsClassObj.getNoOfObservations()-1);
                        startActivity(intent);



                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });

                // Create a negative button for canceling the dialog
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel the dialog
                        dialog.cancel();
                    }
                });

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();



            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HomeActivity.this, CurrentObservation.class);
        intent.putExtra("Position", position);
        startActivity(intent);
    }
}