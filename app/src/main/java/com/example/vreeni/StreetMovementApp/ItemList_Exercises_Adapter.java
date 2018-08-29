package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Java class extending a RecyclerView.Adapter
 * responsible for the display of all items of a specific list in a certain way
 */
public class ItemList_Exercises_Adapter
        extends RecyclerView.Adapter<com.example.vreeni.StreetMovementApp.ItemList_Exercises_Adapter.ViewHolder> {

    private String TAG = "exerciseAdapter";

    private ArrayList<Exercise> list = new ArrayList<>();
    private Context context;
    private LayoutInflater mInflater;

    private RecyclerViewClickListener mListener;
    private PopupWindow popupWindow_exerciseDetails;
    private TextView tv_exerciseDetails_description;
    private TextView tv_exerciseDetails_name;
    private WebView webView;

    public ItemList_Exercises_Adapter(ArrayList<Exercise> list, Context context, RecyclerViewClickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        mListener = listener;

    }

    @Override
    public com.example.vreeni.StreetMovementApp.ItemList_Exercises_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_exercise, parent, false);
        com.example.vreeni.StreetMovementApp.ItemList_Exercises_Adapter.ViewHolder holder =
                new com.example.vreeni.StreetMovementApp.ItemList_Exercises_Adapter.ViewHolder(view, mListener);
        return holder;
    }

    /**
     * defining what is saved and displayed in each row
     *
     * @param holder
     * @param position position specifying which Item is being handled right now
     */
    @Override
    public void onBindViewHolder(com.example.vreeni.StreetMovementApp.ItemList_Exercises_Adapter.ViewHolder holder, int position) {
        holder.nr.setText("" + (int) (position + 1));
        holder.name.setText(list.get(position).getName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "ItemList Exercise clicked");
                openExerciseDetailsPopupWindow(mListener, position);

            }
        });

        if (list.get(position).getRepetitions() != null) {
            holder.repetitions.setText("" + list.get(position).getRepetitions());
        } else holder.repetitions.setText("" + 1);
        Log.d(TAG, "reps: " + list.get(position).getRepetitions());

        if (list.get(position).getSets() != 0) {
            holder.sets.setText("" + list.get(position).getSets());
        } else holder.sets.setText("1");
//            holder.sets.setText(list.get(position).getSets());

        Log.d(TAG, "setting text");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nr;
        private TextView name;
        private TextView repetitions;
        private TextView sets;

        private RecyclerViewClickListener mListener;
        private LayoutInflater mInflater;


        public ViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            nr = itemView.findViewById(R.id.tv_exercise_nr);
            name = itemView.findViewById(R.id.tv_exercise_name);
            repetitions = itemView.findViewById(R.id.tv_exercise_reps);
            sets = itemView.findViewById(R.id.tv_exercise_sets);
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

        tv_exerciseDetails_description = (TextView) layout.findViewById(R.id.tv_exerciseDetails_description);
        if (list.get(position).getDescription() != null) {
            tv_exerciseDetails_description.setText(list.get(position).getDescription());
        }

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

}

