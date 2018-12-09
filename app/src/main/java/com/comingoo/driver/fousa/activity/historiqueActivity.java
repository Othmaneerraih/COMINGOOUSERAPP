package com.comingoo.driver.fousa.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;

import com.comingoo.driver.fousa.async.CheckUserHistoriqueTask;
import com.comingoo.driver.fousa.model.Course;
import com.comingoo.driver.fousa.R;
import com.comingoo.driver.fousa.adapters.HistoryAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class historiqueActivity extends AppCompatActivity {

    private RecyclerView mLocationView;
    private DatabaseReference mLocation;
    private HistoryAdapter cAdapter;
    private List<Course> CoursesData;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        userId = prefs.getString("userId", null);

        mLocation = FirebaseDatabase.getInstance().getReference("DRIVERFINISHEDCOURSES").child(userId);
        mLocation.keepSynced(true);

        CoursesData = new ArrayList<>();
        mLocationView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLocationView.setHasFixedSize(true);
        mLocationView.setLayoutManager(new LinearLayoutManager(this));

        cAdapter = new HistoryAdapter(getApplicationContext(), CoursesData);
        mLocationView.setAdapter(cAdapter);
        new CheckUserHistoriqueTask(mLocation, cAdapter, CoursesData).execute();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    private String driverName;
    private int Rate;

}
