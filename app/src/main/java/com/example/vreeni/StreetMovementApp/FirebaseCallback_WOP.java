package com.example.vreeni.StreetMovementApp;

/**
 * interface used in queries to the database
 * returns a VejstrupLevelGame object, a game specifically designed for the ParkourPark in Vejstrup
 * possibility to use this game format in other locations later on, once its rules and exercises are fully set up
 */
public interface FirebaseCallback_WOP {
    void onQuerySuccess(WOP wop);

    void onFailure();
}

