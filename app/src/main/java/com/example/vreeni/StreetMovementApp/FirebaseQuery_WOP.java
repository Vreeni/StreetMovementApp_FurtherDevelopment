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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FirebaseQuery_WOP {
    private static final String LOG_TAG = "FbQuery_WOP";

    private String currUserEmail;
    private Context context;
    private WOP queriedWOP;

    public FirebaseQuery_WOP(String currUserEmail) {
        this.currUserEmail = currUserEmail;
    }

    public FirebaseQuery_WOP(String currUserEmail, Context context) {
        this.currUserEmail = currUserEmail;
        this.context = context;
    }

    public void query(FirebaseCallback_WOP callback) {
        //data comes back
        try {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final CollectionReference wopVejstrupRef = db.collection("WOP_Vejstrup");
            //query the document of the respective level
            wopVejstrupRef.whereEqualTo(FieldPath.of("participants", currUserEmail), true).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot qSnap = task.getResult();
                                String id;
                                if (!qSnap.isEmpty()) {
                                    DocumentSnapshot doc = qSnap.getDocuments().get(0);
                                    queriedWOP = doc.toObject(WOP.class);
                                    id = doc.getId();
                                    queriedWOP.setDocID(id);
                                    Log.d(LOG_TAG, "min xp" + queriedWOP.getMinimumXP() + " list: " + queriedWOP.getListOfExercises());
                                    callback.onQuerySuccess(queriedWOP);
                                } else {
                                    Log.d(LOG_TAG, "No such document");
                                }
                            } else {
                                Log.d(LOG_TAG, "get failed with ", task.getException());
                            }
                        }
                    });

//            String x = "Returned data";
            if (queriedWOP == null) {
                throw new FirebaseException("Query could not be executed");
            }
        } catch (FirebaseException ex) {
            callback.onFailure();
        }
        Log.d(LOG_TAG, "wop query executed");

    }
}
