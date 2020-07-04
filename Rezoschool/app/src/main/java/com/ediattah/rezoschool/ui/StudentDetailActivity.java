package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StudentDetailActivity extends AppCompatActivity {
    Calendar currentCalendar;
    CompactCalendarView calendarView;
    TextView txt_month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Student Presence & Absence");

        calendarView = findViewById(R.id.calendarView);
        txt_month = findViewById(R.id.txt_month);
        Date currentTime = Calendar.getInstance().getTime();
        txt_month.setText(Utils.getYearMonthString(currentTime));
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        // Add event 1 on Sun, 07 Jun 2015 18:20:51 GMT
        Event ev1 = new Event(Color.GREEN, 1433701251000L, "Some extra data that I want to store.");
        calendarView.addEvent(ev1);

        // Added event 2 GMT: Sun, 07 Jun 2015 19:10:51 GMT
        Event ev2 = new Event(Color.GREEN, 1433704251000L);
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
                Toast.makeText(StudentDetailActivity.this, "Day was clicked", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
//                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                String dateStr = String.valueOf(firstDayOfNewMonth.getYear()) + "-" + String.valueOf(firstDayOfNewMonth.getMonth());
                txt_month.setText(Utils.getYearMonthString(firstDayOfNewMonth));
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}