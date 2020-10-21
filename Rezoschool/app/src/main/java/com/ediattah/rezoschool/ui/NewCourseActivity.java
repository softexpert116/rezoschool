package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dpro.widgets.OnWeekdaysChangeListener;
import com.dpro.widgets.WeekdaysPicker;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.CourseTime;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.ClassListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class NewCourseActivity extends AppCompatActivity {
    EditText edit_start_time, edit_end_time;
    LinearLayout ly_time;
    ArrayList<CourseTime> times = new ArrayList<>();
    int sel_day = 2;
    EditText edit_class, edit_coef;
    Class sel_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        App.hideKeyboard(this);
//        sel_class = (Class)getIntent().getSerializableExtra("sel_class");
        setTitle(getResources().getString(R.string.create_new_course));
        App.hideKeyboard(this);
        ly_time = findViewById(R.id.ly_time);
        edit_class = (EditText)findViewById(R.id.edit_class);
        edit_coef = (EditText)findViewById(R.id.edit_coef);
        edit_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddClassDialog();
            }
        });
        final EditText edit_course = (EditText)findViewById(R.id.edit_course);
        Button btn_create = (Button)findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String class_name = edit_class.getText().toString().trim();
                final String course_name = edit_course.getText().toString().trim();
                final String coef = edit_coef.getText().toString().trim();

                if (course_name.length()*class_name.length()*coef.length() == 0) {
                    Utils.showAlert(NewCourseActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                } else if (times.size() == 0) {
                    Utils.showAlert(NewCourseActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_add_times));
                } else if (Integer.valueOf(coef) > 4 || Integer.valueOf(coef) < 1) {
                    Utils.showAlert(NewCourseActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.coef_limit));
                } else {
                    boolean flag = false;
                    for (Course course: sel_class.courses) {
                        if (course.name.equals(course_name)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        Utils.showAlert(NewCourseActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.the_course_name_already_exists_in_class_) + sel_class.name + ".");
                        return;
                    }
                    Course course = new Course(course_name, coef, times);
                    sel_class.courses.add(course);
                    for (int i = 0; i < Utils.currentSchool.classes.size(); i++) {
                        Class _class = Utils.currentSchool.classes.get(i);
                        if (_class.name.equals(sel_class.name)) {
                            Utils.currentSchool.classes.set(i, sel_class);
                            break;
                        }
                    }
//                    Utils.currentSchool.courses.add(course);
                    Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("classes").setValue(Utils.currentSchool.classes);
                    Toast.makeText(NewCourseActivity.this, getResources().getString(R.string.successfully_created), Toast.LENGTH_SHORT).show();
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
                    Utils.showAlert(NewCourseActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
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
                timePickerDialog.setTitle(getResources().getString(R.string.choose_hour));
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
                timePickerDialog.setTitle(getResources().getString(R.string.choose_hour));
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
    public void openAddClassDialog() {
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
        txt_title.setText(getResources().getString(R.string.choose_class));
        ListView listView = dlg.findViewById(R.id.listView);
        final ClassListAdapter classAdapter = new ClassListAdapter(this, Utils.currentSchool.classes);
        classAdapter.sel_index = -2;
        listView.setAdapter(classAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                classAdapter.sel_index = i;
//                classAdapter.notifyDataSetChanged();
                sel_class = Utils.currentSchool.classes.get(i);
                edit_class.setText(sel_class.name);
                dlg.dismiss();
            }
        });
        final Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setVisibility(View.GONE);
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sel_class = Utils.currentSchool.classes.get(classAdapter.sel_index);
                edit_class.setText(sel_class.name);
                dlg.dismiss();
            }
        });
        dlg.show();
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