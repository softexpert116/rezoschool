package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.ClassListAdapter;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewClassActivity;
import com.ediattah.rezoschool.ui.NewCourseActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SchoolClassFragment extends Fragment {

    MainActivity activity;
    Button btn_class, btn_course;
    boolean flag_class = true;
    ListView listView;
//    ArrayList<Class> array_class = new ArrayList<>();
//    ArrayList<Course> array_course = new ArrayList<>();
    ClassListAdapter classListAdapter;
    SchoolCourseListAdapter schoolCourseListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_class_school, container, false);
        btn_class = v.findViewById(R.id.btn_class);
        btn_course = v.findViewById(R.id.btn_course);
        schoolCourseListAdapter = new SchoolCourseListAdapter(activity, Utils.currentSchool.courses, null);
        classListAdapter = new ClassListAdapter(activity, Utils.currentSchool.classes);
        btn_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_course.setTextColor(Color.parseColor("#d0d0d0"));
                btn_class.setTextColor(Color.parseColor("#ffffff"));
                flag_class = true;
                listView.setAdapter(classListAdapter);
            }
        });
        btn_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_class.setTextColor(Color.parseColor("#d0d0d0"));
                btn_course.setTextColor(Color.parseColor("#ffffff"));
                flag_class = false;
                listView.setAdapter(schoolCourseListAdapter);
            }
        });
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag_class) {
                    Intent intent = new Intent(activity, NewClassActivity.class);
                    activity.startActivity(intent);
                } else {
                    Intent intent = new Intent(activity, NewCourseActivity.class);
                    activity.startActivity(intent);
                }
            }
        });

        listView = v.findViewById(R.id.listView);
        listView.setAdapter(classListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (flag_class) {
//                    Intent intent = new Intent(activity, ClassCourseActivity.class);
//                    intent.putExtra("OBJECT", array_class.get(i));
//                    startActivity(intent);
                } else {

                }
            }
        });

        return v;
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
//    void readCourses() {
//        Utils.mDatabase.child(Utils.tbl_course).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                array_course.clear();
//                if (dataSnapshot.getValue() != null) {
//                    for(DataSnapshot datas: dataSnapshot.getChildren()){
//                        Course course = datas.getValue(Course.class);
//                        course._id = datas.getKey();
//                        array_course.add(course);
//                    }
//                }
//                if (courseListAdapter != null) {
//                    courseListAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
    void class_update_listener() {
        Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("classes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Utils.currentSchool.classes.clear();
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        Class _class = datas.getValue(Class.class);
                        Utils.currentSchool.classes.add(_class);
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        classListAdapter.arrayList = Utils.currentSchool.classes;
                        classListAdapter.notifyDataSetChanged();
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
        course_update_listener();
        class_update_listener();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}