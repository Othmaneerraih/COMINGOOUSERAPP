package com.comingoo.driver.fousa.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.comingoo.driver.fousa.R;
import com.comingoo.driver.fousa.interfaces.CourseCallBack;
import com.comingoo.driver.fousa.interfaces.DataCallBack;
import com.comingoo.driver.fousa.interfaces.PriceCallBack;
import com.comingoo.driver.fousa.service.DriverService;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.comingoo.driver.fousa.utility.Utilities.GetUnixTime;
import static com.comingoo.driver.fousa.utility.Utilities.getDateDay;
import static com.comingoo.driver.fousa.utility.Utilities.getDateMonth;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class MapsNewActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapsVM mapsVM;
    private double driverRating = 0.0;
    private String driverName = "";
    private String driverImage = "";
    private String driverNumber = "";
    private String debit = "";
    private String todayTrips = "";
    private Double todayEarnings = 0.0;
    private Double todayVoyages = 0.0;

    private String courseState = "";
    private String driverId = "";
    private String courseId = "";
    private String clientId = "";
    private String clientName = "";
    private String clientLevel = "";
    private String clientImageUri = "";
    private String clientPhoneNumber = "";
    private String clientlastCourse = "";
    private String clientSolde = "0.0";
    private String clientCredit = "0.0";
    private String startAddress = "";
    private String destAddress = "";
    private String clientTotalRide = "0";
    private String clientLastRideDate = "";
    private LatLng destinationLatLong;
    private DatabaseReference courseRef;
    private int preWaitTime = 0;
    private double currentBil = 0;
    private String distanceTraveled = "0.0";
    private Handler handler;
    private Runnable runnable;
    private boolean isFixed;
    private int totalRecharge = 0;
    private boolean isRatingPopupShowed = false;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String clientType;

    private int RATE = 4;
    private double fixedPrice, price1, price2, price3, promoCode;
    private double currentDebt = 0.0;
    private double currentWallet = 0.0;

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
    private RelativeLayout rlRideFlow;
    private Button courseActionBtn;
    private RelativeLayout clientInfoLayout;
    private RelativeLayout voipView;
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
    private TextView ratingTxt, tvClientRate;
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

    private BitmapFactory.Options bOptions;
    private int imageHeight;
    private int imageWidth;
    private int lastImageHeight;
    private int lastImageWidth;
    private int inSampleSize;
    private float dpWidth;

    private GoogleMap mMap;
    private LatLng userLatLng;
    private RelativeLayout.LayoutParams params;
    private boolean isLoud = false;

    private MediaPlayer mp;
    private TextView caller_name;
    private TextView callState;
    private Handler mHandler = new Handler();
    private CircleImageView iv_cancel_call_voip_one;
    private CircleImageView iv_mute;
    private CircleImageView iv_loud;
    private CircleImageView iv_recv_call_voip_one;
    private int count = 0;
    private Date startTime;
    private long FIVE_MINUTES_DURATION = MILLISECONDS.convert(5, MINUTES);
    private int PANISHMENT_VALUE = 5;

    private int mHour, mMinute; // variables holding the hour and minuteZ

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_new);
        permission();
        initialize();
        action();
    }

    private void action() {

        df2.setRoundingMode(RoundingMode.UP);
        mapsVM = new MapsVM();
        mapsVM.checkLogin(MapsNewActivity.this, new DataCallBack() {
            @Override
            public void callbackCall(boolean success, String drivrNam, String drivrImg,
                                     String drivrNum, String debt, String todystrp, String todysErn, double rat, String drivrId) {

                if (success) {
                    driverName = drivrNam;
                    driverImage = drivrImg;
                    driverNumber = drivrNum;
                    todayEarnings = Double.parseDouble(todysErn);
                    debit = debt;
                    todayTrips = todystrp;
                    driverRating = rat;
                    driverId = drivrId;
                    setUserUi();
                } else {
                    Intent intent = new Intent(MapsNewActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mapsVM.checkCourseTask(new CourseCallBack() {
            @Override
            public void callbackCourseInfo(String coursId,
                                           String clintId, String clintName,
                                           String clintImageUri, String clintPhoneNumber,
                                           String clintlastCourse, String clintSold, String clintCre, String strtAddress,
                                           String destAddr, String distanceTravele, String courseSta, String clintTotalRide,
                                           String clintLastRideDate, String preWTime, LatLng clientDestLatLng) {
                courseState = courseSta;
                courseId = coursId;
                clientId = clintId;
                clientName = clintName;
                clientImageUri = clintImageUri;
                clientPhoneNumber = clintPhoneNumber;
                clientlastCourse = clintlastCourse;
                clientSolde = clintSold;
                clientCredit = clintCre;
                startAddress = strtAddress;
                destAddress = destAddr;
                clientTotalRide = clintTotalRide;
                distanceTraveled = distanceTravele;
                clientLastRideDate = clintLastRideDate;
                if (!preWTime.equals(""))
                    preWaitTime = Integer.parseInt(preWTime);
                else preWaitTime = 0;
                destinationLatLong = clientDestLatLng;
                // Note: ride flow
                courseHandle();
            }
        });


        // Note: Pre wait Time Handler
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                int time = preWaitTime + 1;
                if (courseRef == null)
                    courseRef = FirebaseDatabase.getInstance().getReference("COURSES").child(courseId);
                courseRef.child("preWaitTime").setValue(Integer.toString(time));
            }
        };

        prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        editor = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE).edit();

        boolean isPopupNotDismissedBefore = getSharedPreferences("COMINGOODRIVERDATA",
                MODE_PRIVATE).getBoolean("isRatingPopupDismissedBefore", false);

        if (isPopupNotDismissedBefore)
            calculatePrice();

        if (Objects.equals(prefs.getString("online", "0"), "1")) {
            switchOnlineUI();
        }

        telephoneTv.setOnClickListener(new View.OnClickListener() {
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

        voipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!driverId.isEmpty()) {
                    voipTv.setClickable(false);
                    voipTv.setEnabled(false);
                    Intent intent = new Intent(MapsNewActivity.this, VoipCallingActivity.class);
                    intent.putExtra("driverId", driverId);
                    intent.putExtra("clientId", clientId);
                    intent.putExtra("clientName", clientName);
                    intent.putExtra("clientImage", clientImageUri);
                    startActivity(intent);
                }
            }
        });

        if (!driverId.equals("")) {
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
                closeBtn.setVisibility(View.INVISIBLE);
                callBtn.setVisibility(View.VISIBLE);
                voipView.setVisibility(View.INVISIBLE);
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NOTE : show close button & hide call btn
                closeBtn.setVisibility(View.VISIBLE);
                callBtn.setVisibility(View.INVISIBLE);
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

        // Note: initially driver will be offline
//        switchOfflineUI();

        rlRideFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (courseState.equalsIgnoreCase("0")) {
                    courseRef.child("state").setValue("1");
                    courseActionBtn.setText(getString(R.string.txt_tap_to_start));
                } else if (courseState.equalsIgnoreCase("1")) {
                    courseRef.child("state").setValue("2");
                    courseActionBtn.setText(getString(R.string.txt_finish_course));
                } else if (courseState.equalsIgnoreCase("2")) {
                    courseRef.child("state").setValue("3");
                    destinationLayout.setVisibility(View.GONE);
                }
            }
        });

        wazeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWaze(destinationLatLong);
            }
        });

//        mapsVM.checkingOnlineOffline(new OnlineOfflineCallBack() {
//            @Override
//            public void isOnline(boolean isOnline) {
//                if (isOnline)
//                    switchOnlineUI();
//                else switchOfflineUI();
//            }
//        });


//        CommandActivity commandActivity = new CommandActivity(new OnlineOfflineCallBack() {
//            @Override
//            public void isOnline(boolean isOnline) {
//                if (isOnline)
//                    switchOnlineUI();
//                else switchOfflineUI();
//            }
//        });

    }

    private void setUserUi() {
        if (driverImage != null) {
            if (driverImage.length() > 0) {
                Picasso.get().load(driverImage).fit().centerCrop().into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.driver_profil_picture);
            }
        } else {
            profileImage.setImageResource(R.drawable.driver_profil_picture);
        }

        nameTxt.setText(driverName);
        Log.e(TAG, "setUserUi: " + todayEarnings);
        moneyBtn.setText(df2.format(todayEarnings) + " MAD");

        if (driverRating != 0.0) {
            ratingTxt.setText(df2.format(driverRating) + "");
        } else ratingTxt.setText("4.0");

        comingoonyouLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsNewActivity.this, ComingooAndYouActivity.class);
                intent.putExtra("image", driverImage);
                intent.putExtra("name", driverName);
                intent.putExtra("phone", driverNumber);
                intent.putExtra("courses", todayTrips);
                intent.putExtra("earnings", todayEarnings);
                intent.putExtra("debt", debit);
                startActivity(intent);
            }
        });
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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mapsVM = new MapsVM();
        // NOTE : Banner init
        moneyBtn = findViewById(R.id.money_btn);
        destinationLayout = findViewById(R.id.destination_layout);
        destTimeTxt = findViewById(R.id.dest_time_txt);
        addressTxt = findViewById(R.id.address_txt);

        //initially hide
        destinationLayout.setVisibility(View.GONE);

        // NOTE : Client init
        clientImage = findViewById(R.id.user_image);
        clientNameTv = findViewById(R.id.user_name_txt);
        totalCourseTv = findViewById(R.id.course_count_txt);
        driverInfoTv = findViewById(R.id.driver_details_txt);
        courseDetailsTv = findViewById(R.id.course_details_txt);
        closeBtn = findViewById(R.id.close_button);
        callBtn = findViewById(R.id.call_button);
        rlRideFlow = findViewById(R.id.rl_cancel_course);
        voipView = findViewById(R.id.ll_voip_view);
        dateTv = findViewById(R.id.client_type_txt);
        courseActionBtn = findViewById(R.id.course_action_button);
        telephoneTv = findViewById(R.id.tv_telephone);
        voipTv = findViewById(R.id.tv_voip);
        clientInfoLayout = findViewById(R.id.client_info_layout);
        // NOTE : Those are initially Hide
        voipView.setBackgroundColor(Color.TRANSPARENT);
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
        tvClientRate = findViewById(R.id.tv_client_rating);
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

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        dpWidth = outMetrics.widthPixels / density;
        df2.setRoundingMode(RoundingMode.UP);
    }

    private void courseHandle() {
        courseRef = FirebaseDatabase.getInstance().getReference("COURSES").child(courseId);
        editor.putBoolean("isRatingPopupShowed", false);
        editor.apply();

        if (courseState.equalsIgnoreCase("0")) {
            statusLayout.setVisibility(View.GONE);

            startTime = Calendar.getInstance().getTime();

            // Note: Setting the course into driver's profile
            FirebaseDatabase.getInstance().getReference("DRIVERUSERS")
                    .child(driverId).child("COURSE").setValue(courseId);
            destinationLayout.setVisibility(View.VISIBLE);
            showClientInformation();

        } else if (courseState.equals("1")) {
            handler.postDelayed(runnable, 1000);
            showClientInformation();
            courseActionBtn.setText(getString(R.string.txt_tap_to_start));
        } else if (courseState.equals("2")) {
            showClientInformation();
            handler.removeCallbacks(runnable);
            isRatingPopupShowed = false;
            courseActionBtn.setText(getString(R.string.txt_finish_course));
            cancelRideIv.setVisibility(View.GONE);
        } else if (courseState.equals("3")) {
            courseUIOff();
            // Note: Making driver offline
            FirebaseDatabase.getInstance().getReference().child("ONLINEDRIVERS").child(driverId).removeValue();
            switchOnlineUI();
            if (!isRatingPopupShowed)
                calculatePrice();
        } else if (courseState.equals("5")){
            courseUIOff();
        }
    }

    private void calculatePrice() {
        isRatingPopupShowed = true;
        mapsVM.gettingPriceValue(new PriceCallBack() {
            @Override
            public void callbackPrice(Double att, Double base, Double debtCeil, Double km, Double mini,
                                      Double percent, boolean promo, double earn, double voya, double debt, String level) {
                showRatingDialog(att, base, debtCeil, km, mini, percent, promo, earn, voya, debt, level);
            }
        });
    }

    private void showRatingDialog(Double att, Double base, Double debtCeil, Double km, Double mini,
                                  Double percent, Boolean promo, Double earn, Double voya, Double debt, String level) {

        final Dialog dialog = new Dialog(MapsNewActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) MapsNewActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_finished_course, null, false);
        dialog.setContentView(view);

        final Button star1 = view.findViewById(R.id.star1);
        final Button star2 = view.findViewById(R.id.star2);
        final Button star3 = view.findViewById(R.id.star3);
        final Button star4 = view.findViewById(R.id.star4);
        final Button star5 = view.findViewById(R.id.star5);
        final RelativeLayout rechargeLayout = view.findViewById(R.id.recharge_layout);
        Button price = view.findViewById(R.id.btn_price_show);
        final ImageView ivRateReaction = view.findViewById(R.id.rating_reaction);
        final Button gotMoney = view.findViewById(R.id.button);
        final Button charge = view.findViewById(R.id.btn_recharger);
        final EditText moneyAmount = view.findViewById(R.id.editText);

        editor.putBoolean("isRatingPopupDismissedBefore", true);
        editor.apply();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                switchOnlineUI();
                editor.putBoolean("isRatingPopupDismissedBefore", false);
                editor.apply();
            }
        });

        double minimum = mini;
        double driverDebt = debt;

        long timestamp = GetUnixTime() * -1;

        double preWaitT = 0;

        if (preWaitTime > 180) {
            preWaitT = 3;
        }

        int preWait = preWaitTime / 60;

        promoCode = 0.20;
        price1 = base + (Double.parseDouble(distanceTraveled) * km) + (att * preWait);

        if (price1 < minimum) {
            price1 = minimum;
        }
        price2 = price1 * percent;
        price3 = price2 * (1 - promoCode);

        if (promo)
            currentBil = price3;
        else
            currentBil = price2;

        if (isFixed)
            earn += fixedPrice;
        else
            earn += currentBil;

        voya += 1;


        todayEarnings = earn;
        todayVoyages = voya;

        if (clientSolde != null) {
            if (!clientSolde.equals("")) {
                try {
                    double oldSold = Double.parseDouble(clientSolde);
                    // if user has solde & it is greater then the calculated price
                    if (clientCredit.equals("1") && oldSold >= currentBil) {
                        double newSolde = oldSold - currentBil;
                        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientId).child("SOLDE").setValue("" + newSolde);
                        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientId).child("USECREDIT").setValue("1");
                        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(driverId).child("PAID").setValue("1");
                        double commission = currentBil * percent;
                        double driverIncome = currentBil - commission;
                        double newDebt = driverDebt + driverIncome;
                        currentDebt = newDebt;
                        currentWallet = newSolde;
                        FirebaseDatabase.getInstance().getReference("COURSES").child(courseId).child("price").setValue("0.0");
                        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(driverId).child("debt").setValue(Double.toString(newDebt));
                        price.setText("0.0 MAD");
                    } else {
                        // Note: if user has solde & it is small then the calculated price
                        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(driverId).child("PAID").setValue("0");
                        double commission = currentBil * percent;
                        double userDue = currentBil - oldSold;
                        double newDebt = driverDebt + (currentBil - userDue - commission);
                        currentDebt = newDebt;
                        currentWallet = 0.0;
                        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientId).child("SOLDE").setValue("" + currentWallet);
                        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientId).child("USECREDIT").setValue("0");
                        FirebaseDatabase.getInstance().getReference("COURSES").child(courseId).child("price").setValue(df2.format(userDue));
                        price.setText(df2.format(userDue) + " MAD");
                        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(driverId).child("debt").setValue(Double.toString(newDebt));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //Note: if user has no solde
                try {
                    double commission = currentBil * percent * -1;
                    double newDebt = (driverDebt + commission);
                    currentDebt = newDebt;
                    FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(driverId).child("PAID").setValue("0");
                    FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(driverId).child("debt").setValue(Double.toString(newDebt));
                    FirebaseDatabase.getInstance().getReference("COURSES").child(courseId).child("price").setValue(df2.format(currentBil));
                    price.setText(df2.format(currentBil) + " MAD");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        FirebaseDatabase.getInstance().getReference("clientUSERS").
                child(clientId).child("LASTCOURSE").
                setValue("Captain " + driverName + " / " + df2.format(currentBil) + " MAD");
        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientId).child("COURSE").setValue(courseId);
        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(driverId).child("COURSE").setValue(courseId);

        // Note: inserting driver earnings into Driver profile
        final Map<String, String> earnings = new HashMap<>();
        earnings.put("earnings", "" + todayEarnings);
        earnings.put("voyages", "" + todayVoyages);
        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").
                child(driverId).child("EARNINGS").child(getDateMonth(GetUnixTime()))
                .child(getDateDay(GetUnixTime())).setValue(earnings);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase.getInstance().getReference("COURSES").child(courseId).removeValue();
            }
        }, 3000);

        // Note: Recharge Functionality Here
        charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moneyAmount.getText().toString().length() > 0) {
                    final String value = moneyAmount.getText().toString();
                    final int userProvidedRecharge = Integer.parseInt(value);

                    if (userProvidedRecharge < 100) {
                        totalRecharge = userProvidedRecharge;

                        if (clientSolde.equals("")) {
                            totalRecharge += 0.0;
                        } else {
                            totalRecharge += Double.parseDouble(clientSolde);
                        }

                        double newdebt = currentDebt - userProvidedRecharge;
                        currentDebt = newdebt;
                        double newSold = currentWallet + userProvidedRecharge;

                        if (Integer.parseInt(clientTotalRide) >= 3) {
                            if (totalRecharge <= 100) {
                                mapsVM.rateClient(RATE);
                                // Enter the value into driver wallet here
                                Log.e(TAG, "onDataChange: " + newSold);
                                Toast.makeText(MapsNewActivity.this, getString(R.string.txt_successfully_recharged), Toast.LENGTH_LONG).show();
                                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(driverId).child("debt").setValue("" + newdebt);
                                FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientId).child("SOLDE").setValue("" + newSold);
                                FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientId).child("USECREDIT").setValue("1");
                                dialog.dismiss();
                            } else {
                                Toast.makeText(MapsNewActivity.this, "Vous ne pouvez pas dépasser 100 MAD de recharge pour ce client.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (totalRecharge <= 10) {
                                // Enter the value into driver wallet here
                                mapsVM.rateClient(RATE);
                                Toast.makeText(MapsNewActivity.this, getString(R.string.txt_successfully_recharged), Toast.LENGTH_LONG).show();
                                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(driverId).child("debt").setValue("" + newdebt);
                                FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientId).child("SOLDE").setValue("" + newSold);
                                FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientId).child("USECREDIT").setValue("1");
                                dialog.dismiss();
                            } else {
                                Toast.makeText(MapsNewActivity.this, getString(R.string.txt_highest_ten_mad), Toast.LENGTH_LONG).show();
                            }
                        }
                    } else
                        Toast.makeText(MapsNewActivity.this, getString(R.string.txt_highest_hundrad_mad), Toast.LENGTH_LONG).show();

                }
            }
        });

        gotMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RATE > 0) {
                    dialog.dismiss();
                    mapsVM.rateClient(RATE);
                }
            }
        });

        star1.setBackgroundResource(R.drawable.normal_star);
        star2.setBackgroundResource(R.drawable.normal_star);
        star3.setBackgroundResource(R.drawable.normal_star);
        star4.setBackgroundResource(R.drawable.selected_star);
        ivRateReaction.setImageBitmap(scaleBitmap(150, 150, R.drawable.four_stars));

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RATE = 1;
                star1.setBackgroundResource(R.drawable.selected_star);
                star2.setBackgroundResource(R.drawable.unselected_star);
                star3.setBackgroundResource(R.drawable.unselected_star);
                star4.setBackgroundResource(R.drawable.unselected_star);
                star5.setBackgroundResource(R.drawable.unselected_star);
                ivRateReaction.setImageBitmap(scaleBitmap(150, 150, R.drawable.one_star));
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
                ivRateReaction.setImageBitmap(scaleBitmap(150, 150, R.drawable.two_stars));
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
                ivRateReaction.setImageBitmap(scaleBitmap(150, 150, R.drawable.three_stars));
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
                ivRateReaction.setImageBitmap(scaleBitmap(150, 150, R.drawable.four_stars));
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
                ivRateReaction.setImageBitmap(scaleBitmap(150, 150, R.drawable.five_stars));
            }
        });


        dialog.setCancelable(false);
        final Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }
        switchOnlineUI();
        if (!MapsNewActivity.this.isFinishing()) {
            dialog.show();
        }

        dialog.findViewById(R.id.body).getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) (dpWidth), MapsNewActivity.this.getResources().getDisplayMetrics());

        WindowManager.LayoutParams lp = Objects.requireNonNull(dialog.getWindow()).getAttributes();
        lp.dimAmount = 0.5f;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public Bitmap scaleBitmap(int reqWidth, int reqHeight, int resId) {
        // Raw height and width of image

        bOptions = new BitmapFactory.Options();
        bOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, bOptions);
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

    private void showClientInformation() {
        courseActionBtn.setText(getString(R.string.txt_driver_arrive));
        clientInfoLayout.setVisibility(View.VISIBLE);
        clientNameTv.setText(clientName);
        Picasso.get().load(clientImageUri).into(clientImage);
        totalCourseTv.setText("Courses: " + clientTotalRide);
        driverInfoTv.setText(clientlastCourse);
        dateTv.setText(clientLastRideDate);
        addressTxt.setText(destAddress);
        tvClientRate.setText(String.valueOf(df2.format(driverRating)));
        switchToCourseUI();
    }

    private void switchToCourseUI() {
        statusLayout.setVisibility(View.GONE);
        moneyBtn.setVisibility(View.GONE);
        menuBtn.setVisibility(View.GONE);
        clientInfoLayout.setVisibility(View.VISIBLE);
        cancelRideIv.setVisibility(View.VISIBLE);
        destinationLayout.setVisibility(View.VISIBLE);
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

                punishmentCharge();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Note: remove course after 3000ms
                        FirebaseDatabase.getInstance().getReference("COURSES").child(courseId).removeValue();
                    }
                }, 3000);

                switchOnlineUI();
                courseUIOff();
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
                    if (location != null) {
                        userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        goToLocation(userLatLng.latitude, userLatLng.longitude);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }
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
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.waze_urlt)));
            startActivity(intent);
        }
    }

    private void goToLocation(final Double lat, final Double lng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void punishmentCharge() {
        Date currentTcurrentTimeime = Calendar.getInstance().getTime();
        if (startTime != null) {
            long diff = startTime.getTime() - currentTcurrentTimeime.getTime();
            if (diff >= FIVE_MINUTES_DURATION) {
                // Note: checking client type
                clientType = prefs.getString("Client_Type", "default");
                if (clientType.equals("bon")) {
                    FirebaseDatabase.getInstance().getReference("COURSES").child(courseId).child("state").setValue("5");
                    double punishmentValue = Double.parseDouble(debit) - PANISHMENT_VALUE;
                    FirebaseDatabase.getInstance().getReference("DRIVERUSERS").
                            child(driverId).child("debt").setValue(Double.toString(punishmentValue));
                }
            }
        }
    }

    private void logout() {
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
        prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        prefs.edit().putString("online", "1").apply();
        startService(new Intent(MapsNewActivity.this, DriverService.class));
    }

    private void switchOfflineUI() {
        CustomAnimation.fadeIn(MapsNewActivity.this, offlineBtn, 500, 10);
        CustomAnimation.fadeIn(MapsNewActivity.this, switchOnlineBtn, 500, 10);
        CustomAnimation.fadeOut(MapsNewActivity.this, onlineBtn, 0, 10);
        switchOnlineBtn.setVisibility(View.VISIBLE);
        offlineBtn.setVisibility(View.VISIBLE);
        prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        prefs.edit().putString("online", "0").apply();
        stopService(new Intent(MapsNewActivity.this, DriverService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data.hasExtra("result")) {
            voipTv.setClickable(true);
            voipTv.setEnabled(true);
        }

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getLastLocation();
                        break;
                    case Activity.RESULT_CANCELED:
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
            Toast.makeText(MapsNewActivity.this, getString(R.string.txt_incoming_call), Toast.LENGTH_SHORT).show();
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
            iv_cancel_call_voip_one = dialog.findViewById(R.id.iv_cancel_call_voip_one);
            iv_recv_call_voip_one = dialog.findViewById(R.id.iv_recv_call_voip_one);
            caller_name = dialog.findViewById(R.id.callerName);
            callState = dialog.findViewById(R.id.callState);

            iv_mute = dialog.findViewById(R.id.iv_mute);
            iv_loud = dialog.findViewById(R.id.iv_loud);
            TextView tv_name_voip_one = dialog.findViewById(R.id.tv_name_voip_one);

            final Runnable mUpdate = new Runnable() {

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

            iv_recv_call_voip_one.setClickable(true);
            iv_mute.setVisibility(View.GONE);
            iv_loud.setVisibility(View.GONE);

            mp = MediaPlayer.create(this, R.raw.ring);
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
                        if (mp != null) {
                            if (mp.isPlaying()) {
                                mp.stop();
                                mp.release();
                            }
                        }
                        if (am != null) {
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, origionalVolume, 0);
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            if (ContextCompat.checkSelfPermission(MapsNewActivity.this, android.Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MapsNewActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsNewActivity.this,
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

}
