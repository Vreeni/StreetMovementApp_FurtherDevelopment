package com.example.vreeni.StreetMovementApp; /**
 * Created by vreeni on 19/12/2017.
 */

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Fragment that is displayed after a successful login and that can be accessed from the navigation menu item "Home"
 * final purpose yet to be defined, currently displaying space fillers
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).showBackButton(false);
    }


    @Override
    public void onStart() {
        super.onStart();
//        backButton.setOnClickListener(click -> {
//            ((AppCompatActivity)getContext()).getSupportFragmentManager().popBackStack();
//        });
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


    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id....) {
//            FragmentB b = FragmentB.newInstance(info);
//            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
//                    .addToBackStack("something")
//                    .commit();
//        }
    }

}

