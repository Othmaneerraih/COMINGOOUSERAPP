package com.comingoo.driver.fousa.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.comingoo.driver.fousa.async.UpdateInfoAideTask;
import com.comingoo.driver.fousa.utility.CustomAnimation;
import com.comingoo.driver.fousa.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class aideActivity extends AppCompatActivity {

    private ConstraintLayout Q1, Q2, A1, A2;
    private boolean a1 = false, a2 = false;
    public ConstraintLayout fc, content;
    private ImageView ivArrawOne, ivArrawTwo;

    private ConstraintLayout image;
    private Uri imageUri = null;

    private EditText message;
    private TextView selectImage;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aide);

        final SharedPreferences prefs = getSharedPreferences("COMINGOODRIVERDATA", MODE_PRIVATE);
        userId = prefs.getString("userId", null);

        message = findViewById(R.id.message);
        selectImage = findViewById(R.id.image_text);

        Q1 = findViewById(R.id.Q1);
        A1 = findViewById(R.id.A1);
        Q2 = findViewById(R.id.Q2);
        A2 = findViewById(R.id.A2);

        ivArrawOne = findViewById(R.id.iv_aide_expn_one);
        ivArrawTwo = findViewById(R.id.iv_aide_expn_two);
        fc = findViewById(R.id.fc);
        content = findViewById(R.id.content);

        image = findViewById(R.id.add_image);

        CustomAnimation.animate(aideActivity.this, content, 250, 1, 0);

        findViewById(R.id.add_voice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 10);
                } else {
                    Toast.makeText(aideActivity.this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
                }

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(aideActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(aideActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 2);
                }
            }
        });

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateInfoAideTask(userId,imageUri,message,aideActivity.this,selectImage).execute();
                CustomAnimation.animate(aideActivity.this, content, 1, 250, 500);
            }
        });


        Q1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!a1) {
                    CustomAnimation.animate(aideActivity.this, A1, 100, 2, 500);
                    a1 = true;
                    ivArrawOne.setImageResource(R.drawable.ic_arraw_up);
                } else {
                    CustomAnimation.animate(aideActivity.this, A1, 2, 100, 500);
                    a1 = false;
                    ivArrawOne.setImageResource(R.drawable.expand);
                }
            }
        });

        Q2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!a2) {
                    CustomAnimation.animate(aideActivity.this, A2, 70, 2, 500);
                    a2 = true;
                    ivArrawTwo.setImageResource(R.drawable.ic_arraw_up);
                } else {
                    CustomAnimation.animate(aideActivity.this, A2, 2, 70, 500);
                    a2 = false;
                    ivArrawTwo.setImageResource(R.drawable.expand);
                }

            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);

        if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 2);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK) {

            imageUri = data.getData();
            //image.setBackgroundResource();
            selectImage.setText("image choisi");
        }

        if (requestCode == 10 && resultCode == RESULT_OK) {
            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            message.setText(result.get(0));
        }
    }

}
