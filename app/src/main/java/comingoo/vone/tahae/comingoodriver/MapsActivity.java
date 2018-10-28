package comingoo.vone.tahae.comingoodriver;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.firebase.database.ChildEventListener;
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
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    private Button offlineButton;
    private Button onlineButton;
    private Button switchOnlineButton;

    private ImageButton menuButton;
    private ImageButton myPositionButton;

    private ImageButton wazeButton;
//    private ImageButton contactButton;
//    private Button cancel_ride_btn;

    private RelativeLayout clientInfoLayout;
    private ConstraintLayout destinationLayout;

    private ImageView arrowImage;
    private ImageView whitePersonImage;

    private TextView addressText;

    private TextView destTime;

    private Button courseActionButton;
    private ImageButton cancelCourseButton;

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

    ////////////////////////////////////////////

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
        switchOnlineButton.setBackground(new BitmapDrawable(getResources(), scaleBitmap((int) 60, (int) 60, R.drawable.goo_bt)));
        // switchOnlineButton.setImageBitmap(scaleBitmap(60, 60, R.drawable.goo_bt));
        menuButton.setBackground(new BitmapDrawable(getResources(), scaleBitmap(40, 40, R.drawable.menu_icon)));
        myPositionButton.setBackground(new BitmapDrawable(getResources(), scaleBitmap(40, 40, R.drawable.my_location)));
        wazeButton.setBackground(new BitmapDrawable(getResources(), scaleBitmap(40, 40, R.drawable.waze_icon)));
//        contactButton.setBackground(new BitmapDrawable(getResources(), scaleBitmap(40, 40, R.drawable.contact)));
        arrowImage.setBackground(new BitmapDrawable(getResources(), scaleBitmap(30, 30, R.drawable.arrow_blue)));
        whitePersonImage.setBackground(new BitmapDrawable(getResources(), scaleBitmap(30, 50, R.drawable.person_white)));
    }

    ////////////////////////////////////////////


    private float density;
    private float dpHeight;
    private float dpWidth;
    private Intent intent;
    private TextView tv_appelle_voip, tv_appelle_telephone;

    private String TAG = "MapsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        try {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

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

            tv_appelle_voip = (TextView) findViewById(R.id.tv_appelle_voip);
            tv_appelle_telephone = (TextView) findViewById(R.id.tv_appelle_telephone);

            tv_appelle_telephone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clientPhoneNumber != null) {
                        try {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + clientPhoneNumber));
                            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                startActivity(callIntent);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            tv_appelle_voip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!driverId.isEmpty()) {
                        Intent intent = new Intent(MapsActivity.this, VoipCallingActivity.class);
                        intent.putExtra("driverId", driverId);
                        intent.putExtra("clientId", clientId);
                        intent.putExtra("clientName", clientName);
                        startActivity(intent);

                    }
                }
            });

//

//            contactButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (!clientPhoneNumber.isEmpty() || clientPhoneNumber != null) {
//                        try {
//                            Intent callIntent = new Intent(Intent.ACTION_CALL);
//                            callIntent.setData(Uri.parse("tel:" + clientPhoneNumber));
//                            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//                                startActivity(callIntent);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });

//            cancel_ride_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            switch (which) {
//                                case DialogInterface.BUTTON_POSITIVE:
//                                    //Yes button clicked
//                                    FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).child("state").setValue("5");
//                                    FirebaseDatabase.getInstance().getReference("COURSES").child(courseID).removeValue();
//                                    break;
//
//                                case DialogInterface.BUTTON_NEGATIVE:
//                                    //No button clicked
//                                    break;
//                            }
//                        }
//                    };
//                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
//                    builder.setTitle("Vous étes sure?").setMessage("Voulez-vous annuler la course?").setPositiveButton("Yes", dialogClickListener)
//                            .setNegativeButton("No", dialogClickListener).show();
//                }
//            });

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
                    startActivity(new Intent(MapsActivity.this, historiqueActivity.class));
                }
            });

            Inbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MapsActivity.this, notificationActivity.class));
                }
            });
            Aide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MapsActivity.this, aideActivity.class));
                }
            });
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                    startActivity(new Intent(MapsActivity.this, MainActivity.class));
                    finish();
                }
            });
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.driver_pin);
            smallMarker = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width, height, false);

            new checkCourseTask().execute();
            new checkCourseFinished().execute();

//            final ChildEventListener childEventListener = new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    Log.e(TAG, "onChildChanged: ujjwal not exists"+dataSnapshot.toString());
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
////                    if (!dataSnapshot.child("ujjwal").exists()) {
//                        Log.e(TAG, "onDataChange: ujjwal not exists");
//                        Log.e(TAG, "onDataChange: ujjwal not exists" + dataSnapshot.toString());
////                    }
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    Log.e(TAG, "onChildMoved: ujjwal not exists" + dataSnapshot.toString());
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    Log.e(TAG, "onCancelled: ujjwal not exists" + databaseError.toString());
//                }
//            };
//            FirebaseDatabase.getInstance().getReference("COURSES").addChildEventListener(childEventListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeViews() {
        onlineButton = (Button) findViewById(R.id.online_button);
        offlineButton = (Button) findViewById(R.id.offline_button);
        switchOnlineButton = (Button) findViewById(R.id.switch_online_button);

        menuButton = (ImageButton) findViewById(R.id.menu_button);
        myPositionButton = (ImageButton) findViewById(R.id.my_position_button);

        wazeButton = (ImageButton) findViewById(R.id.waze_button);
//        contactButton = (ImageButton) findViewById(R.id.contact_button);
//        cancel_ride_btn = (Button) findViewById(R.id.cancel_ride_btn);

        clientInfoLayout = (RelativeLayout) findViewById(R.id.clientInfo);
        destinationLayout = (ConstraintLayout) findViewById(R.id.destination_layout);

        arrowImage = (ImageView) findViewById(R.id.arrow_image);
        whitePersonImage = (ImageView) findViewById(R.id.white_person_image);

        addressText = (TextView) findViewById(R.id.addressText);

        destTime = (TextView) findViewById(R.id.destTime);

        courseActionButton = (Button) findViewById(R.id.course_action_button);
        cancelCourseButton = (ImageButton) findViewById(R.id.cancelCourse);

        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);

        Acceuil = (ConstraintLayout) findViewById(R.id.acceuil);
        Historique = (ConstraintLayout) findViewById(R.id.historique);
        Inbox = (ConstraintLayout) findViewById(R.id.inbox);
        ComingoonYou = (ConstraintLayout) findViewById(R.id.comingoonyou);
        Aide = (ConstraintLayout) findViewById(R.id.aide);
        logout = (ConstraintLayout) findViewById(R.id.logout);

        money = (Button) findViewById(R.id.money);
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


    private void switchOnlineUI() {
        //offlineButton.setVisibility(View.GONE);
        //switchOnlineButton.setVisibility(View.GONE);
        //onlineButton.setVisibility(View.VISIBLE);

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
            if (params[0] == true)
                switchOnlineUI();
        }
    }

    private void switchToCourseUI() {
        findViewById(R.id.statusConstraint).setVisibility(View.GONE);
        findViewById(R.id.money).setVisibility(View.GONE);
        menuButton.setVisibility(View.GONE);


        clientInfoLayout.setVisibility(View.VISIBLE);
        destinationLayout.setVisibility(View.VISIBLE);
    }

    private void cancelCourseUI() {
        findViewById(R.id.statusConstraint).setVisibility(View.VISIBLE);
        findViewById(R.id.money).setVisibility(View.VISIBLE);
        menuButton.setVisibility(View.VISIBLE);


        clientInfoLayout.setVisibility(View.GONE);
        destinationLayout.setVisibility(View.GONE);
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


    int RATE = 0;
    int cM =0;

    private class checkCourseFinished extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {

            final SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
            final String userId = prefs.getString("userId", null);
            driverId = userId;

            if (userId == null) return "";
            FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("COURSE").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        FirebaseDatabase.getInstance().getReference("DRIVERFINISHEDCOURSES").
                                child(userId).child(dataSnapshot.getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshott) {

                                final Dialog dialog = new Dialog(MapsActivity.this);
                                dialog.setContentView(R.layout.finished_course);

                                Button dialogButton = (Button) dialog.findViewById(R.id.button);
                                final Button star1 = (Button) dialog.findViewById(R.id.star1);
                                final Button star2 = (Button) dialog.findViewById(R.id.star2);
                                final Button star3 = (Button) dialog.findViewById(R.id.star3);
                                final Button star4 = (Button) dialog.findViewById(R.id.star4);
                                final Button star5 = (Button) dialog.findViewById(R.id.star5);

                                final Button price = (Button) dialog.findViewById(R.id.button3);

                                final ImageView imot = (ImageView) dialog.findViewById(R.id.stars_rating);

                                final Button gotMoney = (Button) dialog.findViewById(R.id.button);
                                final Button charge = (Button) dialog.findViewById(R.id.btn_recharger);
                                final EditText moneyAmount = (EditText) dialog.findViewById(R.id.editText);

                                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").
                                        child(userId).child("PAID").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            if (dataSnapshot.getValue(String.class).equals("0")) {
                                                price.setText(dataSnapshott.child("price").getValue(String.class) + " MAD");
                                            } else {
                                                price.setText("0 MAD");
                                                charge.setVisibility(View.GONE);
                                                moneyAmount.setVisibility(View.GONE);
                                            }
                                        } else {
                                            price.setText(dataSnapshott.child("price").getValue(String.class) + " MAD");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                FirebaseDatabase.getInstance().getReference("PRICES").
                                        addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (drawRouteStart != null) {
                                                    double distanceInKm = distanceInKilometer(drawRouteStart.latitude, drawRouteStart.longitude,
                                                            drawRouteArrival.latitude, drawRouteArrival.longitude);

                                                    Log.e("kilometer", "onDataChange: " + distanceInKm);


                                                    double price1 = Double.parseDouble(dataSnapshot.child("base").getValue(String.class) +
                                                            (Double.parseDouble(dataSnapshot.child("km").getValue(String.class)) * distanceInKm) +
                                                            Double.parseDouble(dataSnapshot.child("att").getValue(String.class)) * driverWaitTime);

                                                    Log.e("kilometer price", "onDataChange: " + price1);
                                                    price.setText(price1 + " MAD");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


//                                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("PAID").addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        if(dataSnapshot.exists()){
//                                            if(dataSnapshot.getValue(String.class).equals("0")) {
//                                                price.setText(dataSnapshott.child("price").getValue(String.class) + " MAD");
//                                            }else{
//                                                price.setText("0 MAD");
//                                                charge.setVisibility(View.GONE);
//                                                moneyAmount.setVisibility(View.GONE);
//                                            }
//                                        }else{
//                                            price.setText(dataSnapshott.child("price").getValue(String.class) + " MAD");
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });


                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        stopService(intent);
                                        startService(intent);
                                    }
                                });
                                gotMoney.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("COURSE").removeValue();
                                        dialog.dismiss();
                                    }
                                });

                                charge.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String val = moneyAmount.getText().toString();
                                        if (moneyAmount.getText().toString().length() > 0) {
                                            final int money = Integer.parseInt(val) - Integer.parseInt(dataSnapshott.child("price").getValue(String.class));
                                            if (money > 0) {
                                                dialog.dismiss();
                                                FirebaseDatabase.getInstance().getReference("clientUSERS").
                                                        child(dataSnapshott.child("client").getValue(String.class)).child("SOLDE").
                                                        addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                cM = money;
                                                                if (dataSnapshot.exists()) {
                                                                    cM += Integer.parseInt(dataSnapshot.getValue(String.class));
                                                                }
                                                                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("COURSE").removeValue();

                                                                FirebaseDatabase.getInstance().getReference("DRIVERFINISHEDCOURSES").
                                                                        child(userId).child(dataSnapshot.getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.exists()) {
                                                                            String riderId = dataSnapshot.child("client").getValue(String.class);
//                                                                            Log.e(TAG, "Rider id, onDataChange: " + riderId);

                                                                            FirebaseDatabase.getInstance().getReference("CLIENTFINISHEDCOURSES").child(riderId).addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                                                    Log.e(TAG, "Rider total, onDataChange: " + dataSnapshot.getChildrenCount() + " Total");

                                                                                    if (dataSnapshot.getChildrenCount() >= 3) {
                                                                                        if (cM <= 100) {
                                                                                            FirebaseDatabase.getInstance().getReference("clientUSERS").child(dataSnapshott.child("client").getValue(String.class)).child("SOLDE").setValue("" + cM);
                                                                                        }
                                                                                    } else {
                                                                                        if (cM <= 10) {
                                                                                            FirebaseDatabase.getInstance().getReference("clientUSERS").child(dataSnapshott.child("client").getValue(String.class)).child("SOLDE").setValue("" + cM);
                                                                                        }
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(DatabaseError databaseError) {

                                                                                }
                                                                            });


                                                                        }
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
                                    }
                                });


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


                                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                                lp.dimAmount = 0.5f;
                                dialog.getWindow().setAttributes(lp);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                            /*    String price = dataSnapshott.child("price").getValue(String.class);
                                Intent finishedCour²se = new Intent(MainActivity.this, finishedCourse.class);
                                finishedCourse.putExtra("price", price+" MAD");
                                finishedCourse.putExtra("courseID", dataSnapshot.getValue(String.class));
                                finishedCourse.putExtra("driverID", dataSnapshott.child("driver").getValue(String.class));

                                startActivity(finishedCourse);
*/
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


//                sinchClient = Sinch.getSinchClientBuilder()
//                        .context(MapsActivity.this)
//                        .userId(number)
//                        .applicationKey("05a626b9-33a4-4b83-b7bc-2d49062ea9ae")
//                        .applicationSecret("gVW3Tm0140e9i17wRwUzzw==")
//                        .environmentHost("clientapi.sinch.com")
//                        .build();
//                sinchClient.setSupportCalling(true);
//                sinchClient.start();
//                sinchClient.startListeningOnActiveConnection();
//
//                sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());


                FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(number).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            String isVerified = dataSnapshot.child("isVerified").getValue(String.class);
                            if (isVerified.equals("0")) {
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

                            if (dataSnapshot.child("rating").child("1").getValue(String.class) != null || dataSnapshot.child("rating").child("2").getValue(String.class) != null || dataSnapshot.child("rating").child("3").getValue(String.class) != null || dataSnapshot.child("rating").child("4").getValue(String.class) != null || dataSnapshot.child("rating").child("5").getValue(String.class) != null) {

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

                            FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(number).child("EARNINGS").child(getDateMonth(GetUnixTime())).child(getDateDay(GetUnixTime())).addValueEventListener(new ValueEventListener() {
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


//    private class SinchCallClientListener implements CallClientListener {
//        @Override
//        public void onIncomingCall(final CallClient callClient, final Call incomingCall) {
//            //Pick up the call!
//
//            findViewById(R.id.callLayout).setVisibility(View.VISIBLE);
//            final Button hangup = (Button) findViewById(R.id.hangup);
//            hangup.setText("Answer");
//            hangup.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    incomingCall.answer();
//                    hangup.setText("Hangup");
//                    hangup.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            incomingCall.hangup();
//                            findViewById(R.id.callLayout).setVisibility(View.GONE);
//                        }
//                    });
//                }
//            });
//
//        }
//    }

    private void logout() {
        final SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        final String number = prefs.getString("userId", null);
        prefs.edit().remove("userId").apply();
    }

    private void setUserUi() {
        CircleImageView driverI = (CircleImageView) findViewById(R.id.profile_image);
        TextView fullName = (TextView) findViewById(R.id.fullName);
        TextView ratingR = (TextView) findViewById(R.id.ratings);

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
        money.setText(todayEarnings + " MAD");


        ComingoonYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, comingoonuActivity.class);
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

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            //Pick up the call!
        }
    }


    private String courseID;
    private String courseState;
    private DatabaseReference courseRef;
    private DataSnapshot driverData;
    private String clientId;
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
        String userId;

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

                            String lat = data.child("startLat").getValue(String.class);
                            String lng = data.child("startLong").getValue(String.class);

                            if (driverData.child("endLat").getValue(String.class) != null) {
                                if (driverData.child("endLat").getValue(String.class).equals("")) {
                                    drawRouteArrival = null;
                                } else {
                                    drawRouteArrival = new LatLng(Double.parseDouble(data.child("endLat").getValue(String.class)), Double.parseDouble(data.child("endLong").getValue(String.class)));
                                }
                            }

                            if (clientId != null) {
                                FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        clientImageUri = dataSnapshot.child("image").getValue(String.class);
                                        clientName = dataSnapshot.child("fullName").getValue(String.class);
                                        clientPhoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                                        lastCourse = dataSnapshot.child("LASTCOURSE").getValue(String.class);
                                        courseHandle();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    } else {
                        //stopCourseService();
                        courseState = "4";
                        courseHandle();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

//            FirebaseDatabase.getInstance().getReference("COURSES").child(userId).
//                    child("state").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (!dataSnapshot.exists()) {
//                        try {
//                            if (Build.VERSION.SDK_INT >= 11) {
//                                MapsActivity.this.recreate();
//                            } else {
//                                finish();
//                                startActivity(MapsActivity.this.getIntent());
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });


            final ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    Log.e(TAG, "onChildChanged: ujjwal not exists" + dataSnapshot.toString());
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        MapsActivity.this.recreate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            FirebaseDatabase.getInstance().getReference("COURSES").child(userId).addChildEventListener(childEventListener);


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
            findViewById(R.id.waze_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    return;
                }
            });
            courseUIOff();
            if (mMap != null)
                mMap.clear();


        } else {

            switchToCourseUI();
            if (courseState.equals("3")) {
                stopCourseService();
                courseState = "4";
                if (mMap != null)
                    mMap.clear();

            }


            startCourseService(courseID);
            checkCourseState();
        }


    }

    CircleImageView clientImage;
    TextView name, textView4;

    public void checkCourseState() {

        switchToCourseUI();
        clientImage = findViewById(R.id.clientImage);
        name = (TextView) findViewById(R.id.name);
        textView4 = (TextView) findViewById(R.id.textView4);

        name.setText(clientName);
        textView4.setText(lastCourse);
        if (clientImageUri != null) {
            if (clientImageUri.length() > 0)
                Picasso.get().load(clientImageUri).fit().centerCrop().into(clientImage);
        }

        if (courseState.equals("0")) {
            addressText.setText(startAddress);
            cancelCourseButton.setVisibility(View.GONE);
            courseActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    courseRef.child("state").setValue("1");
                }
            });
            courseActionButton.setText("Appuyez pour arriver");


            if (userLatLng != null && drawRouteStart != null) {
                new DrawRouteTask().execute(userLatLng, drawRouteStart);

                wazeButton.setVisibility(View.VISIBLE);
                wazeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openWaze(drawRouteStart);
                    }
                });

            }
        }
        if (courseState.equals("1")) {
            addressText.setText(destAddress);
            courseActionButton.setText("Appuyez pour commancer");
            wazeButton.setVisibility(View.GONE);
            courseActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    courseRef.child("state").setValue("2");
                }
            });
            if (mMap != null)
                mMap.clear();
        }
        if (courseState.equals("2")) {
            addressText.setText(destAddress);
            courseActionButton.setText("Appuyez pour terminer");
            courseActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    courseRef.child("state").setValue("3");
                }
            });


            if (drawRouteArrival != null && drawRouteStart != null) {
                new DrawRouteTask().execute(drawRouteStart, drawRouteArrival);

                wazeButton.setVisibility(View.VISIBLE);
                wazeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openWaze(drawRouteArrival);
                    }
                });

            }

        }


    }

    private void switchtoCourseUI() {
        switchOfflineUI();
        findViewById(R.id.statusConstraint).setVisibility(View.GONE);
        findViewById(R.id.money).setVisibility(View.GONE);
        clientInfoLayout.setVisibility(View.VISIBLE);
        destinationLayout.setVisibility(View.VISIBLE);
        menuButton.setVisibility(View.GONE);
    }

    private void courseUIOff() {
        findViewById(R.id.statusConstraint).setVisibility(View.VISIBLE);
        findViewById(R.id.money).setVisibility(View.VISIBLE);
//        clientInfoLayout.setVisibility(View.GONE);
        destinationLayout.setVisibility(View.GONE);
        menuButton.setVisibility(View.VISIBLE);
    }

    public void startCourseService(String id) {
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
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
            startActivity(intent);
        }

    }


    private class DrawRouteTask extends AsyncTask<LatLng, Integer, String> {

        LatLng start;
        LatLng arrival;
        List<LatLng> thePath;
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
                    .apiKey("AIzaSyDKndcnw3IXjPPsP1gmkFLbeuLDfHXxc4o")
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
            } catch (Exception ex) {
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
                        drawPolyGradiant(thePath, "#76b5f9", "#1c549d", 9, 4);
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

    private void drawPolyGradiant(List<LatLng> thePath, String startColor, String endColor, int width, int quality) {

        int Size = thePath.size();

        int Red = Integer.valueOf(startColor.substring(1, 3), 16);
        int Green = Integer.valueOf(startColor.substring(3, 5), 16);
        int Blue = Integer.valueOf(startColor.substring(5, 7), 16);
        int finalRed = Integer.valueOf(endColor.substring(1, 3), 16);
        int finalGreen = Integer.valueOf(endColor.substring(3, 5), 16);
        int finalBlue = Integer.valueOf(endColor.substring(5, 7), 16);

        for (int i = 0; i < quality - 1; i++) {

            float percent = (float) (1 / (float) (2 * quality)) + (float) i / (float) quality;
            int color = Color.argb(255,
                    (Red > finalRed) ? (int) (Red - ((Red - finalRed) * percent)) : (int) (Red + ((finalRed - Red) * percent)),
                    (Green > finalGreen) ? (int) (Green - ((Green - finalGreen) * percent)) : (int) (Green + ((finalGreen - Green) * percent)),
                    (Blue > finalBlue) ? (int) (Blue - ((Blue - finalBlue) * percent)) : (int) (Blue + ((finalBlue - Blue) * percent)));

            PolylineOptions opts = new PolylineOptions().geodesic(false).addAll(thePath.subList((int) ((Size / quality) * i), (int) (Size / quality) * (i + 2))).color(color).width(width + 1);
            mMap.addPolyline(opts);
        }

        float percentage = (float) (1 / (float) (2 * quality)) + (float) (quality - 1) / (float) quality;
        int color = Color.argb(255,
                (Red > finalRed) ? (int) (Red - ((Red - finalRed) * percentage)) : (int) (Red + ((finalRed - Red) * percentage)),
                (Green > finalGreen) ? (int) (Green - ((Green - finalGreen) * percentage)) : (int) (Green + ((finalGreen - Green) * percentage)),
                (Blue > finalBlue) ? (int) (Blue - ((Blue - finalBlue) * percentage)) : (int) (Blue + ((finalBlue - Blue) * percentage)));

        PolylineOptions opts = new PolylineOptions().geodesic(false).addAll(thePath.subList((int) ((Size / quality) * (quality - 1)), (int) (Size / quality) * (quality))).color(color).width(width + 1);
        mMap.addPolyline(opts);

        for (int i = 0; i < (Size - 1); i++) {

            float percent = ((float) i / (float) Size);
            int usedColor = Color.argb(255,
                    (Red > finalRed) ? (int) (Red - ((Red - finalRed) * percent)) : (int) (Red + ((finalRed - Red) * percent)),
                    (Green > finalGreen) ? (int) (Green - ((Green - finalGreen) * percent)) : (int) (Green + ((finalGreen - Green) * percent)),
                    (Blue > finalBlue) ? (int) (Blue - ((Blue - finalBlue) * percent)) : (int) (Blue + ((finalBlue - Blue) * percent)));

            opts = new PolylineOptions().add(thePath.get(i)).geodesic(false).add(thePath.get(i + 1)).color(usedColor).width(width);
            mMap.addPolyline(opts);
        }
    }

    LatLng userLatLng;

//    private void getLastLocation() {
//        // Get last known recent location using new Google Play Services SDK (v11+)
//        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
//
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            locationClient.getLastLocation()
//                    .addOnSuccessListener(new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            // GPS location can be null if GPS is switched off
//                            if (location != null) {
//                                userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//                                goToLocation(userLatLng.latitude, userLatLng.longitude);
//                            }
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("MapDemoActivity", "Error trying to get last GPS location");
//                            e.printStackTrace();
//                        }
//                    });
//        }
//    }

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
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

//         mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 17));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

//        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//            @Override
//            public void onMapLoaded() {
//                //Add markers here
//
//
//
//                mMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(lat, lng))
//                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
//
//            }
//        });

    }


}
