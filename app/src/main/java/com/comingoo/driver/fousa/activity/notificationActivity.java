package com.comingoo.driver.fousa.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.comingoo.driver.fousa.adapters.NotificationAdapter;
import com.comingoo.driver.fousa.async.CheckUserNotificationTask;
import com.comingoo.driver.fousa.model.Notification;
import com.comingoo.driver.fousa.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class notificationActivity extends AppCompatActivity {

    private RecyclerView mLocationView;
    private DatabaseReference mLocation;
    private NotificationAdapter cAdapter;
    private List<Notification> NotificationData;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        userId = prefs.getString("userId", null);

        mLocation = FirebaseDatabase.getInstance().getReference("DRIVERNOTIFICATIONS");
        mLocation.keepSynced(true);

        NotificationData = new ArrayList<>();
        mLocationView = findViewById(R.id.my_recycler_view);
        mLocationView.setHasFixedSize(true);
        mLocationView.setLayoutManager(new LinearLayoutManager(this));

        cAdapter = new NotificationAdapter(NotificationData);
        mLocationView.setAdapter(cAdapter);
        new CheckUserNotificationTask(mLocation, cAdapter, NotificationData).execute();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
