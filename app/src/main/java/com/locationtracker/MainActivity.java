package com.locationtracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.leaking.slideswitch.SlideSwitch;
import com.locationtracker.models.DurationModel;
import com.locationtracker.networkutils.RemoteApiCalls;
import com.locationtracker.service.BackgroundLocationService;
import com.locationtracker.utils.ConnectionDetector;
import com.locationtracker.utils.Constants;
import com.locationtracker.utils.GetLocationPoints;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GetLocationPoints.LocationPoints, SlideSwitch.SlideListener {


    double lat1;
    double lon1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;

    private static final int DEFAULT_ZOOM = 17;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    final static int REQUEST_LOCATION = 199;
    private LocationRequest locationRequest;

    GetLocationPoints.LocationPoints locationpoints;

    ArrayList<LatLng> markerPoints;
    Button start, stop;
    private LocationManager locationManager;
    ConnectionDetector connectionDetector;
    private SlideSwitch slide;
    private FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        markerPoints = new ArrayList<>();
        connectionDetector = new ConnectionDetector(this);

        locationpoints = this;

        frameLayout = (FrameLayout)findViewById(R.id.activity_main);
//        start = (Button) findViewById(R.id.start);
//        stop = (Button) findViewById(R.id.stop);

        slide = (SlideSwitch) findViewById(R.id.slide);
//        slide.setState(false);
        slide.setSlideListener(this);

        if (!BackgroundLocationService.IS_SERVICE_RUNNING) {
            slide.setState(false);

        } else {
            slide.setState(true);

        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(1 * 10 * 1000);
        locationRequest.setFastestInterval(1 * 1 * 1000);

        //Initializing googleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();


//        start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (mLocationPermissionGranted) {
//
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
//                            requestPermissions(new String[]{
//
//                                            Manifest.permission.ACCESS_FINE_LOCATION},
//                                    PERMISSIONS_MULTIPLE_REQUEST);
//                        } else
//
//                        {
//                            Intent service = new Intent(MainActivity.this, BackgroundLocationService.class);
//                            if (!BackgroundLocationService.IS_SERVICE_RUNNING) {
//                                service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
//                                BackgroundLocationService.IS_SERVICE_RUNNING = true;
//
//                            } else {
//                                service.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
//                                BackgroundLocationService.IS_SERVICE_RUNNING = false;
//
//
//                            }
//                            startService(service);
//
//                            displayCloseAlert();
//
//                        }
//
//                    } else {
//                        Intent service = new Intent(MainActivity.this, BackgroundLocationService.class);
//                        if (!BackgroundLocationService.IS_SERVICE_RUNNING) {
//                            service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
//                            BackgroundLocationService.IS_SERVICE_RUNNING = true;
//
//                        } else {
//                            service.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
//                            BackgroundLocationService.IS_SERVICE_RUNNING = false;
//
//                        }
//                        startService(service);
//
//                        displayCloseAlert();
//
//                    }
//                }
//            }
//        });
//
//        stop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent service = new Intent(MainActivity.this, BackgroundLocationService.class);
//
//                stopService(service);
//
//                Toast.makeText(MainActivity.this, "stop tracking", Toast.LENGTH_SHORT).show();
//
//                new GetLocationPoints(getApplicationContext(), locationpoints).execute();
//
//            }
//        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getDeviceLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("TAG", "Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        registerLocationUpdates();

        updateLocationUI();

    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();

        super.onStop();
    }


    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

             /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
//            if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(googleApiClient);
//            }

            // Set the map's camera position to the current location of the device.
//            if (mCameraPosition != null) {
//                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
//            } else
            if (mLastKnownLocation != null) {

                //Adding marker to map
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude())) //setting position
                        .draggable(true) //Making the marker draggable
                        .title("Current Location")); //Adding a title

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
            } else {
                Log.d(TAG, "Current location is null. Using defaults.");
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;

                    updateLocationUI();

                }
                return;

            }
        }
        updateLocationUI();


    }


    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMap != null) {

            getDeviceLocation();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
//                        finish();
                        enableLoc();
                        break;
                    }
                    case Activity.RESULT_OK: {
                        getDeviceLocation();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }

    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

//            if (mLocationPermissionGranted) {

            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(MainActivity.this)) {
                Toast.makeText(MainActivity.this, "Gps not enabled", Toast.LENGTH_SHORT).show();
                enableLoc();
            } else {
                Toast.makeText(MainActivity.this, "Gps already enabled", Toast.LENGTH_SHORT).show();


                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                getDeviceLocation();
            }

//            }

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }


    }


    private void enableLoc() {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        setLocationListener();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });

    }

    private void setLocationListener() {
        Log.d(TAG, "setLocationListener() " + locationRequest);


        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

                    /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
//            if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(googleApiClient);


            getDeviceLocation();
//            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }


    }


    void registerLocationUpdates() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        //initialize the location
        if (mLastKnownLocation != null) {


            LatLng userLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

            lat1 = mLastKnownLocation.getLatitude();
            lon1 = mLastKnownLocation.getLongitude();

            mMap.addMarker(new MarkerOptions()
                    .position(userLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title("Welcome ")
                    .snippet("Latitude:" + lat1 + ",Longitude:" + lon1)
            );

//            Log.v(TAG, "Lat1=" + lat1);
//            Log.v(TAG, "Long1=" + lon1);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 20), 1500, null);


        }
    }


    @Override
    public void getPoints(ArrayList<LatLng> points) {

        mMap.clear();

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(10);
        polylineOptions.addAll(points);
        mMap.addPolyline(polylineOptions);
        if (points.size() > 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 18));
        }

        LatLng origin = points.get(0);
        LatLng destination = points.get(points.size() - 1);

//        Log.d("MainActivity","origin = "+origin);
//        Log.d("MainActivity","destination = "+destination);

        if (connectionDetector.isConnectingToInternet()) {
//            mProgressbar.show("Loading...");
            new RemoteApiCalls.Builder().remoteApiCall(this).durationCall(String.valueOf(origin).substring(10, String.valueOf(origin).length() - 1), String.valueOf(destination).substring(10, String.valueOf(destination).length() - 1));

        } else {
            displayAlert();
        }
    }

    @Override
    public void getSpot(ArrayList<String> spots) {

    }


    @Override
    public void onDurationResult(boolean result, Response<DurationModel> code) {
        if (result) {
            String duration = code.body().rows.get(0).elements.get(0).duration.text;

//            Toast.makeText(this, "duration = " + duration, Toast.LENGTH_SHORT).show();


            Snackbar.make(frameLayout, "Duration = " + duration, Snackbar.LENGTH_LONG)
                    .setAction("", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show();
        }
    }

    @Override
    public void open() {
        if (mLocationPermissionGranted) {

            Toast.makeText(MainActivity.this, "start tracking", Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    requestPermissions(new String[]{

                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_MULTIPLE_REQUEST);
                } else

                {
                    Intent service = new Intent(MainActivity.this, BackgroundLocationService.class);
                    if (!BackgroundLocationService.IS_SERVICE_RUNNING) {
                        service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                        BackgroundLocationService.IS_SERVICE_RUNNING = true;

                    } else {
                        service.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                        BackgroundLocationService.IS_SERVICE_RUNNING = false;


                    }
                    startService(service);
//                    finish();
//                    displayCloseAlert();

                }

            } else {
                Intent service = new Intent(MainActivity.this, BackgroundLocationService.class);
                if (!BackgroundLocationService.IS_SERVICE_RUNNING) {
                    service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                    BackgroundLocationService.IS_SERVICE_RUNNING = true;

                } else {
                    service.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                    BackgroundLocationService.IS_SERVICE_RUNNING = false;

                }

                startService(service);
//                finish();
//                displayCloseAlert();

            }
        }
    }

    @Override
    public void close() {
        Intent service = new Intent(MainActivity.this, BackgroundLocationService.class);
        service.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        BackgroundLocationService.IS_SERVICE_RUNNING = false;

        stopService(service);
        if (BackgroundLocationService.IS_SERVICE_RUNNING) {
            Toast.makeText(MainActivity.this, "stop tracking", Toast.LENGTH_SHORT).show();
        }

        new GetLocationPoints(getApplicationContext(), locationpoints).execute();
    }
}
