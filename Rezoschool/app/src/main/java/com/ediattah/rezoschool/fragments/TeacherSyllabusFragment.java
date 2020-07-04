package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.DayCourseModel;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.DayCourseListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewSyllabusActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TeacherSyllabusFragment extends Fragment {
    Calendar currentCalendar;
    CompactCalendarView calendarView;
    TextView txt_month;
    MainActivity activity;
    Class aClass;
    ArrayList<DayCourseModel> arrayList = new ArrayList<>();
    ListView listView;
    DayCourseListAdapter dayCourseListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_teacher_syllabus, container, false);

        calendarView = v.findViewById(R.id.calendarView);
        txt_month = v.findViewById(R.id.txt_month);
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

        listView = v.findViewById(R.id.listView);
        arrayList.add(new DayCourseModel(App.array_course.get(0), "2019-05-23", "2:10 PM"));
        arrayList.add(new DayCourseModel(App.array_course.get(1), "2019-05-23", "3:10 PM"));
        arrayList.add(new DayCourseModel(App.array_course.get(2), "2019-05-23", "4:10 PM"));
        dayCourseListAdapter = new DayCourseListAdapter(activity, arrayList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, NewSyllabusActivity.class);
                activity.startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}