package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.supercharge.shimmerlayout.ShimmerLayout;

/**
 * class handling the setup of the leaderboard
 * includes a recyclerview displaying a maximum of
 */
public class Fragment_Leaderboard extends Fragment implements AdapterView.OnItemSelectedListener {
    private String TAG = "leaderboard";

    //3 global variables handling the skeleton screens
    public LinearLayout skeletonLayout;
    public ShimmerLayout shimmer;
    public LayoutInflater inflater;

    private TextView tv;

    private RecyclerView recycler;
    private RecyclerView.LayoutManager manager;
    private ItemList_Leaderboard_Adapter adapter;
    private ArrayList<LeaderboardEntry> list = new ArrayList<>();

    private Context context;

    private Spinner spinner;

    public static Fragment_Leaderboard newInstance(Context context) {
        final Bundle bundle = new Bundle();
        Fragment_Leaderboard fragment = new Fragment_Leaderboard();
//        bundle.putString("Context", context);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d(TAG, "bundle info: " + getArguments());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        skeletonLayout = view.findViewById(R.id.skeletonLayout);
        shimmer = view.findViewById(R.id.shimmerSkeleton);
        this.inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        recycler = view.findViewById(R.id.leaderboard_recyclerView);
        recycler.setHasFixedSize(true);
        manager = new GridLayoutManager(this.getContext(), 1, GridLayoutManager.VERTICAL, false);
//        recycler.setLayoutManager(manager);

        int height = 2000; //get height
        ViewGroup.LayoutParams params_new = recycler.getLayoutParams();
        params_new.height = height;
        recycler.setLayoutParams(params_new);
        recycler.setLayoutManager(new LinearLayoutManager(context));

        //create list of type object, populate list with the score entries from firebase

        tv = (TextView) view.findViewById(R.id.leaderboard_entry_name);

        // Spinner element
        spinner = (Spinner) view.findViewById(R.id.leaderboard_spinner);

    }


    @Override
    public void onStart() {
        super.onStart();

        context = this.getContext();

        showSkeleton(true);

        ((MainActivity) getActivity()).showBackButton(false);


        list = getScores_AllActivities(this.getContext());

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("All activities");
        categories.add("Workouts");
        categories.add("Movement specific challenges");
        categories.add("Street Movement challenges");

        // Creating adapter for spinner
        // Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, categories);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);

        // Drop down layout style - list view with radio button
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(spinnerArrayAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public ArrayList<LeaderboardEntry> getScores_AllActivities(Context c) {
        Log.d(TAG, "getting scores");
        FirebaseQuery_Leaderboard query = new FirebaseQuery_Leaderboard(1, "nrOfActivities_total");
        query.query(new FirebaseCallback_Leaderboard() {
            @Override
            public void onQuerySuccess(ArrayList<LeaderboardEntry> scores) {
//                for (LeaderboardEntry e : scores) {
//                    int totalActivities = e.getNrOfActivities_total();
//                }
                animateReplaceSkeleton(recycler);

                Log.d(TAG, "list of scores:" + scores);
                list = scores;
                adapter = new ItemList_Leaderboard_Adapter(list, c);
                recycler.setAdapter(adapter);
            }

            @Override
            public void onFailure() {

            }
        });
        return list;
    }

    public ArrayList<LeaderboardEntry> getScores_AllWorkouts(Context c) {
        Log.d(TAG, "getting scores");
        //query based on week number and type of activity
        FirebaseQuery_Leaderboard query = new FirebaseQuery_Leaderboard(1, "nrOfWorkouts_total");
        query.query(new FirebaseCallback_Leaderboard() {
            @Override
            public void onQuerySuccess(ArrayList<LeaderboardEntry> scores) {
                Log.d(TAG, "list of scores:" + scores);
                list = scores;
                adapter = new ItemList_Leaderboard_Adapter(list, c);
                recycler.setAdapter(adapter);
            }

            @Override
            public void onFailure() {

            }
        });
        return list;
    }

    public ArrayList<LeaderboardEntry> getScores_AllMovementSpecificChallenges(Context c) {
        Log.d(TAG, "getting scores");
        //query based on week number and type of activity
        FirebaseQuery_Leaderboard query = new FirebaseQuery_Leaderboard(1, "nrOfMovementSpecificChallenges_total");
        query.query(new FirebaseCallback_Leaderboard() {
            @Override
            public void onQuerySuccess(ArrayList<LeaderboardEntry> scores) {
                Log.d(TAG, "list of scores:" + scores);
                list = scores;
                adapter = new ItemList_Leaderboard_Adapter(list, c);
                recycler.setAdapter(adapter);
            }

            @Override
            public void onFailure() {

            }
        });
        return list;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        if (position == 0) {
            //all activities
            list = getScores_AllActivities(this.getContext());
        } else if (position == 1) {
            list = getScores_AllWorkouts(this.getContext());
        }
//        to be implemented
//        else if (position == 2) {
//            list = getScores_AllMovementSpecificChallenges(this.getContext());
//        }

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void showSkeleton(boolean show) {

        if (show) {

//            skeletonLayout.removeAllViews();
            skeletonLayout.removeAllViewsInLayout();

            int skeletonRows = getSkeletonRowCount(this.context);
            for (int i = 0; i <= skeletonRows; i++) {
                ViewGroup rowLayout = (ViewGroup) inflater
                        .inflate(R.layout.skeleton_row_layout_exercises, null);
                skeletonLayout.addView(rowLayout);
            }
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmerAnimation();
            skeletonLayout.setVisibility(View.VISIBLE);
            skeletonLayout.bringToFront();
        } else {
            shimmer.stopShimmerAnimation();
            shimmer.setVisibility(View.GONE);
        }
    }

    public int getSkeletonRowCount(Context context) {
        int pxHeight = getDeviceHeight(context);
        int skeletonRowHeight = (int) getResources()
                .getDimension(R.dimen.row_layout_height); //converts to pixel
        return (int) Math.ceil(pxHeight / skeletonRowHeight);
    }

    public int getDeviceHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.heightPixels;
    }


    public void animateReplaceSkeleton(View listView) {

        listView.setVisibility(View.VISIBLE);
        listView.setAlpha(0f);
        listView.animate().alpha(1f).setDuration(1000).start();

        skeletonLayout.animate().alpha(0f).setDuration(1000).withEndAction(new Runnable() {
            @Override
            public void run() {
                showSkeleton(false);
            }
        }).start();

    }
}
