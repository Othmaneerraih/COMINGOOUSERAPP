package com.comingoo.driver.fousa.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.comingoo.driver.fousa.R;
import com.comingoo.driver.fousa.interfaces.DataCallBack;
import com.comingoo.driver.fousa.utility.Utilities;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapsNewActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapsVM mapsVM;
    private double rating = 0.0;
    private String ddriverName = "";
    private String driverImage = "";
    private String driverNumber = "";
    private String debit = "";
    private String todayTrips = "";
    private String todayEarnings = "";
    private String TAG = "MapsNewActivity";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_new);
        permission();


        initialize();
        action();
    }

    private void permission(){
        if (!Utilities.isNetworkConnectionAvailable(MapsNewActivity.this)) {
            Utilities.checkNetworkConnection(MapsNewActivity.this);
        }
        Utilities.displayLocationSettingsRequest(MapsNewActivity.this,TAG,REQUEST_CHECK_SETTINGS);
        if (ContextCompat.checkSelfPermission(MapsNewActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsNewActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 4);
        }
    }


    private void initialize(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        mapsVM = new MapsVM();
    }

    private void action(){
        mapsVM.checkLogin();
        mapsVM.callback = new DataCallBack() {
            @Override
            public void callbackCall(boolean success, String drivrNam, String drivrImg, String drivrNum, String debt, String todystrp, String todysErn, double rat) {
                ddriverName = drivrNam;
                driverImage = drivrImg;
                driverNumber = drivrNum;
                debit = debt;
                todayTrips = todystrp;
                todayEarnings = todysErn;
                rating = rat;
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
