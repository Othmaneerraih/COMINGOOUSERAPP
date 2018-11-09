package com.comingoo.driver.fousa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class commandActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static Activity clientR;
    private TextView name;
    private TextView distance;
    private TextView startText;
    private TextView arrivalText;
    private Button decline;
    private Button accept;
    public static MediaPlayer mp;
    public static Vibrator vibrator;
    private SupportMapFragment map;
    private String lat, lng;
    private String clientID, userId;
    private ProgressBar barTimer;
    public static CountDownTimer countDownTimer;
    private String clientType = "new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        clientR = this;


        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        long[] pattern = { 0, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500};
        vibrator.vibrate(pattern , 0);

        mp = MediaPlayer.create(this, R.raw.ring);
        mp.start();


        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.stop();
                mp.release();
                vibrator.cancel();
            }
        });


        // name  = (TextView) findViewById(R.id.textView10);
        distance = (TextView) findViewById(R.id.textView8);
        startText = (TextView) findViewById(R.id.textView9);
        decline = (Button) findViewById(R.id.decline);
        accept = (Button) findViewById(R.id.accept);
        barTimer = (ProgressBar) findViewById(R.id.barTimer);


        final TextView clientLevel = (TextView) findViewById(R.id.textView6);
        final Intent intent = getIntent();

        double Dist = Double.parseDouble(intent.getStringExtra("distance"));
        int dist = (int) Dist;
        clientID = intent.getStringExtra("name");
        userId = intent.getStringExtra("userId");

        double time = Double.parseDouble(intent.getStringExtra("distance")) * 1.5;
        distance.setText(intent.getStringExtra("distance") + "Km,  " + time + " min");


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

        if (clientType.equalsIgnoreCase("bon")) {
            barTimer.setProgressDrawable(getResources().getDrawable(R.drawable.drawable_new_client));
        } else {
            barTimer.setProgressDrawable(getResources().getDrawable(R.drawable.green_circular));
        }


        map = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_command));
        map.getMapAsync(this);

        lat = intent.getStringExtra("startLat");
        lng = intent.getStringExtra("startLong");

        distance.setText("5 min /" + dist + "km");
        startText.setText("De : " + intent.getStringExtra("start"));

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").child(userId).child(clientID).removeValue();
            }
        });

        FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").
                child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        startTimer();
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                            FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").child(data.getKey()).child(clientID).removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

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


                                    FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").child(data.getKey()).child(clientID).removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }
//                        commandActivity.this.finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    static boolean active = false;

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
            }

            @Override
            public void onFinish() {
                showCustomDialog();
            }
        }.start();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
//        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.person_green);
        int height = 150;
        int width = 80;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.person_green);
        Bitmap b=bitmapdraw.getBitmap();
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

     AlertDialog.Builder dialogBuilder;
     AlertDialog OptionDialog;
    public void showCustomDialog() {
         dialogBuilder = new AlertDialog.Builder(commandActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.content_misses_ride_request, null);
        dialogBuilder.setView(dialogView);
         OptionDialog = dialogBuilder.create();
        Button btnOk = dialogView.findViewById(R.id.btn_passer_hors);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OptionDialog.dismiss();
                FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").child(userId).child(clientID).removeValue();
            }
        });

        Button btnCancel = dialogView.findViewById(R.id.btn_rester_engine);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OptionDialog.dismiss();
                finish();
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
        if (OptionDialog != null)
        OptionDialog.dismiss();
    }

}