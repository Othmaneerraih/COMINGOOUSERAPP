package com.comingoo.driver.fousa.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.comingoo.driver.fousa.interfaces.CourseCallBack;
import com.comingoo.driver.fousa.interfaces.DataCallBack;
import com.comingoo.driver.fousa.service.DriverService;
import com.comingoo.driver.fousa.utility.Utilities;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class MapsVM {
    private double rat = 0.0;
    private String drivrNam = "";
    private String drivrImg = "";
    private String drivrNum = "";
    private String debt = "";
    private String todystrp = "";
    private String todysErn = "";
    private String driverId = "";

    private String courseId = "";
    private String courseState = "";
    private String startAddress = "";
    private String destAddress = "";
    private String clientId = "";
    private String clientImageUri = "";
    private String clientName = "";
    private String clientPhoneNumber = "";
    private String clientlastCourse = "";

    public void checkLogin(final Context context, final DataCallBack callback) {
        final String TAG = "checkLoginVM";
        final SharedPreferences prefs = context.getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        driverId = prefs.getString("userId", null);
        if (driverId == null) {
            //User Is Logged In
            callback.callbackCall(false, "", "", "", "", "", "", 0.0, "");
        } else {
            if (Looper.myLooper() == null) {
                Looper.prepare();
                Looper.myLooper();
            }

            FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(driverId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.e(TAG, "onDataChange: driver exists");
                        String isVerified = dataSnapshot.child("isVerified").getValue(String.class);
                        if (isVerified != null && isVerified.equals("0")) {
                            prefs.edit().remove("phoneNumber").apply();
                            prefs.edit().remove("userId").apply();
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }
                        drivrNam = dataSnapshot.child("fullName").getValue(String.class);
                        drivrImg = dataSnapshot.child("image").getValue(String.class);
                        drivrNum = dataSnapshot.child("phoneNumber").getValue(String.class);
                        prefs.edit().putString("userId", dataSnapshot.getKey()).apply();

                        if (dataSnapshot.child("rating").child("1").getValue(String.class) != null
                                || dataSnapshot.child("rating").child("2").getValue(String.class) != null ||
                                dataSnapshot.child("rating").child("3").getValue(String.class) != null ||
                                dataSnapshot.child("rating").child("4").getValue(String.class) != null ||
                                dataSnapshot.child("rating").child("5").getValue(String.class) != null) {

                            double r1 = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("rating").child("1").getValue(String.class)));
                            double r2 = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("rating").child("2").getValue(String.class)));
                            double r3 = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("rating").child("3").getValue(String.class)));
                            double r4 = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("rating").child("4").getValue(String.class)));
                            double r5 = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("rating").child("5").getValue(String.class)));

                            double gettingTotalRate = r1 + (r2 * 2) + (r3 * 3) + (r4 * 4) + (r5 * 5);
                            double sum = r1 + r2 + r3 + r4 + r5;


                            if (sum != 0)
                                rat = gettingTotalRate / sum;
                        }

                        if (dataSnapshot.child("debt").exists())
                            debt = dataSnapshot.child("debt").getValue(String.class);
                        else
                            debt = "0.0";


                        todysErn = "0";
                        todystrp = "0";

                        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(driverId).child("EARNINGS").child(Utilities.getDateMonth(Utilities.GetUnixTime())).child(Utilities.getDateDay(Utilities.GetUnixTime())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    todysErn = dataSnapshot.child("earnings").getValue(String.class);
                                    todystrp = dataSnapshot.child("voyages").getValue(String.class);
                                    callback.callbackCall(true, drivrNam, drivrImg, drivrNum, debt, todystrp, todysErn, rat, driverId);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                callback.callbackCall(false, "", "", "", "", "", "", 0.0, "");
                            }
                        });

                        callback.callbackCall(true, drivrNam, drivrImg, drivrNum, debt, todystrp, todysErn, rat, driverId);
                    } else {
                        Toast.makeText(context, driverId, Toast.LENGTH_SHORT).show();
                        prefs.edit().remove("phoneNumber").apply();
                        prefs.edit().remove("userId").apply();
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callback.callbackCall(false, "", "", "", "", "", "", 0.0, "");
                }
            });
        }

    }

    public void checkCourseTask(Context context, final CourseCallBack callback) {
        FirebaseDatabase.getInstance().getReference("COURSES").orderByChild("driver").
                equalTo(driverId).limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (final DataSnapshot data : dataSnapshot.getChildren()) {
                        courseId = data.getKey();
                        courseState = data.child("state").getValue(String.class);


                        startAddress = data.child("startAddress").getValue(String.class);
                        destAddress = data.child("endAddress").getValue(String.class);

                        clientId = data.child("client").getValue(String.class);

                        if (clientId != null) {
                            FirebaseDatabase.getInstance().getReference("clientUSERS").
                                    child(clientId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        clientImageUri = dataSnapshot.child("image").getValue(String.class);
                                        clientName = dataSnapshot.child("fullName").getValue(String.class);
                                        clientPhoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                                        clientlastCourse = dataSnapshot.child("LASTCOURSE").getValue(String.class);
                                        callback.callbackCourseInfo(courseId,
                                                clientId, clientName, clientImageUri, clientPhoneNumber, clientlastCourse, startAddress, destAddress, courseState);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    callback.callbackCourseInfo(courseId, clientId, clientName, clientImageUri, clientPhoneNumber, clientlastCourse, startAddress, destAddress, courseState);
                                }
                            });
                        }
                    }
                } else {
                    courseState = "4";
                    callback.callbackCourseInfo(courseId, clientId, clientName, clientImageUri, clientPhoneNumber, clientlastCourse, startAddress, destAddress, courseState);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
