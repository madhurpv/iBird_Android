package com.mv.ibird;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

public class CurrentObservation extends AppCompatActivity implements CurrentObservvationRecyclerAdapter.ItemClickListener, AdapterView.OnItemSelectedListener {

    CurrentObservvationRecyclerAdapter adapter;
    TextView title;

    EditText birdNameEditText, previousTime, detailsEditText;
    Dialog popUpDialog;
    Spinner visibilitySpinner;
    Button createButton;
    Toolbar dropdownToolbarForMenuCurrentObservation;

    SharedPreferences sharedPreferences;
    Gson gson;


    int position;
    AllObservationsClass allObservationsClassObj;

    double latitude = 100000000.0;
    double longitude = 100000000.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_observation);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Bundle extras = getIntent().getExtras();

        start_getting_current_location();


        title = findViewById(R.id.titleOfCurrentObservation);

        position = extras.getInt("Position", -1);


        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        gson = new Gson();
        String json = sharedPreferences.getString("allObservationsClass", "");
        allObservationsClassObj = gson.fromJson(json, AllObservationsClass.class);
        ArrayList<String> titles = allObservationsClassObj.getObservationsArray();
        ArrayList<String> birdNames = allObservationsClassObj.currentObservationClasses.get(position).getBirdNamesArray();
        ArrayList<Long> times = allObservationsClassObj.currentObservationClasses.get(position).getTimeArray();
        ArrayList<Integer> visibilities = allObservationsClassObj.currentObservationClasses.get(position).getVisibilityArray();

        title.setText(String.valueOf(titles.get(position)));


        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.currentObservationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CurrentObservvationRecyclerAdapter(this, birdNames, times, visibilities);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        ArrayList<String> birdNamesArrayList;

        //SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        birdNamesArrayList = new ArrayList<>(sharedPreferences.getStringSet("BirdNames", new HashSet<>()));
        Collections.sort(birdNamesArrayList);
//        ArrayList<String> birdNamesArrayList;
        //birdNamesArrayList=new ArrayList<>();

        /*// set value in array list
        birdNamesArrayList.add("DSA Self Paced");
        birdNamesArrayList.add("Complete Interview Prep");
        birdNamesArrayList.add("Amazon SDE Test Series");
        birdNamesArrayList.add("Compiler Design");
        birdNamesArrayList.add("Git & Github");
        birdNamesArrayList.add("Python foundation");
        birdNamesArrayList.add("Operating systems");
        birdNamesArrayList.add("Theory of Computation");*/

        birdNameEditText = findViewById(R.id.birdNameEditText);
        birdNameEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popUpDialog = new Dialog(CurrentObservation.this);

                // set custom dialog
                popUpDialog.setContentView(R.layout.dialog_searchable_spinner_current_observation);

                // set custom height and width
                popUpDialog.getWindow().setLayout(800, 1400);

                // set transparent background
                popUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // show dialog
                popUpDialog.show();

                // Initialize and assign variable
                EditText editText = popUpDialog.findViewById(R.id.edit_text);
                ListView listView = popUpDialog.findViewById(R.id.list_view);

                // Initialize array adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<>(CurrentObservation.this, android.R.layout.simple_list_item_1, birdNamesArrayList);

                // set adapter
                listView.setAdapter(adapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        birdNameEditText.setText(adapter.getItem(position));

                        // Dismiss dialog
                        popUpDialog.dismiss();
                    }
                });
                return false;
            }
        });


        previousTime = findViewById(R.id.previousTimeEditText);
        detailsEditText = findViewById(R.id.detailsEditText);


        SimpleImageArrayAdapter imageAdapter = new SimpleImageArrayAdapter(this,
                new Integer[]{R.drawable.ic_baseline_visibility_24, R.drawable.ic_baseline_volume_up_24, R.drawable.ic_baseline_home_24, R.drawable.ic_baseline_question_mark_24});
        visibilitySpinner = findViewById(R.id.visiblitySpinner);
        visibilitySpinner.setAdapter(imageAdapter);

        createButton = findViewById(R.id.createButtonCurrentObservation);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (birdNamesArrayList.contains(birdNameEditText.getText().toString()) == false) {
                    birdNamesArrayList.add(birdNameEditText.getText().toString());
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    HashSet<String> birdsSet = new HashSet<>(birdNamesArrayList);
                    myEdit.putStringSet("BirdNames", birdsSet);
                    myEdit.apply();
                }

/*                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(CurrentObservation.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CurrentObservation.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(CurrentObservation.this, "Enable Location Permission", Toast.LENGTH_SHORT).show();
                    return;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Toast.makeText(CurrentObservation.this, "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude(), Toast.LENGTH_LONG).show();*/
                //get_current_location();
                if (ActivityCompat.checkSelfPermission(CurrentObservation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CurrentObservation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                LocationServices.getFusedLocationProviderClient(CurrentObservation.this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //TODO: UI updates.
                        //Toast.makeText(CurrentObservation.this, "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude(), Toast.LENGTH_LONG).show();

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        String birdName = birdNameEditText.getText().toString();
                        long timeInMilliSec = System.currentTimeMillis() - Integer.parseInt(previousTime.getText().toString())* 60000L;
                        int visibility = visibilitySpinner.getSelectedItemPosition()+1;
                        //Toast.makeText(CurrentObservation.this, "Selected at : " + visibility, Toast.LENGTH_SHORT).show();

                        // Update in recycleview
                        birdNames.add(birdName);
                        times.add(timeInMilliSec);
                        visibilities.add(visibility);
                        adapter.notifyDataSetChanged();



                        //Update in sharedpreferences
                        SingleObservationClass newSingleObservationClass;
                        if(detailsEditText.getText().toString().equals("")){
                            newSingleObservationClass = new SingleObservationClass(birdName, latitude, longitude, timeInMilliSec, visibility);
                        }
                        else{
                            newSingleObservationClass = new SingleObservationClass(birdName, latitude, longitude, timeInMilliSec, visibility, detailsEditText.getText().toString());
                        }
                        String json = sharedPreferences.getString("allObservationsClass", "");
                        AllObservationsClass allObservationsClassObj = gson.fromJson(json, AllObservationsClass.class);
                        allObservationsClassObj.currentObservationClasses.get(position).addObservation(newSingleObservationClass);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        String allObservationsClassJson = gson.toJson(allObservationsClassObj);
                        myEdit.putString("allObservationsClass", allObservationsClassJson);
                        myEdit.apply();


                    }
                });


            }
        });

        dropdownToolbarForMenuCurrentObservation = findViewById(R.id.dropdownToolbarForMenuCurrentObservation);
        setSupportActionBar(dropdownToolbarForMenuCurrentObservation);
    }


    @Override
    public void onMyItemClick(View view, int position) {
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),
                "Position : " + position,
                Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_currentobservation, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.exportObservation:
                //Toast.makeText(getApplicationContext(),"Export Observation Selected",Toast.LENGTH_LONG).show();
                export_observation();
                return true;
            case R.id.deleteObservation:
                //Toast.makeText(getApplicationContext(),"Item 2 Selected",Toast.LENGTH_LONG).show();

                // Create a new dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(CurrentObservation.this);

                // Set the title and the message of the dialog
                builder.setTitle("Delete");
                builder.setMessage("Do you really want to delete the observation?");


                // Create a positive button for starting the observation
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        allObservationsClassObj.currentObservationClasses.remove(position);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        String allObservationsClassJson = gson.toJson(allObservationsClassObj);
                        myEdit.putString("allObservationsClass", allObservationsClassJson);
                        myEdit.apply();

                        // Dismiss the dialog
                        dialog.dismiss();

                        HomeActivity.titles.clear();
                        HomeActivity.titles.addAll(allObservationsClassObj.getObservationsArray());
                        //HomeActivity.titles = allObservationsClassObj.getObservationsArray();
                        HomeActivity.times.clear();
                        HomeActivity.times.addAll(allObservationsClassObj.getObservationsTimesArray());
                        //HomeActivity.times = allObservationsClassObj.getObservationsTimesArray();
                        HomeActivity.adapter.notifyDataSetChanged();


                        finish();
                        Toast.makeText(CurrentObservation.this, "Observation Deleted!", Toast.LENGTH_SHORT).show();
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

                return true;
            case R.id.item3:
                Toast.makeText(getApplicationContext(),"Item 3 Selected",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void export_observation() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");
        Date now = new Date();


        try
        {

            String title = allObservationsClassObj.currentObservationClasses.get(position).title;
            LocalDateTime observationDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(allObservationsClassObj.currentObservationClasses.get(position).firstObservationTime), ZoneId.systemDefault());
            DateTimeFormatter observationTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String observationDateString = observationDate.format(observationTimeFormatter);
            String fileName = title + "_" + observationDateString + ".txt";//like 2016_01_12.txt


            File root = new File(Environment.getExternalStorageDirectory()+File.separator+"iBird Observations", "Exports");
            //File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists())
            {
                root.mkdirs();
            }
            File gpxfile = new File(root, fileName);


            FileWriter writer = new FileWriter(gpxfile,true);
            //writer.append("Hello123"+"\n\n");
            writer.append("Title : ").append(title).append("\n");
            writer.append("Date : ").append(observationDateString).append("\n\n\n\n");
            ArrayList<SingleObservationClass> listOfObservations = allObservationsClassObj.currentObservationClasses.get(position).listOfObservations;
            for(int i=0; i<allObservationsClassObj.currentObservationClasses.get(position).getBirdNamesArray().size(); i++){
                LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(listOfObservations.get(i).time), ZoneId.systemDefault());
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss   dd-MM-yyyy");
                writer.append(date.format(timeFormatter)).append("\n");
                writer.append("Bird : ").append(listOfObservations.get(i).birdName).append("\n");
                if(listOfObservations.get(i).visibility == 1){
                    writer.append("Visibility : Seen\n");
                }
                else if(listOfObservations.get(i).visibility == 2){
                    writer.append("Visibility : Heard\n");
                }
                else if(listOfObservations.get(i).visibility == 3){
                    writer.append("Visibility : Nest\n");
                }
                else{
                    writer.append("Visibility : Unknown\n");
                }
                writer.append("Latitude : ").append(String.valueOf(listOfObservations.get(i).latitude)).append("    ");
                writer.append("Longitude : ").append(String.valueOf(listOfObservations.get(i).longitude)).append("\n");
                writer.append("Details : ").append(listOfObservations.get(i).details).append("\n");
                writer.append("\n\n\n");
            }
            writer.flush();
            writer.close();
            Toast.makeText(this, "Data has been written to Report File", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }


    private void start_getting_current_location() {
        /*// Check if the app has location permission
        int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

// If the permission is granted, proceed with the location request
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Create a LocationRequest object
            LastLocationRequest mLocationRequest = LastLocationRequest.;
            mLocationRequest.setInterval(60000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // Create a LocationCallback object
            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            //Log.d("QWER", "2 " + String.valueOf(location.getLatitude()));
                            //mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("You are here!"));
                            //live_location = new LatLng(location.getLatitude(), location.getLongitude());
                            Toast.makeText(CurrentObservation.this, "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            };

            // Get the LocationManager instance
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Check if the location services are enabled
            boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            // If the location services are not enabled, show a dialog to enable them
            if ((!gps_enabled && !network_enabled)) {
                // notify user
                new AlertDialog.Builder(this)
                        .setMessage("Location services are not enabled. Please enable them to continue.")
                        .setPositiveButton("Open Location Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                // If the location services are enabled, request location updates
                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());  // TODO: Solve this error. Btw code works just fine, ignore the error
                mFusedLocationClient.getLastLocation(mLocationRequest);
            }
        } else {
            // If the permission is not granted, request it from the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }*/

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                        //Toast.makeText(CurrentObservation.this, "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
    }




}