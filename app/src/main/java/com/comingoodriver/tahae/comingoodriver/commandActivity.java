package com.comingoodriver.tahae.comingoodriver;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import comingoo.vone.tahae.comingoodriver.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class commandActivity extends AppCompatActivity {
    public static Activity clientR;
    private TextView name;
    private TextView distance;
    private TextView startText;
    private TextView arrivalText;
    private Button decline;
    private Button accept;
    private MediaPlayer mp;
    
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



        mp = MediaPlayer.create(this, R.raw.ring);
        mp.setLooping(true);
        mp.start();



        // name  = (TextView) findViewById(R.id.textView10);
        distance = (TextView) findViewById(R.id.textView8);
        startText = (TextView) findViewById(R.id.textView9);
        decline = (Button) findViewById(R.id.decline);
        accept = (Button) findViewById(R.id.accept);

        final TextView clientLevel = (TextView) findViewById(R.id.textView6);

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        final Intent intent = getIntent();

        double Dist = Double.parseDouble(intent.getStringExtra("distance"));
        int dist = (int) Dist;
        final String clientID = intent.getStringExtra("name");
        final String userId = intent.getStringExtra("userId");

        double time = Double.parseDouble(intent.getStringExtra("distance")) * 1.5;
        distance.setText(intent.getStringExtra("distance") +"Km,  " + time + " min");
        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("image").getValue(String.class) != null){
                    if(dataSnapshot.child("image").getValue(String.class).length() > 0){
                        Picasso.get().load(dataSnapshot.child("image").getValue(String.class)).fit().centerCrop().into((CircleImageView) findViewById(R.id.centerImage));
                    }

                    if(dataSnapshot.child("level").getValue(String.class).equals("2"))
                        clientLevel.setText("Nouveau client");

                    if(dataSnapshot.child("level").getValue(String.class).equals("1"))
                        clientLevel.setText("Client potentiel");

                    if(dataSnapshot.child("level").getValue(String.class).equals("0"))
                        clientLevel.setText("Bon level");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        distance.setText("5 min " +dist + "km");
        startText.setText("De : " + intent.getStringExtra("start"));

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").child(userId).child(clientID).removeValue();
            }
        });

        FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        return ;
                    }
                });
                FirebaseDatabase.getInstance().getReference("COURSES").orderByChild("client").equalTo(clientID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){

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
                                    for(DataSnapshot data : dataSnapshot.getChildren()){
                                        FirebaseDatabase.getInstance().getReference("PICKUPREQUEST").child(data.getKey()).child(clientID).removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }else{

                            for (DataSnapshot dataS : dataSnapshot.getChildren()){
                                if(dataS.child("state").getValue(String.class).equals("3")){

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
                                            for(DataSnapshot data : dataSnapshot.getChildren()){
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

    @Override
    public void onStop() {
        super.onStop();

        active = false;
    }


}
