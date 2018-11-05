    package com.comingoo.driver.fousa;

    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.support.annotation.NonNull;
    import android.support.v7.app.AppCompatActivity;
    import android.view.View;
    import android.widget.Button;
    import android.widget.Toast;

    import com.crashlytics.android.Crashlytics;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.FirebaseException;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.PhoneAuthCredential;
    import com.google.firebase.auth.PhoneAuthProvider;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import io.fabric.sdk.android.Fabric;

    import java.util.HashMap;
    import java.util.Map;
    import java.util.concurrent.TimeUnit;

    public class MainActivity extends AppCompatActivity implements SubscribeDialog.SubscribeDialogListener, PhoneDialog.PhoneDialogListener {
        private Button signup;
        private Button login;
        private String codeId;
        private PhoneDialog phoneDialog;

        private String name;
        private String password;
        private String tele;
        private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;

        private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneDialog = null;
        mDatabase = FirebaseDatabase.getInstance().getReference("DRIVERUSERS");

        signup = (Button) findViewById(R.id.signup);
        login = (Button) findViewById(R.id.login);
        Fabric.with(this, new Crashlytics());

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    startActivity(new Intent(MainActivity.this, signupActivity.class));
//                OpenDialog();
                String url = "https://www.comingoo.com/driver/signup";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                throw new RuntimeException("This is a crash");
                startActivity(new Intent(MainActivity.this, loginActivity.class));
                finish();
            }
        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                if(phoneDialog != null){
                    phoneDialog.dismiss();
                }
                Map<String, String> data = new HashMap();
                data.put("fullName", name);
                data.put("password", password);
                data.put("phoneNumber", tele);
                data.put("image", "");
                data.put("debt", "0");
                data.put("isVerified", "1");
                mDatabase = mDatabase.push();
                mDatabase.setValue(data);



                Map<String, String> dataRating = new HashMap();
                dataRating.put("1", "0");
                dataRating.put("2", "0");
                dataRating.put("3", "0");
                dataRating.put("4", "0");
                dataRating.put("5", "0");

                mDatabase.child("rating").setValue(dataRating);


                finishedSubscription();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                OpenDialogVer();
                codeId = s;
            }
        };


    }

        public void OpenDialog(){
            SubscribeDialog subscribeDialog = new SubscribeDialog();
            subscribeDialog.show(getSupportFragmentManager(), "Subscribe");
        }
        public void OpenDialogVer(){
            phoneDialog = new PhoneDialog();
            phoneDialog.show(getSupportFragmentManager(), "phone");
        }

        @Override
        public void applyTextCode(String code) {
            final PhoneAuthCredential credentials = PhoneAuthProvider.getCredential(codeId, code);


            FirebaseDatabase.getInstance().getReference("DRIVERUSERS").orderByChild("phoneNumber").equalTo(tele).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){

                        FirebaseAuth.getInstance().signInWithCredential(credentials).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if((FirebaseAuth.getInstance().getCurrentUser()) != null){

                                    Map<String, String> data = new HashMap();
                                    data.put("fullName", name);
                                    data.put("password", password);
                                    data.put("phoneNumber", tele);
                                    data.put("image", "");
                                    data.put("debt", "0");
                                    data.put("isVerified", "1");
                                    mDatabase = mDatabase.push();
                                    mDatabase.setValue(data);

                                    Map<String, String> dataRating = new HashMap();
                                    dataRating.put("1", "0");
                                    dataRating.put("2", "0");
                                    dataRating.put("3", "0");
                                    dataRating.put("4", "0");
                                    dataRating.put("5", "0");

                                    mDatabase.child("rating").setValue(dataRating);

                                    FirebaseAuth.getInstance().getCurrentUser().delete();
                                    finishedSubscription();
                                }else {
                                    Toast.makeText(MainActivity.this, "Code Erroné!", Toast.LENGTH_SHORT).show();
                                }
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
        public void applyText(String n, String p, String t) {
            name = n;
            password = p;
            tele = t;//"+212"+t;
            FirebaseDatabase.getInstance().getReference("DRIVERUSERS").orderByChild("phoneNumber").equalTo(tele).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Toast.makeText(MainActivity.this, "Ce numéro est deja liée avec un compt.", Toast.LENGTH_SHORT).show();
                    }else{
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                tele,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                MainActivity.this,               // Activity (for callback binding)
                                mCallBacks);        // OnVerificationStateChangedCallbacks
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void finishedSubscription(){
            Toast.makeText(this, "Bienvenu chez COMINGOO.", Toast.LENGTH_SHORT).show();
        }
    }

