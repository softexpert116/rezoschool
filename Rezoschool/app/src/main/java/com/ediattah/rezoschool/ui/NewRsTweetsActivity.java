package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Comment;
import com.ediattah.rezoschool.Model.Tweet;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.ediattah.rezoschool.R;

import java.util.ArrayList;

public class NewRsTweetsActivity extends AppCompatActivity {
    private Uri imgUri;
    String text;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rs_tweets);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("New RS Tweets");
        App.hideKeyboard(this);

        ImageView img_click = (ImageView)findViewById(R.id.img_click);
        img_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(NewRsTweetsActivity.this);
            }
        });
        final EditText edit_text = (EditText)findViewById(R.id.edit_text);
        Button btn_create = (Button)findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = edit_text.getText().toString().trim();
                if (imgUri == null || text.length() == 0) {
                    Utils.showAlert(NewRsTweetsActivity.this, "Warning", "Please fill in blank field!");
                    return;
                }
                progressDialog = new ProgressDialog(NewRsTweetsActivity.this);
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();

                // file upload
                uploadPictureToFirebase();
            }
        });
    }
    void uploadPictureToFirebase() {
// Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        final StorageReference file_refer = Utils.mStorage.child("tweets/"+ts);

        // Listen for state changes, errors, and completion of the upload.
        file_refer.putFile(imgUri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                file_refer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.dismiss();
                        String downloadUrl = uri.toString();
                        Tweet tweet = new Tweet("", Utils.mUser.getUid(), downloadUrl, text, Utils.getCurrentDateString(), 0, 0, new ArrayList<Comment>());
                        Utils.mDatabase.child(Utils.tbl_tweet).push().setValue(tweet);
                        Toast.makeText(NewRsTweetsActivity.this, "Successfully created!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }

        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgUri = result.getUri();
                ((ImageView) findViewById(R.id.img_pic)).setImageURI(result.getUri());
//                Glide.with(EventCreateActivity.this).load(result.getUri()).centerCrop().placeholder(R.drawable.profile).dontAnimate().into(img_event);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}