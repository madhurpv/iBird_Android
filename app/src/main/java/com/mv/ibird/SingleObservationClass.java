package com.mv.ibird;

public class SingleObservationClass {


    double latitude;
    double longitude;
    long time;
    String birdName;
    int visibility;    // visibility = 1 : Seen,       2: Heard,        3: Nest,        else: ?
    String details;

    public SingleObservationClass(String birdName, double latitude, double longitude, long time, int visibility){
        this.birdName = birdName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.visibility = visibility;
        this.details = "";
    }

    public SingleObservationClass(String birdName, double latitude, double longitude, long time, int visibility, String details){
        this.birdName = birdName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.visibility = visibility;
        this.details = details;
    }
}
