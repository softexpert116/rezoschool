package com.ediattah.rezoschool.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.CourseTime;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.ClassListAdapter;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.adapter.SchoolListAdapter;
import com.ediattah.rezoschool.adapter.TeacherCourseListAdapter;
import com.ediattah.rezoschool.ui.CourseDetailActivity;
import com.ediattah.rezoschool.ui.CourseStudentActivity;
import com.ediattah.rezoschool.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class TeacherSchoolFragment extends Fragment {
    MainActivity activity;
    ListView list_school, list_course, list_class;
    SchoolListAdapter schoolListAdapter;
    TeacherCourseListAdapter teacherCourseListAdapter;
    ClassListAdapter classListAdapter;
    ArrayList<School> array_school = new ArrayList<>();
    ArrayList<Course> array_course = new ArrayList<>();
    ArrayList<Class> array_class = new ArrayList<>();

    public int sel_index = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_teacher_school, container, false);

        list_school = v.findViewById(R.id.list_school);
        list_course = v.findViewById(R.id.list_course);
        list_class = v.findViewById(R.id.list_class);

        schoolListAdapter = new SchoolListAdapter(activity, array_school);
        schoolListAdapter.flag_view = true;
        list_school.setAdapter(schoolListAdapter);
        teacherCourseListAdapter = new TeacherCourseListAdapter(activity, array_course);
        list_course.setAdapter(teacherCourseListAdapter);
        classListAdapter = new ClassListAdapter(activity, array_class);
        classListAdapter.sel_index = -2;
        list_class.setAdapter(classListAdapter);
        list_school.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                schoolListAdapter.sel_index = i;
                Utils.currentSchool = array_school.get(i);
//                Toast.makeText(activity, "Item Clicked", Toast.LENGTH_SHORT).show();
                schoolListAdapter.notifyDataSetChanged();
                course_update_listener(array_school.get(i));
//                courseListAdapter.notifyDataSetChanged();
            }
        });
        list_course.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, CourseDetailActivity.class);
                intent.putExtra("COURSE", array_course.get(i));
                intent.putExtra("SCHOOL", array_school.get(schoolListAdapter.sel_index));
                startActivity(intent);
            }
        });
        list_class.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        school_update_listener();
        Button btn_course = v.findViewById(R.id.btn_course);
        Button btn_class = v.findViewById(R.id.btn_class);
        btn_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_course.setTextColor(getResources().getColor(R.color.white));
                btn_class.setTextColor(getResources().getColor(R.color.gray));
                list_course.setVisibility(View.VISIBLE);
                list_class.setVisibility(View.GONE);
            }
        });
        btn_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_course.setTextColor(getResources().getColor(R.color.gray));
                btn_class.setTextColor(getResources().getColor(R.color.white));
                list_course.setVisibility(View.GONE);
                list_class.setVisibility(View.VISIBLE);
            }
        });
        return v;
    }
    void school_update_listener() {
        Utils.mDatabase.child(Utils.tbl_school).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                array_school.clear();
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        School school = datas.getValue(School.class);
                        school._id = datas.getKey();
                        for (Teacher teacher:school.teachers) {
                            if (teacher.uid.equals(Utils.mUser.getUid())) {
                                array_school.add(school);
                            }
                        }
                    }
                    if (array_school.size() > 0) {
                        Utils.currentSchool = array_school.get(0);
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        schoolListAdapter.arrayList = array_school;
                        if (schoolListAdapter.sel_index == array_school.size()) {
                            schoolListAdapter.sel_index = array_school.size()-1;
                        }
                        if (array_school.size() > 0) {
                            course_update_listener(array_school.get(schoolListAdapter.sel_index));
                        }
                        schoolListAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void course_update_listener(final School school) {
        Utils.mDatabase.child(Utils.tbl_school).child(school._id).child("classes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                array_course.clear(); array_class.clear();
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        Class _class = datas.getValue(Class.class);
                        for (Course course:_class.courses) {
                            for (Teacher teacher:school.teachers) {
                                if (teacher.uid.equals(Utils.mUser.getUid())) {
                                    ArrayList<String> arrayStrList = new ArrayList<String>(Arrays.asList(teacher.courses.split(",")));
                                    if (arrayStrList.contains(course.name)) {
                                        array_course.add(course);
                                        if (!array_class.contains(_class)) {
                                            array_class.add(_class);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    App.school_courses = array_course;
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        teacherCourseListAdapter.arrayList = array_course;
                        teacherCourseListAdapter.notifyDataSetChanged();
                        classListAdapter.arrayList = array_class;
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
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}