package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ediattah.rezoschool.R;

public class NewSchoolCallbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_school_callback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add New Callback");
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}