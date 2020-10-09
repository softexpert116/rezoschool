package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TimeslotActivity extends AppCompatActivity {

    ListView listView;
    SchoolCourseListAdapter schoolCourseListAdapter;
    public static Class sel_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeslot);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sel_class = (Class)getIntent().getSerializableExtra("sel_class");
        setTitle(getResources().getString(R.string.timeslots_for_) + sel_class.name + " " + getResources().getString(R.string._class));


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimeslotActivity.this, NewCourseActivity.class);
                intent.putExtra("sel_class", sel_class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        schoolCourseListAdapter = new SchoolCourseListAdapter(this, sel_class.courses, null);
        listView = findViewById(R.id.listView);
        listView.setAdapter(schoolCourseListAdapter);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}