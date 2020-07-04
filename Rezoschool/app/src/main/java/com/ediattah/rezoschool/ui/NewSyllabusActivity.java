package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.ediattah.rezoschool.App.array_course;

public class NewSyllabusActivity extends AppCompatActivity {
    EditText edit_course, edit_date, edit_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_syllabus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Create New Syllabus");

        edit_course = (EditText)findViewById(R.id.edit_course);
        edit_date = (EditText)findViewById(R.id.edit_date);
        edit_time = (EditText)findViewById(R.id.edit_time);
        edit_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddDialog();
            }
        });
        edit_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar myCalender = Calendar.getInstance();
                int year = myCalender.get(Calendar.YEAR);
                int month = myCalender.get(Calendar.MONTH);
                int date = myCalender.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        if (datePicker.isShown()) {
                            myCalender.set(Calendar.YEAR, i);
                            myCalender.set(Calendar.MONTH, i1);
                            myCalender.set(Calendar.DAY_OF_MONTH, i2);
                            edit_date.setText(new SimpleDateFormat(App.DATE_FORMAT).format(myCalender.getTime()));
                        }
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewSyllabusActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myDateListener, year, month, date);
                datePickerDialog.setTitle("Choose date:");
                datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                datePickerDialog.show();
            }
        });
        edit_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar myCalender = Calendar.getInstance();
                int hour = myCalender.get(Calendar.HOUR_OF_DAY);
                int minute = myCalender.get(Calendar.MINUTE);
                TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            myCalender.set(Calendar.MINUTE, minute);
                            edit_time.setText(new SimpleDateFormat(App.TIME_FORMAT).format(myCalender.getTime()));
                        }
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewSyllabusActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
                timePickerDialog.setTitle("Choose hour:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });
    }
    public void openAddDialog() {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_choose_item, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        ListView listView = dlg.findViewById(R.id.listView);
        SchoolCourseListAdapter schoolCourseListAdapter = new SchoolCourseListAdapter(this, App.array_course, null);
        listView.setAdapter(schoolCourseListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                edit_course.setText(array_course.get(i).name);
                dlg.dismiss();
            }
        });
        dlg.show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}