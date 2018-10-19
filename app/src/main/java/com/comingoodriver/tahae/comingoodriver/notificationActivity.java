package com.comingoodriver.tahae.comingoodriver;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import comingoo.vone.tahae.comingoodriver.R;

public class notificationActivity extends AppCompatActivity {

    private RecyclerView mLocationView;
    private DatabaseReference mLocation;
    private MyAdapter cAdapter;
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

        NotificationData  = new ArrayList<>();
        mLocationView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLocationView.setHasFixedSize(true);
        mLocationView.setLayoutManager(new LinearLayoutManager(this));

        cAdapter = new MyAdapter(NotificationData);
        mLocationView.setAdapter(cAdapter);
        new CheckUserTask().execute();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private class CheckUserTask extends AsyncTask<String, Integer, String> {
        SharedPreferences prefs;
        String userId;
        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
            userId = prefs.getString("userId", null);
            // Do something like display a progress bar
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


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Notification> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case

            public View h;
            public TextView title, content;

            public ViewHolder(View v) {
                super(v);
                h = v;

                title = v.findViewById(R.id.title);
                content = v.findViewById(R.id.content);

            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(List<Notification> myDataset) {
            this.mDataset = myDataset;

        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notifications_rows, parent, false);
            MyAdapter.ViewHolder vh = new MyAdapter.ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final MyAdapter.ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final Notification newCourse = mDataset.get(position);

            holder.title.setText(newCourse.getTitle());
            holder.content.setText(newCourse.getContent());

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }


}
