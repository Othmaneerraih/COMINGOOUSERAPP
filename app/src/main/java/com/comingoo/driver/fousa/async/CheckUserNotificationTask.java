package com.comingoo.driver.fousa.async;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.comingoo.driver.fousa.adapters.NotificationAdapter;
import com.comingoo.driver.fousa.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CheckUserNotificationTask extends AsyncTask<String, Integer, String> {

    private DatabaseReference mLocation;
    private NotificationAdapter cAdapter;
    private List<Notification> NotificationData;

    public CheckUserNotificationTask(DatabaseReference mLocation, NotificationAdapter cAdapter, List<Notification> notificationData) {
        this.mLocation = mLocation;
        this.cAdapter = cAdapter;
        NotificationData = notificationData;
    }

    // Runs in UI before background thread is called
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // This is run in a background thread
    @Override
    protected String doInBackground(String... params) {

        mLocation.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NotificationData.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Notification newNot = new Notification(
                            data.child("title").getValue(String.class),
                            data.child("content").getValue(String.class)
                    );
                    NotificationData.add(newNot);
                }
                cAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return "this string is passed to onPostExecute";
    }

    // This is called from background thread but runs in UI
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        // Do things like update the progress bar
    }

    // This runs in UI when background thread finishes
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // Do things like hide the progress bar or change a TextView
    }
}