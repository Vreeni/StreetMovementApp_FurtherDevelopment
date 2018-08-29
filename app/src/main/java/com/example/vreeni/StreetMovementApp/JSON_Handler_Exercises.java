package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class JSON_Handler_Exercises {
    private static final String LOG_TAG = "JSON_ExerciseLibrary";
    private Context context = getApplicationContext();
    private List<Exercise> listOfExercises;

    public JSON_Handler_Exercises() {
    }

    /**
     * JSON file is saved in the assets folder of the application and read into a String
     *
     * @return file content is returned as a String
     */

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = context.getAssets().open("exerciseLibrary.json");
            Log.d(LOG_TAG, "found json file");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    /**
     * handling the extraction of the desired information from the file
     * file consists of JSONObjects and JSONArrays that can both be understood and accessed as key value pairs
     * try:
     * 1. desired data (= parkour park locations) is part of the array "pins"
     * 2. desired data to be saved in a list of type ParkourPark
     * 3. looping through the array of pins and creating of a new JSONObject for each pin as well as a new JSONArray for each pin containing the pin categories on which this pin can be categorized based on
     * 4. looping through the array of pinCats and creating a new ParkourPark object whenever the categories for the respective pin object matches either "parkour" or "calisthenics" => set boolean values of ParkourPark objects for Parkour / Calisthenics
     * 5. creating JSONArray "photos" for each pin and a new list of type PhotoData to store the information of each photo in a list of photo objects for each pin
     * 6. get information on latitude and longitude ("latitude", "longitude") from the JSON String; format values (one value contains 2 commas) before parsing Strings to doubles; set double values for the ParkourPark object
     * 7. get information on source ("permalink") and set value for the ParkourPark object
     * 8. get information on name/title ("title", if null: "address") and set value for ParkourPark object
     * 9. get information on description ("description"9 and set value for ParkourPark object
     * 10. add ParkourPark object with newly set fields to the list of ParkourPark objects, if list doesnt contain this object already (.contains based on overriden .equals and hashcode methods in ParkourPark class)
     * catch: exceptions
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void retrieveFileFromResource() {
        //1.
        try {
            JSONArray arr = new JSONArray(loadJSONFromAsset());
            Log.d(LOG_TAG, "loading from file" + arr);

            //2.
            listOfExercises = new ArrayList<>();

            //3. loop through the array of exercises
            Log.d(LOG_TAG, "looping thru array" + arr.length());
            for (int i = 0; i < arr.length(); i++) {
//                Log.d(LOG_TAG, "arr" + arr.get(i));
                JSONObject exerciseObject = arr.getJSONObject(i);
                Log.d(LOG_TAG, "exerciseObject" + exerciseObject);

                Exercise exercise = new Exercise();

                //setting the fields, now update them to firebase
                Log.d(LOG_TAG, "exID" + exerciseObject.getString("exerciseID"));

                if (!exerciseObject.getString("exerciseID").equals("")) {
                    exercise.setExerciseID(exerciseObject.getString("exerciseID"));
                } else {
                    continue;
                }
                if (!exerciseObject.getString("name").equals("")) {
                    exercise.setName(exerciseObject.getString("name"));
                } else continue;
                if (!exerciseObject.getString("description").equals("")) {
                    exercise.setDescription(exerciseObject.getString("description"));
                } else exercise.setDescription("");
                if (!exerciseObject.getString("category").equals("")) {
                    exercise.setCategory(exerciseObject.getString("category"));
                } else exercise.setCategory("");
                if (!exerciseObject.getString("repetitions").equals("")) {
                    exercise.setRepetitions(exerciseObject.getString("repetitions"));
                } else exercise.setRepetitions("");
                if (!exerciseObject.getString("sets").equals("")) {
                    exercise.setSets(exerciseObject.getInt("sets"));
                } else exercise.setSets(1);
                if (!exerciseObject.getString("image").equals("")) {
                    exercise.setImage(exerciseObject.getString("image"));
                } else exercise.setImage("");
                if (!exerciseObject.getString("video").equals("")) {
                    exercise.setVideo(exerciseObject.getString("video"));
                } else exercise.setVideo("");
                if (!exerciseObject.getString("xp").equals("")) {
                    exercise.setXp(exerciseObject.getInt("xp"));
                } else exercise.setXp(1);
                if (!exerciseObject.getString("workout").equals("")) {
                    exercise.setWorkout(exerciseObject.getBoolean("workout"));
                } else exercise.setWorkout(false);
                if (!exerciseObject.getString("movementSpecificChallenge").equals("")) {
                    exercise.setMovementSpecificChallenge(exerciseObject.getBoolean("movementSpecificChallenge"));
                } else exercise.setMovementSpecificChallenge(false);
                if (!exerciseObject.getString("streetMovementChallenge").equals("")) {
                    exercise.setStreetMovementChallenge(exerciseObject.getBoolean("streetMovementChallenge"));
                } else exercise.setStreetMovementChallenge(false);
                if (!exerciseObject.getString("singlePlayer").equals("")) {
                    exercise.setSinglePlayer(exerciseObject.getBoolean("singlePlayer"));
                } else exercise.setSinglePlayer(false);
                if (!exerciseObject.getString("multiPlayer").equals("")) {
                    exercise.setMultiPlayer(exerciseObject.getBoolean("multiPlayer"));
                } else exercise.setMultiPlayer(false);
                //end of setting the fields, now update them to firebase

                listOfExercises.add(exercise);
                Log.d(LOG_TAG, "list of exercises:" + listOfExercises.size());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * updates to firestore database always require a reference to the database and a hashmap containing the key value pairs that are supposed to be written to the database
     * db = Reference to database
     * pkRef = DocumentReference created for each ParkourPark object in the list of park locations (if the reference doesnt exist already, it will be created based on the name of the park)
     * creating HashMap and adding key value pairs (key names should be the same as the fields of the object for easier later retrieval)
     * => here, information is added as String-String, String-Geopoint or String-Object
     * update Documents and Document fields in database based on data in hashmap
     */
    public void updateFirestore() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (Exercise e : listOfExercises) {
            DocumentReference exRef = db.collection("Exercises").document(e.getExerciseID());

            HashMap<String, Object> data = new HashMap<>();

            data.put("exerciseID", e.getExerciseID());
            data.put("name", e.getName());
            data.put("description", e.getDescription());
            data.put("category", e.getCategory());
            data.put("repetitions", e.getRepetitions());
            data.put("sets", e.getSets());
            data.put("image", e.getImage());
            data.put("video", e.getVideo());
            data.put("xp", e.getXp());
            data.put("workout", e.isWorkout());
            data.put("movementSpecificChallenge", e.isMovementSpecificChallenge());
            data.put("streetMovementChallenge", e.isStreetMovementChallenge());
            data.put("singlePlayer", e.isSinglePlayer());
            data.put("multiPlayer", e.isMultiPlayer());

            exRef
                    .set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(LOG_TAG, "New Document has been saved: " + exRef.getId());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(LOG_TAG, "New Document could not be saved");
                }
            });
        }
    }


    /**
     * formatting the description strings by removing unwanted characters
     *
     * @param description unformatted input String with potentially unwanted characters
     * @return formatted String
     */
    public String formatDescription(String description) {
        //formatting the description strings for irregularities
        String item1 = "<p class=\"p1\">";
        String item2 = "</a>";
        String item3 = "</p>";
        String item4 = "<a target=";
        if (description.contains(item1)) {
            description = description.replace(item1, "");
        }
        if (description.contains(item2)) {
            description = description.replace(item2, "");
        }
        if (description.contains(item3)) {
            description = description.replace(item3, "");
        }
        if (description.contains(item4)) {
            description = description.split(item4)[0];
        }
        return description;
    }
}
