package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Absence;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.Exam;
import com.ediattah.rezoschool.Model.Quarter;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StudentDetailActivity extends AppCompatActivity {
    Calendar currentCalendar;
    CompactCalendarView calendarView;
    TextView txt_month, txt_result_day, txt_evaluation_day, txt_evaluation_quarter, txt_result_quarter, txt_result_week, txt_evaluation_week, txt_absence_week, txt_result_month, txt_evaluation_month, txt_absence_month, txt_absence_quarter;
    LinearLayout ly_absence;
    Date sel_date;
    Student student;
    String sel_absence_url;
    RelativeLayout ly_absence_cover;
    ImageView img_absence;
    EditText edit_course;
    TextView txt_quarter;
    ArrayList<Event> list_event = new ArrayList<>();
    ArrayList<Course> array_courses = new ArrayList<>();
    Course sel_course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.student_presence_absence));
        student = (Student) getIntent().getSerializableExtra("OBJECT");
        for (Class _class:Utils.currentSchool.classes) {
            if (_class.name.equals(student.class_name)) {
                array_courses = _class.courses;
                break;
            }
        }
        if (array_courses.size() > 0) {
            sel_course = array_courses.get(0);
        }
        edit_course = findViewById(R.id.edit_course);
        edit_course.setText(sel_course.name + " x " + sel_course.coef);
        edit_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddCourseDialog();
            }
        });
        txt_result_day = findViewById(R.id.txt_result_day);
        txt_evaluation_day = findViewById(R.id.txt_evaluation_day);
        txt_result_week = findViewById(R.id.txt_result_week);
        txt_evaluation_week = findViewById(R.id.txt_evaluation_week);
        txt_absence_week = findViewById(R.id.txt_absence_week);
        txt_result_month = findViewById(R.id.txt_result_month);
        txt_evaluation_month = findViewById(R.id.txt_evaluation_month);
        txt_absence_month = findViewById(R.id.txt_absence_month);
        txt_result_quarter = findViewById(R.id.txt_result_quarter);
        txt_evaluation_quarter = findViewById(R.id.txt_evaluation_quarter);
        txt_absence_quarter = findViewById(R.id.txt_absence_quarter);
        txt_quarter = findViewById(R.id.txt_quarter);
        Button btn_update = findViewById(R.id.btn_update);
        Button btn_justification = findViewById(R.id.btn_justification);
        ly_absence = findViewById(R.id.ly_absence);
        ly_absence_cover = findViewById(R.id.ly_absence_cover);
        img_absence = findViewById(R.id.img_absence);
        ly_absence_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ly_absence_cover.setVisibility(View.GONE);
                img_absence.setVisibility(View.GONE);
            }
        });
        btn_justification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ly_absence_cover.setVisibility(View.VISIBLE);
//                img_absence.setVisibility(View.VISIBLE);
//                Glide.with(StudentDetailActivity.this).load(sel_absence_url).apply(new RequestOptions()
//                        .placeholder(R.drawable.default_pic1).centerCrop().dontAnimate()).into(img_absence);
                Intent intent = new Intent(StudentDetailActivity.this, ImageViewerActivity.class);
                intent.putExtra("url", sel_absence_url);
                startActivity(intent);
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sel_date_str = Utils.getDateString(sel_date);
                openUpdateResultDialog(sel_date_str);
            }
        });
        calendarView = findViewById(R.id.calendarView);
        txt_month = findViewById(R.id.txt_month);
        sel_date = Calendar.getInstance().getTime();
        txt_month.setText(Utils.getYearMonthString(sel_date));
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);

        // define a listener to receive callbacks when certain events happen.
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                txt_result_day.setText("");
                txt_evaluation_day.setText("");
                ly_absence.setVisibility(View.GONE);
                sel_date = dateClicked;
                List<Event> list = (ArrayList<Event>) calendarView.getEvents(dateClicked);
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        String m_name = list.get(i).getData().toString();
                        if (m_name.contains("Exam")) {
                            Exam exam = (Exam) list.get(i).getData();
                            txt_result_day.setText(exam.num1 + " / " + exam.num2);
                            txt_evaluation_day.setText(getEvaluation(exam.num1, exam.num2));
                        } else {
                            Absence absence = (Absence)list.get(i).getData();
                            ly_absence.setVisibility(View.VISIBLE);
                            sel_absence_url = absence.url;
                        }

                    }

                }
                get_exam_weekly();
                get_exam_monthly();
                get_exam_quarterly();
                get_absence_weekly();
                get_absence_monthly();
                get_absence_quarterly();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
//                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                String dateStr = String.valueOf(firstDayOfNewMonth.getYear()) + "-" + String.valueOf(firstDayOfNewMonth.getMonth());
                txt_month.setText(Utils.getYearMonthString(firstDayOfNewMonth));
            }
        });
        get_exams();
        get_absences();

    }
    public void openAddCourseDialog() {
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
        ListView listView = dlg.findViewById(R.id.listView);
        final SchoolCourseListAdapter schoolCourseListAdapter = new SchoolCourseListAdapter(this, array_courses, null);
        schoolCourseListAdapter.flag_timeslot = true;
        listView.setAdapter(schoolCourseListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sel_course = App.school_courses.get(i);
                edit_course.setText(sel_course.name + " x " + sel_course.coef);
                get_exams();
                dlg.dismiss();

            }
        });
        Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setVisibility(View.GONE);
        dlg.show();
    }
    public void openUpdateResultDialog(final String sel_date_str) {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_update_result, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.2);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        final EditText edit_result = dlg.findViewById(R.id.edit_result);
        edit_result.setText(txt_result_day.getText().toString().trim());
        final Button btn_update = (Button)dlg.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String result = edit_result.getText().toString().trim();
                Utils.mDatabase.child(Utils.tbl_exam).orderByChild("school_id").equalTo(Utils.currentSchool._id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            for (DataSnapshot datas:dataSnapshot.getChildren()) {
                                Exam exam = datas.getValue(Exam.class);
                                exam._id = datas.getKey();
                                assert exam != null;
                                if (exam.uid.equals(student.uid)) {
                                    String date_str = Utils.getDateString(exam.date);
                                    if (date_str.equals(sel_date_str)) {
                                        if (result.length() == 0) {
                                            Utils.mDatabase.child(Utils.tbl_exam).child(exam._id).setValue(null);
                                        } else {
                                            Utils.mDatabase.child(Utils.tbl_exam).child(exam._id).child("result").setValue(result);
                                        }
                                        Toast.makeText(StudentDetailActivity.this, getResources().getString(R.string.successfully_updated), Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                    }
                                } else {
                                }
                            }
                        }
//                        if (result.length() > 0) {
//                            Utils.mDatabase.child(Utils.tbl_exam).push().setValue(new Exam("", sel_date, result, Utils.currentSchool._id, student.uid, "", ""));
//                        }
//                        Toast.makeText(StudentDetailActivity.this, getResources().getString(R.string.successfully_updated), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                dlg.dismiss();
            }
        });
        dlg.show();
    }

    void get_exams() {
        Utils.mDatabase.child(Utils.tbl_exam).orderByChild("school_id").equalTo(Utils.currentSchool._id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                calendarView.removeAllEvents();
                txt_result_day.setText("");
                txt_evaluation_day.setText("");
                list_event.clear();
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        Exam exam = datas.getValue(Exam.class);
                        if (!exam.course_name.equals(sel_course.name)) {
                            continue;
                        }
                        exam._id = datas.getKey();
                        assert exam != null;
                        if (exam.uid.equals(student.uid)) {
                            Event ev = new Event(Color.parseColor("#0000ff"), exam.date.getTime(), exam);
                            calendarView.addEvent(ev);
                            list_event.add(ev);
                            if (Utils.getDateString(exam.date).equals(Utils.getDateString(sel_date))) {
                                txt_result_day.setText(exam.num1 + " / " + exam.num2);
                                txt_evaluation_day.setText(getEvaluation(exam.num1, exam.num2));
                            }
                        }
                    }
                    get_exam_weekly();
                    get_exam_monthly();
                    get_exam_quarterly();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void get_exam_weekly() {
        txt_result_week.setText("");
        int num1 = 0, num2 = 0;
        ArrayList<Event> arrayList = new ArrayList<>();
        for (Event event:list_event) {
            String m_name = event.getData().toString();
            if (m_name.contains("Exam")) {
                Exam exam = (Exam)event.getData();
                long firstDayTime = Utils.getFirstDayOfWeek(sel_date);
                long lastDayTime = Utils.getFirstDayOfWeek(sel_date)+3600*1000*24*7;
                if (exam.date.getTime() >= firstDayTime && exam.date.getTime() < lastDayTime) {
                    arrayList.add(event);
                    num1 += exam.num1*Integer.parseInt(exam.course_coef); num2 += exam.num2*Integer.parseInt(exam.course_coef);
                }
            }
        }
        if (arrayList.size() == 0) {
            txt_result_week.setText("");
            txt_evaluation_week.setText("");
        } else {
            txt_result_week.setText(num1 + " / " + num2);
            txt_evaluation_week.setText(getEvaluation(num1, num2));
        }
    }
    void get_exam_monthly() {
        txt_result_month.setText("");
        int num1 = 0, num2 = 0;
        ArrayList<Event> arrayList = new ArrayList<>();
        for (Event event:list_event) {
            String m_name = event.getData().toString();
            if (m_name.contains("Exam")) {
                Exam exam = (Exam)event.getData();
                long firstDayTime = Utils.getFirstDayOfMonth(sel_date);
                if (exam.date.getTime() >= firstDayTime && exam.date.getTime() < Utils.getLastDayOfMonth(sel_date)) {
                    arrayList.add(event);
                    num1 += exam.num1*Integer.parseInt(exam.course_coef); num2 += exam.num2*Integer.parseInt(exam.course_coef);
                }
            }
        }
        if (arrayList.size() == 0) {
            txt_result_month.setText("");
            txt_evaluation_month.setText("");
        } else {
            txt_result_month.setText(num1 + " / " + num2);
            txt_evaluation_month.setText(getEvaluation(num1, num2));
        }
    }
    void get_exam_quarterly() {
        Quarter quarter = Utils.getCurrentQuarter(sel_date);
        txt_result_quarter.setText("");
        txt_quarter.setText("Quarterly - " + quarter.name + "th");
        int num1 = 0, num2 = 0;
        ArrayList<Event> arrayList = new ArrayList<>();
        for (Event event:list_event) {
            String m_name = event.getData().toString();
            if (m_name.contains("Exam")) {
                Exam exam = (Exam)event.getData();
                if (exam.date.getTime() >= quarter.first_time && exam.date.getTime() <= quarter.last_time) {
                    arrayList.add(event);
                    num1 += exam.num1*Integer.parseInt(exam.course_coef); num2 += exam.num2*Integer.parseInt(exam.course_coef);
                }
            }
        }
        if (arrayList.size() == 0) {
            txt_result_quarter.setText("");
            txt_evaluation_quarter.setText("");
        } else {
            txt_result_quarter.setText(num1 + " / " + num2);
            txt_evaluation_quarter.setText(getEvaluation(num1, num2));
        }
    }
    void get_absence_weekly() {
        txt_absence_week.setText("");
        ArrayList<Event> arrayList = new ArrayList<>();
        for (Event event:list_event) {
            String m_name = event.getData().toString();
            if (m_name.contains("Absence")) {
                Absence absence = (Absence)event.getData();
                long firstDayTime = Utils.getFirstDayOfWeek(sel_date);
                long lastDayTime = Utils.getFirstDayOfWeek(sel_date)+3600*1000*24*7;
                if (absence.date.getTime() >= firstDayTime && absence.date.getTime() < lastDayTime) {
                    arrayList.add(event);
                }
            }
        }
        if (arrayList.size() == 0) {
            txt_absence_week.setText("");
        } else {
            txt_absence_week.setText(String.valueOf(arrayList.size()));
        }
    }
    void get_absence_monthly() {
        txt_absence_month.setText("");
        ArrayList<Event> arrayList = new ArrayList<>();
        for (Event event:list_event) {
            String m_name = event.getData().toString();
            if (m_name.contains("Absence")) {
                Absence absence = (Absence)event.getData();
                long firstDayTime = Utils.getFirstDayOfMonth(sel_date);
//                long lastDayTime = Utils.getFirstDayOfWeek(sel_date)+3600*1000*24*Utils.getLastDayOfMonth(sel_date);
                if (absence.date.getTime() >= firstDayTime && absence.date.getTime() < Utils.getLastDayOfMonth(sel_date)) {
                    arrayList.add(event);
                }
            }
        }
        if (arrayList.size() == 0) {
            txt_absence_month.setText("");
        } else {
            txt_absence_month.setText(String.valueOf(arrayList.size()));
        }
    }
    void get_absence_quarterly() {
        Quarter quarter = Utils.getCurrentQuarter(sel_date);
        txt_absence_quarter.setText("");
        ArrayList<Event> arrayList = new ArrayList<>();
        for (Event event:list_event) {
            String m_name = event.getData().toString();
            if (m_name.contains("Absence")) {
                Absence absence = (Absence)event.getData();
                if (absence.date.getTime() >= quarter.first_time && absence.date.getTime() <= quarter.last_time) {
                    arrayList.add(event);
                }
            }
        }
        if (arrayList.size() == 0) {
            txt_absence_quarter.setText("");
        } else {
            txt_absence_quarter.setText(String.valueOf(arrayList.size()));
        }
    }
    void get_absences() {
        ly_absence.setVisibility(View.GONE);
        Utils.mDatabase.child(Utils.tbl_absence).orderByChild("school_id").equalTo(Utils.currentSchool._id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                calendarView.removeAllEvents();
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        Absence absence = datas.getValue(Absence.class);
                        absence._id = datas.getKey();
                        assert absence != null;
                        if (absence.uid.equals(student.uid)) {
                            Event ev = new Event(Color.parseColor("#ff0000"), absence.date.getTime(), absence);
                            list_event.add(ev);
                            calendarView.addEvent(ev);
                            if (Utils.getDateString(absence.date).equals(Utils.getDateString(sel_date))) {
                                ly_absence.setVisibility(View.VISIBLE);
                                sel_absence_url = absence.url;
                            }
                        }
                    }
                    get_absence_weekly();
                    get_absence_monthly();
                    get_exam_quarterly();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    String getEvaluation(int num1, int num2) {
        String evaluation = "";
        float val = (float)num1/num2;
        if (val >= 0.9) {
            evaluation = getResources().getString(R.string.excellent);
        } else if (val >= 0.8) {
            evaluation = getResources().getString(R.string.good);
        } else if (val >= 0.5) {
            evaluation = getResources().getString(R.string.normal);
        } else {
            evaluation = getResources().getString(R.string.poor);
        }
        return evaluation;
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}