package com.locationtracker;

/**
 * Created by rajesh on 29/4/17.
 */

public class LocationModel {

    //private variables
    int _id;
    String _location;


    // Empty constructor
    public LocationModel(){

    }
    // constructor
    public LocationModel(int id, String location){
        this._id = id;
        this._location = location;

    }

    // constructor
    public LocationModel(String location){
        this._location = location;

    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getName(){
        return this._location;
    }

    // setting name
    public void setName(String location){
        this._location = location;
    }


}