package com.mv.ibird;

import java.util.ArrayList;

public class AllObservationsClass {

    ArrayList<CurrentObservationClass> currentObservationClasses = new ArrayList<>();

    public AllObservationsClass(){

    }

    public void addObservation(CurrentObservationClass currentObservationClass){
        this.currentObservationClasses.add(currentObservationClass);
    }

    public int getNoOfObservations(){
        return currentObservationClasses.size();
    }

    public ArrayList<String> getObservationsArray(){
        ArrayList<String> titles = new ArrayList<>();
        for(int i=0; i<currentObservationClasses.size(); i++){
            titles.add(currentObservationClasses.get(i).title);
        }
        return titles;
    }

    public ArrayList<Long> getObservationsTimesArray(){
        ArrayList<Long> titles = new ArrayList<>();
        for(int i=0; i<currentObservationClasses.size(); i++){
            titles.add(currentObservationClasses.get(i).firstObservationTime);
        }
        return titles;
    }

}
