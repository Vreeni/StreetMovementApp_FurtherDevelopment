package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.Map;

/**
 * Fragment displaying a map with an overview of the locations, where a so called Street Movement challenge is offered
 */
public class Fragment_Training_StreetMovementChallenge extends Fragment {

    private static final String LOG_TAG = "Street Movement Chllng";

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private View mView;

    private ParkourPark parkourPark;
    private Object pkParkRef;

    private Tab_FragmentPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String activity;
    private String setting;

    public static Fragment_Training_StreetMovementChallenge newInstance(String act, String set) {
        final Bundle bundle = new Bundle();
        Fragment_Training_StreetMovementChallenge fragment = new Fragment_Training_StreetMovementChallenge();
        bundle.putString("Activity", act);
        bundle.putString("Setting", set);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activity = getArguments().getString("Activity");
            setting = "Outdoors";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_street_movement_challenges, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_streetmovementchallenge);
        tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs_streetmovementchallenge);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter = new Tab_FragmentPagerAdapter(this.getChildFragmentManager(), this.getActivity());

        //pass info to the respective tabs containing further fragments that are being displayed within this fragment
        Tab_Fragment_Training_StreetMovementChallenge_MapView mapViewTab = Tab_Fragment_Training_StreetMovementChallenge_MapView.newInstance(activity, setting);
        Tab_Fragment_Training_StreetMovementChallenge_ListView listViewTab = Tab_Fragment_Training_StreetMovementChallenge_ListView.newInstance(activity, setting);
        adapter.addFragment(listViewTab, "List");
        adapter.addFragment(mapViewTab, "View on map");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

//        backButton.setOnClickListener(click -> {
//            ((AppCompatActivity)getContext()).getSupportFragmentManager().popBackStack();
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    public ParkourPark getTrainingLocation(Context c) {
        FirebaseQuery_StreetMovementChallenge_ParkourPark query =
                new FirebaseQuery_StreetMovementChallenge_ParkourPark(pkParkRef);
        query.query(new FirebaseCallback_StreetMovementChallenge_ParkourPark() {
            @Override
            public void onQuerySuccess(ParkourPark pkPark) {
//                Log.d(LOG_TAG, "list of exercise references:" + listofexercisereferences);
                parkourPark = pkPark;
                Log.d(LOG_TAG, "parkour park: " + parkourPark);

//                addApprovedLocationMarkersOnMap(parkourPark);
//                RecyclerViewClickListener listener = (view, position) -> {
//                    Toast.makeText(getContext(), "Position " + position, Toast.LENGTH_SHORT).show();
//                };

//                adapter = new ItemList_Exercises_Adapter(p, c, listener);
//                recycler.setAdapter(adapter);
            }

            @Override
            public void onFailure() {
            }
        });
        return parkourPark;
    }

    public void loadImgWithGlide(String url, ImageView iv) {
        Glide
                .with(this)
                .load(url)
                .override(200, 200) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
                .into(iv);
    }

}