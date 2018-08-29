package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * class handling all queries to the "Scores" collection on the database
 * returning a predefined number of 10 leaderboard entries with their document IDS being the email address of the respective user
 * a score object also contains a reference to the user document that is linked to this score
 * handling of another query to retrieve the corresponding user document
 */
public class FirebaseQuery_Leaderboard {
    private static final String TAG = "FbQuery_Score";

    private Context context;
    private LeaderboardEntry score;
    private int week;
    private String activity;


    public FirebaseQuery_Leaderboard(int week, String activityType) {
        this.week = week;
        this.activity = activityType;
//        this.context = context;
    }

    public void query(FirebaseCallback_Leaderboard callback) {
        //data comes back
        try {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final CollectionReference scoreQuery = db.collection("Scores");

            com.google.firebase.firestore.Query query =
//                    scoreQuery.whereEqualTo("week", week)
                    scoreQuery.orderBy(activity, Query.Direction.DESCENDING).limit(10);
            Log.d(TAG, activity);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot qSnap = task.getResult();
                        if (!qSnap.isEmpty()) {
                            Log.d(TAG, "qsnap not empty");
                            ArrayList<LeaderboardEntry> listOfScores = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()) {
                                LeaderboardEntry entry = doc.toObject(LeaderboardEntry.class);
                                entry.setUsername(doc.getId());
                                Log.d(TAG, entry.getUsername());
                                listOfScores.add(entry);
                                DocumentReference queriedScoreRef = scoreQuery.document(entry.getUsername());
                                queriedScoreRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document != null) {
                                                score = task.getResult().toObject(LeaderboardEntry.class);
                                                callback.onQuerySuccess(listOfScores);
                                                Log.d(TAG, "DocumentSnapshot data: " + listOfScores);
                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d("Query Data", "Data is not valid");
                        }
                    }
                }
            });
//            String x = "Returned data";
            if (score == null) {
                throw new FirebaseException("Query could not be executed");
            }
        } catch (FirebaseException ex) {
            callback.onFailure();
        }
        Log.d(TAG, "executed");
    }

//    void querySecond(){}
}


