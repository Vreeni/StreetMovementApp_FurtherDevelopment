package com.example.vreeni.StreetMovementApp;

import java.util.ArrayList;

/**
 * interface used in queries to the database
 * returns an ArrayList of type LeaderboardEntry
 */
public interface FirebaseCallback_Leaderboard {

    void onQuerySuccess(ArrayList<LeaderboardEntry> list);

    void onFailure();
}
