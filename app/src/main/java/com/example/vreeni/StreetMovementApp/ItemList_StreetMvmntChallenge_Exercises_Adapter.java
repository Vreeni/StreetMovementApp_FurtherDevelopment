package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.StrictMath.toIntExact;

/**
 * Java class extending a RecyclerView.Adapter
 * responsible for the display of all items of a specific list in a certain way
 */
public class ItemList_StreetMvmntChallenge_Exercises_Adapter
        extends RecyclerView.Adapter<com.example.vreeni.StreetMovementApp.ItemList_StreetMvmntChallenge_Exercises_Adapter.ViewHolder> {

    private String TAG = "exerciseAdapter";
    private Map<String, Object> exercisesCompletedMap = new HashMap<>();
    private ArrayList<Exercise> list = new ArrayList<>();
    private ArrayList<Exercise> listOfExercisesCompleted = new ArrayList<>();
    private int xp;
    private Context context;
    private LayoutInflater mInflater;

    private RecyclerViewClickListener mListener;
    private Communication_Adapter_Fragment_WOP communiator;
    private PopupWindow popupWindow_exerciseDetails;
    private TextView tv_exerciseDetails_description;
    private TextView tv_exerciseDetails_name;
    private WebView webView;


    public ItemList_StreetMvmntChallenge_Exercises_Adapter(ArrayList<Exercise> list, Context context, RecyclerViewClickListener listener, Communication_Adapter_Fragment_WOP comm) {
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        mListener = listener;
        communiator = comm;

    }

    @Override
    public com.example.vreeni.StreetMovementApp.ItemList_StreetMvmntChallenge_Exercises_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_wop_exercise, parent, false);
        com.example.vreeni.StreetMovementApp.ItemList_StreetMvmntChallenge_Exercises_Adapter.ViewHolder holder =
                new com.example.vreeni.StreetMovementApp.ItemList_StreetMvmntChallenge_Exercises_Adapter.ViewHolder(view, mListener, communiator);
        return holder;
    }

    /**
     * defining what is saved and displayed in each row
     *
     * @param holder
     * @param position position specifying which Item is being handled right now
     */
    @Override
    public void onBindViewHolder(com.example.vreeni.StreetMovementApp.ItemList_StreetMvmntChallenge_Exercises_Adapter.ViewHolder holder, int position) {
        holder.nr.setText("" + (int) (position + 1));
        holder.name.setText(list.get(position).getName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "ItemList WOP Exercise clicked");
                openExerciseDetailsPopupWindow(mListener, position);
            }
        });

        if (list.get(position).getDescription() != null) {
//            holder.description.setText("" + list.get(position).getDescription());
        } else holder.description.setText("");
        Log.d(TAG, "description: " + list.get(position).getDescription());

        getExercisesCompleted(holder, position);
        holder.completed.setOnClickListener(v -> {
            //add xp to user profile; but only when user presses save?
            if (holder.completed.isChecked()) {
                addXP(holder, position);
                Log.d(TAG, "checkbox got checked" + exercisesCompletedMap);
            } else {
                subtractXP(holder, position);
                Log.d(TAG, "checkbox got unchecked" + exercisesCompletedMap);
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nr;
        private TextView name;
        private TextView description;
        private CheckBox completed;
        private Communication_Adapter_Fragment_WOP mCommunicator;


        public ViewHolder(View itemView, RecyclerViewClickListener listener, Communication_Adapter_Fragment_WOP communicator) {
            super(itemView);

            nr = itemView.findViewById(R.id.tv_smchallenge_exercise_nr);
            name = itemView.findViewById(R.id.tv_smchallenge_exercise_name);
            description = itemView.findViewById(R.id.tv_smchallenge_exercise_description);
            completed = itemView.findViewById(R.id.checkbox_smchallenge_exercise_completed);
            mCommunicator = communicator;

        }

    }

    public void openExerciseDetailsPopupWindow(RecyclerViewClickListener listener, int position) {
        View layout = mInflater.inflate(R.layout.popup_window_exercise_details, null);
        popupWindow_exerciseDetails = new PopupWindow(
                layout,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        Log.d(TAG, "popup opened");
        tv_exerciseDetails_name = (TextView) layout.findViewById(R.id.tv_exerciseDetails_name);
        tv_exerciseDetails_name.setText(list.get(position).getName());

        webView = (WebView) layout.findViewById(R.id.webView_exerciseDetails);
        if (list.get(position).getVideo() != null) {
            setupVideo(list.get(position).getVideo(), context);
        }

        Button btn_cancel = (Button) layout.findViewById(R.id.btn_cancelExerciseView);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow_exerciseDetails.dismiss();
            }
        });

        try {
            popupWindow_exerciseDetails.showAtLocation(layout, Gravity.CENTER, 0, 0);
            dimBehind(popupWindow_exerciseDetails);
        } catch (Exception e) {
            Log.d(TAG, "popup window exception" + e);
        }

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


    public void populatePopupWindow(TextView tv, ArrayList<Exercise> list) {

    }

    public void setupVideo(String vidEx1, Context context) {
        //set up the webview - vimeo vide
        webView.setInitialScale(1);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        Log.e("WebView Log", width + "-" + height);
        String data_html = "<!DOCTYPE html><html> <head> <meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <link rel=\"stylesheet\" media=\"screen and (-webkit-device-pixel-ratio:1.5)\" href=\"hdpi.css\" /></head> <body style=\"background:white;margin:0 0 0 0; padding:0 0 0 0;\"> <iframe style=\"background:white;\" width=' " + width + "' height='" + height / 2 + "' src=\"" + vidEx1 + "\" frameborder=\"0\"></iframe> </body> </html> ";
        webView.loadDataWithBaseURL("http://vimeo.com", data_html, "text/html", "UTF-8", null);
        //end of webview
    }


    public void getExercisesCompleted(com.example.vreeni.StreetMovementApp.ItemList_StreetMvmntChallenge_Exercises_Adapter.ViewHolder holder, int pos) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();
        FirebaseQuery_WOP_Participant query_wop_participant = new FirebaseQuery_WOP_Participant(currUser.getEmail());
        query_wop_participant.query(new FirebaseCallback_WOP_Participant() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onQuerySuccess(WOP_Participant wop_participant) {
                if (wop_participant.getUserID() != null) {
                    exercisesCompletedMap = (HashMap) wop_participant.getExercises_completed();
                    // loop through map checking for true values
                    for (Map.Entry<String, Object> entry : exercisesCompletedMap.entrySet()) {
                        //if key equals corresponding exercises for this level
                        if (entry.getKey().equals(list.get(pos).getExerciseID())) {
                            holder.completed.setChecked(true);
                        }
                    }
                    Log.d(TAG, "map with exercises completed: " + exercisesCompletedMap);
                }
            }

            @Override
            public void onFailure() {
                xp = 0;

            }
        });
    }


    public void addXP(com.example.vreeni.StreetMovementApp.ItemList_StreetMvmntChallenge_Exercises_Adapter.ViewHolder holder, int pos) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();
        FirebaseQuery_WOP_Participant query_wop_participant = new FirebaseQuery_WOP_Participant(currUser.getEmail());
        query_wop_participant.query(new FirebaseCallback_WOP_Participant() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onQuerySuccess(WOP_Participant wop_participant) {
                if (wop_participant.getUserID() != null) {
                    long xp_long = wop_participant.getXp();
                    xp = toIntExact(xp_long);
                    Log.d(TAG, "queried xp: " + xp);
                    xp = xp + list.get(pos).getXp();
                    int xp_ex = list.get(pos).getXp();
                    boolean complete = true;
                    //upload to FB
                    //popup window will appear indicating the gain of xp => can progress be uploaded simultaneously?
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currUser = mAuth.getCurrentUser();
                    Map<String, Object> data = new HashMap<>();
                    data.put("xp", xp);
                    Log.d(TAG, "xp to upload " + xp);
                    exercisesCompletedMap.put(list.get(pos).getExerciseID(), true);
                    data.put("exercises_completed", exercisesCompletedMap);
                    db.collection("WOP_Vejstrup_Participants").document(currUser.getEmail())
                            .update(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    holder.mCommunicator.onCheckboxSelected(xp, xp_ex, complete);
//                                    openExerciseCompletedPopupWindow();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });
                }
            }

            @Override
            public void onFailure() {
                xp = 0;

            }
        });
    }

    public void subtractXP(com.example.vreeni.StreetMovementApp.ItemList_StreetMvmntChallenge_Exercises_Adapter.ViewHolder holder, int pos) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currUser = mAuth.getCurrentUser();
        FirebaseQuery_WOP_Participant query_wop_participant = new FirebaseQuery_WOP_Participant(currUser.getEmail());
        query_wop_participant.query(new FirebaseCallback_WOP_Participant() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onQuerySuccess(WOP_Participant wop_participant) {
                if (wop_participant.getUserID() != null) {
                    long xp_long = wop_participant.getXp();
                    xp = toIntExact(xp_long);
                    Log.d(TAG, "queried xp: " + xp);
                    xp = xp - list.get(pos).getXp();
                    int xp_ex = list.get(pos).getXp();
                    boolean complete = false;
                    //upload to FB
                    //popup window will appear indicating the gain of xp => can progress be uploaded simultaneously?
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currUser = mAuth.getCurrentUser();
                    Map<String, Object> data = new HashMap<>();
                    data.put("xp", xp);
                    Log.d(TAG, "xp to upload " + xp);
                    exercisesCompletedMap.remove(list.get(pos).getExerciseID(), true);
                    data.put("exercises_completed", exercisesCompletedMap);
                    db.collection("WOP_Vejstrup_Participants").document(currUser.getEmail())
                            .update(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    holder.mCommunicator.onCheckboxSelected(xp, xp_ex, complete);

                                    //checking whether the participant has reached the minimum required amount of xp necessary to proceed to the next level
                                    //is handled from fragment (tv displaying xp)

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });
                }
            }

            @Override
            public void onFailure() {
                xp = 0;

            }
        });
    }


}

