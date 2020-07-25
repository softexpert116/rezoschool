package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Syllabus;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.adapter.SchoolListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class NewSyllabusActivity extends AppCompatActivity {
    EditText edit_school, edit_course, edit_date, edit_time;
    Date sel_date;
    School sel_school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_syllabus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Create New Syllabus");
        App.hideKeyboard(this);
        edit_school = (EditText)findViewById(R.id.edit_school);
        edit_course = (EditText)findViewById(R.id.edit_course);
        edit_date = (EditText)findViewById(R.id.edit_date);
        edit_time = (EditText)findViewById(R.id.edit_time);
        edit_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddSchoolDialog();
            }
        });
        edit_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sel_school == null) {
                    Utils.showAlert(NewSyllabusActivity.this, "Warning", "Please select school number.");
                    return;
                }
                openAddCourseDialog();
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
                            sel_date = myCalender.getTime();
                            edit_date.setText(new SimpleDateFormat(App.DATE_FORMAT).format(sel_date));
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
        Button btn_create = findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String course = edit_course.getText().toString().trim();
                String time = edit_time.getText().toString().trim();
                if (course.length()*time.length() == 0) {
                    Utils.showAlert(NewSyllabusActivity.this, "Warning", "Please fill in the blank field");
                    return;
                }
                if (sel_date == null || sel_school == null) {
                    Utils.showAlert(NewSyllabusActivity.this, "Warning", "Please fill in the blank field");
                    return;
                }
                Syllabus syllabus = new Syllabus("", sel_date, time, course, sel_school._id, Utils.mUser.getUid());
                Utils.mDatabase.child(Utils.tbl_syllabus).push().setValue(syllabus);
                Toast.makeText(NewSyllabusActivity.this, "Successfully created!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void openAddSchoolDialog() {
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
        final LinearLayout ly_no_items = dlg.findViewById(R.id.ly_no_items);
        txt_title.setText("Choose School");
        ListView listView = dlg.findViewById(R.id.listView);
        final ArrayList<School> array_all_school = new ArrayList<>();
        final SchoolListAdapter schoolAdapter = new SchoolListAdapter(this, array_all_school);
        schoolAdapter.flag_view = true;
        schoolAdapter.sel_index = -1;
        listView.setAdapter(schoolAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sel_school = array_all_school.get(i);
                edit_school.setText(sel_school.number);
                dlg.dismiss();
            }
        });
        final Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setVisibility(View.GONE);
        Utils.mDatabase.child(Utils.tbl_school).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    array_all_school.clear();
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        School school = datas.getValue(School.class);
                        school._id = datas.getKey();
                        array_all_school.add(school);
                    }
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        schoolAdapter.arrayList = array_all_school;
                        schoolAdapter.notifyDataSetChanged();
                        if (array_all_school.size() == 0) {
                            ly_no_items.setVisibility(View.VISIBLE);
                        } else {
                            ly_no_items.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sel_school = array_all_school.get(schoolAdapter.sel_index);
//                edit_school.setText(sel_school.number);
//                dlg.dismiss();
            }
        });
        dlg.show();
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
        final LinearLayout ly_no_items = dlg.findViewById(R.id.ly_no_items);
        TextView txt_title = dlg.findViewById(R.id.txt_title);
        txt_title.setText("Choose Course");
        ListView listView = dlg.findViewById(R.id.listView);
        final ArrayList<Course> array_course_sel = new ArrayList<>();
        final SchoolCourseListAdapter courseAdapter = new SchoolCourseListAdapter(this, sel_school.courses, array_course_sel);
        listView.setAdapter(courseAdapter);
        if (sel_school.courses.size() == 0) {
            ly_no_items.setVisibility(View.VISIBLE);
        } else {
            ly_no_items.setVisibility(View.GONE);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Course course = sel_school.courses.get(i);
                edit_course.setText(course.name);
                dlg.dismiss();
            }
        });
        final Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setVisibility(View.GONE);
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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