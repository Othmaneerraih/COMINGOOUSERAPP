package com.comingoo.driver.fousa.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.comingoo.driver.fousa.adapters.ComingooUAdapter;
import com.comingoo.driver.fousa.async.CheckUserComingoUTask;
import com.comingoo.driver.fousa.model.Car;
import com.comingoo.driver.fousa.utility.CustomAnimation;
import com.comingoo.driver.fousa.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class comingoonuActivity extends AppCompatActivity {
    private String TAG = "comingoonuActivity";
    private int selectedScreen = 0;
    private ConstraintLayout parametreLayout, profileLayout, addCarLayout, changePasswordLayout, carsLayout, portFeuilleLayout;
    private ImageView paramsImage, profilImage, portFImage;

    private ConstraintLayout pA, pR, pO;

    private ConstraintLayout changePasswordButton;
    private ImageButton backChangePassword;

    private ConstraintLayout manageCars;
    private ImageButton backManageCars;

    private Button addCarButton;

    private ImageButton backAddCar;

    private TextView tvTarrif, todayEarnings, debt, courses, userName, phoneNumber;

    private Button changePassBtn;

    private ImageView ivTarifBack;


    private RecyclerView mLocationView;
    private DatabaseReference mLocation;
    private ComingooUAdapter cAdapter;
    private List<Car> carsData;

    private EditText typeCar, modelCar, immCar, colorCar;
    private Button addCar;

    private ConstraintLayout tarifs;
    private ConstraintLayout tarifsLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comingoonu);

        initialize();

        todayEarnings.setText(getIntent().getStringExtra("earnings") + " MAD");
        todayEarnings.setTextColor(Color.GREEN);
        double debtPrice = Double.parseDouble(getIntent().getStringExtra("debt"));
        debt.setText(debtPrice + "MAD");

        if (debtPrice > 0) {
            debt.setTextColor(Color.GREEN);
        } else {
            debt.setTextColor(Color.RED);
        }
        courses.setText(getIntent().getStringExtra("courses"));
        userName.setText(getIntent().getStringExtra("name"));
        phoneNumber.setText(getIntent().getStringExtra("phone"));

        ivTarifBack = findViewById(R.id.iv_back_tarifs);

        selectedScreen = 0;
        updateUI();

        ivTarifBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tarifsLayout.setVisibility(View.GONE);
                ivTarifBack.setVisibility(View.GONE);
                tvTarrif.setVisibility(View.GONE);
                parametreLayout.setVisibility(View.VISIBLE);
            }
        });

        tarifs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvTarrif.setVisibility(View.VISIBLE);
                ivTarifBack.setVisibility(View.VISIBLE);
                selectedScreen = 6;
                updateUI();
            }
        });

        addCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCar(typeCar.getText().toString(), modelCar.getText().toString(), immCar.getText().toString(), colorCar.getText().toString());
            }
        });

        SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        String userId = prefs.getString("userId", null);


        mLocation = FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("CARS");
        mLocation.keepSynced(true);

        carsData = new ArrayList<>();
        mLocationView = findViewById(R.id.RecyclerView);
        mLocationView.setHasFixedSize(true);
        mLocationView.setLayoutManager(new LinearLayoutManager(this));

        cAdapter = new ComingooUAdapter(carsData,mLocation);
        mLocationView.setAdapter(cAdapter);

        new CheckUserComingoUTask(mLocation,cAdapter,carsData).execute();


        String Month;
        String day;
        Month = getDateMonth(GetUnixTime());
        day = getDateDay(GetUnixTime());
        if (Month.equals("01")) Month = "Janvier";
        if (Month.equals("02")) Month = "Février";
        if (Month.equals("03")) Month = "Mars";
        if (Month.equals("04")) Month = "Avril";
        if (Month.equals("05")) Month = "Mai";
        if (Month.equals("06")) Month = "Juin";
        if (Month.equals("07")) Month = "Juillet";
        if (Month.equals("08")) Month = "Aout";
        if (Month.equals("09")) Month = "Septembre";
        if (Month.equals("10")) Month = "Octobre";
        if (Month.equals("11")) Month = "Novembre";
        if (Month.equals("12")) Month = "Décembre";

        TextView d = findViewById(R.id.date);
        d.setText(Month + " " + day);


        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView oldPass = findViewById(R.id.oldPass);
                TextView newPass = findViewById(R.id.newPass);
                TextView confirmPass = findViewById(R.id.confirmPass);
                updatePassword(oldPass.getText().toString(), newPass.getText().toString(), confirmPass.getText().toString());
            }
        });
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScreen = 3;
                updateUI();
            }
        });
        backChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScreen = 1;
                updateUI();
            }
        });

        manageCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScreen = 4;
                updateUI();
            }
        });
        backManageCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScreen = 1;
                updateUI();
            }
        });
        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScreen = 2;
                updateUI();
            }
        });
        backAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScreen = 4;
                updateUI();
            }
        });

        pA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScreen = 0;
                updateUI();
            }
        });

        pR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScreen = 1;
                updateUI();
            }
        });

        pO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedScreen = 5;
                updateUI();
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initialize() {

        changePassBtn = findViewById(R.id.change_password_btn);

        typeCar = findViewById(R.id.marque_car);
        modelCar = findViewById(R.id.modele_car);
        immCar = findViewById(R.id.imm_car);
        colorCar = findViewById(R.id.color_car);
        addCar = findViewById(R.id.add_car);

        tarifs = findViewById(R.id.tarifs);
        tarifsLayout = findViewById(R.id.tarifsLayout);
        parametreLayout = findViewById(R.id.parametres_layout);
        profileLayout = findViewById(R.id.profil_layout);
        addCarLayout = findViewById(R.id.add_car_layout);
        changePasswordLayout = findViewById(R.id.change_password);
        carsLayout = findViewById(R.id.cars_layout);
        portFeuilleLayout = findViewById(R.id.porte_feuille_layout);

        paramsImage = findViewById(R.id.imageView13);
        profilImage = findViewById(R.id.imageView17);
        portFImage = findViewById(R.id.imageView16);


        pA = findViewById(R.id.paLayout);
        pR = findViewById(R.id.prLayout);
        pO = findViewById(R.id.poLayout);

        tvTarrif = findViewById(R.id.tv_tarrif);

        changePasswordButton = findViewById(R.id.change_password_button);
        backChangePassword = findViewById(R.id.back_select_password);

        manageCars = findViewById(R.id.manageCars);
        backManageCars = findViewById(R.id.back_select_cars);

        addCarButton = findViewById(R.id.add_car_button);
        backAddCar = findViewById(R.id.back_select_add_car);


        todayEarnings = findViewById(R.id.earnings_value);
        debt = findViewById(R.id.debt_value);
        courses = findViewById(R.id.courses_value);
        userName = findViewById(R.id.name_value);
        phoneNumber = findViewById(R.id.phone_value);
    }

    private void addNewCar(String marque, String modele, String imm, String colorCar) {
        if (marque.length() >= 3 && modele.length() >= 3 && imm.length() >= 3 && colorCar.length() >= 3) {

            String name = marque + " " + modele;
            String description = imm + " " + colorCar;
            Map<String, String> data = new HashMap<>();
            DatabaseReference dataBase = mLocation.push();

            data.put("name", name);
            data.put("description", description);
            data.put("selected", "0");
            data.put("id", dataBase.getKey());

            dataBase.setValue(data);

            selectedScreen = 4;
            updateUI();
        } else {
            Toast.makeText(comingoonuActivity.this, "Tous les champs doivent étres remplis!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideEverything() {
        parametreLayout.setVisibility(View.GONE);
        profileLayout.setVisibility(View.GONE);
        addCarLayout.setVisibility(View.GONE);
        changePasswordLayout.setVisibility(View.GONE);
        carsLayout.setVisibility(View.GONE);
        portFeuilleLayout.setVisibility(View.GONE);
        tarifsLayout.setVisibility(View.GONE);
    }

    private void toolBarUI() {

        if (selectedScreen == 0 || selectedScreen == 6) {
            paramsImage.setBackgroundResource(R.drawable.parametres_selected);
            profilImage.setBackgroundResource(R.drawable.profil_cnu);
            portFImage.setBackgroundResource(R.drawable.porte_feuille_unselected);

        } else if (selectedScreen == 1 || selectedScreen == 2 || selectedScreen == 3 || selectedScreen == 4) {
            paramsImage.setBackgroundResource(R.drawable.parametre_cnu);
            profilImage.setBackgroundResource(R.drawable.profil_selected);
            portFImage.setBackgroundResource(R.drawable.porte_feuille_unselected);
        } else {
            paramsImage.setBackgroundResource(R.drawable.parametre_cnu);
            profilImage.setBackgroundResource(R.drawable.profil_cnu);
            portFImage.setBackgroundResource(R.drawable.porte_feuille_selected);
        }

    }

    private void updateUI() {
        hideEverything();
        toolBarUI();
        switch (selectedScreen) {
            case 0:
                CustomAnimation.fadeIn(comingoonuActivity.this, parametreLayout, 500, 10);
                break;
            case 1:
                CustomAnimation.fadeIn(comingoonuActivity.this, profileLayout, 500, 10);
                break;
            case 2:
                CustomAnimation.fadeIn(comingoonuActivity.this, addCarLayout, 500, 10);
                break;
            case 3:
                CustomAnimation.fadeIn(comingoonuActivity.this, changePasswordLayout, 500, 10);
                break;
            case 4:
                CustomAnimation.fadeIn(comingoonuActivity.this, carsLayout, 500, 10);
                break;
            case 5:
                CustomAnimation.fadeIn(comingoonuActivity.this, portFeuilleLayout, 500, 10);
                break;
            case 6:
                CustomAnimation.fadeIn(comingoonuActivity.this, tarifsLayout, 500, 10);
                break;
        }

    }


    private void updatePassword(final String oldPassword, final String newPassword, final String confirmPassword) {
        changePassBtn.setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        if (newPassword.length() != 0 && confirmPassword.length() != 0) {
            SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
            final String userId = prefs.getString("userId", null);
            FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        if (oldPassword.equals(dataSnapshot.child("password").getValue(String.class))) {
                            Toast.makeText(comingoonuActivity.this, "Mot de passe changé!", Toast.LENGTH_SHORT).show();
                            FirebaseDatabase.getInstance().getReference("DRIVERUSERS").child(userId).child("password").setValue(newPassword);
                            changePassBtn.setVisibility(View.VISIBLE);
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            selectedScreen = 1;
                            updateUI();
                        } else {
                            Toast.makeText(comingoonuActivity.this, "Mot de passe erroné", Toast.LENGTH_SHORT).show();
                            changePassBtn.setVisibility(View.VISIBLE);
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
            Toast.makeText(comingoonuActivity.this, "Tous les champs doivent étres remplis!!", Toast.LENGTH_SHORT).show();
            changePassBtn.setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }


    private String getDateMonth(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000L);
        String date = DateFormat.format("MM", cal).toString();
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

}
