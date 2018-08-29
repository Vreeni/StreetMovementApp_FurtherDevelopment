package com.example.vreeni.StreetMovementApp;

import java.util.ArrayList;

/**
 * interface used in queries to the Database
 * returns an ArrayList of type Exercise on query success
 */
public interface FirebaseCallback_Exercises {

    void onQuerySuccess(ArrayList<Exercise> list);

    void onFailure();
}
