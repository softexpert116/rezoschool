package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dpro.widgets.OnWeekdaysChangeListener;
import com.dpro.widgets.WeekdaysPicker;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.CourseTime;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CourseDetailActivity extends AppCompatActivity {

    Course sel_course;
    LinearLayout ly_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sel_course = (Course)getIntent().getSerializableExtra("COURSE");
        setTitle(getResources().getString(R.string.timeslots_for_) + sel_course.name + " " + getResources().getString(R.string.course));

        TextView txt_course = findViewById(R.id.txt_course);
        WeekdaysPicker weekdaysPicker = findViewById(R.id.weekdays);
        ly_time = findViewById(R.id.ly_time);
        txt_course.setText(sel_course.name);
        weekdaysPicker.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
                // Do Something
                refresh_timeSlot(clickedDayOfWeek);
            }
        });
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (CourseTime courseTime:sel_course.times) {
            arrayList.add(courseTime.dayOfWeek);
        }
        weekdaysPicker.setSelectOnlyOne(true);
//        weekdaysPicker.setSelectedDays(arrayList);
        weekdaysPicker.selectDay(arrayList.get(0));
        refresh_timeSlot(arrayList.get(0));

    }

    void refresh_timeSlot(int weekday) {
        ly_time.removeAllViews();
        ArrayList<CourseTime> times = new ArrayList<>();
        for (CourseTime courseTime:sel_course.times) {
            if (courseTime.dayOfWeek == weekday) {
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
                lp1.setMargins(0, 5, 0, 10);
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
//        if (times.size() == 0) {
//            ly_time.setVisibility(View.GONE);
//        } else {
//            ly_time.setVisibility(View.VISIBLE);
//            for (CourseTime courseTime:times) {
//            }
//        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}