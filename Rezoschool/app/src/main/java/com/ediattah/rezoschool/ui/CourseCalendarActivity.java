package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.Syllabus;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.adapter.TeacherListAdapter;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.ediattah.rezoschool.App.array_course;
import static com.ediattah.rezoschool.App.array_teacher;

public class CourseCalendarActivity extends AppCompatActivity {
    Course sel_course;
    ArrayList<Event> list_event = new ArrayList<>();
    Date sel_date;
    ArrayList<Teacher> list_teacher = new ArrayList<>();
    LinearLayout ly_no_items;

    Calendar currentCalendar;
    CompactCalendarView calendarView;
    TextView txt_month;

    Class aClass;
    ArrayList<Syllabus> arrayList = new ArrayList<>();
    ListView listView;
    DayCourseListAdapter dayCourseListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_calendar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Calendar per Course ");
        sel_course = (Course)getIntent().getSerializableExtra("OBJECT");
        ly_no_items = findViewById(R.id.ly_no_items);
        calendarView = findViewById(R.id.calendarView);
        txt_month = findViewById(R.id.txt_month);
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
                updateListView();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
//                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                String dateStr = String.valueOf(firstDayOfNewMonth.getYear()) + "-" + String.valueOf(firstDayOfNewMonth.getMonth());
                txt_month.setText(Utils.getYearMonthString(firstDayOfNewMonth));
            }
        });

        listView = findViewById(R.id.listView);
        dayCourseListAdapter = new DayCourseListAdapter(this, arrayList);
        listView.setAdapter(dayCourseListAdapter);


        Button btn_teacher = (Button)findViewById(R.id.btn_teacher);
        btn_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_teacher.clear();
                for (Teacher teacher:Utils.currentSchool.teachers) {
                    if (teacher.courses.contains(sel_course.name)) {
                        list_teacher.add(teacher);
                    }
                }
                openAddDialog();
            }
        });
    }
    public void read_syllabus() {
        Utils.mDatabase.child(Utils.tbl_syllabus).orderByChild("school_id").equalTo(Utils.currentSchool._id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                list_event.clear();
                calendarView.removeAllEvents();
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        Syllabus syllabus = datas.getValue(Syllabus.class);
                        syllabus._id = datas.getKey();
                        if (syllabus.course.equals(sel_course.name)) {
                            Event ev = new Event(Color.parseColor("#0000ff"), syllabus.date.getTime(), syllabus);
                            calendarView.addEvent(ev);
                            list_event.add(ev);
                            if (Utils.getDateString(syllabus.date).equals(Utils.getDateString(sel_date))) {
                                arrayList.add(syllabus);
                            }
                        }
                    }
                }
                updateListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void updateListView() {
        if (arrayList.size() == 0) {
            ly_no_items.setVisibility(View.VISIBLE);
        } else {
            ly_no_items.setVisibility(View.GONE);
        }
        dayCourseListAdapter.notifyDataSetChanged();
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
        TeacherListAdapter teacherListAdapter = new TeacherListAdapter(CourseCalendarActivity.this, list_teacher);
        listView.setAdapter(teacherListAdapter);

        dlg.show();
    }
    @Override
    public void onResume() {
        super.onResume();
        read_syllabus();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}