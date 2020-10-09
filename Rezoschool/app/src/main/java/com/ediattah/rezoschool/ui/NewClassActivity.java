package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.CourseTime;
import com.ediattah.rezoschool.Model.Level;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.adapter.SchoolLevelListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NewClassActivity extends AppCompatActivity {
    EditText edit_level, edit_name;
    SchoolCourseListAdapter schoolCourseListAdapter;
    SchoolLevelListAdapter schoolLevelListAdapter;
    ArrayList<Course> array_course_sel = new ArrayList<>();
    ProgressDialog progressDialog;
//    TextView txt_course;
//    LinearLayout ly_course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.create_new_class));
        App.hideKeyboard(this);

        edit_level = findViewById(R.id.edit_level);
        edit_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddLevelDialog();
            }
        });
        edit_name = findViewById(R.id.edit_name);

//        ly_course = findViewById(R.id.ly_course);
//        txt_course = (TextView) findViewById(R.id.txt_course);
//        txt_course.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openAddCourseDialog();
//            }
//        });
        Button btn_create = (Button)findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String level = edit_level.getText().toString().trim();
                final String name = edit_name.getText().toString().trim();
                if (level.length()*name.length() == 0) {
                    Utils.showAlert(NewClassActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
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
                    Utils.showAlert(NewClassActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.the_class_name_already_exists));
                    return;
                }
                Class _class = new Class(level, name, array_course_sel);
//                for (Class aClass:Utils.currentSchool.classes) {
//                    if (aClass.level.equals(_class.level)) {
//                        for (Course aCourse:aClass.courses) {
//                            for (Course _course:array_course_sel) {
//                                if (_course.name.equals(aCourse.name) && _course.times.get(0).dayOfWeek == aCourse.times.get(0).dayOfWeek) {
//                                    Utils.showAlert(NewClassActivity.this, "Warning", aCourse.name + " course time table already set by " + aClass.name +
//                                            "\nPlease try other one.");
//                                    return;
//                                }
//                            }
//                        }
//                    }
//                }
                Utils.currentSchool.classes.add(_class);
                Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("classes").setValue(Utils.currentSchool.classes);
                Toast.makeText(NewClassActivity.this, getResources().getString(R.string.successfully_created), Toast.LENGTH_SHORT).show();

            }
        });
    }
//    public void openAddCourseDialog() {
//        final Dialog dlg = new Dialog(this);
//        Window window = dlg.getWindow();
//        View view = getLayoutInflater().inflate(R.layout.dialog_choose_item, null);
//        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
//        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
//        view.setMinimumWidth(width);
//        view.setMinimumHeight(height);
//        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dlg.setContentView(view);
//        window.setGravity(Gravity.CENTER);
//        dlg.show();
//        ListView listView = dlg.findViewById(R.id.listView);
//        array_course_sel.clear();
//        schoolCourseListAdapter = new SchoolCourseListAdapter(this, Utils.currentSchool.courses, array_course_sel);
//        schoolCourseListAdapter.flag_class = true;
//        listView.setAdapter(schoolCourseListAdapter);
//
//        Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
//        btn_choose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                refresh_timeLayout();
//                dlg.dismiss();
//            }
//        });
//        dlg.show();
//    }
//    public void refresh_timeLayout() {
////        ly_course.removeAllViews();
//        LayoutInflater inflater = LayoutInflater.from(NewClassActivity.this);
//        for (int i = 0; i < array_course_sel.size(); i++) {
//            final Course course = array_course_sel.get(i);
//            View view1 = inflater.inflate(R.layout.cell_class_course, null);
//            TextView txt_course = view1.findViewById(R.id.txt_course);
//            TextView txt_day = view1.findViewById(R.id.txt_day);
//            TextView txt_time = view1.findViewById(R.id.txt_time);
//            Button btn_remove = view1.findViewById(R.id.btn_remove);
//                    final int finalI = i;
//                    btn_remove.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            array_course_sel.remove(finalI);
//                            ly_course.removeViewAt(finalI);
//                            refresh_timeLayout();
//                        }
//                    });
//            txt_course.setText(course.name);
//            CourseTime courseTime = course.times.get(0);
//            txt_day.setText(Utils.getDayStrFromInt(courseTime.dayOfWeek));
//            txt_time.setText(courseTime.start_time + " ~ " + courseTime.end_time);
//            ly_course.addView(view1, i);
//        }
//    }
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
        txt_title.setText(getResources().getString(R.string.choose_level));
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