package com.comingoo.driver.fousa.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.comingoo.driver.fousa.service.CourseService;
import com.comingoo.driver.fousa.utility.CustomAnimation;
import com.comingoo.driver.fousa.service.DriverService;
import com.comingoo.driver.fousa.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.location.GpsStatus.GPS_EVENT_STARTED;
import static android.location.GpsStatus.GPS_EVENT_STOPPED;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Button offlineButton;
    private Button onlineButton;
    private Button switchOnlineButton;

    private ImageButton menuButton;
    private ImageButton myPositionButton;

    public static ImageButton wazeButton;

    private RelativeLayout clientInfoLayout;
    private ConstraintLayout destinationLayout, userInfoLayout;

    private ImageView arrowImage;
    private ImageView whitePersonImage;

    private TextView addressText;

    private TextView destTime;

    private Button courseActionButton;
    private RelativeLayout cancel_view;
    private ImageView ivCancelCourse;

    private Button money;

    private FlowingDrawer mDrawer;

    private ConstraintLayout Acceuil;
    private ConstraintLayout Historique;
    private ConstraintLayout Inbox;
    private ConstraintLayout ComingoonYou;
    private ConstraintLayout Aide;
    private ConstraintLayout logout;
    private String driverId = "";


    private Marker startPositionMarker;

    int height = 250;
    int width = 120;
    BitmapDrawable bitmapdraw;
    Bitmap smallMarker;

    private static final String APP_KEY = "185d9822-a953-4af6-a780-b0af1fd31bf7";
    private static final String APP_SECRET = "ZiJ6FqH5UEWYbkMZd1rWbw==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS}, 1);
        }

        if (offlineButton.getVisibility() == View.VISIBLE) {
            switchOnlineUI();
        }
    }

    BitmapFactory.Options bOptions;
    int imageHeight;
    int imageWidth;
    int lastImageHeight;
    int lastImageWidth;
    int inSampleSize;

    public Bitmap scaleBitmap(int reqWidth, int reqHeight, int resId) {
        // Raw height and width of image

        bOptions = new BitmapFactory.Options();
        bOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, bOptions);
        imageHeight = bOptions.outHeight;
        imageWidth = bOptions.outWidth;

        imageHeight = bOptions.outHeight;
        imageWidth = bOptions.outWidth;
        inSampleSize = 1;

        if (imageHeight > reqHeight || imageWidth > reqWidth) {

            lastImageHeight = imageHeight / 2;
            lastImageWidth = lastImageWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((lastImageHeight / inSampleSize) >= reqHeight
                    && (lastImageWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }


        // First decode with inJustDecodeBounds=true to check dimensions
        bOptions = new BitmapFactory.Options();
        bOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, bOptions);

        // Calculate inSampleSize
        bOptions.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        bOptions.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(getResources(), resId, bOptions);
    }

    private void loadImages() {
        //  locationStartPin.setImageBitmap(scaleBitmap(76, 56, R.drawable.depart_pin));
        switchOnlineButton.setBackground(new BitmapDrawable(getResources(), scaleBitmap(60, 60, R.drawable.goo_bt)));
        // switchOnlineButton.setImageBitmap(scaleBitmap(60, 60, R.drawable.goo_bt));
        menuButton.setBackground(new BitmapDrawable(getResources(), scaleBitmap(40, 40, R.drawable.menu_icon)));
        myPositionButton.setBackground(new BitmapDrawable(getResources(), scaleBitmap(40, 40, R.drawable.my_location)));
        wazeButton.setBackground(new BitmapDrawable(getResources(), scaleBitmap(40, 40, R.drawable.waze_icon)));
//        contactButton.setBackground(new BitmapDrawable(getResources(), scaleBitmap(40, 40, R.drawable.contact)));
        arrowImage.setBackground(new BitmapDrawable(getResources(), scaleBitmap(30, 30, R.drawable.arrow_blue)));
        whitePersonImage.setBackground(new BitmapDrawable(getResources(), scaleBitmap(30, 50, R.drawable.person_white)));
    }

    private float density;
    private float dpHeight;
    private float dpWidth;
    private Intent intent;
    private Call call;
    private TextView tv_appelle_voip, tv_appelle_telephone;
    private SinchClient sinchClient;
    private String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        statusCheck();
        if (!isNetworkConnectionAvailable()) {
            checkNetworkConnection();
        }
        displayLocationSettingsRequest(MapsActivity.this);
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

            new CheckLoginService().execute();

            if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 4);
            }
            intent = new Intent(MapsActivity.this, DriverService.class);

            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);

            density = getResources().getDisplayMetrics().density;
            dpHeight = outMetrics.heightPixels / density;
            dpWidth = outMetrics.widthPixels / density;

            initializeViews();

            SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
            if (prefs.getString("online", "0").equals("1"))
                switchOnlineUI();

            loadImages();

            tv_appelle_voip = findViewById(R.id.tv_appelle_voip);
            tv_appelle_telephone = findViewById(R.id.tv_appelle_telephone);

            tv_appelle_telephone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clientPhoneNumber != null) {
                        try {
                            String callNumber = clientPhoneNumber;
                            if (callNumber.contains("+212")) {
                                callNumber = callNumber.replace("+212", "");
                            }

                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + callNumber));
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            tv_appelle_voip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!driverId.isEmpty()) {
                        tv_appelle_voip.setClickable(false);
                        tv_appelle_voip.setEnabled(false);
                        Intent intent = new Intent(MapsActivity.this, VoipCallingActivity.class);
                        intent.putExtra("driverId", driverId);
                        intent.putExtra("clientId", clientId);
                        intent.putExtra("clientName", clientName);
                        intent.putExtra("clientImage", clientImageUri);
                        startActivity(intent);
                    }
                }
            });

            clientImage = findViewById(R.id.clientImage);
            name = findViewById(R.id.name);
            tvLastCourse = findViewById(R.id.textView5);
            totalCourse = findViewById(R.id.textView2);
            close_button = findViewById(R.id.close_button);
            call_button = findViewById(R.id.call_button);
            voip_view = findViewById(R.id.voip_view);
            date = findViewById(R.id.textView6);
            df2.setRoundingMode(RoundingMode.UP);

            sinchClient = Sinch.getSinchClientBuilder()
                    .context(this)
                    .userId(driverId)
                    .applicationKey(APP_KEY)
                    .applicationSecret(APP_SECRET)
                    .environmentHost(ENVIRONMENT)
                    .build();

            sinchClient.setSupportCalling(true);
            sinchClient.startListeningOnActiveConnection();
            sinchClient.start();

            sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());


            close_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    close_button.setVisibility(View.GONE);
                    call_button.setVisibility(View.VISIBLE);
                    voip_view.setVisibility(View.GONE);
                }
            });

            call_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    close_button.setVisibility(View.VISIBLE);
                    call_button.setVisibility(View.GONE);
                    voip_view.setVisibility(View.VISIBLE);

                }
            });


            switchOnlineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchOnlineUI();
                }
            });

            onlineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchOfflineUI();
                }
            });

            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawer.openMenu();
                }
            });
            Acceuil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawer.closeMenu();
                }
            });
            Historique.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MapsActivity.this, HistoriqueActivity.class));
                }
            });

            Inbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MapsActivity.this, NotificationActivity.class));
                }
            });
            Aide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MapsActivity.this, AideActivity.class));
                }
            });
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                    switchOfflineUI();
                    stopCourseService();
                    startActivity(new Intent(MapsActivity.this, MainActivity.class));
                    finish();
                }
            });
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.driver_pin);
            smallMarker = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width, height, false);

            new checkCourseTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


        ivCancelCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rideCancelDialog();
            }
        });
    }

    private void rideCancelDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.content_cancel_ride_dialog, null);
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


                FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).child("state").setValue("5");

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 3000ms
                        FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).removeValue();
                    }
                }, 3000);

                switchOnlineUI();

                voip_view.setVisibility(View.GONE);
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

    private void initializeViews() {
        onlineButton = findViewById(R.id.online_button);
        offlineButton = findViewById(R.id.offline_button);
        switchOnlineButton = findViewById(R.id.switch_online_button);

        menuButton = findViewById(R.id.menu_button);
        myPositionButton = findViewById(R.id.my_position_button);

        wazeButton = findViewById(R.id.waze_button);

        clientInfoLayout = findViewById(R.id.clientInfo);
        destinationLayout = findViewById(R.id.destination_layout);
        userInfoLayout = findViewById(R.id.constraintLayout);

        arrowImage = findViewById(R.id.arrow_image);
        whitePersonImage = findViewById(R.id.white_person_image);

        addressText = findViewById(R.id.addressText);

        destTime = findViewById(R.id.destTime);

        courseActionButton = findViewById(R.id.course_action_button);
        cancel_view = findViewById(R.id.cancel_view);
        ivCancelCourse = findViewById(R.id.iv_cancel_ride);

        mDrawer = findViewById(R.id.drawerlayout);

        Acceuil = findViewById(R.id.acceuil);
        Historique = findViewById(R.id.historique);
        Inbox = findViewById(R.id.inbox);
        ComingoonYou = findViewById(R.id.comingoonyou);
        Aide = findViewById(R.id.aide);
        logout = findViewById(R.id.logout);

        money = findViewById(R.id.money);

        clientInfoLayout.setVisibility(View.GONE);
        userInfoLayout.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS}, 1);
        } else {
            //startLocationUpdates();
            mMap.setMyLocationEnabled(true);
            getLastLocation();
            myPositionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getLastLocation();
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        tv_appelle_voip.setClickable(true);
        tv_appelle_voip.setEnabled(true);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            getLastLocation();
        }


        FirebaseDatabase.getInstance().getReference("ONLINEDRIVERS").child(driverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    switchOfflineUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            getLastLocation();
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;

            Toast.makeText(MapsActivity.this, "incoming call", Toast.LENGTH_SHORT).show();
//            try {
//                if (VoipCallingActivity.activity != null)
//                    if (!VoipCallingActivity.activity.isFinishing())
//                        VoipCallingActivity.activity.finish();
//                showDialog(MapsActivity.this, call);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            showDialog(MapsActivity.this, call);
        }
    }

    //    private AudioManager audioManager;
    boolean isLoud = false;
    private MediaPlayer mp;
    TextView callState, caller_name, tv_name_voip_one;
    private Handler mHandler = new Handler();
    CircleImageView iv_user_image_voip_one, iv_cancel_call_voip_one, iv_mute, iv_loud, iv_recv_call_voip_one;

    RelativeLayout relativeLayout;
    RelativeLayout.LayoutParams params;

    private int mHour, mMinute; // variables holding the hour and minute
    private Runnable mUpdate = new Runnable() {

        @Override
        public void run() {
            mMinute += 1;
            // just some checks to keep everything in order
            if (mMinute >= 60) {
                mMinute = 0;
                mHour += 1;
            }
            if (mHour >= 24) {
                mHour = 0;
            }
            // or call your method
            caller_name.setText(mHour + ":" + mMinute);
            mHandler.postDelayed(this, 1000);
        }
    };

    public void showDialog(final Context context, final Call call) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_incomming_call, null, false);
            dialog.setContentView(view);

            iv_user_image_voip_one = dialog.findViewById(R.id.iv_user_image_voip_one);
            iv_cancel_call_voip_one = dialog.findViewById(R.id.iv_cancel_call_voip_one);
            iv_recv_call_voip_one = dialog.findViewById(R.id.iv_recv_call_voip_one);
            caller_name = dialog.findViewById(R.id.callerName);
            callState = dialog.findViewById(R.id.callState);

            iv_mute = dialog.findViewById(R.id.iv_mute);
            iv_loud = dialog.findViewById(R.id.iv_loud);
            tv_name_voip_one = dialog.findViewById(R.id.tv_name_voip_one);

            iv_recv_call_voip_one.setClickable(true);
//        iv_recv_call_voip_one.setEnabled(true);
            iv_mute.setVisibility(View.GONE);
            iv_loud.setVisibility(View.GONE);

            mp = MediaPlayer.create(this, R.raw.ring);
//        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
                        mHandler.removeCallbacks(mUpdate);// we need to remove our updates if the activity isn't focused(or even destroyed) or we could get in trouble
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

                        mHour = 00;//c.get(Calendar.HOUR_OF_DAY);
                        mMinute = 00;//c.get(Calendar.MINUTE);
                        caller_name.setText(mHour + ":" + mMinute);
                        mHandler.postDelayed(mUpdate, 1000); // 60000 a minute
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCallProgressing(Call progressingCall) {
                    //call is ringing
                    try {
                        if (mp != null) {
                            if (mp.isPlaying()) {
                                mp.stop();
                                mp.release();
                            }
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
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
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
                        am.setStreamVolume(AudioManager.STREAM_MUSIC, origionalVolume, 0);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            if (ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_PHONE_STATE},
                        1);
            }

            caller_name.setVisibility(View.VISIBLE);
            caller_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            caller_name.setTypeface(null, Typeface.NORMAL);      // for Normal Text

            caller_name.setText(clientName + " vous appelle");
            tv_name_voip_one.setText(clientName);
            if (clientImageUri != null) {
                if (!clientImageUri.isEmpty()) {
                    Picasso.get().load(clientImageUri).into(iv_user_image_voip_one);
                }
            }

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
                        audioManager.setSpeakerphoneOn(true);
                        iv_loud.setImageResource(R.drawable.clicked_speaker_bt);
                        isLoud = true;
                    } else {
                        iv_loud.setImageResource(R.drawable.speaker_bt);
                        audioManager.setSpeakerphoneOn(false);
                        isLoud = false;
                    }
                }
            });


            iv_mute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mute(audioManager);
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
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mute(AudioManager audioManager) {
        if (!audioManager.isMicrophoneMute()) {
            audioManager.setMicrophoneMute(true);
            iv_mute.setImageResource(R.drawable.clicked_mute);
        } else {
            audioManager.setMicrophoneMute(false);
            iv_mute.setImageResource(R.drawable.mute_bt);
        }
    }

    private void switchOnlineUI() {
        CustomAnimation.fadeOut(MapsActivity.this, offlineButton, 0, 10);
        CustomAnimation.fadeOut(MapsActivity.this, switchOnlineButton, 0, 10);
        CustomAnimation.fadeIn(MapsActivity.this, onlineButton, 500, 10);
        SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        prefs.edit().putString("online", "1").apply();
        startService(intent);
    }

    private void switchOfflineUI(boolean... params) {
        CustomAnimation.fadeIn(MapsActivity.this, offlineButton, 500, 10);
        CustomAnimation.fadeIn(MapsActivity.this, switchOnlineButton, 500, 10);
        CustomAnimation.fadeOut(MapsActivity.this, onlineButton, 0, 10);

        SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        prefs.edit().putString("online", "0").apply();
        stopService(intent);
        if (params.length > 0) {
            if (params[0])
                switchOnlineUI();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && RESULT_OK == -1 && data.hasExtra("result")) {
            tv_appelle_voip.setClickable(true);
            tv_appelle_voip.setEnabled(true);
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

    private void switchToCourseUI() {
        findViewById(R.id.statusConstraint).setVisibility(View.GONE);
        findViewById(R.id.money).setVisibility(View.GONE);
        menuButton.setVisibility(View.GONE);
        clientInfoLayout.setVisibility(View.VISIBLE);
        ivCancelCourse.setVisibility(View.VISIBLE);
        destinationLayout.setVisibility(View.VISIBLE);
        userInfoLayout.setBackgroundColor(Color.WHITE);
    }

    private void cancelCourseUI() {
        findViewById(R.id.statusConstraint).setVisibility(View.VISIBLE);
        findViewById(R.id.money).setVisibility(View.VISIBLE);
        menuButton.setVisibility(View.VISIBLE);
        clientInfoLayout.setVisibility(View.GONE);
        ivCancelCourse.setVisibility(View.GONE);
        destinationLayout.setVisibility(View.GONE);
    }


    private String getDateMonth(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000L);
        return DateFormat.format("MM-yyyy", cal).toString();
    }

    private String getDateDay(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000L);
        return DateFormat.format("dd", cal).toString();
    }

    public int GetUnixTime() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        return ((int) (now / 1000));

    }

    int RATE = 4;
    int cM = 0;
    boolean rideMorethanThree = false;
    Double finalPriceOfCourse = 0.0;
    Dialog dialog;
    boolean isPriceSeted = false;
    double debt = 0;
    private LatLng startPos;
    private LatLng endPos;
    private boolean isFixed;
    private double fixedPrice, price1, price2, price3, promoCode;
    double currentBil = 0;
    boolean isPromoCode = false;
    private Button price;
    private double currentDebt = 0.0;
    private double currentWallet = 0.0;
    private DecimalFormat df2 = new DecimalFormat("0.##");

    private class checkCourseFinished extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            final SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
            final String userId = prefs.getString("userId", null);
            driverId = userId;

            if (userId == null) return "";

            FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).
                    child("COURSE").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        FirebaseDatabase.getInstance().getReference("DRIVERFINISHEDCOURSES").
                                child(userId).child(Objects.requireNonNull(dataSnapshot.getValue(String.class))).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshott) {
                                if (dataSnapshott.exists()) {
                                    if (!isRatingPopupShowed) {
                                        isRatingPopupShowed = true;
                                        dialog = new Dialog(MapsActivity.this);
                                        dialog.setContentView(R.layout.finished_course);

                                        Button dialogButton = dialog.findViewById(R.id.button);
                                        final Button star1 = dialog.findViewById(R.id.star1);
                                        final Button star2 = dialog.findViewById(R.id.star2);
                                        final Button star3 = dialog.findViewById(R.id.star3);
                                        final Button star4 = dialog.findViewById(R.id.star4);
                                        final Button star5 = dialog.findViewById(R.id.star5);

                                        price = dialog.findViewById(R.id.button3);

                                        final ImageView imot = dialog.findViewById(R.id.stars_rating);

                                        final Button gotMoney = dialog.findViewById(R.id.button);
                                        final Button charge = dialog.findViewById(R.id.btn_recharger);
                                        final EditText moneyAmount = dialog.findViewById(R.id.editText);

                                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                stopService(intent);
                                                switchOnlineUI();
                                            }
                                        });


///////////***************calculating task start form here *************************


                                        FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).
                                                addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            final String clientID = dataSnapshot.child("client").getValue(String.class);
                                                            final int preWaitTime = Integer.parseInt(dataSnapshot.child("preWaitTime").getValue(String.class));
                                                            final double distanceTraveled = Double.parseDouble(dataSnapshot.child("distanceTraveled").getValue(String.class));
                                                            final int waitTime = Integer.parseInt(dataSnapshot.child("preWaitTime").getValue(String.class));
                                                            final String startA = (dataSnapshot.child("startAddress").getValue(String.class));
                                                            final String endA = (dataSnapshot.child("endAddress").getValue(String.class));

                                                            startPos = new LatLng(Double.parseDouble(dataSnapshot.child("startLat").getValue(String.class)),
                                                                    Double.parseDouble(dataSnapshot.child("startLong").getValue(String.class)));
                                                            if (Objects.requireNonNull(dataSnapshot.child("endLat").getValue(String.class)).length() > 0)
                                                                endPos = new LatLng(Double.parseDouble(dataSnapshot.child("endLat").getValue(String.class)),
                                                                        Double.parseDouble(dataSnapshot.child("endLong").getValue(String.class)));


                                                            FirebaseDatabase.getInstance().getReference("PRICES").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    Log.e("Price", " Price Calculation in Course Service");
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

                                                                        int preWait = waitTime / 60;
                                                                        SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
                                                                        prefs.edit().putString("online", "1").apply();


// *****************************         need to add here commision & promo code calculation   **********************************************************

                                                                        promoCode = 0.20;
                                                                        price1 = base + (distanceTraveled * km) + (att * waitTime);

                                                                        if (price1 < min) {
                                                                            price1 = min;
                                                                        }
                                                                        price2 = price1 * percent;
                                                                        price3 = price2 * (1 - promoCode);

                                                                        // need to get promo code here
                                                                        if (clientID != null) {
                                                                            FirebaseDatabase.getInstance().getReference("clientUSERS").
                                                                                    child(clientID).child("PROMOCODE").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    Log.e(TAG, "onDataChange: ");
                                                                                    if (dataSnapshot.exists()) {
                                                                                        Log.e(TAG, "PROMOCODE onDataChange: " + dataSnapshot.getValue(String.class));
                                                                                        isPromoCode = true;
                                                                                    } else {
                                                                                        isPromoCode = false;
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });
                                                                        }

                                                                        Log.e(TAG, "onDataChange: " + price1);
                                                                        Log.e(TAG, "onDataChange: " + price2);
                                                                        Log.e(TAG, "onDataChange: " + price3);
                                                                        Log.e(TAG, "onDataChange: " + isPromoCode);

                                                                        if (isPromoCode)
                                                                            currentBil = price3;
                                                                        else currentBil = price2;

                                                                        Log.e(TAG, "onDataChange: final currentBil Ujjwal:  " + currentBil);

                                                                        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").
                                                                                child(userId).child("EARNINGS").child(getDateMonth(GetUnixTime())).child(getDateDay(GetUnixTime())).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                                                                double earned = 0;
                                                                                int voyages = 0;
                                                                                if (dataSnapshot.exists()) {
                                                                                    try {
                                                                                        earned = Double.parseDouble(dataSnapshot.child("earnings").getValue(String.class));
                                                                                        voyages = Integer.parseInt(dataSnapshot.child("voyages").getValue(String.class));
                                                                                    } catch (NumberFormatException e) {
                                                                                        e.printStackTrace();
                                                                                    } catch (Exception e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                }

                                                                                if (isFixed)
                                                                                    earned += fixedPrice;
                                                                                else
                                                                                    earned += currentBil;

                                                                                voyages += 1;


                                                                                final double ee = earned;
                                                                                final int vv = voyages;

                                                                                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("debt").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                        double debt = 0;
                                                                                        if (dataSnapshot.exists()) {
                                                                                            debt = Double.parseDouble(dataSnapshot.getValue(String.class));
                                                                                            currentDebt = debt;
                                                                                        }

                                                                                        final Map<String, String> earnings = new HashMap<>();
                                                                                        earnings.put("earnings", "" + ee);
                                                                                        earnings.put("voyages", "" + vv);


                                                                                        Log.e(TAG, "onDataChange:clientID in calculation: ClientID: " + clientID);
                                                                                        Log.e(TAG, "onDataChange:clientID in calculation: DriverID: " + courseID);
                                                                                        Log.e(TAG, "onDataChange:clientID in calculation: currentBill: " + currentBil);

                                                                                        final double priviousDebt = debt;
                                                                                        if (clientID != null) {
                                                                                            FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                    if (dataSnapshot.child("SOLDE").exists()) {
                                                                                                        try {
                                                                                                            double oldSold = Double.parseDouble(dataSnapshot.child("SOLDE").getValue(String.class));

                                                                                                            // if user has solde & it is greater then the calculated price
                                                                                                            if (dataSnapshot.child("USECREDIT").getValue(String.class).equals("1") && Double.parseDouble(dataSnapshot.child("SOLDE").getValue(String.class)) >= currentBil) {

                                                                                                                double newSolde = oldSold - currentBil;
                                                                                                                FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("SOLDE").setValue("" + newSolde);
                                                                                                                FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("USECREDIT").setValue("1");
                                                                                                                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("PAID").setValue("1");
                                                                                                                double commission = currentBil * percent;
                                                                                                                double driverIncome = currentBil - commission;
                                                                                                                double newDebt = priviousDebt + driverIncome;
                                                                                                                currentDebt = newDebt;
                                                                                                                currentWallet = newSolde;
                                                                                                                FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).child("price").setValue("0.0");
                                                                                                                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("debt").setValue(Double.toString(newDebt));
                                                                                                                price.setText("0.0 MAD");

                                                                                                            } else {
                                                                                                                // if user has solde & it is small then the calculated price

                                                                                                                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("PAID").setValue("0");

                                                                                                                double commission = currentBil * percent;
                                                                                                                double userDue = currentBil - oldSold;
                                                                                                                double newDebt = priviousDebt + (currentBil - userDue - commission);
                                                                                                                currentDebt = newDebt;
                                                                                                                Log.e(TAG, "onDataChange: 3333333 old sold: " + commission);
                                                                                                                Log.e(TAG, "onDataChange: 3333333 old currentbill: " + userDue);
                                                                                                                currentWallet = 0.0;
                                                                                                                FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("SOLDE").setValue("" + currentWallet);
                                                                                                                FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("USECREDIT").setValue("0");
                                                                                                                FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).child("price").setValue(df2.format(userDue));


                                                                                                                price.setText(df2.format(userDue) + " MAD");

                                                                                                                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("debt").setValue(Double.toString(newDebt));
                                                                                                                Log.e(TAG, "onDataChange: new Debt" + newDebt);
                                                                                                            }
                                                                                                        } catch (NumberFormatException e) {
                                                                                                            e.printStackTrace();
                                                                                                            Log.e(TAG, "onDataChange:NumberFormatException " + e.getMessage());
                                                                                                        } catch (Exception e) {
                                                                                                            Log.e(TAG, "onDataChange:Exception " + e.getMessage());
                                                                                                            e.printStackTrace();
                                                                                                        }
                                                                                                    } else {

                                                                                                        // if user has  no solde
                                                                                                        try {
                                                                                                            double commission = currentBil * percent * -1;
                                                                                                            Log.e(TAG, "onDataChange: 4444444 commission: " + commission);

                                                                                                            double newDebt = (priviousDebt + commission);
                                                                                                            currentDebt = newDebt;
                                                                                                            FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("PAID").setValue("0");
                                                                                                            FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("debt").setValue(Double.toString(newDebt));
                                                                                                            FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).child("price").setValue(df2.format(currentBil));
                                                                                                            price.setText(df2.format(currentBil) + " MAD");
                                                                                                        } catch (Exception e) {
                                                                                                            e.printStackTrace();
                                                                                                        }
                                                                                                    }

                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                                }
                                                                                            });
                                                                                        }


                                                                                        if (clientID != null) {
                                                                                            FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("level").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                    if (Objects.requireNonNull(dataSnapshot.getValue(String.class)).equals("2"))
                                                                                                        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("level").setValue("1");

                                                                                                    if (Objects.requireNonNull(dataSnapshot.getValue(String.class)).equals("1"))
                                                                                                        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("level").setValue("0");
                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                                }
                                                                                            });
                                                                                        }


                                                                                        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("LASTCOURSE").setValue("Derniére course : Captain " + driverName + " / " + df2.format(currentBil) + " MAD");
                                                                                        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientID).child("COURSE").setValue(courseID);
                                                                                        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("COURSE").setValue(courseID);
                                                                                        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("EARNINGS").child(getDateMonth(GetUnixTime())).child(getDateDay(GetUnixTime())).setValue(earnings);


                                                                                        final Handler handler = new Handler();
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).removeValue();
                                                                                            }
                                                                                        }, 3000);

                                                                                        switchOnlineUI();
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


                                                                        DatabaseReference mCourse = null;
                                                                        if (clientID != null) {
                                                                            mCourse = FirebaseDatabase.getInstance().getReference("CLIENTFINISHEDCOURSES").child(clientID).child(courseID);
                                                                        }

                                                                        Map<String, String> data = new HashMap<>();
                                                                        data.put("client", clientID);
                                                                        data.put("driver", userId);
                                                                        data.put("startAddress", startA);
                                                                        data.put("endAddress", endA);
                                                                        data.put("distance", Double.toString(distanceTraveled));
                                                                        data.put("waitTime", Integer.toString(preWait));
                                                                        data.put("preWaitTime", Integer.toString(preWaitTime / 60));
                                                                        if (isFixed) {
                                                                            data.put("fixedDest", "1");
                                                                            data.put("price", Integer.toString((int) fixedPrice));

                                                                        } else {
                                                                            data.put("fixedDest", "0");
                                                                            data.put("price", Double.toString(currentBil));
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
                                                                        dData.put("preWaitTime", Integer.toString(preWaitTime / 60));
                                                                        if (isFixed) {
                                                                            dData.put("fixedDest", "1");
                                                                            dData.put("price", Integer.toString((int) fixedPrice));

                                                                        } else {
                                                                            dData.put("fixedDest", "0");
                                                                            dData.put("price", Double.toString(currentBil));
                                                                        }
                                                                        dCourse.setValue(dData);
                                                                        dCourse.child("date").setValue(timestamp);
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
                                        ///////********************END *************************


                                        charge.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                if (moneyAmount.getText().toString().length() > 0) {
                                                    final String value = moneyAmount.getText().toString();
                                                    final int userProvidedRecharge = Integer.parseInt(value);

                                                    if (userProvidedRecharge < 100) {
                                                        final String riderId = dataSnapshott.child("client").getValue(String.class);

                                                        if (riderId != null) {
                                                            FirebaseDatabase.getInstance().getReference("clientUSERS").
                                                                    child(riderId).child("SOLDE").
                                                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            cM = userProvidedRecharge;
                                                                            if (dataSnapshot.exists()) {
                                                                                if (Objects.requireNonNull(dataSnapshot.getValue(String.class)).equals("")) {
                                                                                    cM += 0.0;
                                                                                } else {
                                                                                    cM += Double.parseDouble(Objects.requireNonNull(dataSnapshot.getValue(String.class)));
                                                                                }

                                                                            }

                                                                            FirebaseDatabase.getInstance().getReference("CLIENTFINISHEDCOURSES").child(riderId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    if (dataSnapshot.exists()) {

                                                                                        Log.e(TAG, "onDataChange driver already have: " + debt);
                                                                                        Log.e(TAG, "onDataChange wants user input: " + cM);

                                                                                        double newdebt = currentDebt - userProvidedRecharge;
                                                                                        currentDebt = newdebt;
                                                                                        double newSold = currentWallet + userProvidedRecharge;
                                                                                        Log.e(TAG, "onDataChange after calculation: " + debt);

                                                                                        if (dataSnapshot.getChildrenCount() >= 3) {
                                                                                            rideMorethanThree = true;
                                                                                            if (cM <= 100) {
                                                                                                // Enter the value into driver wallet here
                                                                                                Log.e(TAG, "onDataChange: " + debt);
                                                                                                Log.e(TAG, "onDataChange: " + newSold);
                                                                                                Toast.makeText(MapsActivity.this, getString(R.string.txt_successfully_recharged), Toast.LENGTH_LONG).show();
                                                                                                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("debt").setValue("" + newdebt);
                                                                                                FirebaseDatabase.getInstance().getReference("clientUSERS").child(Objects.requireNonNull(dataSnapshott.child("client").getValue(String.class))).child("SOLDE").setValue("" + newSold);
                                                                                                FirebaseDatabase.getInstance().getReference("clientUSERS").child(Objects.requireNonNull(dataSnapshott.child("client").getValue(String.class))).child("USECREDIT").setValue("1");
                                                                                                dialog.dismiss();
                                                                                            } else {
                                                                                                Toast.makeText(MapsActivity.this, "Vous ne pouvez pas dépasser 100 MAD de recharge pour ce client.", Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        } else {
                                                                                            rideMorethanThree = false;
                                                                                            if (cM <= 10) {
                                                                                                // Enter the value into driver wallet here
                                                                                                Log.e(TAG, "onDataChange: " + debt);
                                                                                                Log.e(TAG, "onDataChange: " + newSold);
                                                                                                Toast.makeText(MapsActivity.this, getString(R.string.txt_successfully_recharged), Toast.LENGTH_LONG).show();
                                                                                                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("debt").setValue(/*Double.toString(*/"" + newdebt/*)*/);
                                                                                                FirebaseDatabase.getInstance().getReference("clientUSERS").child(Objects.requireNonNull(dataSnapshott.child("client").getValue(String.class))).child("SOLDE").setValue("" + newSold);
                                                                                                FirebaseDatabase.getInstance().getReference("clientUSERS").child(Objects.requireNonNull(dataSnapshott.child("client").getValue(String.class))).child("USECREDIT").setValue("1");
                                                                                                dialog.dismiss();
                                                                                            } else {
                                                                                                Toast.makeText(MapsActivity.this, "Vous ne pouvez pas dépasser 10 MAD de recharge pour ce client.", Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                    dialog.dismiss();
                                                                                }
                                                                            });


                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    });
                                                        }
                                                    } else
                                                        Toast.makeText(MapsActivity.this, "Vous ne pouvez pas dépasser 100 MAD de recharge pour ce client.", Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        });

                                        dialogButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    if (RATE > 0) {
                                                        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientId).child("rating").child(Integer.toString(RATE)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {
                                                                    int Rating = Integer.parseInt(dataSnapshot.getValue(String.class)) + 1;
                                                                    FirebaseDatabase.getInstance().getReference("clientUSERS").
                                                                            child(clientId).child("rating").child(Integer.toString(RATE)).setValue("" + Rating);
                                                                }
                                                                dialog.dismiss();
                                                                isRatingPopupShowed = true;
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                dialog.dismiss();
                                                                isRatingPopupShowed = true;
                                                            }
                                                        });

                                                        final Handler handler = new Handler();
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                //Do something after 3000ms
                                                                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("COURSE").removeValue();
                                                            }
                                                        }, 3000);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    dialog.dismiss();
                                                    isRatingPopupShowed = true;
                                                }

                                            }
                                        });


                                        star1.setBackgroundResource(R.drawable.normal_star);
                                        star2.setBackgroundResource(R.drawable.normal_star);
                                        star3.setBackgroundResource(R.drawable.normal_star);
                                        star4.setBackgroundResource(R.drawable.selected_star);
                                        imot.setImageBitmap(scaleBitmap(150, 150, R.drawable.four_stars));

                                        star1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                RATE = 1;
                                                star1.setBackgroundResource(R.drawable.selected_star);
                                                star2.setBackgroundResource(R.drawable.unselected_star);
                                                star3.setBackgroundResource(R.drawable.unselected_star);
                                                star4.setBackgroundResource(R.drawable.unselected_star);
                                                star5.setBackgroundResource(R.drawable.unselected_star);

                                                imot.setImageBitmap(scaleBitmap(150, 150, R.drawable.one_star));
                                            }
                                        });
                                        star2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                RATE = 2;

                                                star1.setBackgroundResource(R.drawable.normal_star);
                                                star2.setBackgroundResource(R.drawable.selected_star);
                                                star3.setBackgroundResource(R.drawable.unselected_star);
                                                star4.setBackgroundResource(R.drawable.unselected_star);
                                                star5.setBackgroundResource(R.drawable.unselected_star);

                                                imot.setImageBitmap(scaleBitmap(150, 150, R.drawable.two_stars));
                                            }
                                        });
                                        star3.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                RATE = 3;

                                                star1.setBackgroundResource(R.drawable.normal_star);
                                                star2.setBackgroundResource(R.drawable.normal_star);
                                                star3.setBackgroundResource(R.drawable.selected_star);
                                                star4.setBackgroundResource(R.drawable.unselected_star);
                                                star5.setBackgroundResource(R.drawable.unselected_star);

                                                imot.setImageBitmap(scaleBitmap(150, 150, R.drawable.three_stars));
                                            }
                                        });
                                        star4.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                RATE = 4;

                                                star1.setBackgroundResource(R.drawable.normal_star);
                                                star2.setBackgroundResource(R.drawable.normal_star);
                                                star3.setBackgroundResource(R.drawable.normal_star);
                                                star4.setBackgroundResource(R.drawable.selected_star);
                                                star5.setBackgroundResource(R.drawable.unselected_star);

                                                imot.setImageBitmap(scaleBitmap(150, 150, R.drawable.four_stars));
                                            }
                                        });
                                        star5.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                RATE = 5;

                                                star1.setBackgroundResource(R.drawable.normal_star);
                                                star2.setBackgroundResource(R.drawable.normal_star);
                                                star3.setBackgroundResource(R.drawable.normal_star);
                                                star4.setBackgroundResource(R.drawable.normal_star);
                                                star5.setBackgroundResource(R.drawable.selected_star);

                                                imot.setImageBitmap(scaleBitmap(150, 150, R.drawable.five_stars));
                                            }
                                        });


                                        dialog.setCancelable(false);
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.show();

                                        dialog.findViewById(R.id.body).getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) (dpWidth), MapsActivity.this.getResources().getDisplayMetrics());


                                        WindowManager.LayoutParams lp = Objects.requireNonNull(dialog.getWindow()).getAttributes();
                                        lp.dimAmount = 0.5f;
                                        dialog.getWindow().setAttributes(lp);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                    }
                                } else {
                                    courseUIOff();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        courseUIOff();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            return "this string is passed to onPostExecute";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


        }
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


    int driverWaitTime = 0;
    double Rating;
    private String driverName;
    private String driverImage;
    private String Debt;
    private String todayEarnings;
    private String todayTrips;
    private String driverNumber;

    private class CheckLoginService extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {


            final SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
            final String number = prefs.getString("userId", null);
            driverId = number;
            if (number == null) {
                //User Is Logged In
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {

                if (Looper.myLooper() == null) {
                    Looper.prepare();
                    Looper.myLooper();
                }


                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(number).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            String isVerified = dataSnapshot.child("isVerified").getValue(String.class);
                            if (isVerified != null && isVerified.equals("0")) {
                                prefs.edit().remove("phoneNumber").apply();
                                prefs.edit().remove("userId").apply();
                                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            driverName = dataSnapshot.child("fullName").getValue(String.class);
                            driverImage = dataSnapshot.child("image").getValue(String.class);
                            driverNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                            prefs.edit().putString("userId", dataSnapshot.getKey()).apply();

                            if (dataSnapshot.child("rating").child("1").getValue(String.class) != null
                                    || dataSnapshot.child("rating").child("2").getValue(String.class) != null ||
                                    dataSnapshot.child("rating").child("3").getValue(String.class) != null ||
                                    dataSnapshot.child("rating").child("4").getValue(String.class) != null ||
                                    dataSnapshot.child("rating").child("5").getValue(String.class) != null) {

                                int r1 = Integer.parseInt(dataSnapshot.child("rating").child("1").getValue(String.class));
                                int r2 = Integer.parseInt(dataSnapshot.child("rating").child("2").getValue(String.class));
                                int r3 = Integer.parseInt(dataSnapshot.child("rating").child("3").getValue(String.class));
                                int r4 = Integer.parseInt(dataSnapshot.child("rating").child("4").getValue(String.class));
                                int r5 = Integer.parseInt(dataSnapshot.child("rating").child("5").getValue(String.class));

                                int t = r1 + (r2 * 2) + (r3 * 3) + (r4 * 4) + (r5 * 5);
                                int total = r1 + r2 + r3 + r4 + r5;

                                Rating = 0;
                                if (total != 0)
                                    Rating = (double) (t / total);
                            }

                            if (dataSnapshot.child("debt").exists())
                                Debt = dataSnapshot.child("debt").getValue(String.class);
                            else
                                Debt = "0.0";


                            todayEarnings = "0";
                            todayTrips = "0";

                            FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(number).child("EARNINGS").child(getDateMonth(GetUnixTime())).child(getDateDay(GetUnixTime())).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {
                                        todayEarnings = dataSnapshot.child("earnings").getValue(String.class);
                                        todayTrips = dataSnapshot.child("voyages").getValue(String.class);

                                        setUserUi();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            setUserUi();
                        } else {
                            Toast.makeText(MapsActivity.this, number, Toast.LENGTH_SHORT).show();
                            prefs.edit().remove("phoneNumber").apply();
                            prefs.edit().remove("userId").apply();
                            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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


    private void logout() {
        final SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        final String number = prefs.getString("userId", null);
        prefs.edit().remove("userId").apply();
    }

    private void setUserUi() {
        CircleImageView driverI = findViewById(R.id.profile_image);
        TextView fullName = findViewById(R.id.fullName);
        TextView ratingR = findViewById(R.id.ratings);

        if (driverImage != null) {
            if (driverImage.length() > 0) {
                Picasso.get().load(driverImage).fit().centerCrop().into(driverI);
            } else {
                driverI.setImageResource(R.drawable.driver_profil_picture);
            }
        } else {
            driverI.setImageResource(R.drawable.driver_profil_picture);
        }
        fullName.setText(driverName);
        ratingR.setText(Rating + "");

        money.setText(df2.format(Double.parseDouble(todayEarnings)) + " MAD");


        ComingoonYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, ComingooAndYouActivity.class);
                intent.putExtra("image", driverImage);
                intent.putExtra("name", driverName);
                intent.putExtra("phone", driverNumber);
                intent.putExtra("courses", todayTrips);
                intent.putExtra("earnings", todayEarnings);
                intent.putExtra("debt", Debt);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);

        if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                //startLocationUpdates();
                if (mMap != null)
                    mMap.setMyLocationEnabled(true);

                getLastLocation();
                myPositionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getLastLocation();
                    }
                });
            }
        }
    }


    private String courseID;
    private String courseState;
    private DatabaseReference courseRef;
    private DataSnapshot driverData;
    private String clientId;
    String userId;
    private String clientImageUri;
    private String clientName;
    private String clientPhoneNumber;
    private String lastCourse = "";

    private LatLng drawRouteStart;
    private LatLng drawRouteArrival;

    private String startAddress;
    private String destAddress;

    private class checkCourseTask extends AsyncTask<String, Integer, String> {
        SharedPreferences prefs;


        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
            userId = prefs.getString("userId", null);
            driverId = userId;
            // Do something like display a progress bar
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {

            FirebaseDatabase.getInstance().getReference("COURSES").orderByChild("driver").
                    equalTo(userId).limitToFirst(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (final DataSnapshot data : dataSnapshot.getChildren()) {
                            courseID = data.getKey();
                            courseRef = FirebaseDatabase.getInstance().getReference("COURSES").child(courseID);
                            courseState = data.child("state").getValue(String.class);
                            driverData = data;

                            startAddress = data.child("startAddress").getValue(String.class);
                            destAddress = data.child("endAddress").getValue(String.class);

                            clientId = data.child("client").getValue(String.class);
                            if (driverData.child("endLat").getValue(String.class) != null) {
                                if (Objects.equals(driverData.child("endLat").getValue(String.class), "")) {
                                    drawRouteArrival = null;
                                } else {
                                    drawRouteArrival = new LatLng(Double.parseDouble(data.child("endLat").getValue(String.class)),
                                            Double.parseDouble(data.child("endLong").getValue(String.class)));
                                }
                            }

                            if (clientId != null) {
                                FirebaseDatabase.getInstance().getReference("clientUSERS").
                                        child(clientId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Log.e(TAG, "onDataChange:clientAllInfo: " + dataSnapshot.toString());
                                            clientImageUri = dataSnapshot.child("image").getValue(String.class);
                                            clientName = dataSnapshot.child("fullName").getValue(String.class);
                                            clientPhoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                                            lastCourse = dataSnapshot.child("LASTCOURSE").getValue(String.class);
                                            courseHandle();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    } else {
                        courseState = "4";
                        courseHandle();

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


    private void courseHandle() {
        if (courseState.equals("4")) {
            wazeButton.setVisibility(View.GONE);
            courseUIOff();
            if (mMap != null)
                mMap.clear();

        } else {
            switchToCourseUI();
            if (courseState.equals("3")) {
                stopCourseService();
                courseState = "4";
                isRatingPopupShowed = false;
                if (mMap != null)
                    mMap.clear();

            }

            try {
                startCourseService();
                checkCourseState();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void checkNetworkConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection to continue");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if (isConnected) {
            Log.d("Network", "Connected");
            return true;
        } else {
            Log.d("Network", "Not Connected");
            return false;
        }
    }

    CircleImageView /*clientImage,*/ close_button, call_button;
    com.mikhaellopez.circularimageview.CircularImageView clientImage;
    TextView name, tvLastCourse, totalCourse, date;
    LinearLayout voip_view;
    private boolean isRatingPopupShowed = false;

    public void checkCourseState() {
        FirebaseDatabase.getInstance().getReference("CLIENTFINISHEDCOURSES").child(clientId)/*.child(userId)
                .orderByKey()*/.limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Log.e(TAG, "date of ride: " + Objects.requireNonNull(child.child("date").getValue()).toString());

                            String longV = Objects.requireNonNull(child.child("date").getValue()).toString();
                            String dateString = convertDate(longV, "dd/MM/yyyy hh:mm:ss");

                            date.setText(dateString);
                            userInfoLayout.setBackgroundColor(Color.WHITE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Handle possible errors.
            }
        });


        name.setText(clientName);
        tvLastCourse.setText(lastCourse);
        userInfoLayout.setBackgroundColor(Color.WHITE);

        if (clientId != null || !clientId.isEmpty()) {
            FirebaseDatabase.getInstance().getReference("CLIENTFINISHEDCOURSES").child(clientId).
                    addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            totalCourse.setText("Courses:" + dataSnapshot.getChildrenCount());
                            userInfoLayout.setBackgroundColor(Color.WHITE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        } else {
            courseUIOff();
        }

        if (clientImageUri != null) {
            if (!clientImageUri.isEmpty()) {
                Picasso.get().load(clientImageUri).into(clientImage);
                userInfoLayout.setBackgroundColor(Color.WHITE);
            }
        }

        if (courseState.equals("4")) {
            courseUIOff();
        }

        if (courseState.equals("0")) {
            addressText.setText(startAddress);
            cancel_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    courseRef.child("state").setValue("1");
                }
            });
            courseActionButton.setText("Appuyez pour arriver");


            if (userLatLng != null && drawRouteStart != null) {
                new DrawRouteTask().execute(userLatLng, drawRouteStart);

                wazeButton.setVisibility(View.VISIBLE);


            }
            wazeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWaze(drawRouteStart);
                }
            });
        }

        if (courseState.equals("1")) {
            addressText.setText(destAddress);
            courseActionButton.setText("Appuyez pour commancer");
            cancel_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    courseRef.child("state").setValue("2");
                    isRatingPopupShowed = false;
                }
            });
            if (mMap != null)
                mMap.clear();
        }

        if (courseState.equals("2")) {
            addressText.setText(destAddress);
            courseActionButton.setText("Appuyez pour terminer");
            cancel_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    courseRef.child("state").setValue("3");
                    courseState = "4";
                    wazeButton.setVisibility(View.GONE);
                    stopCourseService();
                    if (!isRatingPopupShowed) {
                        new checkCourseFinished().execute();
                    }
                }
            });


            if (drawRouteArrival != null && drawRouteStart != null) {
                new DrawRouteTask().execute(drawRouteStart, drawRouteArrival);
                wazeButton.setVisibility(View.VISIBLE);
            }

            wazeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWaze(drawRouteArrival);
                }
            });
        }

        userInfoLayout.setBackgroundColor(Color.WHITE);
    }

    public String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }


    private void courseUIOff() {
        findViewById(R.id.statusConstraint).setVisibility(View.VISIBLE);
        findViewById(R.id.money).setVisibility(View.VISIBLE);
        clientInfoLayout.setVisibility(View.GONE);
        destinationLayout.setVisibility(View.GONE);
        menuButton.setVisibility(View.VISIBLE);
        ivCancelCourse.setVisibility(View.GONE);
    }

    public void startCourseService() {
        Intent intent = new Intent(this, CourseService.class);
        SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        prefs.edit().putString("courseID", courseID).apply();
        prefs.edit().remove("online");
        FirebaseDatabase.getInstance().getReference().child("ONLINEDRIVERS").child(prefs.getString("userId", "1")).removeValue();
        startService(intent);
    }


    public void stopCourseService() {
        Intent intent = new Intent(this, CourseService.class);
        stopService(intent);
    }


    private void openWaze(LatLng location) {
        String link = "https://waze.com/ul?";
        if (location != null) {
            link += "ll=" + location.latitude + "," + location.longitude + "&navigate=yes";
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.waze"));
            startActivity(intent);
        }
    }


    private class DrawRouteTask extends AsyncTask<LatLng, Integer, String> {

        LatLng start;
        LatLng arrival;
        ArrayList<LatLng> thePath;
        LatLng mid;
        LatLngBounds.Builder builder;

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Do something like display a progress bar
            mid = null;
            builder = new LatLngBounds.Builder();
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(LatLng... params) {


            start = params[0];
            arrival = params[1];

            if (start != null && arrival != null)
                mid = new LatLng((start.latitude + arrival.latitude) / 2, (start.longitude + arrival.longitude) / 2);

            //Define list to get all latlng for the route
            thePath = new ArrayList();
            thePath.add(start);

            builder.include(start);

            //Execute Directions API request
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(getResources().getString(R.string.google_maps_key))
                    .build();
            DirectionsApiRequest req = DirectionsApi.getDirections(context, start.latitude + "," + start.longitude, arrival.latitude + "," + arrival.longitude);
            try {
                DirectionsResult res = req.await();

                //Loop through legs and steps to get encoded polylines of each step
                if (res.routes != null && res.routes.length > 0) {
                    DirectionsRoute route = res.routes[0];

                    if (route.legs != null) {
                        for (int i = 0; i < route.legs.length; i++) {
                            DirectionsLeg leg = route.legs[i];
                            if (leg.steps != null) {
                                for (int j = 0; j < leg.steps.length; j++) {
                                    DirectionsStep step = leg.steps[j];
                                    if (step.steps != null && step.steps.length > 0) {
                                        for (int k = 0; k < step.steps.length; k++) {
                                            DirectionsStep step1 = step.steps[k];
                                            EncodedPolyline points1 = step1.polyline;
                                            if (points1 != null) {
                                                //Decode polyline and add points to list of route coordinates
                                                List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                                for (com.google.maps.model.LatLng coord1 : coords1) {
                                                    thePath.add(new LatLng(coord1.lat, coord1.lng));
                                                    builder.include(new LatLng(coord1.lat, coord1.lng));
                                                }
                                            }
                                        }
                                    } else {
                                        EncodedPolyline points = step.polyline;
                                        if (points != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords = points.decodePath();
                                            for (com.google.maps.model.LatLng coord : coords) {
                                                thePath.add(new LatLng(coord.lat, coord.lng));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "this string is passed to onPostExecute";
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //Draw the polyline
            // Do things like update the progress bar
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (thePath != null) {
                if (thePath.size() > 0) {
                    if (mid != null) {
                        thePath.add(arrival);
                        //drawPolyGradiant(thePath, "#f9ad81" ,"#aba100",9, 6);
                        drawPolyGradiant(thePath, "#76b5f9", "#1c549d");
                        builder.include(arrival);
                        int padding = 200;
                        LatLngBounds bounds = builder.build();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                    }

                } else {
                    new DrawRouteTask().execute(start, arrival);
                }
            } else {
                new DrawRouteTask().execute(start, arrival);
            }
        }
    }

    private void drawPolyGradiant(List<LatLng> thePath, String startColor, String endColor) {

        int Size = thePath.size();

        int Red = Integer.valueOf(startColor.substring(1, 3), 16);
        int Green = Integer.valueOf(startColor.substring(3, 5), 16);
        int Blue = Integer.valueOf(startColor.substring(5, 7), 16);
        int finalRed = Integer.valueOf(endColor.substring(1, 3), 16);
        int finalGreen = Integer.valueOf(endColor.substring(3, 5), 16);
        int finalBlue = Integer.valueOf(endColor.substring(5, 7), 16);

        for (int i = 0; i < 4 - 1; i++) {

            float percent = 1 / (float) (2 * 4) + (float) i / (float) 4;
            int color = Color.argb(255,
                    (Red > finalRed) ? (int) (Red - ((Red - finalRed) * percent)) : (int) (Red + ((finalRed - Red) * percent)),
                    (Green > finalGreen) ? (int) (Green - ((Green - finalGreen) * percent)) : (int) (Green + ((finalGreen - Green) * percent)),
                    (Blue > finalBlue) ? (int) (Blue - ((Blue - finalBlue) * percent)) : (int) (Blue + ((finalBlue - Blue) * percent)));

            PolylineOptions opts = new PolylineOptions().geodesic(false).addAll(thePath.subList((Size / 4) * i, Size / 4 * (i + 2))).color(color).width(9 + 1);
            mMap.addPolyline(opts);
        }

        float percentage = 1 / (float) (2 * 4) + (float) (4 - 1) / (float) 4;
        int color = Color.argb(255,
                (Red > finalRed) ? (int) (Red - ((Red - finalRed) * percentage)) : (int) (Red + ((finalRed - Red) * percentage)),
                (Green > finalGreen) ? (int) (Green - ((Green - finalGreen) * percentage)) : (int) (Green + ((finalGreen - Green) * percentage)),
                (Blue > finalBlue) ? (int) (Blue - ((Blue - finalBlue) * percentage)) : (int) (Blue + ((finalBlue - Blue) * percentage)));

        PolylineOptions opts = new PolylineOptions().geodesic(false).addAll(thePath.subList((Size / 4) * (4 - 1), Size / 4 * (4))).color(color).width(9 + 1);
        mMap.addPolyline(opts);

        for (int i = 0; i < (Size - 1); i++) {

            float percent = ((float) i / (float) Size);
            int usedColor = Color.argb(255,
                    (Red > finalRed) ? (int) (Red - ((Red - finalRed) * percent)) : (int) (Red + ((finalRed - Red) * percent)),
                    (Green > finalGreen) ? (int) (Green - ((Green - finalGreen) * percent)) : (int) (Green + ((finalGreen - Green) * percent)),
                    (Blue > finalBlue) ? (int) (Blue - ((Blue - finalBlue) * percent)) : (int) (Blue + ((finalBlue - Blue) * percent)));

            opts = new PolylineOptions().add(thePath.get(i)).geodesic(false).add(thePath.get(i + 1)).color(usedColor).width(9);
            mMap.addPolyline(opts);
        }
    }

    LatLng userLatLng;

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

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
//                            getLastLocation();
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

}
