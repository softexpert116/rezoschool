package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dpro.widgets.OnWeekdaysChangeListener;
import com.dpro.widgets.WeekdaysPicker;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.CourseTime;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NewCourseActivity extends AppCompatActivity {
    EditText edit_start_time, edit_end_time;
    LinearLayout ly_time;
    ArrayList<CourseTime> times = new ArrayList<>();
    int sel_day = 2;
//    Class sel_class;
//    TimeslotActivity timeslotActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        sel_class = (Class)getIntent().getSerializableExtra("sel_class");
        setTitle("Create New Course in " + TimeslotActivity.sel_class.name + " Class");
        App.hideKeyboard(this);
        ly_time = findViewById(R.id.ly_time);
        final EditText edit_course = (EditText)findViewById(R.id.edit_course);
        Button btn_create = (Button)findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String course_name = edit_course.getText().toString().trim();
                if (course_name.length() == 0) {
                    Utils.showAlert(NewCourseActivity.this, "Warning", "Please fill in course name");
                } else if (times.size() == 0) {
                    Utils.showAlert(NewCourseActivity.this, "Warning", "Please add times");
                } else {
                    boolean flag = false;
                    for (Course course: TimeslotActivity.sel_class.courses) {
                        if (course.name.equals(course_name)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        Utils.showAlert(NewCourseActivity.this, "Warning", "The course name already exists in class " + TimeslotActivity.sel_class.name + ".");
                        return;
                    }
                    Course course = new Course(course_name, times);
                    TimeslotActivity.sel_class.courses.add(course);
                    for (int i = 0; i < Utils.currentSchool.classes.size(); i++) {
                        Class _class = Utils.currentSchool.classes.get(i);
                        if (_class.name.equals(TimeslotActivity.sel_class.name)) {
                            Utils.currentSchool.classes.set(i, TimeslotActivity.sel_class);
                            break;
                        }
                    }
//                    Utils.currentSchool.courses.add(course);
                    Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("classes").setValue(Utils.currentSchool.classes);
                    Toast.makeText(NewCourseActivity.this, "Successfully created!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button btn_clear_time = findViewById(R.id.btn_clear_time);
        btn_clear_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_start_time.setText("");
                edit_end_time.setText("");
                for (CourseTime courseTime:times) {
                    if (courseTime.dayOfWeek == sel_day) {
                        times.remove(courseTime);
                        break;
                    }
                }
                refresh_timeLayout();
            }
        });
        Button btn_set_time = findViewById(R.id.btn_set_time);
        btn_set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String start_time = edit_start_time.getText().toString().trim();
                String end_time = edit_end_time.getText().toString().trim();
                if (start_time.length()*end_time.length() == 0) {
                    Utils.showAlert(NewCourseActivity.this, "Warning", "Please fill in start and end time");
                    return;
                }
                CourseTime courseTime = new CourseTime(sel_day, start_time, end_time);
                boolean flag = false;
                for (int i = 0; i < times.size(); i++) {
                    if (times.get(i).dayOfWeek == courseTime.dayOfWeek) {
                        times.set(i, courseTime);
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    times.add(courseTime);
                }
                refresh_timeLayout();
            }
        });
        edit_start_time = findViewById(R.id.edit_start_time);
        edit_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewCourseActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
                timePickerDialog.setTitle("Choose hour:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });
        edit_end_time = findViewById(R.id.edit_end_time);
        edit_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewCourseActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
                timePickerDialog.setTitle("Choose hour:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });
        WeekdaysPicker weekdaysPicker = (WeekdaysPicker) findViewById(R.id.weekdays);
        weekdaysPicker.setSelectOnlyOne(true);
        weekdaysPicker.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
                // Do Something
                sel_day = clickedDayOfWeek;
                edit_start_time.setText("");
                edit_end_time.setText("");

                for (CourseTime courseTime:times) {
                    if (courseTime.dayOfWeek == sel_day) {
                        edit_start_time.setText(courseTime.start_time);
                        edit_end_time.setText(courseTime.end_time);
                        break;
                    }
                }
            }
        });
    }
    void order_times() {
        Collections.sort(times, new Comparator<CourseTime>() {
            @Override
            public int compare(CourseTime lhs, CourseTime rhs) {
                return String.valueOf(lhs.dayOfWeek).compareTo(String.valueOf(rhs.dayOfWeek));
            }
        });
    }
    void refresh_timeLayout() {
        order_times();
        if (times.size() == 0) {
            ly_time.setVisibility(View.GONE);
        } else {
            ly_time.setVisibility(View.VISIBLE);
            ly_time.removeAllViews();
            for (CourseTime courseTime:times) {
                LinearLayout ly_row = new LinearLayout(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                if (ly_time.getChildCount() > 0) {
                    lp.setMargins(0, 10, 0, 0);
                }
                ly_row.setLayoutParams(lp);
                ly_row.setPadding(10, 5, 10, 5);
                ly_row.setOrientation(LinearLayout.VERTICAL);
                ly_row.setBackgroundColor(Color.parseColor("#A9F1D3"));
                TextView txt_day = new TextView(this);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp1.setMargins(0, 5, 0, 0);
                txt_day.setLayoutParams(lp1);
//                txt_day.setPadding(0, 5, 0, 5);
                txt_day.setText(Utils.getDayStrFromInt(courseTime.dayOfWeek));
                txt_day.setTextColor(Color.parseColor("#D81B60"));
                ly_row.addView(txt_day);
                TextView txt_start_time = new TextView(this);
                txt_start_time.setText(courseTime.start_time + "~" + courseTime.end_time);
                ly_row.addView(txt_start_time);
                ly_time.addView(ly_row);
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}