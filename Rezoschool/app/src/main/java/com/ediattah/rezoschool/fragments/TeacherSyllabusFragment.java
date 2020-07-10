package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.Syllabus;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
    ArrayList<Syllabus> arrayList = new ArrayList<>();
    ListView listView;
    DayCourseListAdapter dayCourseListAdapter;
    ArrayList<Event> list_event = new ArrayList<>();
    Date sel_date;
    LinearLayout ly_no_items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_teacher_syllabus, container, false);

        ly_no_items = v.findViewById(R.id.ly_no_items);
        calendarView = v.findViewById(R.id.calendarView);
        txt_month = v.findViewById(R.id.txt_month);
        sel_date = Calendar.getInstance().getTime();
        txt_month.setText(Utils.getYearMonthString(sel_date));
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);

        // define a listener to receive callbacks when certain events happen.
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

        listView = v.findViewById(R.id.listView);
        dayCourseListAdapter = new DayCourseListAdapter(activity, arrayList);
        listView.setAdapter(dayCourseListAdapter);

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, NewSyllabusActivity.class);
                activity.startActivity(intent);
            }
        });
        read_syllabus();
        return v;
    }
    public void read_syllabus() {
        Utils.mDatabase.child(Utils.tbl_syllabus).orderByChild("uid").equalTo(Utils.mUser.getUid()).addValueEventListener(new ValueEventListener() {
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
    public void onResume() {
        super.onResume();

    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}