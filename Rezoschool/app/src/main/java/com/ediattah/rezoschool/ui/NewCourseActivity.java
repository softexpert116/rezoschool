package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class NewCourseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Create New Course");
        App.hideKeyboard(this);
        final EditText edit_course = (EditText)findViewById(R.id.edit_course);
        Button btn_create = (Button)findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String course_name = edit_course.getText().toString().trim();
                if (course_name.length() == 0) {
                    Utils.showAlert(NewCourseActivity.this, "Warning", "Please fill in the blank field");
                } else {
                    boolean flag = false;
                    for (Course course: Utils.currentSchool.courses) {
                        if (course.name.equals(course_name)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        Utils.showAlert(NewCourseActivity.this, "Warning", "The course name already exists.");
                        return;
                    }
                    Course course = new Course(course_name);
                    Utils.currentSchool.courses.add(course);
                    Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("courses").setValue(Utils.currentSchool.courses);
                    Toast.makeText(NewCourseActivity.this, "Successfully created!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}