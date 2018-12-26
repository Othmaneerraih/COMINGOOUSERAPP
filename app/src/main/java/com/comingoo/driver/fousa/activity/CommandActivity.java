package com.comingoo.driver.fousa.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.comingoo.driver.fousa.R;
import com.comingoo.driver.fousa.service.DriverService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommandActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static Activity clientR;
    private TextView tvUserRating;
    private TextView ratingShow;
    private TextView distance;
    private TextView startText;
    private TextView tvDestination;
    private TextView arrivalText;
    private Button decline;
    private Button accept;
    public static MediaPlayer mp;
    public static Vibrator vibrator;
    private SupportMapFragment map;
    private String lat, lng, destinatinLat, destinationLong;
    private String clientID, userId;
    private ProgressBar barTimer;
    public static CountDownTimer countDownTimer;
    private String clientType = "new";
    private Double driverPosLat, driverPosLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_new);
        clientR = this;

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        long[] pattern = {0, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500};
        vibrator.vibrate(pattern, 0);

        mp = MediaPlayer.create(this, R.raw.ring);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.start();

        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        final int origionalVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        switch (am.getRingerMode()) {
            case 0:
                vibrator.cancel();
                mp.start();
                break;
            case 1:
                vibrator.vibrate(pattern, 0);
                mp.start();
                break;
            case 2:
                vibrator.vibrate(pattern, 0);
                mp.start();
                break;
        }


        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    if (mp != null) {
                        if (mp.isPlaying()) {
                            mp.stop();
                            mp.release();
                        }
                    }
                    vibrator.cancel();
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, origionalVolume, 0);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });


        tvUserRating = findViewById(R.id.comingoonyouu_txt);
        ratingShow = findViewById(R.id.rating_txt);
        distance = findViewById(R.id.textView8);
        startText = findViewById(R.id.inbox_txt);
        tvDestination = findViewById(R.id.tv_destination);
        decline = findViewById(R.id.decline);
        accept = findViewById(R.id.accept);
        barTimer = findViewById(R.id.barTimer);

        final Intent intent = getIntent();
        int dist = 0;
        double Dist;
        int time = 0;

        lat = intent.getStringExtra("startLat");
        lng = intent.getStringExtra("startLong");

        destinatinLat = intent.getStringExtra("endLat");
        destinationLong = intent.getStringExtra("endLong");

        driverPosLat = intent.getDoubleExtra("driverPosLat", 0.0);
        driverPosLong = intent.getDoubleExtra("driverPosLong", 0.0);


        try {
            String gatedDistance = "";
            gatedDistance = intent.getStringExtra("distance");
            if (gatedDistance != "") {
                Dist = Double.parseDouble(intent.getStringExtra("distance"));
                dist = (int) Math.round(Dist);
                time = (int) (Dist * 1.5);
                distance.setText(intent.getStringExtra("distance") + "Km,  " + time + " min");
            } else {
                LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                LatLng driverLatLong = new LatLng(driverPosLat, driverPosLong);
                double distanceInKm = distanceInKilometer(latLng.latitude, latLng.longitude,
                        driverLatLong.latitude, driverLatLong.longitude);

                dist = (int) Math.round(distanceInKm);
                time = (int) (distanceInKm * 1.5);
                distance.setText(intent.getStringExtra("distance") + "Km,  " + time + " min");

            }
            Log.e("Commandac", "onCreate: distance " + intent.getStringExtra("distance"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.e("Commandac", "onCreate: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Commandac", "onCreate:1111111 " + e.getMessage());
            dist = 0;
            time = 0;
        }
        clientID = intent.getStringExtra("name");
        userId = intent.getStringExtra("userId");


        try {
            FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("rating")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.getKey().isEmpty()) {

                                if (dataSnapshot.getValue() == null) {
                                    ratingShow.setText("0");
                                } else {
                                    int oneStarPerson = 0;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                        oneStarPerson = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("1").getValue(String.class)));

                                        int one = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("1").getValue(String.class)));
                                        int twoStarPerson = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("2").getValue(String.class)));
                                        int two = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("2").getValue(String.class))) * 2;
                                        int threeStarPerson = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("3").getValue(String.class)));
                                        int three = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("3").getValue(String.class))) * 3;
                                        int fourStarPerson = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("4").getValue(String.class)));
                                        int four = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("4").getValue(String.class))) * 4;
                                        int fiveStarPerson = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("5").getValue(String.class)));
                                        int five = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("5").getValue(String.class))) * 5;

                                        double totalRating = one + two + three + four + five;
                                        double totalRatingPerson = oneStarPerson + twoStarPerson + threeStarPerson + fourStarPerson + fiveStarPerson;

                                        try {
                                            double avgRating = totalRating / totalRatingPerson;
                                            String avg = String.format("%.2f", avgRating);
                                            String newString = avg.replace(",", ".");
                                            ratingShow.setText(newString);
                                        } catch (ArithmeticException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                            } else {
                                ratingShow.setText(4.5 + "");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            ratingShow.setText(4.5 + "");
                        }
                    });


            FirebaseDatabase.getInstance().getReference("CLIENTFINISHEDCOURSES").
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(clientID)) {
                                FirebaseDatabase.getInstance().getReference("CLIENTFINISHEDCOURSES").child(clientID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    int size = (int) dataSnapshot.getChildrenCount();
                                                    if (size > 0) {
                                                        clientType = "bon";
                                                    } else clientType = "new";
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
        } catch (ArithmeticException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (clientType.equalsIgnoreCase("bon")) {
            barTimer.setProgressDrawable(getResources().getDrawable(R.drawable.drawable_new_client));
        } else {
            barTimer.setProgressDrawable(getResources().getDrawable(R.drawable.green_circular));
        }


        map = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_command));
        map.getMapAsync(this);

        distance.setText(time + " min /" + dist + "km");
        startText.setText("De : " + intent.getStringExtra("start"));
        tvDestination.setText("à : " + intent.getStringExtra("arrival"));

        accept.setClickable(true);
        accept.setEnabled(true);

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mp != null) {
                        if (mp.isPlaying()) {
                            mp.stop();
                            mp.release();
                        }
                    }
                    vibrator.cancel();
                    startDriverService();
                    FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").child(userId).child(clientID).removeValue();
                    CommandActivity.this.finish();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (userId != null && clientID != null) {
            if (!userId.isEmpty() && !clientID.isEmpty()) {
                FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").
                        child(userId).child(clientID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            startDriverService();
                            CommandActivity.this.finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        startTimer();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accept.setClickable(false);
                accept.setEnabled(false);
                try {
                    if (mp != null) {
                        if (mp.isPlaying()) {
                            mp.stop();
                            mp.release();
                        }
                    }
                    vibrator.cancel();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

                FirebaseDatabase.getInstance().getReference("COURSES").orderByChild("client").
                        equalTo(clientID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {

                            DatabaseReference courseDatabase = FirebaseDatabase.getInstance().getReference("COURSES").push();
                            Map<String, String> data = new HashMap<>();
                            data.put("client", clientID);
                            data.put("driver", userId);
                            data.put("startLat", intent.getStringExtra("startLat"));
                            data.put("startLong", intent.getStringExtra("startLong"));
                            data.put("endLat", intent.getStringExtra("endLat"));
                            data.put("endLong", intent.getStringExtra("endLong"));

                            data.put("driverPosLat", intent.getStringExtra("driverPosLat"));
                            data.put("driverPosLong", intent.getStringExtra("driverPosLong"));
                            data.put("fixedDest", intent.getStringExtra("isFixed"));
                            data.put("fixedPrice", intent.getStringExtra("fixedPrice"));

                            data.put("startAddress", intent.getStringExtra("start"));
                            data.put("endAddress", intent.getStringExtra("arrival"));


                            //default Values
                            data.put("state", "0");
                            data.put("preWaitTime", "0");
                            data.put("waitTime", "0");
                            data.put("price", "0");
                            data.put("distanceTraveled", "0");


                            courseDatabase.setValue(data);
                            FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").child(userId).child(clientID).removeValue();

                        } else {

                            for (DataSnapshot dataS : dataSnapshot.getChildren()) {
                                if (dataS.child("state").getValue(String.class).equals("3")) {

                                    DatabaseReference courseDatabase = FirebaseDatabase.getInstance().getReference("COURSES").push();
                                    Map<String, String> data = new HashMap<>();
                                    data.put("client", clientID);
                                    data.put("driver", userId);
                                    data.put("startLat", intent.getStringExtra("startLat"));
                                    data.put("startLong", intent.getStringExtra("startLong"));
                                    data.put("endLat", intent.getStringExtra("endLat"));
                                    data.put("endLong", intent.getStringExtra("endLong"));

                                    data.put("driverPosLat", intent.getStringExtra("driverPosLat"));
                                    data.put("driverPosLong", intent.getStringExtra("driverPosLong"));
                                    data.put("fixedDest", intent.getStringExtra("isFixed"));
                                    data.put("fixedPrice", intent.getStringExtra("fixedPrice"));

                                    data.put("startAddress", intent.getStringExtra("start"));
                                    data.put("endAddress", intent.getStringExtra("arrival"));

                                    //default Values
                                    data.put("state", "0");
                                    data.put("preWaitTime", "0");
                                    data.put("waitTime", "0");
                                    data.put("price", "0");
                                    data.put("distanceTraveled", "0");


                                    courseDatabase.setValue(data);
                                    FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").child(userId).child(clientID).removeValue();
                                }
                            }
                        }
                        MapsActivity.wazeButton.setVisibility(View.VISIBLE);
                        CommandActivity.this.finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void startDriverService() {
        Intent intent = new Intent(CommandActivity.this, DriverService.class);
        startService(intent);
    }

    private void stopDriverService() {
        Intent intent = new Intent(CommandActivity.this, DriverService.class);
        stopService(intent);
    }

    // Calculating KM from 2 LatLong
    private double distanceInKilometer(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                barTimer.setProgress((int) seconds);
                if (seconds == 0) {
                    try {
                        if (mp != null) {
                            if (mp.isPlaying()) {
                                mp.stop();
                                mp.release();
                            }
                        }

                        vibrator.cancel();
                        showCustomDialog(CommandActivity.this);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFinish() {
            }
        }.start();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
//        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.person_green);
        int height = 150;
        int width = 80;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.person_green);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .build();
        googleMap.addMarker(markerOptions);
        // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    public void showCustomDialog(final Context context) {
        try{
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.dialog_misses_ride_request, null, false);
            Button btnOk = dialogView.findViewById(R.id.btn_passer_hors);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").child(userId).child(clientID).removeValue();
                    stopService(new Intent(CommandActivity.this, DriverService.class));
                }
            });

            Button btnCancel = dialogView.findViewById(R.id.btn_rester_engine);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").child(userId).child(clientID).removeValue();
                    dialog.dismiss();
                    finish();
                }
            });
            dialog.setCancelable(false);
            dialog.setContentView(dialogView);
            dialog.show();
        }catch (WindowManager.BadTokenException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

}
