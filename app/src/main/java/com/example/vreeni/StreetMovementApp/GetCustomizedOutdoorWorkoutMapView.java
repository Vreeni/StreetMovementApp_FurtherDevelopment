package com.example.vreeni.StreetMovementApp;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by vreee on 8/02/2018.
 */

public class GetCustomizedOutdoorWorkoutMapView extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    private static final String LOG_TAG = "OutdoorWorkout MapView";

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private View mView;

    private CheckBox chckBxPk;
    private CheckBox chckBxCali;

    private List<Marker> listOfPkMarkers;
    private List<Marker> listOfCaliMarkers;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Streetmekka, Copenhagen) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(55.66208270000001, 12.540357099999937);
    private static final int DEFAULT_ZOOM = 13;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private Marker currentMarker;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_google_maps, container, false);

        mMapView = (MapView) mView.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);


        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            CameraPosition mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        mMapView.getMapAsync(this); //this is important

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) mView.findViewById(R.id.mapview);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        //display the checkboxes show parkour parks / show calisthenics park
        chckBxPk = (CheckBox) mView.findViewById(R.id.showPkParks);
        chckBxCali = (CheckBox) mView.findViewById(R.id.showCaliParks);



    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        listOfPkMarkers= new ArrayList<>();
        listOfCaliMarkers = new ArrayList<>();

        //set marker
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnInfoWindowClickListener(this);

        // Prompt the user for permission.
        getLocationPermission();
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
/*
  update information on firebase
*/
//        JasonHandler jh = new JasonHandler();
//        jh.retrieveFileFromResource();
//        //making changes to the park locations via this method
//        jh.updateFirestore();

        //download the locations of the parks from firestore and display them on the map
        getLocationsFromFirestoreToMap();

        //handle checkbox clicks
        if (chckBxPk !=null) {
            chckBxPk.setChecked(true);
            chckBxPk.setOnClickListener(v -> {
                if (chckBxPk.isChecked()) showPkSpots();
                else hidePkSpots();
            });
        }
        if (chckBxCali !=null) {
            chckBxCali.setChecked(true);
            chckBxCali.setOnClickListener(v -> {
                if (chckBxCali.isChecked()) showCaliSpots();
                else hideCaliSpots();
            });
        }

    }



    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mMapView.onSaveInstanceState(outState);
        if (mGoogleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mGoogleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this.getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                          mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        Log.d(LOG_TAG, "Current location null. Using defaults.");
                        Log.e(LOG_TAG, "Exception: %s", task.getException());
                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            } else {
                //permission to show location denied
                mGoogleMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
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
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mGoogleMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    public void getLocationsFromFirestoreToMap() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ParkourParks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document != null) {
//                                    Log.d(LOG_TAG, document.getId() + " => " + document.getData());

                                    ParkourPark queriedLocation = document.toObject(ParkourPark.class);
                                    queriedLocation.setDescription(document.getString("description (in Danish)"));


                                    //add the queried object to a list of parkour and/or calisthenics spots
                                    if (queriedLocation.isParkour()) {
                                        //show pk spots so that everything is loaded in in on map ready
                                        addMarkersOnMap(queriedLocation);
                                    }
                                    if (queriedLocation.isCalisthenics()){
                                        //show cali spots so that everything is loaded in in on map ready
                                        addMarkersOnMap(queriedLocation);
                                    }
                                }
                            }
                        } else {
                            Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (marker.equals(currentMarker)) {
            //do sth
            Log.d(LOG_TAG, "InfoWindow clicked: " + marker.getTitle());
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void showPkSpots() {
        //show parkour spots only
        for (Marker m : listOfPkMarkers) {
            m.setVisible(true);
        }
        //create class to store marker info?
    }

    public void showCaliSpots () {
        //show cali spots only
        for (Marker m : listOfCaliMarkers) {
            m.setVisible(true);
        }
    }

    public void hidePkSpots() {
        for (Marker m : listOfPkMarkers) {
            m.setVisible(false);
        }
    }

    public void hideCaliSpots() {
        for (Marker m : listOfCaliMarkers) {
            m.setVisible(false);
        }
    }

    public void addMarkersOnMap(ParkourPark queriedLocation) {
        GeoPoint gp = queriedLocation.getCoordinates();
        String name = queriedLocation.getName();
        String shortDescription = queriedLocation.getDescription();
        HashMap<String, Object> photoI = queriedLocation.getPhoto_0();
        String photoURL;
        if (photoI != null) {
            photoURL = (String) photoI.get("url");
        } else {
            //default photo
            photoURL = "http://map.gadeidraet.dk/content/uploads/2016/06/068eb118452253193acfc9a00cb5b8f9_frederiksbergroskildevej300x300.jpg";
        }
        Log.d(LOG_TAG, "photo url: " + photoURL);
        //show pk spots so that everything is loaded in in on map ready
        //define marker options and set the custom info window
        MarkerOptions markerOpt = new MarkerOptions();
        markerOpt.position(/*some location*/ new LatLng(gp.getLatitude(), gp.getLongitude()))
                .title(name + "_" +
                        "Description: " + shortDescription)
                .snippet(photoURL);
        //pass along the picture URL to the customInfoWindow
        GoogleMapsCustomInfoWindow adapter = new GoogleMapsCustomInfoWindow(getActivity(), photoURL);
        mGoogleMap.setInfoWindowAdapter(adapter);
        Marker m = mGoogleMap.addMarker(markerOpt);
        if (queriedLocation.isParkour()) {
            listOfPkMarkers.add(m);
        }
        if (queriedLocation.isCalisthenics()){
            listOfCaliMarkers.add(m);
        }
        m.showInfoWindow();
    }
}