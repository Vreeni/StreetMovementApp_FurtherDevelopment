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
 * class handling all queries to the database querying exercise objects
 * basis for the queries is the list of exercises that are stored in a workout / movement-specific challenge
 */
public class FirebaseQuery_Exercises {
    private static final String TAG = "FbQuery_Exercises";

    private Context context;
    private Exercise exercise;
    private ArrayList<Object> listofexerciseReferences;
    private ArrayList<Exercise> listofexercises;


    public FirebaseQuery_Exercises(ArrayList<Object> exercises) {
        this.listofexerciseReferences = exercises;
        listofexercises = new ArrayList<>();
    }

    public void query(FirebaseCallback_Exercises callback) {
        //data comes back
        try {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            for (Object exerciseRef : listofexerciseReferences) {
                DocumentReference ref = (DocumentReference) exerciseRef;
                ref
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "recyclerview_item_exercise: " + documentSnapshot);
                            exercise = documentSnapshot.toObject(Exercise.class);
                            listofexercises.add(exercise);
                            callback.onQuerySuccess(listofexercises);
                        } else {
                        }
                    }
                });

            }
            if (exercise == null) {
                throw new FirebaseException("Query could not be executed");
            }
        } catch (FirebaseException ex) {
            callback.onFailure();
        }

    }

//    void querySecond(){}
}




