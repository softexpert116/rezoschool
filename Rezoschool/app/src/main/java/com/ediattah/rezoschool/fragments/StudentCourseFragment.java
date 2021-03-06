package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.CourseTime;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.ui.CourseCalendarActivity;
import com.ediattah.rezoschool.ui.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;


public class StudentCourseFragment extends Fragment {
    MainActivity activity;
    ArrayList<Course> arrayList = new ArrayList<>();
    LinearLayout ly_no_items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_student_course, container, false);
        ListView listView = (ListView)v.findViewById(R.id.listView);
        ly_no_items = v.findViewById(R.id.ly_no_items);
        for (Course course:Utils.currentClass.courses) {
            arrayList.add(course);
        }
        if (arrayList.size() == 0) {
            ly_no_items.setVisibility(View.VISIBLE);
        } else {
            ly_no_items.setVisibility(View.GONE);
        }
        SchoolCourseListAdapter schoolCourseListAdapter = new SchoolCourseListAdapter(activity, arrayList, new ArrayList<Course>());
        listView.setAdapter(schoolCourseListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, CourseCalendarActivity.class);
                intent.putExtra("OBJECT", arrayList.get(i));
                startActivity(intent);
            }
        });
        if (Utils.currentClass.name.length() == 0) {
            Utils.showAlert(activity, getResources().getString(R.string.warning), getResources().getString(R.string.please_apply_to_a_school));
        }
        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}