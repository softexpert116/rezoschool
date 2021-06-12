package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.CourseTime;
import com.ediattah.rezoschool.Model.Tweet;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.adapter.TimeslotListAdapter;
import com.ediattah.rezoschool.adapter.TweetListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewCourseActivity;
import com.ediattah.rezoschool.ui.NewRsTweetsActivity;
import com.ediattah.rezoschool.ui.TimeslotActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TimeslotFragment extends Fragment {
    MainActivity activity;
    ListView listView;
    TimeslotListAdapter timeslotListAdapter;
    SchoolCourseListAdapter schoolCourseListAdapter;
    Button btn_monday, btn_tuesday, btn_wednesday, btn_thursday, btn_friday, btn_saturday, btn_sunday;
    ArrayList<Course> arrayList = new ArrayList<>();
    LinearLayout ly_no_items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timeslot, container, false);
        listView = view.findViewById(R.id.listView);
        btn_monday = view.findViewById(R.id.btn_monday);
        btn_tuesday = view.findViewById(R.id.btn_tuesday);
        btn_wednesday = view.findViewById(R.id.btn_wednesday);
        btn_thursday = view.findViewById(R.id.btn_thursday);
        btn_friday = view.findViewById(R.id.btn_friday);
        btn_saturday = view.findViewById(R.id.btn_saturday);
        btn_sunday = view.findViewById(R.id.btn_sunday);
        ly_no_items = view.findViewById(R.id.ly_no_items);

        btn_monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sel_week_button(0);
            }
        });
        btn_tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sel_week_button(1);
            }
        });
        btn_wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sel_week_button(2);
            }
        });
        btn_thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sel_week_button(3);
            }
        });
        btn_friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sel_week_button(4);
            }
        });
        btn_saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sel_week_button(5);
            }
        });
        btn_sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sel_week_button(6);
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, NewCourseActivity.class);
                activity.startActivity(intent);
            }
        });
        sel_week_button(0);
        return view;
    }

    void sel_week_button(int i) {
        btn_monday.setBackground(getResources().getDrawable(R.color.transparent));
        btn_tuesday.setBackground(getResources().getDrawable(R.color.transparent));
        btn_wednesday.setBackground(getResources().getDrawable(R.color.transparent));
        btn_thursday.setBackground(getResources().getDrawable(R.color.transparent));
        btn_friday.setBackground(getResources().getDrawable(R.color.transparent));
        btn_saturday.setBackground(getResources().getDrawable(R.color.transparent));
        btn_sunday.setBackground(getResources().getDrawable(R.color.transparent));
        switch (i) {
            case 0:
                btn_monday.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
                break;
            case 1:
                btn_tuesday.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
                break;
            case 2:
                btn_wednesday.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
                break;
            case 3:
                btn_thursday.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
                break;
            case 4:
                btn_friday.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
                break;
            case 5:
                btn_saturday.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
                break;
            case 6:
                btn_sunday.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
                break;
        }
//        timeslotListAdapter = new TimeslotListAdapter(activity, App.school_courses, i);
//        listView.setAdapter(timeslotListAdapter);
        arrayList.clear();
        for (Course course:App.school_courses) {
            for (CourseTime courseTime : course.times) {
                if (courseTime.dayOfWeek != i) continue;
                arrayList.add(course);
            }
        }

        timeslotListAdapter = new TimeslotListAdapter(activity, arrayList, i);
        listView.setAdapter(timeslotListAdapter);
        if (arrayList.size() == 0) {
            ly_no_items.setVisibility(View.VISIBLE);
        } else {
            ly_no_items.setVisibility(View.GONE);
        }
    }

    void course_update_listener() {
        Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Utils.currentSchool.courses.clear();
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        Course course = datas.getValue(Course.class);
                        Utils.currentSchool.courses.add(course);
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        schoolCourseListAdapter.arrayList = Utils.currentSchool.courses;
                        schoolCourseListAdapter.notifyDataSetChanged();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        timeslotListAdapter.notifyDataSetChanged();
//        course_update_listener();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}