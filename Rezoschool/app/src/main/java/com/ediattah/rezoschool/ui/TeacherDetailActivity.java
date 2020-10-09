package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.Syllabus;
import com.ediattah.rezoschool.Model.Teacher;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.DayCourseModel;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.DayCourseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TeacherDetailActivity extends AppCompatActivity {
    Calendar currentCalendar;
    CompactCalendarView calendarView;
    TextView txt_month;
    LinearLayout ly_no_items;
    Class aClass;
    ArrayList<Syllabus> arrayList = new ArrayList<>();
    ListView listView;
    DayCourseListAdapter dayCourseListAdapter;
    Date sel_date;
    ArrayList<Event> list_event = new ArrayList<>();
    Teacher sel_teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_detail);
        sel_teacher = (Teacher)getIntent().getSerializableExtra("TEACHER");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.teacher_syllabus_of_the_courses));

        ly_no_items = findViewById(R.id.ly_no_items);
        calendarView = findViewById(R.id.calendarView);
        txt_month = findViewById(R.id.txt_month);
        sel_date = Calendar.getInstance().getTime();
        txt_month.setText(Utils.getYearMonthString(sel_date));
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        // define a listener to receive callbacks when certain events happen.

        listView = findViewById(R.id.listView);
        dayCourseListAdapter = new DayCourseListAdapter(this, arrayList);
        listView.setAdapter(dayCourseListAdapter);

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                arrayList.clear();
                List<Event> events = calendarView.getEvents(dateClicked);
                sel_date = dateClicked;
                for (Event event:events) {
                    Syllabus syllabus = (Syllabus) event.getData();
                    arrayList.add(syllabus);
                }
                if (arrayList.size() == 0) {
                    ly_no_items.setVisibility(View.VISIBLE);
                } else {
                    ly_no_items.setVisibility(View.GONE);
                }
                dayCourseListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
//                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                String dateStr = String.valueOf(firstDayOfNewMonth.getYear()) + "-" + String.valueOf(firstDayOfNewMonth.getMonth());
                txt_month.setText(Utils.getYearMonthString(firstDayOfNewMonth));
            }
        });
        read_syllabus();
    }
    public void read_syllabus() {
        Utils.mDatabase.child(Utils.tbl_syllabus).orderByChild("uid").equalTo(sel_teacher.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                list_event.clear();
                calendarView.removeAllEvents();
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        Syllabus syllabus = datas.getValue(Syllabus.class);
                        syllabus._id = datas.getKey();
                        Event ev = new Event(Color.parseColor("#0000ff"), syllabus.date.getTime(), syllabus);
                        calendarView.addEvent(ev);
                        list_event.add(ev);
                        if (Utils.getDateString(syllabus.date).equals(Utils.getDateString(sel_date))) {
                            arrayList.add(syllabus);
                        }
                    }
                }
                if (arrayList.size() == 0) {
                    ly_no_items.setVisibility(View.VISIBLE);
                } else {
                    ly_no_items.setVisibility(View.GONE);
                }
                dayCourseListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}