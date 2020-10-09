package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.TeacherListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class ParentViewTeacherActivity extends AppCompatActivity {
    ArrayList<Teacher> arrayList = new ArrayList<>();
    TeacherListAdapter teacherListAdapter;
    Student sel_student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_teacher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sel_student = (Student)getIntent().getSerializableExtra("OBJECT");
        setTitle(getResources().getString(R.string.teacher));

        ListView listView = findViewById(R.id.listView);
        teacherListAdapter = new TeacherListAdapter(this, arrayList);
        listView.setAdapter(teacherListAdapter);

    }
    void read_student_class() {
        Utils.mDatabase.child(Utils.tbl_school).child(sel_student.school_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.getValue() != null) {
                    School school = dataSnapshot.getValue(School.class);
                    for (Class _class:school.classes) {
                        if (_class.name.equals(sel_student.class_name)) {
                            for (Course course:_class.courses) {
                                for (Teacher teacher:school.teachers) {
                                    if (teacher.courses.contains(course.name)) {
                                        if (!arrayList.contains(teacher)) {
                                            arrayList.add(teacher);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                teacherListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        read_student_class();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}