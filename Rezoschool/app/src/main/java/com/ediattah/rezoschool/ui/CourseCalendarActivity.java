package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.DayCourseModel;
import com.ediattah.rezoschool.Model.SchoolTeacherModel;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.DayCourseListAdapter;
import com.ediattah.rezoschool.adapter.SchoolTeacherListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.ediattah.rezoschool.App.array_course;
import static com.ediattah.rezoschool.App.array_teacher;

public class CourseCalendarActivity extends AppCompatActivity {
    Calendar currentCalendar;
    CompactCalendarView calendarView;
    TextView txt_month;

    Class aClass;
    ArrayList<DayCourseModel> arrayList = new ArrayList<>();
    ListView listView;
    DayCourseListAdapter dayCourseListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_calendar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Calendar per Course ");

        calendarView = findViewById(R.id.calendarView);
        txt_month = findViewById(R.id.txt_month);
        Date currentTime = Calendar.getInstance().getTime();
        txt_month.setText(Utils.getYearMonthString(currentTime));
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        // Add event 1 on Sun, 07 Jun 2015 18:20:51 GMT
        Event ev1 = new Event(Color.GREEN, 1433701251000L, "Some extra data that I want to store.");
        calendarView.addEvent(ev1);

        // Added event 2 GMT: Sun, 07 Jun 2015 19:10:51 GMT
        Event ev2 = new Event(Color.GREEN, 1566000000000L);
        calendarView.addEvent(ev2);
        // Query for events on Sun, 07 Jun 2015 GMT.
        // Time is not relevant when querying for events, since events are returned by day.
        // So you can pass in any arbitary DateTime and you will receive all events for that day.
        List<Event> events = calendarView.getEvents(1433701251000L); // can also take a Date object

        // define a listener to receive callbacks when certain events happen.
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = calendarView.getEvents(dateClicked);
//                Log.d(TAG, "Day was clicked: " + dateClicked + " with events " + events);
//                Toast.makeText(TeacherDetailActivity.this, "Day was clicked", Toast.LENGTH_SHORT).show();
                listView.setAdapter(dayCourseListAdapter);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
//                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                String dateStr = String.valueOf(firstDayOfNewMonth.getYear()) + "-" + String.valueOf(firstDayOfNewMonth.getMonth());
                txt_month.setText(Utils.getYearMonthString(firstDayOfNewMonth));
            }
        });

        listView = findViewById(R.id.listView);
        arrayList.add(new DayCourseModel(App.array_course.get(0), "2019-05-23", "2:10 PM"));
        arrayList.add(new DayCourseModel(App.array_course.get(0), "2019-05-23", "3:10 PM"));
        arrayList.add(new DayCourseModel(App.array_course.get(0), "2019-05-23", "4:10 PM"));
        dayCourseListAdapter = new DayCourseListAdapter(this, arrayList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        Button btn_teacher = (Button)findViewById(R.id.btn_teacher);
        btn_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddDialog();
            }
        });
    }
    public void openAddDialog() {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_view_teacher, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        ListView listView = dlg.findViewById(R.id.listView);
        ArrayList<SchoolTeacherModel> arrayList = new ArrayList<>();
        for (int i = 0; i < array_teacher.size(); i++) {
            arrayList.add(new SchoolTeacherModel(i+1, array_teacher.get(i), array_course.get(0)));
        }
//        SchoolTeacherListAdapter teacherListAdapter = new SchoolTeacherListAdapter(CourseCalendarActivity.this, null, arrayList);
//        listView.setAdapter(teacherListAdapter);

        dlg.show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}