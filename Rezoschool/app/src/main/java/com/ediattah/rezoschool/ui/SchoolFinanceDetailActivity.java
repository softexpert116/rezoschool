package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.FinanceDetailListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolFinanceDetailActivity extends AppCompatActivity {
    FinanceDetailListAdapter financeDetailListAdapter;
    ArrayList<Student> array_student_accepted = new ArrayList<>();
    ListView listView;
    LinearLayout ly_no_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_finance_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.finance_details));
        listView = findViewById(R.id.listView);
        financeDetailListAdapter = new FinanceDetailListAdapter(this, array_student_accepted);
        listView.setAdapter(financeDetailListAdapter);
        ly_no_items = findViewById(R.id.ly_no_items);
        read_students();
    }
    public void read_students() {
        array_student_accepted.clear();
        for (Student student:Utils.currentSchool.students) {
            if (student.isAllow) {
                array_student_accepted.add(student);
            }
        }
        financeDetailListAdapter.notifyDataSetChanged();
        if (array_student_accepted.size() == 0) {
            ly_no_items.setVisibility(View.VISIBLE);
        } else {
            ly_no_items.setVisibility(View.GONE);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}