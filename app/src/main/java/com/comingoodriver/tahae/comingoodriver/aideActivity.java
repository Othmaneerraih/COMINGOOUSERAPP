package com.comingoodriver.tahae.comingoodriver;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import comingoo.vone.tahae.comingoodriver.R;

public class aideActivity extends AppCompatActivity {
    private ConstraintLayout Q1, Q2, A1, A2;
    private boolean a1 = false, a2 = false;
    public ConstraintLayout fc, content;

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

        message = (EditText) findViewById(R.id.message);
        selectImage = (TextView) findViewById(R.id.image_text);

        Q1 = (ConstraintLayout) findViewById(R.id.Q1);
        A1 = (ConstraintLayout) findViewById(R.id.A1);
        Q2 = (ConstraintLayout) findViewById(R.id.Q2);
        A2 = (ConstraintLayout) findViewById(R.id.A2);

        fc = (ConstraintLayout) findViewById(R.id.fc);
        content = (ConstraintLayout) findViewById(R.id.content);

        image = (ConstraintLayout) findViewById(R.id.add_image);

        fc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAnimation.animate(aideActivity.this, content, 250, 1, 500);
            }
        });

        findViewById(R.id.add_voice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(aideActivity.this, "This feature is not yet available!", Toast.LENGTH_SHORT).show();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(aideActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(aideActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


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
               new UpdateInfoTask().execute();
                CustomAnimation.animate(aideActivity.this, content, 1, 250, 500);
            }
        });


        Q1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!a1) {
                    CustomAnimation.animate(aideActivity.this, A1, 100, 2, 500);
                    a1 = true;
                }else{
                    CustomAnimation.animate(aideActivity.this, A1, 2, 100, 500);
                    a1 = false;
                }
            }
        });
        Q2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!a2) {
                    CustomAnimation.animate(aideActivity.this, A2, 70, 2, 500);
                    a2 = true;
                }else{
                    CustomAnimation.animate(aideActivity.this, A2, 2, 70, 500);
                    a2 = false;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult){
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);

        if(grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED){

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 2);

        }

    }

    private boolean finished;
    private class UpdateInfoTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            finished = false;
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {

            final Map<String, String> data = new HashMap<>();
            data.put("user", userId);
            data.put("message", message.getText().toString());
            if(imageUri != null) {

                final StorageReference filepath= FirebaseStorage.getInstance().getReference("DRIVERCONTACTUS").child(userId);
                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                data.put("image", uri.toString());
                                FirebaseDatabase.getInstance().getReference("CONTACTUSDRIVER").push().setValue(data);
                            }
                        });
                        finished = true;
                    }
                });

                data.put("image", imageUri.toString());

            }else {
                data.put("image", "");
                FirebaseDatabase.getInstance().getReference("CONTACTUSDRIVER").push().setValue(data);
                finished = true;
            }

            while(!finished){

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

            Toast.makeText(aideActivity.this, "message envoyé.", Toast.LENGTH_SHORT).show();
            message.setText("");
            selectImage.setText("Ajouter une image");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == RESULT_OK){

            imageUri = data.getData();
            //image.setBackgroundResource();
            selectImage.setText("image choisi");


        }
    }

}
