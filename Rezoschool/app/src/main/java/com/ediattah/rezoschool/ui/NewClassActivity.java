package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.Level;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.adapter.SchoolLevelListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NewClassActivity extends AppCompatActivity {
    EditText edit_start_time, edit_end_time, edit_course, edit_level, edit_name;
    SchoolCourseListAdapter schoolCourseListAdapter;
    SchoolLevelListAdapter schoolLevelListAdapter;
    ArrayList<Course> array_course_sel = new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Create New Class");
        App.hideKeyboard(this);

        edit_level = findViewById(R.id.edit_level);
        edit_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddLevelDialog();
            }
        });
        edit_name = findViewById(R.id.edit_name);
        edit_start_time = (EditText)findViewById(R.id.edit_start_time);
        edit_start_time.setOnClickListener(new View.OnClickListener() {
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
                            edit_start_time.setText(new SimpleDateFormat(App.TIME_FORMAT).format(myCalender.getTime()));
                        }
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewClassActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
                timePickerDialog.setTitle("Choose hour:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });
        edit_end_time = (EditText)findViewById(R.id.edit_end_time);
        edit_end_time.setOnClickListener(new View.OnClickListener() {
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
                            edit_end_time.setText(new SimpleDateFormat(App.TIME_FORMAT).format(myCalender.getTime()));
                        }
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewClassActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
                timePickerDialog.setTitle("Choose hour:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });
        edit_course = (EditText)findViewById(R.id.edit_course);
        edit_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddCourseDialog();
            }
        });
        Button btn_create = (Button)findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String level = edit_level.getText().toString().trim();
                final String name = edit_name.getText().toString().trim();
                final String start_time = edit_start_time.getText().toString().trim();
                final String end_time = edit_end_time.getText().toString().trim();
                final String courses = edit_course.getText().toString();
                if (level.length()*name.length()*start_time.length()*end_time.length()*courses.length() == 0) {
                    Utils.showAlert(NewClassActivity.this, "Warning", "Please fill in the blank field");
                    return;
                }
                boolean flag = false;
                for (Class _class: Utils.currentSchool.classes) {
                    if (_class.name.equals(name)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    Utils.showAlert(NewClassActivity.this, "Warning", "The class name already exists.");
                    return;
                }
                Class _class = new Class(level, name, start_time, end_time, courses);
                Utils.currentSchool.classes.add(_class);
                Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("classes").setValue(Utils.currentSchool.classes);
                Toast.makeText(NewClassActivity.this, "Successfully created!", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void openAddCourseDialog() {
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
        array_course_sel.clear();
        schoolCourseListAdapter = new SchoolCourseListAdapter(this, Utils.currentSchool.courses, array_course_sel);
        listView.setAdapter(schoolCourseListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Course course = Utils.currentSchool.courses.get(i);
                if (array_course_sel.contains(course)) {
                    array_course_sel.remove(course);
                } else {
                    array_course_sel.add(course);
                }
                schoolCourseListAdapter.notifyDataSetChanged();
            }
        });
        Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courses = "";
                for (Course course: array_course_sel) {
                    if (courses.length() == 0) {
                        courses += course.name;
                    } else {
                        courses += "," + course.name;
                    }
                }
                edit_course.setText(courses);
                dlg.dismiss();
            }
        });
        dlg.show();
    }

    public void openAddLevelDialog() {
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
        TextView txt_title = dlg.findViewById(R.id.txt_title);
        txt_title.setText("Choose Level");
        ListView listView = dlg.findViewById(R.id.listView);
        schoolLevelListAdapter = new SchoolLevelListAdapter(this, Utils.currentSchool.levels);
        schoolLevelListAdapter.flag_list = true;
        listView.setAdapter(schoolLevelListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Level level = Utils.currentSchool.levels.get(i);
                edit_level.setText(level.name);
                dlg.dismiss();
            }
        });
        Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setVisibility(View.GONE);
        dlg.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}