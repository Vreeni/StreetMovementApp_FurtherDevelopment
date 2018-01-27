package com.example.vreeni.firebaseauthentication;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

import static com.example.vreeni.firebaseauthentication.User.AGE;
import static com.example.vreeni.firebaseauthentication.User.EMAIL;
import static com.example.vreeni.firebaseauthentication.User.FULLNAME;
import static com.example.vreeni.firebaseauthentication.User.NATIONALITY;
import static com.example.vreeni.firebaseauthentication.User.NICKNAME;
import static com.example.vreeni.firebaseauthentication.User.STATUS;
import static com.example.vreeni.firebaseauthentication.User.TAG;

public class UserProfileFragment extends Fragment implements View.OnClickListener {
    private Button btnEditProfile;

    private TextView txtProfileName;
    private TextView txtProfileEmail;
    private TextView txtProfileNickname;
    private TextView txtProfileAge;
    private TextView txtProfileNationality;
    private TextView txtProfileStatus;

    //get firestore database data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference usersDocRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //DATA FROM FIRESTORE
        displayFirestoreData();

        //DATA FROM REALTIME DATABASE
        //get and set standard information of curr user, made available through firebase
        //displayUserStandardDetails();
        //get and set extra information that the user has entered to the firebase database
        //displayUserExtraDetails();

        btnEditProfile = (Button) view.findViewById(R.id.edit_user_info);
        btnEditProfile.setOnClickListener(this);

        txtProfileName = (TextView) view.findViewById(R.id.profile_section_fullname);
        txtProfileEmail = (TextView) getView().findViewById(R.id.profile_section_email);
        txtProfileNickname = (TextView) getView().findViewById(R.id.profile_section_nickname);
        txtProfileAge = (TextView) getView().findViewById(R.id.profile_section_age);
        txtProfileNationality = (TextView) getView().findViewById(R.id.profile_section_nationality);
        txtProfileStatus = (TextView) getView().findViewById(R.id.profile_section_status);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        //if the button representing the "train now or create workout" fragment is clicked, create this fragment
        if (v.getId() == R.id.edit_user_info) {
            fragment = new EditUserProfileFragment();
        }
        if (fragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    //maybe create an interface that can then be implemented by both the user class, the user profile fragment and the edit user profile
    //realtime database updates
    public void displayFirestoreData() {
        if (usersDocRef != null) {
        }
            //this.getActivity makes sure the listener only works when in this FragmentActivity
        usersDocRef.addSnapshotListener(this.getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString(FULLNAME);
                    String email = documentSnapshot.getString(EMAIL);
                    String nickname = documentSnapshot.getString(NICKNAME);
                    String age = documentSnapshot.getString(AGE);
                    String nationality = documentSnapshot.getString(NATIONALITY);
                    String status = documentSnapshot.getString(STATUS);

                    //setting all the text views in the user profile
                    //TextView txtProfileName = (TextView) getView().findViewById(R.id.profile_section_fullname);
                    txtProfileName.setText(name);
                   // TextView txtProfileEmail = (TextView) getView().findViewById(R.id.profile_section_email);
                    txtProfileEmail.setText(email);
                    //TextView txtProfileNickname = (TextView) getView().findViewById(R.id.profile_section_nickname);
                    txtProfileNickname.setText(nickname);
                    //TextView txtProfileAge = (TextView) getView().findViewById(R.id.profile_section_age);
                    txtProfileAge.setText(age);
                    //TextView txtProfileNationality = (TextView) getView().findViewById(R.id.profile_section_nationality);
                    txtProfileNationality.setText(nationality);
                    //TextView txtProfileStatus = (TextView) getView().findViewById(R.id.profile_section_status);
                    txtProfileStatus.setText(status);
                } else if (e != null) {
                    Log.w(TAG, "An exception occured", e);
                }
            }
        });

    }


    public void displayUserStandardDetails() {
        FirebaseAuth user = FirebaseAuth.getInstance();

        //get user email from firebase api
        String email = user.getCurrentUser().getEmail();
        TextView txtProfileEmail = (TextView) getView().findViewById(R.id.profile_section_email);
        txtProfileEmail.setText(email);

        //get user name form firebase api
        String name = user.getCurrentUser().getDisplayName();
        TextView txtProfileName = (TextView) getView().findViewById(R.id.profile_section_fullname);
        txtProfileName.setText(name);

    }

    public void displayUserExtraDetails() {

        //check if user has entered a nickname
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = mDatabase.getRef();
        DatabaseReference singleUserRef = usersRef.getRef();
        mDatabase.addListenerForSingleValueEvent
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //if the currently logged user exists
                        if (dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(NICKNAME).exists()) {
                            //user has chosen a nick name
                            String nickname = dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("Nickname").getValue(String.class);
                            TextView txtProfileNickname = (TextView) getView().findViewById(R.id.profile_section_nickname);
                            txtProfileNickname.setText(nickname);
                        } else {
                            //no nickname yet
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        //check if user has entered age
        mDatabase.addListenerForSingleValueEvent
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //if the currently logged user exists
                        if (dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("Age").exists()) {
                            //user has set his age
                            String age = dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("Age").getValue(String.class);
                            TextView txtProfileAge = (TextView) getView().findViewById(R.id.profile_section_age);
                            txtProfileAge.setText(age);
                        } else {
                            //
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        //check if user has entered nationality
        mDatabase.addListenerForSingleValueEvent
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //if the currently logged user exists
                        if (dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("Nationality").exists()) {
                            //user has set a nationality
                            String nationality = dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("Nationality").getValue(String.class);
                            TextView txtProfileNationality = (TextView) getView().findViewById(R.id.profile_section_nationality);
                            txtProfileNationality.setText(nationality);
                        } else {
                            //
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });



    }

    //retrieving data from the main activity
    //leave this method for practice purpose, but not necessary, as the fragment can access all the firebase details itself
    /*public void displayUserInfos(View v, Bundle b) {
        String email = getArguments().getString("EMAIL");
        TextView txtProfileEmail = (TextView) getView().findViewById(R.id.profile_section_email);
        txtProfileEmail.setText(email);

        String name = getArguments().getString("NAME");
        TextView txtProfileName = (TextView) getView().findViewById(R.id.profile_section_fullname);
        txtProfileName.setText(name);
        TextView txtProfileUsername = (TextView) getView().findViewById(R.id.profile_section_nickname);
        txtProfileUsername.setText(nickname);
    }*/
}

