package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Utils.mUser != null) {
                    // go to main page
                    App.goToMainPage(SplashActivity.this);
                } else {
                    Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    SplashActivity.this.startActivity(loginIntent);
                    finish();
                }
            }
        }, 3000);
    }
}