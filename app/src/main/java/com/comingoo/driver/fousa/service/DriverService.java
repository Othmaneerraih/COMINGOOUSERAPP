package com.comingoo.driver.fousa.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.comingoo.driver.fousa.activity.CommandActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class DriverService extends Service {
    private LatLng userLoc;
    private DatabaseReference driverLcoationDatabase;
    private GeoFire geoFire;

    private DatabaseReference mDatabase;
    private DatabaseReference driverPickupRequests;

    private String userId;

    private List<String> requestUsersID;
    private List<String> requestUsersLocation;
    private List<String> startingText;
    private List<String> arrivalText;
    private List<String> userLevel;
    private List<String> startLat;
    private List<String> startLong;
    private List<String> endLat;
    private List<String> endLong;
    private List<String> isFixed;
    private List<String> fixedPrice;


    private boolean isRunning = false;
    private Runnable runnable;

    private static final long INTERVAL = 1000 * 2;


    private int counterHolder;

    boolean checkStop = false;

    Service myService;

    @Override
    public void onCreate() {
        super.onCreate();
        myService = this;
        Context context = getApplicationContext();
    }


    private boolean checkIfLocationOpened() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (provider.contains("gps") || provider.contains("network")) {
            return true;
        }
        // otherwise return false
        return false;
    }


    private class CheckDebtTask extends AsyncTask<String, Integer, String> {
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

            if (userId == null) {
                return "";
            }

            FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("debt").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        final double debt = Double.parseDouble(dataSnapshot.getValue(String.class));
                        FirebaseDatabase.getInstance().getReference("PRICES").child("debtCeil").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    double debtCeil = Double.parseDouble(dataSnapshot.getValue(String.class));
                                    if (debt >= debtCeil) {
                                        SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
                                        prefs.edit().remove("online").apply();
                                        toaster("Vous devez payer votre dette pour aller en ligne.");
                                        myService.stopSelf();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

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

    private void toaster(String message) {
        Toast.makeText(myService, message, Toast.LENGTH_SHORT).show();
    }


    private class DriverServiceTask extends AsyncTask<String, Integer, String> {
        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {


            mDatabase = FirebaseDatabase.getInstance().getReference().child("DRIVERUSERS");
            driverLcoationDatabase = FirebaseDatabase.getInstance().getReference().child("ONLINEDRIVERS");
            driverPickupRequests = FirebaseDatabase.getInstance().getReference().child("PICKUPREQUEST");


            requestUsersID = new ArrayList<>();
            requestUsersLocation = new ArrayList<>();
            startingText = new ArrayList<>();
            arrivalText = new ArrayList<>();
            userLevel = new ArrayList<>();
            startLat = new ArrayList<>();
            startLong = new ArrayList<>();
            endLat = new ArrayList<>();
            endLong = new ArrayList<>();
            isFixed = new ArrayList<>();
            fixedPrice = new ArrayList<>();

            geoFire = new GeoFire(driverLcoationDatabase);

            SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
            String number = prefs.getString("phoneNumber", null);
            userId = prefs.getString("userId", null);
            if (userId == null) {
                //User Is Logged In
                DriverService.this.stopSelf();
            } else {
                // startLocationUpdates();
                new LocationUpdatesTask().execute();
                getLastLocation();
                mDatabase.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.child("isVerified").getValue(String.class).equals("0")) {
                                //Intent inte = new Intent(ComingoDriverService.this, ComingoDriverService.class);
                                //stopService(inte);
                                SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
                                prefs.edit().remove("online").apply();
                                DriverService.this.stopSelf();
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            final Handler handler = new Handler(Looper.getMainLooper());
            runnable = new Runnable() {
                int counter = 0;

                Handler checkHandler = new Handler(Looper.getMainLooper());
                Runnable checkRunnable;

                @Override
                public void run() {
                    isRunning = true;
                    if (counter >= requestUsersID.size()) {
                        counter = 0;
                        isRunning = false;
                        handler.removeCallbacks(this);
                        return;
                    }
                    counterHolder = counter;

                    if (requestUsersID.get(counter).equals("-1")) {
                        counter++;
                        handler.postDelayed(runnable, 0); // Optional, to repeat the task.
                        return;
                    }

                    final Intent intent = new Intent(DriverService.this, CommandActivity.class);

                    intent.putExtra("userId", userId);
                    intent.putExtra("name", requestUsersID.get(counter));
                    intent.putExtra("start", "" + startingText.get(counter));
                    intent.putExtra("arrival", "" + arrivalText.get(counter));
                    intent.putExtra("distance", "" + requestUsersLocation.get(counter));

                    intent.putExtra("startLat", "" + startLat.get(counter));
                    intent.putExtra("startLong", "" + startLong.get(counter));
                    intent.putExtra("endLat", "" + endLat.get(counter));
                    intent.putExtra("endLong", "" + endLong.get(counter));

                    intent.putExtra("isFixed", "" + isFixed.get(counter));
                    intent.putExtra("fixedPrice", "" + fixedPrice.get(counter));


                    if (userLoc != null) {
                        intent.putExtra("driverPosLat", "" + userLoc.latitude);
                        intent.putExtra("driverPosLong", "" + userLoc.longitude);
                    } else {
                        intent.putExtra("driverPosLat", "");
                        intent.putExtra("driverPosLong", "");
                    }


                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                    final DatabaseReference clientRequetFollow = FirebaseDatabase.getInstance()
                            .getReference("PICKUPREQUEST").child(userId).child(requestUsersID.get(counter));
                    clientRequetFollow.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                CommandActivity.countDownTimer.cancel();
//                                CommandActivity.clientR.finish();
                                if (CommandActivity.mp != null) {
                                    CommandActivity.mp.release();
                                    CommandActivity.vibrator.cancel();
                                }
                                counter++;
                                checkStop = true;
                                checkHandler.removeCallbacks(checkRunnable);
                                handler.postDelayed(runnable, 0); // Optional, to repeat the task.
                                clientRequetFollow.removeEventListener(this);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                    checkRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (counter < requestUsersID.size()) {
                                final DatabaseReference clientRequetFollow =
                                        FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").
                                                child(userId).child(requestUsersID.get(counter));

                                clientRequetFollow.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists() && !requestUsersID.get(counter).equals("-1")
                                                && requestUsersID.get(counter).equals(requestUsersID.get(counter))) {

                                            if (requestUsersID.size() > 1) {
                                                requestUsersID.set(counter, "-1");
                                            }
                                            if (CommandActivity.active) {
                                                CommandActivity.countDownTimer.cancel();
                                                if (CommandActivity.mp != null) {
                                                    CommandActivity.mp.release();
                                                    CommandActivity.vibrator.cancel();
                                                }
//                                                CommandActivity.clientR.finish();
                                            }

//                                            clientRequetFollow.removeValue();
//                                            clientRequetFollow.removeEventListener(this);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                if (CommandActivity.active) {
                                    CommandActivity.countDownTimer.cancel();
//                                    CommandActivity.clientR.finish();
                                    if (CommandActivity.mp != null) {
                                        CommandActivity.mp.release();
                                        CommandActivity.vibrator.cancel();
                                    }
                                }
                            }
                        }

                    };

                    checkHandler.postDelayed(checkRunnable, 17000); // Optional, to repeat the task.
                }
            };

            if (userId == null)
                return "";

            DatabaseReference PickupDatabase = FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").child(userId);
            PickupDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final @NonNull DataSnapshot dataSnapshot) {
                    if (!isRunning) {
                        requestUsersID.clear();
                        requestUsersLocation.clear();
                        startingText.clear();
                        arrivalText.clear();
                        userLevel.clear();
                    }

                    List<String> holder = new ArrayList<>();

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (dataSnapshot.exists()) {
                            if (!isRunning) {
                                requestUsersID.add(data.getKey());
                                requestUsersLocation.add(data.child("distance").getValue().toString());
                                startingText.add(data.child("start").getValue().toString());
                                arrivalText.add(data.child("arrival").getValue().toString());

                                startLat.add(data.child("startLat").getValue().toString());
                                startLong.add(data.child("startLong").getValue().toString());
                                endLat.add(data.child("endLat").getValue().toString());
                                endLong.add(data.child("endLong").getValue().toString());
                                isFixed.add(data.child("destFix").getValue().toString());
                                fixedPrice.add(data.child("fixedPrice").getValue().toString());

                            } else {
                                holder.add(data.getKey());
                                if (!idInList(data.getKey(), requestUsersID)) {
                                    requestUsersID.add(data.getKey());
                                    requestUsersLocation.add(data.child("distance").getValue().toString());
                                    startingText.add(data.child("start").getValue().toString());
                                    arrivalText.add(data.child("arrival").getValue().toString());

                                    startLat.add(data.child("startLat").getValue().toString());
                                    startLong.add(data.child("startLong").getValue().toString());
                                    endLat.add(data.child("endLat").getValue().toString());
                                    endLong.add(data.child("endLong").getValue().toString());

                                    isFixed.add(data.child("destFix").getValue().toString());
                                    fixedPrice.add(data.child("fixedPrice").getValue().toString());

                                }
                            }
                        }
                    }

                    if (isRunning) {
                        for (int i = 0; i < requestUsersID.size(); i++) {
                            if (!idInList(requestUsersID.get(i), holder)) {
                                requestUsersID.set(i, "-1");
                            }

                        }
                    } else {
                        if (CommandActivity.active) {
                            CommandActivity.countDownTimer.cancel();
//                            CommandActivity.clientR.finish();
                            if (CommandActivity.mp != null) {
                                CommandActivity.mp.release();
                                CommandActivity.vibrator.cancel();
                            }
                        }
                    }

                    //handler.removeCallbacks(runnable);
                    if (requestUsersID.size() > 0 && !isRunning) {
                        runnable.run();
                    }

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


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new CheckDebtTask().execute();
        new DriverServiceTask().execute();

        final Handler h = new Handler(Looper.getMainLooper());
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                getLastLocation();
                h.postDelayed(this, 500);
            }
        };
        r.run();

        return START_STICKY;
    }


    public boolean idInList(String ID, List<String> idList) {
        for (String userId : idList) {
            if (ID.equals(userId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        if (userId != null)
            driverLcoationDatabase.child(userId).removeValue();
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }


    public void onLocationChanged(Location location) {
        new LocationChangedTask().execute(location);
    }


    private class LocationUpdatesTask extends AsyncTask<String, Integer, String> {

        private static final long FASTEST_INTERVAL = 1000 * 1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            //createLocationRequest();
            getLastLocation();
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


    public double GetDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);
        // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        // Distance in km
        return d;
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }


    private class LocationChangedTask extends AsyncTask<Location, Integer, String> {
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
        protected String doInBackground(Location... params) {
            Location location = params[0];
            if (userLoc != null) {
                double distance = GetDistanceFromLatLonInKm(userLoc.latitude, userLoc.longitude, location.getLatitude(), location.getLongitude());
                if (distance < 0.1)
                    return "";
            }

            userLoc = new LatLng(location.getLatitude(), location.getLongitude());
            final SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
            String online = prefs.getString("online", "0");
            if (userId != null && online.equals("1")) {
                geoFire.setLocation(userId, new GeoLocation(userLoc.latitude, userLoc.longitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });
                driverPickupRequests.child(userId).onDisconnect().removeValue();
                driverLcoationDatabase.child(userId).onDisconnect().removeValue();
            } else {
                if (userId != null)
                    driverLcoationDatabase.child(userId).removeValue();
            }

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


    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                onLocationChanged(location);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MapDemoActivity", "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });
        }
    }


}
