package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.StudentBulkSMSListAdapter;

import java.util.ArrayList;

public class BulkSMSActivity extends AppCompatActivity {
    ArrayList<String> array_sel_name = new ArrayList<>();
    ArrayList<String> array_sel_phone = new ArrayList<>();
    School sel_school;
    TextView txt_receivers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_s_m_s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sel_school = (School)getIntent().getSerializableExtra("SCHOOL");
        setTitle("Bulk SMS");

        txt_receivers = findViewById(R.id.txt_receivers);
        TextView txt_select = findViewById(R.id.txt_select);
        if (sel_school != null) {
            txt_select.setText("Select users in school " + sel_school.number);
            StudentBulkSMSListAdapter studentBulkSMSListAdapter = new StudentBulkSMSListAdapter(this, txt_receivers, sel_school.students, array_sel_name, array_sel_phone);
            ListView listView = (ListView)findViewById(R.id.listView);
            listView.setAdapter(studentBulkSMSListAdapter);
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}