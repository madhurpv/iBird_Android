package com.mv.ibird;

import java.util.ArrayList;
import java.util.Collections;

public class CurrentObservationClass {

    long firstObservationTime;
    String title;
    ArrayList<SingleObservationClass> listOfObservations = new ArrayList<>();

    public ArrayList<SingleObservationClass> getListOfObservations() {
        return listOfObservations;
    }

    public void addObservation(SingleObservationClass singleObservationClass){
        if(listOfObservations == null || listOfObservations.isEmpty()){
            firstObservationTime = singleObservationClass.time;
        }
        listOfObservations.add(singleObservationClass);
        listOfObservations.sort((o1, o2) -> (int) (o1.time - o2.time));
    }

    public CurrentObservationClass(String title){
        this.title = title;
        this.firstObservationTime = System.currentTimeMillis();
    }

    public ArrayList<String> getBirdNamesArray(){
        ArrayList<String> birdNamesArray = new ArrayList<>();
        for(int i=0; i<listOfObservations.size(); i++){
            birdNamesArray.add(listOfObservations.get(i).birdName);
        }
        return birdNamesArray;
    }

    public ArrayList<Integer> getVisibilityArray(){
        ArrayList<Integer> visibilityArray = new ArrayList<>();
        for(int i=0; i<listOfObservations.size(); i++){
            visibilityArray.add(listOfObservations.get(i).visibility);
        }
        return visibilityArray;
    }

    public ArrayList<Long> getTimeArray(){
        ArrayList<Long> timeArray = new ArrayList<>();
        for(int i=0; i<listOfObservations.size(); i++){
            timeArray.add(listOfObservations.get(i).time);
        }
        return timeArray;
    }
}
