package com.locationtracker.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.locationtracker.Database;
import com.locationtracker.service.BackgroundLocationService;

import java.util.ArrayList;

/**
 * Created by rajesh on 30/4/17.
 */

public class GetLocationPoints extends AsyncTask<String,String,String> {
    String id;
    Context context;
    ArrayList<LatLng>points=new ArrayList<>();
    ArrayList<String> spot=new ArrayList<>();
    LocationPoints mLocationPoints;
    private Database database;
    private LatLng latLng;

    public GetLocationPoints( Context context,LocationPoints mLocationPoints)
    {
//        this.id=id;
        this.context=context;
        this.mLocationPoints=mLocationPoints;
    }
    @Override
    protected String doInBackground(String... strings) {


        database = new Database(context);
        for(int i = 0;i < database.getLocationCount();i++){

//            Log.d("lllll", "loc = " + database.getAllContacts().get(i).getName());

            String[] latlong =  database.getAllLocations().get(i).getName().split(",");
            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);
            latLng = new LatLng(latitude, longitude);
            points.add(latLng);

        }
        database.deleteAll();

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        if(!BackgroundLocationService.IS_SERVICE_RUNNING) {
//
//        }
//        Log.d("over","Yaaa"+points.size());

        if(points.size()>0) {
            mLocationPoints.getPoints(points);
        }

    }
    public interface LocationPoints
    {
        void getPoints(ArrayList<LatLng> points);
        void getSpot(ArrayList<String> spots);
    }
}