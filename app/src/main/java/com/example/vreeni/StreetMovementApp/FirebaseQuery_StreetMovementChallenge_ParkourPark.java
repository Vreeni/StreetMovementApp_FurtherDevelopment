package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * class handling all queries to the database querying the parkour parks that are linked to a Street Movement challenge
 */
public class FirebaseQuery_StreetMovementChallenge_ParkourPark {
    private static final String TAG = "FbQuery_SMChallenge";

    private Context context;
    private ParkourPark parkourPark;
    private Object pkParkReference;
    private ArrayList<Exercise> listofexercises = new ArrayList<>();
    private String activity;


    public FirebaseQuery_StreetMovementChallenge_ParkourPark(Object parkourPark) {
//        this.context = context;
        this.pkParkReference = parkourPark;
    }

    public void query(FirebaseCallback_StreetMovementChallenge_ParkourPark callback) {
        //data comes back
        try {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference ref = (DocumentReference) pkParkReference;
            ref
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "pk park: " + documentSnapshot);
                        parkourPark = documentSnapshot.toObject(ParkourPark.class);
                        parkourPark.setDescription(documentSnapshot.getString("description (in Danish)"));
//                            String id = documentSnapshot.getId();
                        callback.onQuerySuccess(parkourPark);

                    } else {
                    }
                }
            });

            if (parkourPark == null) {
                throw new FirebaseException("Query could not be executed");
            }
        } catch (FirebaseException ex) {
            callback.onFailure();
        }

    }

//    void querySecond(){}


}
