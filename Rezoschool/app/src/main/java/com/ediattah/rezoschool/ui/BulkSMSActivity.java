package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.StudentBulkSMSListAdapter;

import java.util.ArrayList;

public class BulkSMSActivity extends AppCompatActivity {
    ArrayList<Student> array_student_accepted = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_s_m_s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Bulk SMS");
        StudentBulkSMSListAdapter studentBulkSMSListAdapter = new StudentBulkSMSListAdapter(this, array_student_accepted);
        ListView listView = (ListView)findViewById(R.id.listView);
//        array_student_accepted.add(new Student(1, 1, 1, 1, true, true));
//        array_student_accepted.add(new Student(2, 2, 1, 1, true, true));
//        array_student_accepted.add(new Student(3, 3, 1, 1, true, true));
//        array_student_accepted.add(new Student(4, 4, 1, 1, true, true));
//        array_student_accepted.add(new Student(5, 5, 1, 1, true, true));
        listView.setAdapter(studentBulkSMSListAdapter);

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}