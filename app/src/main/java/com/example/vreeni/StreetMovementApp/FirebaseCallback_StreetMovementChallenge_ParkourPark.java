package com.example.vreeni.StreetMovementApp;

import java.util.ArrayList;


/**
 * interface used in queries to the database
 * returns a ParkourPark object, providing information on a specific training location
 */
public interface FirebaseCallback_StreetMovementChallenge_ParkourPark {

    void onQuerySuccess(ParkourPark park);

    void onFailure();
}
