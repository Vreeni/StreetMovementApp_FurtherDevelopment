package com.example.vreeni.StreetMovementApp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Fragment displaying account settings such as privacy settings and notifications
 * yet to be implemented
 */
public class Fragment_Settings extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = "Settings";

    private Button btn_addGeofences;
    private Button btn_removeGeofences;


    /**
     * Constructor that can hold an activity's setting and specific training location, when the training flow was initiated from the map
     * setting and specific location will be null if started from the training function in the navigation drawer
     *
     * @param
     * @param
     * @return
     */
    public static Fragment_Settings newInstance() {
        final Bundle bundle = new Bundle(); //to pass arguments to the next fragment
        Fragment_Settings fragment = new Fragment_Settings();
        //put sth
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //do sth
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_addGeofences = (Button) view.findViewById(R.id.add_geofences_button);

        btn_removeGeofences = (Button) view.findViewById(R.id.remove_geofences_button);
    }


    @Override
    public void onStart() {
        super.onStart();
        btn_addGeofences.setOnClickListener(this);
        btn_removeGeofences.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        //if the button representing the "Workout" fragment is clicked, create this fragment
        if (v.getId() == R.id.add_geofences_button) {


        }
        //if the button representing the "Movement Specific Challenge" fragment is clicked, create this fragment
        if (v.getId() == R.id.remove_geofences_button) {


        }
    }
}





