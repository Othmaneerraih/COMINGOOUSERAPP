package com.comingoo.driver.fousa.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
                    if(phoneNumberValidation(phoneNumber.getText().toString())){
                        login(phoneNumber.getText().toString(), password.getText().toString());
                    }else{
                        Toast.makeText(LoginActivity.this, "Veuillez entrer un numéro de téléphone valide 612345678", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }

//    private Boolean phoneNumberValidation(String number){
//        char x = number.charAt(0);
//        char y = number.charAt(1);
//        if(x != '0' && y != '6'){
//            return false;
//        }
//        else return number.length() <= 10;
//    }

    private Boolean phoneNumberValidation(String number){
        char x = number.charAt(0);
        if(x == '0'){
            return false;
        }

        if(number.length()!= 9){
            return false;
        }

        return true;
    }


    private void login(final String number,final String password){

        String n = "+212"+number;
        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").orderByChild("phoneNumber").equalTo(n).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        if(Objects.requireNonNull(data.child("password").getValue(String.class)).equals(password)){
                            if(Objects.requireNonNull(data.child("isVerified").getValue(String.class)).equals("0")){
                                Toast.makeText(LoginActivity.this, "This account is currently disabled", Toast.LENGTH_SHORT).show();
                            }else {
                                loggedIn("+212"+ number, data.getKey());
//                                loggedIn( number, data.getKey());
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
            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
            startActivity(intent);
            finish();
        }

    }

}
