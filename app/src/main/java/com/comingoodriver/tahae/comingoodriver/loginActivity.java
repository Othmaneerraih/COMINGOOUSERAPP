package com.comingoodriver.tahae.comingoodriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import comingoo.vone.tahae.comingoodriver.R;

public class loginActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private EditText password;
    private ImageButton loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkLogin();

        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        password = (EditText) findViewById(R.id.password);
        loginBtn = (ImageButton) findViewById(R.id.loginBtn);



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setClickable(false);
                login(phoneNumber.getText().toString(), password.getText().toString());
            }
        });


    }


    private void login(final String number,final String password){

        String n = "+212"+number;
        FirebaseDatabase.getInstance().getReference("DRIVERUSERS").orderByChild("phoneNumber").equalTo(n).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        if(data.child("password").getValue(String.class).equals(password)){
                            if(data.child("isVerified").getValue(String.class).equals("0")){
                                //Account is Banned
                            }else {
                                loggedIn("+212"+ number, data.getKey());
                            }
                        }else{
                            //Wrong Password
                            Toast.makeText(loginActivity.this, "Mot de passe erroné!!", Toast.LENGTH_SHORT).show();
                            loginBtn.setClickable(true);
                        }

                    }
                }else{
                    // Number/User  Not Found
                    Toast.makeText(loginActivity.this, "Ce compt n'existe pas!", Toast.LENGTH_SHORT).show();
                    loginBtn.setClickable(true);
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
            Intent intent = new Intent(loginActivity.this, MapsActivity.class);
            startActivity(intent);
            finish();
        }

    }

}
