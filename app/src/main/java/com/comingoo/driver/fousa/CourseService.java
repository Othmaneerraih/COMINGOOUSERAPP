package com.comingoo.driver.fousa;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class CourseService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private DatabaseReference onlineDriver;
    private DatabaseReference courseRef;

    private String courseID;

    private int state = 0; // Four level , the variable can exist in three of them
    private int preWaitTime = 0;
    private int waitTime = 0;
    private double price = 0;
    private double finalDistance = 0;
    private boolean countingPreWait = false;
    private boolean countingDistance = false;

    private List<Location> travelLocations = new ArrayList<>();
    private Double distanceTraveled = 0.0;

    private ValueEventListener listener;

    private double time = 0;


    private Service myService;


    private Location userLoc;

    private Service thisService;

    private String clientID;
    private String userId;


    private LatLng startPos;
    private LatLng endPos;

    private String startA;
    private String endA;

    private boolean isFixed;
    private double fixedPrice;

    private boolean checkedState;

    private EventListener driverO;
    private String promoCode = null;


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    double speed;


    private Handler h;
    private Runnable r;

    private Handler hh;
    private Runnable rr;

    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private String driverName;

    private int promoVal = 0;


    private class CourseServiceTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isFixed = false;
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {

            SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
            userId = prefs.getString("userId", null);
            courseID = prefs.getString("courseID", null);

            if (userId == null)
                thisService.stopSelf();

            startPos = null;
            endPos = null;

            startA = "Address non trouvé";
            endA = "Address non trouvé";

            courseRef = FirebaseDatabase.getInstance().getReference("COURSES").child(courseID);
            courseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        try {
                            clientID = dataSnapshot.child("client").getValue(String.class);
                            state = Integer.parseInt(dataSnapshot.child("state").getValue(String.class));
                            preWaitTime = Integer.parseInt(dataSnapshot.child("preWaitTime").getValue(String.class));
                            if (!dataSnapshot.child("distanceTraveled").getValue(String.class).equals("0"))
                                distanceTraveled = Double.parseDouble(dataSnapshot.child("distanceTraveled").getValue(String.class));

                            waitTime = Integer.parseInt(dataSnapshot.child("waitTime").getValue(String.class));
                            price = Double.parseDouble(dataSnapshot.child("price").getValue(String.class));
                            if (Double.parseDouble(dataSnapshot.child("fixedDest").getValue(String.class)) == 1) {
                                isFixed = true;
                                fixedPrice = Double.parseDouble(dataSnapshot.child("fixedPrice").getValue(String.class));
                            }
                            //checkState();


                            if (state == 3 && !checkedState) {
                                new CheckStateTask().execute();
                                courseRef.removeEventListener(this);
                            } else {
                                new CheckStateTask().execute();
                            }
                        } catch (Exception e) {
                            thisService.stopSelf();
                        }
                    } else {
                        state = 4;
                        thisService.stopSelf();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //desconnect Driver if still Online
            onlineDriver = FirebaseDatabase.getInstance().getReference("ONLINEDRIVERS").child(userId);
            
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        thisService = this;
        myService = this;
        checkedState = false;
        driverName = "";
        SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                driverName = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        h = new Handler(Looper.getMainLooper());
        hh = new Handler(Looper.getMainLooper());

        r = new Runnable() {
            @Override
            public void run() {
                if (countingDistance) {
                    if (checkIfLocationOpened()) {
                        time += 0.3;
                        getLastLocation();
                    }
                    h.postDelayed(this, 300);
                }
            }
        };

//        new LocationUpdatesTask().execute();
        new CourseServiceTask().execute();


        return START_STICKY;
    }

    private class CheckStateTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {

            final Handler handler = new Handler(Looper.getMainLooper());
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (countingPreWait) {
                        int time = preWaitTime + 1;
                        courseRef.child("preWaitTime").setValue(Integer.toString(time));
                        handler.postDelayed(this, 1000);
                    }
                }
            };

            if (state == 1) {
                if (!countingPreWait) {
                    countingPreWait = true;
                    runnable.run();
                }
            }

            FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("PROMOCODE").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        promoCode = dataSnapshot.getValue(String.class);
                        FirebaseDatabase.getInstance().getReference("CLIENTNOTIFICATIONS").orderByChild(promoCode).equalTo(promoCode).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    promoVal = Integer.parseInt(data.child("value").getValue(String.class));
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


            if (state == 2) {
                countingPreWait = false;
                if (!countingDistance) {
                    countingDistance = true;
                    r.run();
                }
            }

            if (state == 3 && !checkedState) {
                getLastLocation();
                checkedState = true;
                countingPreWait = false;
                countingDistance = false;


                FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String clientID = dataSnapshot.child("client").getValue(String.class);
                            final int preWaitTime = Integer.parseInt(dataSnapshot.child("preWaitTime").getValue(String.class));
                            final double distanceTraveled = Double.parseDouble(dataSnapshot.child("distanceTraveled").getValue(String.class));
                            final int waitTime = Integer.parseInt(dataSnapshot.child("waitTime").getValue(String.class));
                            final String startA = (dataSnapshot.child("startAddress").getValue(String.class));
                            final String endA = (dataSnapshot.child("endAddress").getValue(String.class));

                            startPos = new LatLng(Double.parseDouble(dataSnapshot.child("startLat").getValue(String.class)), Double.parseDouble(dataSnapshot.child("startLong").getValue(String.class)));
                            if (dataSnapshot.child("endLat").getValue(String.class).length() > 0)
                                endPos = new LatLng(Double.parseDouble(dataSnapshot.child("endLat").getValue(String.class)), Double.parseDouble(dataSnapshot.child("endLong").getValue(String.class)));


                            FirebaseDatabase.getInstance().getReference("PRICES").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {
                                        double att = Double.parseDouble(dataSnapshot.child("att").getValue(String.class));
                                        double base = Double.parseDouble(dataSnapshot.child("base").getValue(String.class));
                                        double km = Double.parseDouble(dataSnapshot.child("km").getValue(String.class));
                                        double min = Double.parseDouble(dataSnapshot.child("minimum").getValue(String.class));
                                        final double percent = Double.parseDouble(dataSnapshot.child("percent").getValue(String.class));

                                        long timestamp = GetUnixTime() * -1;

                                        double preWaitT = 0;

                                        if (preWaitTime > 180) {
                                            preWaitT = 3;
                                        }
                                        int preWait = (int) (waitTime / 60);


                                        double price = Math.ceil(base + (distanceTraveled * km) + (preWait * att) + preWaitT);
                                        if (price < min) {
                                            price = min;
                                        }


                                        SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
                                        prefs.edit().putString("online", "1").apply();

                                        DatabaseReference mCourse = FirebaseDatabase.getInstance().getReference("CLIENTFINISHEDCOURSES").child(clientID).child(courseID);

                                        Map<String, String> data = new HashMap<>();
                                        data.put("client", clientID);
                                        data.put("driver", userId);
                                        data.put("startAddress", startA);
                                        data.put("endAddress", endA);
                                        data.put("driver", userId);
                                        data.put("distance", Double.toString(distanceTraveled));
                                        data.put("waitTime", Integer.toString(preWait));
                                        data.put("preWaitTime", Integer.toString((int) preWaitTime / 60));
                                        if (isFixed) {
                                            data.put("fixedDest", "1");
                                            data.put("price", Integer.toString((int) fixedPrice));

                                        } else {
                                            data.put("fixedDest", "0");
                                            data.put("price", Integer.toString((int) price));
                                        }
                                        mCourse.setValue(data);
                                        mCourse.child("date").setValue(timestamp);

                                        DatabaseReference dCourse = FirebaseDatabase.getInstance().getReference("DRIVERFINISHEDCOURSES").child(userId).child(courseID);

                                        Map<String, String> dData = new HashMap<>();
                                        dData.put("client", clientID);
                                        dData.put("driver", userId);
                                        dData.put("startAddress", startA);
                                        dData.put("endAddress", endA);
                                        dData.put("distance", Double.toString(distanceTraveled));
                                        dData.put("waitTime", Integer.toString(preWait));
                                        dData.put("preWaitTime", Integer.toString((int) preWaitTime / 60));
                                        if (isFixed) {
                                            dData.put("fixedDest", "1");
                                            dData.put("price", Integer.toString((int) fixedPrice));

                                        } else {
                                            dData.put("fixedDest", "0");
                                            dData.put("price", Integer.toString((int) (price - (price * promoVal))));
                                        }
                                        dCourse.setValue(dData);
                                        dCourse.child("date").setValue(timestamp);


                                        final double getP = price;
                                        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("EARNINGS").child(getDateMonth(GetUnixTime())).child(getDateDay(GetUnixTime())).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                double earned = 0;
                                                int voyages = 0;
                                                if (dataSnapshot.exists()) {

                                                    earned = Double.parseDouble(dataSnapshot.child("earnings").getValue(String.class));
                                                    voyages = Integer.parseInt(dataSnapshot.child("voyages").getValue(String.class));

                                                }


                                                if (isFixed)
                                                    earned += fixedPrice;
                                                else
                                                    earned += getP;

                                                voyages += 1;


                                                final double ee = earned;
                                                final int vv = voyages;
                                                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("debt").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        float debt = 0;
                                                        if (dataSnapshot.exists()) {
                                                            debt = Float.parseFloat(dataSnapshot.getValue(String.class));
                                                        }


                                                        final Map<String, String> earnings = new HashMap<>();
                                                        earnings.put("earnings", "" + ee);
                                                        earnings.put("voyages", "" + vv);


                                                        final float ddd = debt;
                                                        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.child("USECREDIT").getValue(String.class).equals("1") && Integer.parseInt(dataSnapshot.child("SOLDE").getValue(String.class)) >= (int) getP) {
                                                                    int newSolde = Integer.parseInt(dataSnapshot.child("SOLDE").getValue(String.class)) - (int) getP;
                                                                    FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("SOLDE").setValue("" + newSolde);
                                                                    FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("PAID").setValue("1");

                                                                    float newDebt = (ddd - (float) getP) + (float) (getP * percent);
                                                                    FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("debt").setValue(Float.toString(newDebt));
                                                                } else {
                                                                    FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("PAID").setValue("0");
                                                                    FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("debt").setValue(Float.toString((float) (ddd + (float) (getP * percent))));
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("level").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.getValue(String.class).equals("2"))
                                                                    FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("level").setValue("1");

                                                                if (dataSnapshot.getValue(String.class).equals("1"))
                                                                    FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("level").setValue("0");
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).removeValue();
                                                        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("LASTCOURSE").setValue("Derniére course : Captain " + driverName + " / " + getP + " MAD");
                                                        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("COURSE").setValue(courseID);
                                                        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("COURSE").setValue(courseID);
                                                        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("EARNINGS").child(getDateMonth(GetUnixTime())).child(getDateDay(GetUnixTime())).setValue(earnings);


                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });


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


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            ///////////////////////////////////////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////
            //Course Canceled

            if (state == 5) {
                countingPreWait = false;
                countingDistance = false;
                FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).removeValue();
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

    private boolean checkIfLocationOpened() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (provider.contains("gps") || provider.contains("network")) {
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        h.removeCallbacks(r);
        hh.removeCallbacks(rr);
        //fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();


        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private String getDateMonth(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000L);
        String date = DateFormat.format("MM-yyyy", cal).toString();
        return date;
    }

    private String getDateDay(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000L);
        String date = DateFormat.format("dd", cal).toString();
        return date;
    }

    public int GetUnixTime() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        int utc = (int) (now / 1000);
        return (utc);

    }

    public void onLocationChanged(Location location) {
        new LocationChangedTask().execute(location);
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

            if (countingDistance && userLoc != null && location != null && time != 0) {
                double distance = GetDistanceFromLatLonInKm(userLoc.getLatitude(), userLoc.getLongitude(), location.getLatitude(), location.getLongitude());
                if (distance < 0.005)
                    return "";


                int TT = (int) time;
                int speed = (int) (((distance) * 3600) / TT);
                // speed = (int) location.getSpeed() * 18 / 5;
                finalDistance += distance;
                distance += distanceTraveled;

                if (speed <= 16)
                    waitTime += time;

                if (finalDistance > distance)
                    distance = finalDistance;


                finalDistance = distance;
                String distanceData = new DecimalFormat("##.####").format(distance);
                courseRef.child("waitTime").setValue(Integer.toString(waitTime));
                courseRef.child("distanceTraveled").setValue(distanceData);
                time = 0;
            }
            userLoc = location;
            if (state == 0 || state == 1 || state == 2) {
                FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).child("driverPosLat").setValue("" + userLoc.getLatitude());
                FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).child("driverPosLong").setValue("" + userLoc.getLongitude());
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


    private class LocationUpdatesTask extends AsyncTask<String, Integer, String> {

        private static final long FASTEST_INTERVAL = 1000 * 1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            // createLocationRequest();
            //createLocationCallback();
            startLocationUpdates();
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


    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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


    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;


    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;


        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            onLocationChanged(mLastLocation);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }


    private void startLocationUpdates() {
        initializeLocationManager();
        try {
            if (Looper.myLooper() == null)
                Looper.prepare();

            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {

            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            final Handler h = new Handler(Looper.getMainLooper());
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    if (ContextCompat.checkSelfPermission(myService, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        getFusedLocationProviderClient(myService).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        // do work here
                                        onLocationChanged(locationResult.getLastLocation());
                                    }
                                },
                                Looper.myLooper());
                    }
                }
            };
            h.postDelayed(r, 5000);

        } catch (SecurityException e) {
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
