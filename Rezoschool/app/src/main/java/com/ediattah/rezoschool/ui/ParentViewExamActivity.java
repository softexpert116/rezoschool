package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Exam;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.ExamListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class ParentViewExamActivity extends AppCompatActivity {
    Student sel_student;
    ArrayList<Exam> arrayList = new ArrayList<>();
    ListView listView;
    ExamListAdapter examListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_view_exam);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sel_student = (Student)getIntent().getSerializableExtra("OBJECT");
        setTitle("Exam results");
        listView = findViewById(R.id.listView);
        examListAdapter = new ExamListAdapter(this, arrayList);
        listView.setAdapter(examListAdapter);
    }
    void read_student_exam() {
        Utils.mDatabase.child(Utils.tbl_exam).orderByChild("uid").equalTo(sel_student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        Exam exam = datas.getValue(Exam.class);
                        if (exam.school_id.equals(sel_student.school_id)) {
                            arrayList.add(exam);
                        }
                    }
                }
                examListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        read_student_exam();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}