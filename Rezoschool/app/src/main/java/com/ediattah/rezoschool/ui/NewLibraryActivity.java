package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ediattah.rezoschool.Model.Comment;
import com.ediattah.rezoschool.Model.Library;
import com.ediattah.rezoschool.Model.Tweet;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class NewLibraryActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    boolean isPublic = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_library);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add New Document");
        final EditText edit_title = findViewById(R.id.edit_title);
        final EditText edit_description = findViewById(R.id.edit_description);
        final EditText edit_url = findViewById(R.id.edit_url);
        final EditText edit_category = findViewById(R.id.edit_category);
        RadioButton radio_public = findViewById(R.id.radio_public);
        RadioButton radio_private = findViewById(R.id.radio_private);
        radio_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPublic = true;
            }
        });
        radio_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPublic = false;
            }
        });
        radio_public.setChecked(true);
        Button btn_create = findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String title = edit_title.getText().toString().trim();
                final String description = edit_description.getText().toString().trim();
                final String url = edit_url.getText().toString().trim();
                final String category = edit_category.getText().toString().trim();
                if (title.length()*description.length()*url.length()*category.length() == 0) {
                    Utils.showAlert(NewLibraryActivity.this, "Warning", "Plese fill in the blank field");
                    return;
                }
                if (!URLUtil.isValidUrl(url)) {
                    Utils.showAlert(NewLibraryActivity.this, "Warning", "Invalid url!");
                    return;
                }
                progressDialog = new ProgressDialog(NewLibraryActivity.this);
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();
                Utils.mDatabase.child(Utils.tbl_library).orderByChild("title").equalTo(title).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        if (dataSnapshot.getValue() != null) {
                            Utils.showAlert(NewLibraryActivity.this, "Warning", "The title already exists!");
                        } else {
                            Library library = new Library("", Utils.mUser.getUid(), Utils.currentSchool._id, title, description, url, category, isPublic, true);
                            Utils.mDatabase.child(Utils.tbl_library).push().setValue(library);
                            Toast.makeText(NewLibraryActivity.this, "Successfully submitted!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();

                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}