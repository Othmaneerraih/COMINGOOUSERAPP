package com.comingoo.driver.fousa.async;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.comingoo.driver.fousa.adapters.ComingooUAdapter;
import com.comingoo.driver.fousa.model.Car;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CheckUserComingoUTask extends AsyncTask<String, Integer, String> {
    private DatabaseReference mLocation;
    private ComingooUAdapter cAdapter;
    private List<Car> carsData;

    public CheckUserComingoUTask(DatabaseReference mLocation, ComingooUAdapter cAdapter, List<Car> carsData) {
        this.mLocation = mLocation;
        this.cAdapter = cAdapter;
        this.carsData = carsData;
    }

    // Runs in UI before background thread is called
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Do something like display a progress bar
    }

    // This is run in a background thread
    @Override
    protected String doInBackground(String... params) {

        mLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carsData.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Car newCar = new Car(
                            data.child("name").getValue(String.class),
                            data.child("description").getValue(String.class),
                            data.child("selected").getValue(String.class),
                            data.child("id").getValue(String.class)
                    );
                    carsData.add(newCar);
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