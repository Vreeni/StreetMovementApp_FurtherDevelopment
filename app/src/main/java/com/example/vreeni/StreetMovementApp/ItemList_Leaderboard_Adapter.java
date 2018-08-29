package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Java class extending a RecyclerView.Adapter
 * responsible for the display of all items of a specific list (the first 5 leaderboard entries) in a certain way
 */
public class ItemList_Leaderboard_Adapter extends RecyclerView.Adapter<ItemList_Leaderboard_Adapter.ViewHolder> {

    private String TAG = "leaderboard adapter";

    private ArrayList<LeaderboardEntry> list;
    private Context context;
    private LayoutInflater mInflater;

    public ItemList_Leaderboard_Adapter(ArrayList<LeaderboardEntry> list, Context context) {
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        Log.d(TAG, "list size: " + list.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_leaderboard_entry, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    /**
     * defining what is saved and displayed in each row
     *
     * @param holder
     * @param position position specifying which Leaderboard Entry Object is being handled right now
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.rank.setText("" + (int) (position + 1));

        //include if statement to define what to call? weekly, total, workouts, parkour...
        //for 1-3st rank include gold, silver and bronze badge
        holder.score.setText("" + list.get(position).getNrOfActivities_total());

        getUserInformation(holder, list.get(position).getUserReference());

        Log.d(TAG, "setting text");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rank;
        private ImageView img;
        private TextView name;
        private TextView score;

        public ViewHolder(View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.leaderboard_entry_rank);
            img = itemView.findViewById(R.id.leaderboard_entry_img);
            name = itemView.findViewById(R.id.leaderboard_entry_name);
            score = itemView.findViewById(R.id.leaderboard_entry_score);
        }
    }


    public void getUserInformation(ViewHolder holder, DocumentReference userRef) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String profilePic = documentSnapshot.getString("profilePicture");
                    Glide.with(context)
                            .load(profilePic)
                            .placeholder(R.drawable.ic_contact)
                            .override(100, 100)
                            .into(holder.img);
//                    Log.d(TAG, "user profile picture: " + profilePic);

                    String name;
                    if (documentSnapshot.getString("name") != null) {
                        name = documentSnapshot.getString("name");
                    } else name = documentSnapshot.getString("email");
                    holder.name.setText(name);

                } else {
                }
            }
        });
    }

    //        new DownLoadImageTask(holder.img).execute(getProfilePic(list.get(position).getUserReference()));
    /*
        final AsyncTask<Params, Progress, Result>
            execute(Params... params)
                Executes the task with the specified parameters.
     */
    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }


}

