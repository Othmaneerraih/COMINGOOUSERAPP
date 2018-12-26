package com.comingoo.driver.fousa.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.comingoo.driver.fousa.R;
import com.comingoo.driver.fousa.interfaces.DataCallBack;
import com.comingoo.driver.fousa.utility.CustomAnimation;
import com.comingoo.driver.fousa.utility.Utilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsNewActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapsVM mapsVM;
    private double rating = 0.0;
    private String driverName = "";
    private String driverImage = "";
    private String driverNumber = "";
    private String debit = "";
    private String todayTrips = "";
    private String todayEarnings = "";
    private String driverId = "";
    private String TAG = "MapsNewActivity";
    private DecimalFormat df2 = new DecimalFormat("0.##");

    private CircularImageView clientImage;
    private TextView clientNameTv;
    private TextView driverInfoTv;
    private TextView totalCourseTv;
    private TextView dateTv;
    private CircleImageView closeBtn;
    private CircleImageView callBtn;
    private TextView courseDetailsTv;
    private RelativeLayout cancelView;
    private Button courseActionBtn;
    private RelativeLayout clientInfoLayout;
    private LinearLayout voipView;
    private TextView telephoneTv;
    private TextView voipTv;

    private RelativeLayout destinationLayout;
    private TextView destTimeTxt;
    private TextView addressTxt;

    private Button offlineBtn;
    private Button switchOnlineBtn;
    private Button onlineBtn;
    private RelativeLayout statusLayout;

    private CircleImageView cancelRideIv;
    private Button moneyBtn;
    private ImageButton currentLocationBtn;
    private ImageButton menuBtn;
    private ImageButton wazeBtn;

    private FlowingDrawer mDrawer;
    private CircleImageView profileImage;
    private TextView nameTxt;
    private TextView ratingTxt;
    private RelativeLayout homeLayout;
    private TextView homeTxt;
    private RelativeLayout historiqueLayout;
    private TextView historiqueTxt;
    private RelativeLayout inboxLayout;
    private TextView inboxTxt;
    private RelativeLayout comingoonyouLayout;
    private TextView comingoonyouTxt;
    private RelativeLayout aideLayout;
    private TextView aideTxt;
    private RelativeLayout logoutLayout;
    private TextView logoutTxt;

    private GoogleMap mMap;
    private LatLng userLatLng;
    private RelativeLayout.LayoutParams params;
    private boolean isLoud = false;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_new);
        permission();
        initialize();
    }

    private void permission() {
        if (!Utilities.isNetworkConnectionAvailable(MapsNewActivity.this)) {
            Utilities.checkNetworkConnection(MapsNewActivity.this);
        }
        Utilities.displayLocationSettingsRequest(MapsNewActivity.this, TAG, REQUEST_CHECK_SETTINGS);
        if (ContextCompat.checkSelfPermission(MapsNewActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsNewActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 4);
        }
    }


    private void initialize() {
        // NOTE : Map init
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // NOTE : Banner init
        moneyBtn = findViewById(R.id.money_btn);
        destinationLayout = findViewById(R.id.destination_layout);
        destTimeTxt = findViewById(R.id.dest_time_txt);
        addressTxt = findViewById(R.id.address_txt);
        //initially hide
//        destinationLayout.setVisibility(View.GONE);

        // NOTE : Client init
        clientImage = findViewById(R.id.user_image);
        clientNameTv = findViewById(R.id.user_name_txt);
        totalCourseTv = findViewById(R.id.course_count_txt);
        driverInfoTv = findViewById(R.id.driver_details_txt);
        courseDetailsTv = findViewById(R.id.course_details_txt);
        closeBtn = findViewById(R.id.close_button);
        callBtn = findViewById(R.id.call_button);
        cancelView = findViewById(R.id.cancel_view);
        voipView = findViewById(R.id.voip_view);
        dateTv = findViewById(R.id.date_txt);
        courseActionBtn = findViewById(R.id.course_action_button);
        telephoneTv = findViewById(R.id.tv_telephone);
        voipTv = findViewById(R.id.tv_voip);
        clientInfoLayout = findViewById(R.id.client_info_layout);
        // NOTE : Those are initially Hide
        clientInfoLayout.setVisibility(View.GONE);
        closeBtn.setVisibility(View.GONE);
        voipView.setVisibility(View.GONE);

        // NOTE : botton status init
        offlineBtn = findViewById(R.id.offline_button);
        switchOnlineBtn = findViewById(R.id.switch_online_button);
        onlineBtn = findViewById(R.id.online_button);
        statusLayout = findViewById(R.id.status_layout);

        // NOTE : Those are initially Hide
        onlineBtn.setVisibility(View.GONE);

        // NOTE : Common init
        currentLocationBtn = findViewById(R.id.my_position_button);
        menuBtn = findViewById(R.id.menu_button);
        wazeBtn = findViewById(R.id.waze_button);
        cancelRideIv = findViewById(R.id.iv_cancel_ride);
        //initially hide
        cancelRideIv.setVisibility(View.GONE);
        wazeBtn.setVisibility(View.GONE);

        // NOTE : Drawer init
        mDrawer = findViewById(R.id.drawer_layout);
        profileImage = findViewById(R.id.profile_image);
        nameTxt = findViewById(R.id.name_txt);
        ratingTxt = findViewById(R.id.rating_txt);
        homeLayout = findViewById(R.id.home_layout);
        homeTxt = findViewById(R.id.home_txt);
        historiqueLayout = findViewById(R.id.historique_layout);
        historiqueTxt = findViewById(R.id.history_txt);
        inboxLayout = findViewById(R.id.inbox_layout);
        inboxTxt = findViewById(R.id.inbox_txt);
        comingoonyouLayout = findViewById(R.id.comingoonyou_layout);
        comingoonyouTxt = findViewById(R.id.comingoonyou_txt);
        aideLayout = findViewById(R.id.aide_layout);
        aideTxt = findViewById(R.id.aide_txt);
        logoutLayout = findViewById(R.id.logout_layout);
        logoutTxt = findViewById(R.id.logout_txt);


        df2.setRoundingMode(RoundingMode.UP);
        mapsVM = new MapsVM();

        mapsVM.checkLogin(MapsNewActivity.this, new DataCallBack() {
            @Override
            public void callbackCall(boolean success, String drivrNam, String drivrImg, String drivrNum, String debt, String todystrp, String todysErn, double rat, String drivrId) {
                if(success){
                    driverName = drivrNam;
                    driverImage = drivrImg;
                    driverNumber = drivrNum;
                    debit = debt;
                    todayTrips = todystrp;
                    todayEarnings = todysErn;
                    rating = rat;
                    driverId = drivrId;
                }else{
                    Intent intent = new Intent(MapsNewActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        if (prefs.getString("online", "0").equals("1"))
            switchOnlineUI();


        telephoneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (clientPhoneNumber != null) {
//                    try {
//                        String callNumber = clientPhoneNumber;
//                        if (callNumber.contains("+212")) {
//                            callNumber = callNumber.replace("+212", "");
//                        }
//
//                        Intent intent = new Intent(Intent.ACTION_DIAL);
//                        intent.setData(Uri.parse("tel:" + callNumber));
//                        startActivity(intent);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });

        voipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!driverId.isEmpty()) {
                    voipTv.setClickable(false);
                    voipTv.setEnabled(false);
//                    Intent intent = new Intent(MapsNewActivity.this, VoipCallingActivity.class);
//                    intent.putExtra("driverId", driverId);
//                    intent.putExtra("clientId", clientId);
//                    intent.putExtra("clientName", clientName);
//                    intent.putExtra("clientImage", clientImageUri);
//                    startActivity(intent);
                }
            }
        });

        if(!driverId.equals("")){
            SinchClient sinchClient = Sinch.getSinchClientBuilder()
                    .context(this)
                    .userId(driverId)
                    .applicationKey(getString(R.string.sinch_key))
                    .applicationSecret(getString(R.string.sinch_app_secret))
                    .environmentHost(getString(R.string.sinch_envirenment))
                    .build();

            sinchClient.setSupportCalling(true);
            sinchClient.startListeningOnActiveConnection();
            sinchClient.start();

            sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
        }



        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NOTE : show call button & hide close btn
                closeBtn.setVisibility(View.GONE);
                callBtn.setVisibility(View.VISIBLE);
                voipView.setVisibility(View.GONE);
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NOTE : show close button & hide call btn
                closeBtn.setVisibility(View.VISIBLE);
                callBtn.setVisibility(View.GONE);
                voipView.setVisibility(View.VISIBLE);

            }
        });


        switchOnlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchOnlineUI();
            }
        });

        onlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchOfflineUI();
            }
        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openMenu();
            }
        });
        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeMenu();
            }
        });
        historiqueLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsNewActivity.this, HistoriqueActivity.class));
            }
        });

        inboxLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsNewActivity.this, NotificationActivity.class));
            }
        });
        aideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsNewActivity.this, AideActivity.class));
            }
        });
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                switchOfflineUI();
                startActivity(new Intent(MapsNewActivity.this, MainActivity.class));
                finish();
            }
        });


        cancelRideIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideCancelDialog();
            }
        });

    }

    private void rideCancelDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MapsNewActivity.this);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cancel_ride_dialog, null);
        alertDialog.getWindow().setContentView(dialogView);

        final Button btnYesCancelRide = dialogView.findViewById(R.id.btn_yes_cancel_ride);
        final Button btnNoDontCancelRide = dialogView.findViewById(R.id.btn_dont_cancel_ride);

        btnYesCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnYesCancelRide.setBackgroundColor(Color.WHITE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btnYesCancelRide.setTextColor(getApplicationContext().getColor(R.color.primaryLight));
                } else {
                    btnYesCancelRide.setTextColor(getApplicationContext().getResources().getColor(R.color.primaryLight));
                }

                btnNoDontCancelRide.setBackgroundColor(Color.TRANSPARENT);
                btnNoDontCancelRide.setTextColor(Color.WHITE);


//                FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).child("state").setValue("5");

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 3000ms
//                        FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).removeValue();
                    }
                }, 3000);

                switchOnlineUI();

                alertDialog.dismiss();
            }
        });

        btnNoDontCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                btnYesCancelRide.setBackgroundColor(Color.TRANSPARENT);
                btnYesCancelRide.setTextColor(Color.WHITE);
                btnNoDontCancelRide.setBackgroundColor(Color.WHITE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btnNoDontCancelRide.setTextColor(getApplicationContext().getColor(R.color.primaryLight));
                } else {
                    btnNoDontCancelRide.setTextColor(getApplicationContext().getResources().getColor(R.color.primaryLight));
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (ActivityCompat.checkSelfPermission(MapsNewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MapsNewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MapsNewActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsNewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS}, 1);
        } else {
            mMap.setMyLocationEnabled(true);
            getLastLocation();
            currentLocationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getLastLocation();
                }
            });
        }
    }


    private void courseUIOff() {
        moneyBtn.setVisibility(View.VISIBLE);
        statusLayout.setVisibility(View.VISIBLE);
        clientInfoLayout.setVisibility(View.GONE);
        destinationLayout.setVisibility(View.GONE);
        menuBtn.setVisibility(View.VISIBLE);
        cancelRideIv.setVisibility(View.GONE);
    }

    public void getLastLocation() {
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // GPS location can be null if GPS is switched off
                    if (location != null) {
                        userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        goToLocation(userLatLng.latitude, userLatLng.longitude);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("MapDemoActivity", "Error trying to get last GPS location");
                    e.printStackTrace();
                }
            });
        }
    }


    private void goToLocation(final Double lat, final Double lng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void logout() {
        final SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        prefs.edit().remove("userId").apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);

        if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(MapsNewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsNewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsNewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                //startLocationUpdates();
                if (mMap != null)
                    mMap.setMyLocationEnabled(true);

                getLastLocation();
                currentLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getLastLocation();
                    }
                });
            }
        }
    }

    private void mute(AudioManager audioManager, CircleImageView iv_mute) {
        if (!audioManager.isMicrophoneMute()) {
            audioManager.setMicrophoneMute(true);
            iv_mute.setImageResource(R.drawable.clicked_mute);
        } else {
            audioManager.setMicrophoneMute(false);
            iv_mute.setImageResource(R.drawable.mute_bt);
        }
    }

    private void switchOnlineUI() {
        CustomAnimation.fadeOut(MapsNewActivity.this, offlineBtn, 0, 10);
        CustomAnimation.fadeOut(MapsNewActivity.this, switchOnlineBtn, 0, 10);
        CustomAnimation.fadeIn(MapsNewActivity.this, onlineBtn, 500, 10);
        SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        prefs.edit().putString("online", "1").apply();
    }

    private void switchOfflineUI(boolean... params) {
        CustomAnimation.fadeIn(MapsNewActivity.this, offlineBtn, 500, 10);
        CustomAnimation.fadeIn(MapsNewActivity.this, switchOnlineBtn, 500, 10);
        CustomAnimation.fadeOut(MapsNewActivity.this, onlineBtn, 0, 10);

        SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        prefs.edit().putString("online", "0").apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && RESULT_OK == -1 && data.hasExtra("result")) {
            voipTv.setClickable(true);
            voipTv.setEnabled(true);
        }

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        getLastLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(MapsNewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsNewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsNewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            getLastLocation();
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {

            Toast.makeText(MapsNewActivity.this, "incoming call", Toast.LENGTH_SHORT).show();
            showDialog(MapsNewActivity.this, incomingCall);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        voipTv.setClickable(true);
        voipTv.setEnabled(true);

        if (ActivityCompat.checkSelfPermission(MapsNewActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsNewActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsNewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            getLastLocation();
        }
    }

    public void showDialog(final Context context, final Call call) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dialog_incomming_call, null, false);
            dialog.setContentView(view);


            CircleImageView iv_user_image_voip_one = dialog.findViewById(R.id.iv_user_image_voip_one);
            final CircleImageView iv_cancel_call_voip_one = dialog.findViewById(R.id.iv_cancel_call_voip_one);
            final CircleImageView iv_recv_call_voip_one = dialog.findViewById(R.id.iv_recv_call_voip_one);
            final TextView caller_name = dialog.findViewById(R.id.callerName);
            final TextView callState = dialog.findViewById(R.id.callState);

            final CircleImageView iv_mute = dialog.findViewById(R.id.iv_mute);
            final CircleImageView iv_loud = dialog.findViewById(R.id.iv_loud);
            TextView tv_name_voip_one = dialog.findViewById(R.id.tv_name_voip_one);

            iv_recv_call_voip_one.setClickable(true);
            iv_mute.setVisibility(View.GONE);
            iv_loud.setVisibility(View.GONE);

            final MediaPlayer mp = MediaPlayer.create(this, R.raw.ring);
            mp.start();

            call.addCallListener(new CallListener() {
                @Override
                public void onCallEnded(Call endedCall) {
                    //call ended by either party
                    dialog.findViewById(R.id.incoming_call_view).setVisibility(View.GONE);
                    setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
                    try {
                        if (mp != null) {
                            if (mp.isPlaying()) {
                                mp.stop();
                                mp.release();
                            }
                        }
                        iv_mute.setVisibility(View.GONE);
                        iv_loud.setVisibility(View.GONE);
                        caller_name.setVisibility(View.GONE);
                        callState.setText("");
                        dialog.dismiss();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCallEstablished(final Call establishedCall) {
                    //incoming call was picked up
                    dialog.findViewById(R.id.incoming_call_view).setVisibility(View.VISIBLE);
                    setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                    try {
                        if (mp != null) {
                            if (mp.isPlaying()) {
                                mp.stop();
                                mp.release();
                            }
                        }
                        callState.setText("connected");
                        iv_mute.setVisibility(View.VISIBLE);
                        iv_loud.setVisibility(View.VISIBLE);

                        iv_recv_call_voip_one.setVisibility(View.GONE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        }
                        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        iv_cancel_call_voip_one.setLayoutParams(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCallProgressing(Call progressingCall) {
                    //call is ringing
                    try {
                        if (mp.isPlaying()) {
                            mp.stop();
                            mp.release();
                        }
                        dialog.findViewById(R.id.incoming_call_view).setVisibility(View.VISIBLE);
                        caller_name.setText(progressingCall.getDetails().getDuration() + "");
                        caller_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        iv_mute.setVisibility(View.VISIBLE);
                        iv_loud.setVisibility(View.VISIBLE);
                        caller_name.setTypeface(null, Typeface.BOLD);
                        callState.setText("ringing");
                        iv_recv_call_voip_one.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        }
                        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        iv_cancel_call_voip_one.setLayoutParams(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
                    //don't worry about this right now
                }
            });

            final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            final int origionalVolume = am != null ? am.getStreamVolume(AudioManager.STREAM_MUSIC) : 0;
            if (am != null) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            }

            if (am != null) {
                switch (am.getRingerMode()) {
                    case 0:
                        mp.start();
                        break;
                    case 1:
                        mp.start();
                        break;
                    case 2:
                        mp.start();
                        break;
                }
            }

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    try {
                        if (mp.isPlaying()) {
                            mp.stop();
                            mp.release();
                        }
                        if (am != null) {
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, origionalVolume, 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            if (ContextCompat.checkSelfPermission(MapsNewActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MapsNewActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsNewActivity.this,
                        new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_PHONE_STATE},
                        1);
            }

            caller_name.setVisibility(View.VISIBLE);
            caller_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            caller_name.setTypeface(null, Typeface.NORMAL);      // for Normal Text

//            caller_name.setText(clientName + " vous appelle");
//            tv_name_voip_one.setText(clientName);
//            if (clientImageUri != null) {
//                if (!clientImageUri.isEmpty()) {
//                    Picasso.get().load(clientImageUri).into(iv_user_image_voip_one);
//                }
//            }

            iv_cancel_call_voip_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call.hangup();
                    try {
                        if (mp != null) {
                            if (mp.isPlaying()) {
                                mp.stop();
                                mp.release();
                            }
                        }
                        dialog.dismiss();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            params = (RelativeLayout.LayoutParams) iv_cancel_call_voip_one.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            iv_cancel_call_voip_one.setLayoutParams(params);


            iv_recv_call_voip_one.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        if (mp != null) {
                            if (mp.isPlaying()) {
                                mp.stop();
                                mp.release();
                            }
                        }
                        call.answer();
                        iv_recv_call_voip_one.setClickable(false);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            iv_loud.setBackgroundColor(Color.WHITE);
            iv_loud.setCircleBackgroundColor(Color.WHITE);
            iv_mute.setBackgroundColor(Color.WHITE);
            iv_mute.setCircleBackgroundColor(Color.WHITE);

            final AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setMode(AudioManager.MODE_IN_CALL);
            }
            if (audioManager != null) {
                audioManager.setSpeakerphoneOn(false);
            }
            if (audioManager != null) {
                audioManager.setMicrophoneMute(false);
            }

            iv_loud.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isLoud) {
                        if (audioManager != null) {
                            audioManager.setSpeakerphoneOn(true);
                        }
                        iv_loud.setImageResource(R.drawable.clicked_speaker_bt);
                        isLoud = true;
                    } else {
                        iv_loud.setImageResource(R.drawable.speaker_bt);
                        if (audioManager != null) {
                            audioManager.setSpeakerphoneOn(false);
                        }
                        isLoud = false;
                    }
                }
            });


            iv_mute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (audioManager != null) {
                        mute(audioManager,iv_mute);
                    }
                }
            });

            final Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            }
            if (window != null) {
                window.setGravity(Gravity.CENTER);
            }
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}