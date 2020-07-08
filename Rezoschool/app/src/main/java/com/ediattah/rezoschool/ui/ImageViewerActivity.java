package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.R;
import com.github.chrisbanes.photoview.PhotoView;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Image Viewer");

        String url = getIntent().getStringExtra("url");
        PhotoView photoView = (PhotoView)findViewById(R.id.iv_photo);
        Glide.with(this).load(url).apply(new RequestOptions()
                .placeholder(R.drawable.default_pic).centerInside().dontAnimate()).into(photoView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}