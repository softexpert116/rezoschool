package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.StudentAcceptedListAdapter;

import java.util.ArrayList;

public class CourseStudentActivity extends AppCompatActivity {
    StudentAcceptedListAdapter studentAcceptedListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_student);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Students per course");

        ListView listView = findViewById(R.id.listView);
        ArrayList<Student> array_student_accepted = new ArrayList<>();
//        array_student_accepted.add(new Student(1, 1, 1, 1, true, true));
//        array_student_accepted.add(new Student(2, 2, 1, 1, true, true));
//        array_student_accepted.add(new Student(3, 3, 1, 1, true, true));
//        array_student_accepted.add(new Student(4, 4, 1, 1, true, true));
//        array_student_accepted.add(new Student(5, 5, 1, 1, true, true));
        studentAcceptedListAdapter = new StudentAcceptedListAdapter(this, null, array_student_accepted);
        listView.setAdapter(studentAcceptedListAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}