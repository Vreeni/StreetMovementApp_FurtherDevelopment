package com.example.vreeni.StreetMovementApp;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vreeni on 30/12/2017.
 */

/**
 * Class representing a user document in the database
 * => containing all the fields that are also listed in the database, so data from a database query can be converted to a workout object
 */
//possiblty make it implement parcelable interface as well
public class User {

    //FIRESTORE
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String name;
    private String email;
    private String nationality;
    private String nickname;
    private String status;
    private String age;
    private String loginMethod;


    private GeoPoint position;

    private long workoutsCompleted;
    private long warmupsSkipped;
    private long warmupsCompleted;


    static final String LOGINMETHOD = "loginMethod";
    static final String AGE = "age";
    static final String EMAIL = "email";
    static final String FULLNAME = "name";
    static final String NATIONALITY = "nationality";
    static final String NICKNAME = "nickname";
    static final String STATUS = "status";
    static final String WORKOUTSCOMPLETED = "workoutsCompleted";
    static final String WARMUPSSKIPPED = "warmupsSkipped";
    static final String WARMUPSCOMPLETED = "warmupsSkipped";
    static final String LISTOFHOMEWORKOUTS = "listOfHomeWorkouts";
    static final String LISTOFOUTDOORWORKOUTS = "listOfOutdoorWorkouts";
    static final String LISTOFMOVEMENTSPECIFICCHALLENGES = "listOfMovementSpecificChallenges";
    static final String LISTOFSTREETMOVEMENTCHALLENGES = "listOfStreetMovementChallenges";
    static final String POSITION = "position";
    static final String POSITIONLASTUPDATE = "positionLastUpdate";





    private ArrayList<Object> listOfHomeWorkouts = new ArrayList<>();
    private ArrayList<Object> listOfOutdoorWorkouts = new ArrayList<>();
    private ArrayList<Object> listOfMovSpecChallenges = new ArrayList<>();
    private ArrayList<Object> listOfSMChallenges = new ArrayList<>();



    public User() {}

    public GeoPoint getPosition() {
        return position;
    }

    public void setPosition(GeoPoint position) {
        this.position = position;
    }

    public String getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }

    public ArrayList<Object> getListOfHomeWorkouts() {
        return listOfHomeWorkouts;
    }

    public void setListOfHomeWorkouts(ArrayList<Object> listOfHomeWorkouts) {
        this.listOfHomeWorkouts = listOfHomeWorkouts;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    public ArrayList<Object> getListOfMovSpecChallenges() {
        return listOfMovSpecChallenges;
    }

    public void setListOfMovSpecChallenges(ArrayList<Object> listOfMovSpecChallenges) {
        this.listOfMovSpecChallenges = listOfMovSpecChallenges;
    }

    public ArrayList<Object> getListOfSMChallenges() {
        return listOfSMChallenges;
    }

    public void setListOfSMChallenges(ArrayList<Object> listOfSMChallenges) {
        this.listOfSMChallenges = listOfSMChallenges;
    }

    public ArrayList<Object> getListOfOutdoorWorkouts() {
        return listOfOutdoorWorkouts;
    }

    public void setListOfOutdoorWorkouts(ArrayList<Object> listOfOutdoorWorkouts) {
        this.listOfOutdoorWorkouts = listOfOutdoorWorkouts;
    }

    public long getWarmupsCompleted() {
        return warmupsCompleted;
    }

    public void setWarmupsCompleted(long warmupsCompleted) {
        this.warmupsCompleted = warmupsCompleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public long getWorkoutsCompleted() {
        return workoutsCompleted;
    }

    public void setWorkoutsCompleted(long workoutsCompleted) {
        this.workoutsCompleted = workoutsCompleted;
    }

    public long getWarmupsSkipped() {
        return warmupsSkipped;
    }

    public void setWarmupsSkipped(long warmupsSkipped) {
        this.warmupsSkipped = warmupsSkipped;
    }


}



