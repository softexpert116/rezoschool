package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.StudentAcceptedListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CourseStudentActivity extends AppCompatActivity {
    StudentAcceptedListAdapter studentListAdapter;
    Course sel_course;
    School sel_school;
    ArrayList<Class> array_class = new ArrayList<>();
    ArrayList<Student> array_student = new ArrayList<>();
    LinearLayout ly_no_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_student);
        sel_course = (Course)getIntent().getSerializableExtra("COURSE");
        sel_school = (School) getIntent().getSerializableExtra("SCHOOL");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.students_per) + sel_course.name + " " + getResources().getString(R.string.course));

        ListView listView = findViewById(R.id.listView);
        ly_no_items = findViewById(R.id.ly_no_items);
        studentListAdapter = new StudentAcceptedListAdapter(this, array_student, false, null);
        listView.setAdapter(studentListAdapter);
        read_course_classes();
    }
    void read_course_classes() {
        array_class.clear();
        for(Class _class: Utils.currentSchool.classes){
            if (_class.courses.contains(sel_course.name)) {
                array_class.add(_class);
            }
        }
        read_course_students();
    }
    void read_course_students() {
        for (Student student:Utils.currentSchool.students) {
            String class_name = student.class_name;
            for (Class _class:array_class) {
                if (class_name.equals(_class.name)) {
                    array_student.add(student);
                    break;
                }
            }
        }
        if (array_student.size() == 0) {
            ly_no_items.setVisibility(View.VISIBLE);
        } else {
            ly_no_items.setVisibility(View.GONE);
        }
        studentListAdapter.notifyDataSetChanged();

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}