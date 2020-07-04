package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.ui.CourseCalendarActivity;
import com.ediattah.rezoschool.ui.MainActivity;

import static com.ediattah.rezoschool.App.array_course;

public class StudentCourseFragment extends Fragment {
    MainActivity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_student_course, container, false);
        ListView listView = (ListView)v.findViewById(R.id.listView);
        SchoolCourseListAdapter schoolCourseListAdapter = new SchoolCourseListAdapter(activity, array_course, null);
        listView.setAdapter(schoolCourseListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, CourseCalendarActivity.class);
                startActivity(intent);
            }
        });
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