package com.example.vreeni.StreetMovementApp;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Tab displayed in the fragment that shows information on a specific location
 * shows an overview of the training location
 */
public class Tab_Overview_Fragment extends Fragment implements View.OnClickListener {
    private String TAG = "Overview Tab ";
    private long mLastClickTime = 0;
    private TextView tv_name;
    private TextView tv_description;
    private TextView tv_smc_description;
    private Button btn_inSpotViewTabTrainHere;
    private Button btn_inSpotViewTabShare;

    private String activity;
    private String setting;
    private ParkourPark pk;
    private Location mLastKnownLocation;
    private String name_SMC;


    public static Tab_Overview_Fragment newInstance(String act, String set, ParkourPark spot, Location mLastKnownLocation) {
        final Bundle bundle = new Bundle();
        Tab_Overview_Fragment fragment = new Tab_Overview_Fragment();
        bundle.putString("Activity", act);
        bundle.putString("Setting", set);
        bundle.putParcelable("TrainingLocation", spot);
        bundle.putParcelable("UserLocation", mLastKnownLocation);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activity = getArguments().getString("Activity");
            setting = getArguments().getString("Setting");
            pk = getArguments().getParcelable("TrainingLocation");
            mLastKnownLocation = getArguments().getParcelable("UserLocation");
            Log.d(TAG, "bundle info: " + getArguments());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_description, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_name = (TextView) view.findViewById(R.id.tv_parkourpark_name);
        tv_description = (TextView) view.findViewById(R.id.tv_parkourpark_description);
        tv_smc_description = (TextView) view.findViewById(R.id.tv_smc_description);
        btn_inSpotViewTabTrainHere = (Button) view.findViewById(R.id.btn_inSpotViewTabTrainHere);
        btn_inSpotViewTabShare = (Button) view.findViewById(R.id.btn_inSpotViewTabShareSpot);


    }

    @Override
    public void onStart() {
        super.onStart();


        tv_name.setText(pk.getName());
        tv_description.setText(pk.getDescription());

        if ((activity != null) && (activity.equals("Street Movement challenge"))) {
            try {
                getSMCInfo();
            } catch (FirebaseException e) {
                e.printStackTrace();
            }
            btn_inSpotViewTabTrainHere.setText("Go to challenge");
        } else {
        }
        btn_inSpotViewTabTrainHere.setOnClickListener(this);
        btn_inSpotViewTabShare.setOnClickListener(this);
        Log.d(TAG, "passed parkourpark - parkDescription " + pk.getName());

//        backButton.setOnClickListener(click -> {
//            ((AppCompatActivity)getContext()).getSupportFragmentManager().popBackStack();
//        });
    }

    public void getSMCInfo() throws FirebaseException {
        try {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final CollectionReference smcsRef = db.collection("StreetMovementChallenges");
            DocumentReference pkRef = db.collection("ParkourParks").document(pk.getName());

            //query to get all documents that both home workouts and suited for beginners
            com.google.firebase.firestore.Query query = smcsRef.whereEqualTo("location", pkRef);
            query.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            // Get the first document
                            DocumentSnapshot first = documentSnapshots.getDocuments().get(0);
                            name_SMC = first.getString("description_short");
                            tv_smc_description.setVisibility(View.VISIBLE);
                            tv_smc_description.setText(name_SMC);
                            final CollectionReference smcRef_singleChallenge = db.collection(first.toString());
                        }
                    });
            if (name_SMC == null) {
                throw new FirebaseException("Exception: Activity is not a Street Movement challenge.");
            }
        } catch (FirebaseException ex) {
            Log.d(TAG, "Exception: ", ex);
        }
        Log.d(activity, "executed");
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
        //make sure button is not clicked accidentally 2 times in a row
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if (v.getId() == R.id.btn_inSpotViewTabShareSpot) {
            //share link to spot on SoMe
            Location trainingLocation = new Location("provider");
            trainingLocation.setLatitude(pk.getCoordinates().getLatitude());
            trainingLocation.setLongitude(pk.getCoordinates().getLongitude());
            String maps_link_generic = "https://maps.google.com/?q=";
            String maps_link_coordinates = maps_link_generic + pk.getCoordinates().getLatitude() + "," + pk.getCoordinates().getLongitude();
            shareText(btn_inSpotViewTabShare, maps_link_coordinates);

        }
        if (v.getId() == R.id.btn_inSpotViewTabTrainHere) {
            //convert the parkour park's geolocation to a location object
            Location trainingLocation = new Location("provider");
            trainingLocation.setLatitude(pk.getCoordinates().getLatitude());
            trainingLocation.setLongitude(pk.getCoordinates().getLongitude());

            //if user is in a radius of 250m from the spot, then he can start a training
            //start outdoor training cycle => setting first, then type of activity, then details on that, level etc.
            if (activity != null) { //training flow already started from nav. drawer => training => choooseactivity etc
                // continue the normal training flow
                //if user is in a radius of 250m from the spot, then he can start a training
                if (mLastKnownLocation.distanceTo(trainingLocation) < 750) { //for testing just make it bigger or the other way round
                    if (activity.equals("Street Movement challenge")) {
                        //see the street movement challenge connected to the specific spot
                        Fragment_Training_SMC_WOPVejstrup vejstrup = Fragment_Training_SMC_WOPVejstrup.newInstance(activity, setting, pk);
                        ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, vejstrup, "vejstrup")
                                .addToBackStack("vejstrup")
                                .commit();
                    } else {
                        //get a workout or movement specific challenge
                        Fragment_Training_TrainNowORCreateTraining trainNowOrCreate = Fragment_Training_TrainNowORCreateTraining.newInstance(activity, setting, pk);
                        ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, trainNowOrCreate, "trainNowOrCreate")
                                .addToBackStack("trainNowOrCreate")
                                .commit();
                    }
                } else {
                    //else: navigate to the spot
                    Log.d(TAG, "go to the spot first" + mLastKnownLocation + ", " + mLastKnownLocation.distanceTo(trainingLocation));

                    Toast.makeText(getActivity().getApplicationContext(), "Go to the spot to start training", Toast.LENGTH_LONG).show();
                }

            } else {
                //no training flow initiated yet
                Log.d(TAG, "location infos " + mLastKnownLocation + ", " + trainingLocation);
                //if user is in a radius of 500m from the spot, then he can start a training
                if (mLastKnownLocation.distanceTo(trainingLocation) < 750) {
                    Fragment_Training_ChooseActivity chooseAct = Fragment_Training_ChooseActivity.newInstance(setting, pk);
                    ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, chooseAct, "chooseAct")
                            .addToBackStack("chooseAct")
                            .commit();
                }
                //else: navigate to the spot
                else {
                    //Do you want to use navigation services to get there?
//                    POPUP? DIAGLOG?
                    Toast.makeText(getActivity().getApplicationContext(), "Go to the spot to start training", Toast.LENGTH_LONG).show();

                    Log.d(TAG, "actvitiy: " + activity + "go to the spot first" + mLastKnownLocation + ", " + mLastKnownLocation.distanceTo(trainingLocation));
                }
            }

        }
    }

    public void shareText(View view, String link) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBodyText = "Let's train here: " + link;
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject/Title");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        startActivity(Intent.createChooser(intent, "Choose sharing method"));
    }

}