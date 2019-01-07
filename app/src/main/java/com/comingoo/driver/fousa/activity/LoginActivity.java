package com.comingoo.driver.fousa.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.comingoo.driver.fousa.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(!isNetworkConnectionAvailable()){
            checkNetworkConnection();
        }
        checkLogin();

        phoneNumber = findViewById(R.id.phoneNumber);
        password = findViewById(R.id.password);
        ImageButton loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNumber.getText().toString().isEmpty() && password.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Fill all the fields", Toast.LENGTH_SHORT).show();
                }else{
                    login(phoneNumber.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    public void checkNetworkConnection(){
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
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

    public boolean isNetworkConnectionAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if(isConnected) {
            Log.d("Network", "Connected");
            return true;
        }
        else{
            Log.d("Network","Not Connected");
            return false;
        }
    }

    private Boolean phoneNumberValidation(String number){
        char x = number.charAt(0);
        char y = number.charAt(1);
        if(x != '0' && y != '6'){
            return false;
        }
        else return number.length() <= 10;
    }


    private void login(final String number,final String password){

        String n = number;//"+212"+number;
        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").orderByChild("phoneNumber").equalTo(n).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        if(Objects.requireNonNull(data.child("password").getValue(String.class)).equals(password)){
                            if(Objects.requireNonNull(data.child("isVerified").getValue(String.class)).equals("0")){
                                Toast.makeText(LoginActivity.this, "This account is currently disabled", Toast.LENGTH_SHORT).show();
                            }else {
//                                loggedIn("+212"+ number, data.getKey());
                                loggedIn( number, data.getKey());
                            }
                        }else{
                            //Wrong Password
                            Toast.makeText(LoginActivity.this, "Mot de passe erroné!!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }else{
                    // Number/User  Not Found
                    Toast.makeText(LoginActivity.this, "Ce compt n'existe pas!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void loggedIn(String number, String id){
        SharedPreferences.Editor editor = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE).edit();
        editor.putString("phoneNumber", number);
        editor.putString("userId", id);
        editor.apply();
        checkLogin();
    }
    public void checkLogin(){
        SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        String number = prefs.getString("userId", null);
        if(number != null){
            //User Is Logged In
            Intent intent = new Intent(LoginActivity.this, MapsNewActivity.class);
            startActivity(intent);
            finish();
        }

    }

}
