package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class Fragment_Education extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = "Edu";

    private Button btnWorkout;
    private Button btnMovSpecChallenge;
    private Button btnSMChallenge;
    private ImageView iv_wk;
    private ImageView iv_movSpec;
    private ImageView iv_streetMovementChallenge;
    private PopupWindow popupWindow;
    private PopupWindow popupWindowImage;


    /**
     * Constructor that can hold an activity's setting and specific training location, when the training flow was initiated from the map
     * setting and specific location will be null if started from the training function in the navigation drawer
     */
    public static Fragment_Education newInstance() {
        final Bundle bundle = new Bundle(); //to pass arguments to the next fragment
        Fragment_Education fragment = new Fragment_Education();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_education, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnWorkout = (Button) view.findViewById(R.id.btn_edu_workout);
        btnMovSpecChallenge = (Button) view.findViewById(R.id.btn_edu_movSpecChallenge);
        btnSMChallenge = (Button) view.findViewById(R.id.btn_edu_streetMovChallenge);

        iv_wk = (ImageView) view.findViewById(R.id.iv_edu_workout);
        iv_movSpec = (ImageView) view.findViewById(R.id.iv_edu_movementSpecificChallenge);
        iv_streetMovementChallenge = (ImageView) view.findViewById(R.id.iv_edu_streetMovementChallenge);
    }


    @Override
    public void onStart() {
        super.onStart();
        btnWorkout.setOnClickListener(this);
        btnMovSpecChallenge.setOnClickListener(this);
        btnSMChallenge.setOnClickListener(this);
        iv_wk.setOnClickListener(this);
        iv_movSpec.setOnClickListener(this);
        iv_streetMovementChallenge.setOnClickListener(this);
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
        if (v.getId() == R.id.btn_edu_workout) {
            String workout = getString(R.string.edu_workout);
            try {
                openPopupWindow("Workout", workout);
            } catch (Exception e) {
                Log.d(LOG_TAG, "popupwindow:" + e);
            }
        }
        if (v.getId() == R.id.btn_edu_movSpecChallenge) {
            String movSpec = getString(R.string.edu_movSpecChallenge);
            try {
                openPopupWindow("Movement specific challenge", movSpec);
            } catch (Exception e) {
                Log.d(LOG_TAG, "popupwindow:" + e);
            }
        }
        if (v.getId() == R.id.btn_edu_streetMovChallenge) {
            Log.d(LOG_TAG, "sm challenge clicked");
            String smChallenge = getString(R.string.edu_streetMovChallenge);
            try {
                openPopupWindow("Street Movement challenge", smChallenge);
            } catch (Exception e) {
                Log.d(LOG_TAG, "popupwindow:" + e);
            }
        }
        if (v.getId() == R.id.iv_edu_workout) {
            openImageInPopupWindow("Workout");
        }
        if (v.getId() == R.id.iv_edu_movementSpecificChallenge) {
            openImageInPopupWindow("Movement specific challenge");
        }
        if (v.getId() == R.id.iv_edu_streetMovementChallenge) {
            openImageInPopupWindow("Street Movement challenge");
        }
    }

    public void openPopupWindow(String activityName, String activityDescription) {
        View layout = getLayoutInflater().inflate(R.layout.popup_window_education, null);
        popupWindow = new PopupWindow(
                layout,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        TextView tv_name = (TextView) layout.findViewById(R.id.tv_edu_activity_name);
        TextView tv_description = (TextView) layout.findViewById(R.id.tv_edu_activity_description);
        tv_name.setText(activityName);
        tv_description.setText(activityDescription);
//        ImageView iv_activity = (ImageView) layout.findViewById(R.id.iv_edu_activity_image);
        Button btn_cancel = (Button) layout.findViewById(R.id.btn_cancel_edu);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        Button btn_continueToExerciseLibrary = (Button) layout.findViewById(R.id.btn_continueToExerciseLibrary);
        btn_continueToExerciseLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //continue to exercise library
            }
        });

        popupWindow.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);
        Log.d(LOG_TAG, "opening popup window");
    }


    public void openImageInPopupWindow(String activityName) {
        View layout = getLayoutInflater().inflate(R.layout.popup_window_edu_images, null);
        popupWindowImage = new PopupWindow(
                layout,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        TextView tv_name = (TextView) layout.findViewById(R.id.tv_edu_image);
        tv_name.setText(activityName);
        ImageView iv_edu_image = (ImageView) layout.findViewById(R.id.iv_edu_image);
        Glide.with(this)
                .load(activityName) //load STRING??
                .placeholder(R.drawable.noimgavailable)
                .override(100, 100)
                .into(iv_edu_image);
        Button btn_cancel = (Button) layout.findViewById(R.id.btn_cancel_edu_imageview);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowImage.dismiss();
            }
        });
        popupWindowImage.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
        dimBehind(popupWindowImage);
        Log.d(LOG_TAG, "opening popup window");
    }

    /**
     * once popup window is open, dim everything behind it for the time it is opened
     *
     * @param popupWindow based on which popup window is set as a parameter, it is taken as reference point and everything behind is dimmed
     */
    private void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.7f;
        wm.updateViewLayout(container, p);
    }
}
