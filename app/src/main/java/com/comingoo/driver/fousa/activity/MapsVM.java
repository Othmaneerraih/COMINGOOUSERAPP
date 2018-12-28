package com.comingoo.driver.fousa.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.comingoo.driver.fousa.interfaces.CourseCallBack;
import com.comingoo.driver.fousa.interfaces.DataCallBack;
import com.comingoo.driver.fousa.interfaces.OnlineOfflineCallBack;
import com.comingoo.driver.fousa.interfaces.PriceCallBack;
import com.comingoo.driver.fousa.utility.Utilities;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.comingoo.driver.fousa.utility.Utilities.GetUnixTime;
import static com.comingoo.driver.fousa.utility.Utilities.getDateDay;
import static com.comingoo.driver.fousa.utility.Utilities.getDateMonth;

public class MapsVM {
    private double rat = 0.0;
    private String drivrNam = "";
    private String drivrImg = "";
    private String drivrNum = "";
    private String debt = "";
    private String todystrp = "";
    private String todysErn = "";
    private String driverId = "";

    private boolean isPromoCode;
    private double earned = 0;
    private double voyages = 0;
    private double driverDebt = 0;

    private String courseId = "";
    private String courseState = "";
    private String startAddress = "";
    private String destAddress = "";
    private String clientId = "";
    private String clientLevel = "";
    private String clientImageUri = "";
    private String clientName = "";
    private String clientPhoneNumber = "";
    private String clientlastCourse = "";
    private String clientSolde = "";
    private String clientCredit = "";
    private String clientTotalCourse = "";
    private String clientLastRideDate = "";
    private String preWaitTime = "";
    private String distanceTraveled = "";
    private LatLng driverArraivalLatLong;

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

                        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(driverId).child("EARNINGS").child(getDateMonth(GetUnixTime())).child(getDateDay(GetUnixTime())).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void checkCourseTask(final CourseCallBack callback) {
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
                        distanceTraveled = data.child("distanceTraveled").getValue(String.class);
                        preWaitTime = data.child("preWaitTime").getValue(String.class);
                        clientId = data.child("client").getValue(String.class);

                        if (data.child("endLat").getValue(String.class) != null) {
                            if (Objects.equals(data.child("endLat").getValue(String.class), "")) {
                                driverArraivalLatLong = null;
                            } else {
                                driverArraivalLatLong = new LatLng(Double.parseDouble(data.child("endLat").getValue(String.class)),
                                        Double.parseDouble(data.child("endLong").getValue(String.class)));
                            }
                        }

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
                                        clientSolde = dataSnapshot.child("SOLDE").getValue(String.class);
                                        clientCredit = dataSnapshot.child("USECREDIT").getValue(String.class);
                                        clientLevel = dataSnapshot.child("level").getValue(String.class);

                                        if (dataSnapshot.child("PROMOCODE").exists()) {
                                            isPromoCode = true;
                                        } else {
                                            isPromoCode = false;
                                        }


                                        Log.e("MapsVM", "clientId: "+clientId );
                                        FirebaseDatabase.getInstance().getReference("CLIENTFINISHEDCOURSES").child(clientId)/*.child(userId)
                .orderByKey()*/.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    try {
                                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
//                                                            Log.e(TAG, "date of ride: " + Objects.requireNonNull(child.child("date").getValue()).toString());
                                                            String longV = Objects.requireNonNull(child.child("date").getValue()).toString();
                                                            String dateString = convertDate(longV, "dd/MM/yyyy hh:mm:ss");
                                                            clientLastRideDate = dateString;
                                                            Log.e("MapsVM", "onDataChange:clientLastRideDate "+clientLastRideDate );

                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });

                                        // Note: Getting total Course Number of client
                                        FirebaseDatabase.getInstance().getReference("CLIENTFINISHEDCOURSES").child(clientId).
                                                addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        clientTotalCourse = String.valueOf(dataSnapshot.getChildrenCount());
                                                        Log.e("MapsVM", "onDataChange:getChildrenCount "+dataSnapshot.getChildrenCount() );




                                                        callback.callbackCourseInfo(courseId,
                                                                clientId, clientName, clientImageUri,
                                                                clientPhoneNumber, clientlastCourse, clientSolde, clientCredit,
                                                                startAddress, destAddress, distanceTraveled, courseState,
                                                                clientTotalCourse, clientLastRideDate, preWaitTime,
                                                                driverArraivalLatLong);


                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    }
                                                });

                                        // Note: Getting last Course date time of client
//                                        FirebaseDatabase.getInstance().getReference("CLIENTFINISHEDCOURSES").child(clientId)
//                                                .limitToLast(1).addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                if (dataSnapshot.exists()) {
//                                                    try {
//                                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                                                        }
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                                                callback.callbackCourseInfo(courseId,
//                                                        clientId, clientName, clientImageUri,
//                                                        clientPhoneNumber, clientlastCourse, clientSolde, clientCredit,
//                                                        startAddress, destAddress, distanceTraveled, courseState,
//                                                        clientTotalCourse, clientLastRideDate, preWaitTime, driverArraivalLatLong);
//                                            }
//                                        });



                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    callback.callbackCourseInfo(courseId,
                                            clientId, clientName, clientImageUri,
                                            clientPhoneNumber, clientlastCourse, clientSolde, clientCredit,
                                            startAddress, destAddress, distanceTraveled, courseState,
                                            clientTotalCourse, clientLastRideDate, preWaitTime, driverArraivalLatLong);
                                }
                            });
                        }
                    }
                } else {
                    courseState = "4";
                    callback.callbackCourseInfo(courseId, clientId, clientName,
                            clientImageUri, clientPhoneNumber, clientlastCourse, clientSolde, clientCredit,
                            startAddress, destAddress, distanceTraveled, courseState,
                            clientTotalCourse, clientLastRideDate, preWaitTime, driverArraivalLatLong);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public void gettingPriceValue(final PriceCallBack priceCallBack) {
        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").
                child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        driverDebt = Double.parseDouble(dataSnapshot.child("debt").getValue(String.class));
                        earned = Double.parseDouble(dataSnapshot.child("EARNINGS").child(getDateMonth(GetUnixTime())).
                                child(getDateDay(GetUnixTime())).child("earnings").getValue(String.class));
                        voyages = Integer.parseInt(dataSnapshot.child("EARNINGS").child(getDateMonth(GetUnixTime())).
                                child(getDateDay(GetUnixTime())).child("voyages").getValue(String.class));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseDatabase.getInstance().getReference("PRICES").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Price", " Price Calculation in Course Service");
                if (dataSnapshot.exists()) {
                    double att = Double.parseDouble(dataSnapshot.child("att").getValue(String.class));
                    double base = Double.parseDouble(dataSnapshot.child("base").getValue(String.class));
                    double debtCeil = Double.parseDouble(dataSnapshot.child("debtCeil").getValue(String.class));
                    double km = Double.parseDouble(dataSnapshot.child("km").getValue(String.class));
                    double min = Double.parseDouble(dataSnapshot.child("minimum").getValue(String.class));
                    final double percent = Double.parseDouble(dataSnapshot.child("percent").getValue(String.class));
                    priceCallBack.callbackPrice(att, base,
                            debtCeil, km, min, percent, isPromoCode, earned, voyages, driverDebt, clientLevel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void rateClient(final int Rate) {
        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientId).child("rating").child(Integer.toString(Rate)).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int rating = Integer.parseInt(dataSnapshot.getValue(String.class)) + 1;
                        FirebaseDatabase.getInstance().getReference("clientUSERS").child(clientId)
                                .child("rating").child(Integer.toString(Rate)).setValue("" + rating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    public void checkingOnlineOffline(final OnlineOfflineCallBack onlineOfflineCallBack) {
        FirebaseDatabase.getInstance().getReference().child("ONLINEDRIVERS").child(driverId).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            onlineOfflineCallBack.isOnline(true);
                        else onlineOfflineCallBack.isOnline(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
