package com.comingoo.driver.fousa.async;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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

public class UpdateInfoAideTask extends AsyncTask<String, Integer, String> {

    private String userId;
    private Uri imageUri;
    private EditText message;
    private Context context;
    private TextView selectImage;

    public UpdateInfoAideTask(String userId, Uri imageUri, EditText message, Context context, TextView selectImage) {
        this.userId = userId;
        this.imageUri = imageUri;
        this.message = message;
        this.context = context;
        this.selectImage = selectImage;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // This is run in a background thread
    @Override
    protected String doInBackground(String... params) {

        final Map<String, String> data = new HashMap<>();
        data.put("user", userId);
        data.put("message", message.getText().toString());
        if (imageUri != null) {

            final StorageReference filepath = FirebaseStorage.getInstance().getReference("DRIVERCONTACTUS").child(userId);
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
                }
            });

            data.put("image", imageUri.toString());

        } else {
            data.put("image", "");
            FirebaseDatabase.getInstance().getReference("CONTACTUSDRIVER").push().setValue(data);
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

        Toast.makeText(context, "message envoyé.", Toast.LENGTH_SHORT).show();
        message.setText("");
        selectImage.setText("Ajouter une image");
    }
}