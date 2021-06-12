package com.ediattah.rezoschool.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.Exam;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.Model.Tweet;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.ClassListAdapter;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.adapter.UserListAdapter;
import com.ediattah.rezoschool.ui.ExamActivity;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.StudentDetailActivity;
import com.ediattah.rezoschool.ui.TweetDetailActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jonas.jgraph.graph.NChart;
import com.jonas.jgraph.models.NExcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SchoolExamFragment extends Fragment {
    MainActivity activity;
    NChart chartView, chartViewStudent, chartViewTeacher, chartViewClass;
    LinearLayout ly_month, ly_quarter;
//    float examArray[] = new float[12];
    float intArray[] = new float[12];
    ArrayList<String> st_array = new ArrayList<>();
    ImageView img_student, img_teacher;
    TextView txt_teacher;
    ArrayList<Float> m_array = new ArrayList<>();
    ArrayList<Float> q_array = new ArrayList<>();
    ArrayList<String> m_sorted, q_sorted;
    ArrayList<User> array_user = new ArrayList<>();
    User sel_student;
//    Course sel_course;
//    Class sel_class;
    Button btn_student, btn_course, btn_class;
    MaterialSpinner spinner0, spinner11, spinner12, spinner21, spinner22, spinner_year0, spinner_year1, spinner_year2;
    List<String> periodList = new ArrayList<>();
    List<String> courseList = new ArrayList<>();
    List<String> yearList = new ArrayList<>();
    List<String> monthList = new ArrayList<>();
    List<String> quartList = new ArrayList<>();
    ArrayList<Float> examArr = new ArrayList<>();
    ArrayList<String> xArr = new ArrayList<>();
    Calendar myCalender;
    int sel_year0, sel_year1, sel_year2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_school_exam, container, false);
        spinner0 = (MaterialSpinner) v.findViewById(R.id.spinner0);
        spinner11 = (MaterialSpinner) v.findViewById(R.id.spinner11);
        spinner12 = (MaterialSpinner) v.findViewById(R.id.spinner12);
        spinner21 = (MaterialSpinner) v.findViewById(R.id.spinner21);
        spinner22 = (MaterialSpinner) v.findViewById(R.id.spinner22);
        spinner_year0 = (MaterialSpinner) v.findViewById(R.id.spinner_year0);
        spinner_year1 = (MaterialSpinner) v.findViewById(R.id.spinner_year1);
        spinner_year2 = (MaterialSpinner) v.findViewById(R.id.spinner_year2);

        courseList.add("All");
        for (Course course:App.school_courses) {
            courseList.add(course.name + " x " + course.coef);
        }
        periodList = Arrays.asList("per Exam", "per Month", "per Quarter");
        yearList = Arrays.asList("2021","2022","2023","2024","2025","2026","2027","2028","2029","2030");
        monthList = Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
        quartList = Arrays.asList("Q1", "Q2", "Q3", "Q4");
        spinner0.setItems(courseList);
        spinner11.setItems(periodList);
        spinner12.setItems(courseList);
        spinner21.setItems(periodList);
        spinner22.setItems(courseList);
        spinner_year0.setItems(yearList);
        spinner_year1.setItems(yearList);
        spinner_year2.setItems(yearList);
        myCalender = Calendar.getInstance();
        sel_year0 = myCalender.getTime().getYear();
        sel_year1 = myCalender.getTime().getYear();
        sel_year2 = myCalender.getTime().getYear();
        spinner_year0.setSelectedIndex(yearList.indexOf(String.valueOf((sel_year0-100)+2000)));
        spinner_year1.setSelectedIndex(yearList.indexOf(String.valueOf((sel_year1-100)+2000)));
        spinner_year2.setSelectedIndex(yearList.indexOf(String.valueOf((sel_year2-100)+2000)));
        spinner_year0.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                sel_year0 = Integer.valueOf(yearList.get(position))-2000+100;
                loadExams(0);
            }
        });
        spinner_year1.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                sel_year1 = Integer.valueOf(yearList.get(position))-2000+100;
                if (sel_student == null) {
                    return;
                }
                loadExams(1);
            }
        });
        spinner11.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (sel_student == null) {
                    return;
                }
                loadExams(1);
            }
        });
        spinner12.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (sel_student == null) {
                    return;
                }
                loadExams(1);
            }
        });
        spinner0.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                String _sel_course = courseList.get(position);
                for (int i = 0; i < 12; i++) {
                    examArr.set(i, 0.0f);
                }
                Utils.mDatabase.child(Utils.tbl_exam).orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                Exam exam = datas.getValue(Exam.class);
                                exam._id = datas.getKey();
                                if (!exam.school_id.equals(Utils.currentSchool._id)) continue;
                                if (sel_year0 != exam.date.getYear()) continue;
                                if (_sel_course.equals("All")) {
                                    examArr.set(exam.date.getMonth(), examArr.get(exam.date.getMonth())+1);
                                } else if (_sel_course.equals(exam.course_name + " x " + exam.course_coef)) {
                                    examArr.set(exam.date.getMonth(), examArr.get(exam.date.getMonth())+1);
                                }
                            }
                            initChart(chartView, initGraphData(examArr));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btn_student = v.findViewById(R.id.btn_student);
        btn_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddStudentDialog();
            }
        });

//        btn_course = v.findViewById(R.id.btn_course);
//        btn_course.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openAddCourseDialog();
//            }
//        });
//        btn_class = v.findViewById(R.id.btn_class);
//        btn_class.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openAddClassDialog();
//            }
//        });
        img_student = v.findViewById(R.id.img_student);
        img_teacher = v.findViewById(R.id.img_teacher);
        txt_teacher = v.findViewById(R.id.txt_teacher);
        chartView = (NChart) v.findViewById(R.id.chartView);
        chartViewStudent = (NChart) v.findViewById(R.id.chartViewStudent);
        chartViewTeacher = (NChart) v.findViewById(R.id.chartViewTeacher);
        chartViewClass = (NChart) v.findViewById(R.id.chartViewClass);
        ly_month = v.findViewById(R.id.ly_month);
        ly_quarter = v.findViewById(R.id.ly_quarter);
//        initMonthlyTop(); initQuarterlyTop();




        Button btn_exam = v.findViewById(R.id.btn_exam);
        btn_exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ExamActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        for (int i = 0; i < Utils.currentSchool.students.size(); i++) {
            Student student = Utils.currentSchool.students.get(i);
            if (!student.isAllow) continue;
            st_array.add(i, Utils.currentSchool.students.get(i).uid);
            m_array.add(i, 0.0f);
            q_array.add(i, 0.0f);
        }
        loadExams(0);
        read_students();
        return v;
    }
    void loadExams(int type) {
        for (int i = 0; i < 12; i++) {
            intArray[i] = 0;
        }
        examArr = new ArrayList<>();
        xArr = new ArrayList<>();
        int n = 0;
        int index11 = spinner11.getSelectedIndex();
        int index21 = spinner21.getSelectedIndex();
        switch (type) {
            case 0:
                n = 12;
                xArr = new ArrayList<String>(monthList);
                break;
            case 1:
                if (index11 == 1) {
                    n = 12;
                    xArr = new ArrayList<String>(monthList);
                } else if (index11 == 2) {
                    n = 4;
                    xArr = new ArrayList<String>(quartList);
                } else if (index11 == 0) {
                    n = 0;
                }
            case 2:
                if (index11 == 1) {
                    n = 12;
                } else if (index11 == 2) {
                    n = 4;
                } else if (index11 == 0) {
                    n = 0;
                }
                break;

        }
        for (int i = 0; i < n; i++) {
            examArr.add(0.0f);
        }
        int month = myCalender.get(Calendar.MONTH);
        Utils.mDatabase.child(Utils.tbl_exam).orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        Exam exam = datas.getValue(Exam.class);
                        exam._id = datas.getKey();
                        if (!exam.school_id.equals(Utils.currentSchool._id)) continue;

                        int _month = exam.date.getMonth();
                        int _date = exam.date.getDate();
                        if (type == 0) {
                            if (sel_year0 != exam.date.getYear()) continue;
                            examArr.set(_month, examArr.get(_month)+1);
                            for (int j = 0; j < st_array.size(); j++) {
                                if (exam.uid.equals(st_array.get(j))) {
                                    if (month == exam.date.getMonth()) {
                                        float val = (float) exam.num1 / exam.num2;
                                        if (m_array.get(j) > 0) {
                                            m_array.set(j, (m_array.get(j) + val) / 2);
                                        } else {
                                            m_array.set(j, (m_array.get(j) + val));
                                        }
                                    }
                                    if (month >= 0 && month < 3) {
                                        if (_month >= 0 && _month < 3) {
                                            float val = (float) exam.num1 / exam.num2;
                                            if (q_array.get(j) > 0) {
                                                q_array.set(j, (q_array.get(j) + val) / 2);
                                            } else {
                                                q_array.set(j, (q_array.get(j) + val));
                                            }
                                        }
                                    }
                                    if (month >= 3 && month < 6) {
                                        if (_month >= 3 && _month < 6) {
                                            float val = (float) exam.num1 / exam.num2;
                                            if (q_array.get(j) > 0) {
                                                q_array.set(j, (q_array.get(j) + val) / 2);
                                            } else {
                                                q_array.set(j, (q_array.get(j) + val));
                                            }
                                        }
                                    }
                                    if (month >= 6 && month < 9) {
                                        if (_month >= 6 && _month < 9) {
                                            float val = (float) exam.num1 / exam.num2;
                                            if (q_array.get(j) > 0) {
                                                q_array.set(j, (q_array.get(j) + val) / 2);
                                            } else {
                                                q_array.set(j, (q_array.get(j) + val));
                                            }
                                        }
                                    }
                                    if (month >= 9 && month < 12) {
                                        if (_month >= 9 && _month < 12) {
                                            float val = (float) exam.num1 / exam.num2;
                                            if (q_array.get(j) > 0) {
                                                q_array.set(j, (q_array.get(j) + val) / 2);
                                            } else {
                                                q_array.set(j, (q_array.get(j) + val));
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (type == 1) {
                            if (sel_year1 != exam.date.getYear()) continue;
                            if (!exam.uid.equals(sel_student._id)) {
                                continue;
                            }
                            String _sel_course = courseList.get(spinner12.getSelectedIndex());
                            if (!_sel_course.equals("All")) {
                                if (!_sel_course.equals(exam.course_name + " x " + exam.course_coef)) {
                                    continue;
                                }
                            }
                            float res = (float) exam.num1 / exam.num2;
                            int index = spinner11.getSelectedIndex();
                            switch (index) {
                                case 0:
                                    examArr.add(res);
                                    xArr.add(String.valueOf(_month+1) + "/" + String.valueOf(_date));
                                    break;
                                case 1:
                                    if (examArr.get(_month) == 0) {
                                        examArr.set(_month, res);
                                    } else {
                                        examArr.set(_month, (examArr.get(_month)+res)/2);
                                    }
                                    break;
                                case 2:
                                    if (_month >= 0 && _month < 3) {
                                        if (examArr.get(0) == 0) {
                                            examArr.set(0, res);
                                        } else {
                                            examArr.set(0, (examArr.get(0)+res)/2);
                                        }
                                    }
                                    if (_month >= 3 && _month < 6) {
                                        if (examArr.get(1) == 0) {
                                            examArr.set(1, res);
                                        } else {
                                            examArr.set(1, (examArr.get(1)+res)/2);
                                        }
                                    }
                                    if (_month >= 6 && _month < 9) {
                                        if (examArr.get(2) == 0) {
                                            examArr.set(2, res);
                                        } else {
                                            examArr.set(2, (examArr.get(2)+res)/2);
                                        }
                                    }
                                    if (_month >= 9 && _month < 12) {
                                        if (examArr.get(3) == 0) {
                                            examArr.set(3, res);
                                        } else {
                                            examArr.set(3, (examArr.get(3)+res)/2);
                                        }
                                    }
                                    break;

                                default:
                            }
                        } else if (type == 2) {
//                            if (!exam.course_name.equals(sel_course.name) || !exam.course_coef.equals(sel_course.coef)) {
//                                continue;
//                            }
//                            float res = (float) exam.num1 / exam.num2;
//                            if (intArray[_month] == 0) {
//                                intArray[_month] += res;
//                            } else {
//                                intArray[_month] += res;
//                                intArray[_month] /= 2;
//                            }
                        } else if (type == 3) {
//                            for (Course course : sel_class.courses) {
//                                if (course.name.equals(exam.course_name) && course.coef.equals(exam.course_coef)) {
//                                    float res = (float) exam.num1 / exam.num2;
//                                    if (intArray[_month] == 0) {
//                                        intArray[_month] += res;
//                                    } else {
//                                        intArray[_month] += res;
//                                        intArray[_month] /= 2;
//                                    }
//                                    break;
//                                }
//                            }
                        }

                    }
                }
                if (type == 0) {
                    initChart(chartView, initGraphData(examArr));
                    ArrayList<String> tmpArr = new ArrayList<>(st_array);
                    m_sorted = sortWithIndex(st_array,m_array);
                    q_sorted = sortWithIndex(tmpArr,q_array);
                    initMonthlyTop();
                    initQuarterlyTop();
                } else if (type == 1) {
                    try {
                        initChart(chartViewStudent, initGraphData(examArr));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (type == 2) {
//                    initChart(chartViewCourse, initGraphData(intArray));
                } else if (type == 3) {
//                    initChart(chartViewClass, initGraphData(intArray));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<String> sortWithIndex (ArrayList<String> strArr, ArrayList<Float> intIndex )
    {
        if (! isSorted(intIndex)){
            Collections.sort(strArr, Comparator.comparing(s -> intIndex.get(strArr.indexOf(s))));
        }
        return strArr;
    }

    public static boolean isSorted(ArrayList<Float> arr) {
        for (int i = 0; i < arr.size() - 1; i++) {
            if (arr.get(i + 1) < arr.get(i)) {
                return false;
            };
        }
        return true;
    }

    void initMonthlyTop() {
        final LayoutInflater inflater = LayoutInflater.from(activity);
        ly_month.removeAllViews();
        for (int i = m_sorted.size(); i > 0; i--) {
            Utils.mDatabase.child(Utils.tbl_user).orderByKey().equalTo(m_sorted.get(i-1))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                User user = childSnapshot.getValue(User.class);
                                View view1 = inflater.inflate(R.layout.cell_user_simple, null);
                                ImageView img_photo = view1.findViewById(R.id.img_photo);
                                TextView txt_name = view1.findViewById(R.id.txt_name);
                                txt_name.setText(user.name);
                                try {
                                    Glide.with(activity).load(user.photo).apply(new RequestOptions()
                                            .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                ly_month.addView(view1);
                                if (ly_month.getChildCount() >= 3) break;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w( "loadPost:onCancelled", databaseError.toException());
                        }
                    });

        }

    }
    void initQuarterlyTop() {
        final LayoutInflater inflater = LayoutInflater.from(activity);
        ly_quarter.removeAllViews();
        for (int i = q_sorted.size(); i > 0; i--) {
            Utils.mDatabase.child(Utils.tbl_user).orderByKey().equalTo(q_sorted.get(i-1))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                User user = childSnapshot.getValue(User.class);
                                View view1 = inflater.inflate(R.layout.cell_user_simple, null);
                                ImageView img_photo = view1.findViewById(R.id.img_photo);
                                TextView txt_name = view1.findViewById(R.id.txt_name);
                                txt_name.setText(user.name);
                                try {
                                    Glide.with(activity).load(user.photo).apply(new RequestOptions()
                                            .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ly_quarter.addView(view1);
                                if (ly_quarter.getChildCount() >= 3) break;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w( "loadPost:onCancelled", databaseError.toException());
                        }
                    });

        }

    }
    void initChart(NChart nChart, List<NExcel> data) {
        if (data.size() == 0) {
            nChart.setVisibility(View.INVISIBLE);
            return;
        } else {
            nChart.setVisibility(View.VISIBLE);
        }
        nChart.setBarWidth(40);
        nChart.setInterval(10);
        nChart.setAbove(0);
        nChart.setSelectedModed(NChart.SelectedMode.selecetdMsgShow);
        nChart.setBarStanded(7);
        nChart.setNormalColor(getResources().getColor(R.color.colorPrimary));
        nChart.cmdFill(data);
    }
    private List<NExcel> initGraphData(ArrayList<Float> array) {
        List<NExcel> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            list.add(new NExcel(array.get(i), xArr.get(i)));
        }

        return list;
    }
    private void updateGraphData(List<NExcel> data, int month, int count) {
        for (NExcel nExcel:data) {
            if (nExcel.getXmsg().equals(String.valueOf(month))) {
                nExcel.setNum(count);
                nExcel.setHeight(count);
                nExcel.setUpper(count);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
    public void read_students() {

        array_user.clear();
        for (Student student: Utils.currentSchool.students) {
            if (student.isAllow) {
                Utils.mDatabase.child(Utils.tbl_user).child(student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            User user = dataSnapshot.getValue(User.class);
                            user._id = dataSnapshot.getKey();
                            array_user.add(user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            }
        }
    }
    public void openAddStudentDialog() {
        final Dialog dlg = new Dialog(activity);
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
        TextView txt_title = dlg.findViewById(R.id.txt_title);
        txt_title.setText(getResources().getString(R.string.select_students));
        UserListAdapter userListAdapter = new UserListAdapter(activity, array_user);
        listView.setAdapter(userListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sel_student = array_user.get(i);
                dlg.dismiss();
                btn_student.setText(sel_student.name);
                try {
                    Glide.with(activity).load(sel_student.photo).apply(new RequestOptions()
                            .placeholder(R.drawable.default_pic).centerCrop().dontAnimate()).into(img_student);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                loadExams(1);
            }
        });
        Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setVisibility(View.GONE);
        dlg.show();
    }
    /*
    public void openAddCourseDialog() {
        final Dialog dlg = new Dialog(activity);
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
        final ArrayList<Course> array_course_sel = new ArrayList<>();
        final SchoolCourseListAdapter schoolCourseListAdapter = new SchoolCourseListAdapter(activity, App.school_courses, null);
        schoolCourseListAdapter.flag_timeslot = true;
        listView.setAdapter(schoolCourseListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sel_course = App.school_courses.get(i);
                btn_course.setText(sel_course.name + " x " + sel_course.coef);
                dlg.dismiss();
                loadExams(2);
                img_teacher.setImageResource(R.drawable.default_user);
                txt_teacher.setText("");
                for (Teacher teacher:Utils.currentSchool.teachers) {
                    if (teacher.courses.contains(sel_course.name)) {
                        Utils.mDatabase.child(Utils.tbl_user).child(teacher.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    User user = dataSnapshot.getValue(User.class);
                                    user._id = dataSnapshot.getKey();
                                    txt_teacher.setText(user.name);
                                    try {
                                        Glide.with(activity).load(user.photo).apply(new RequestOptions()
                                                .placeholder(R.drawable.default_pic).centerCrop().dontAnimate()).into(img_teacher);
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("The read failed: " + databaseError.getCode());
                            }
                        });
                        break;
                    }
                }

            }
        });
        Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setVisibility(View.GONE);
        dlg.show();
    }
    public void openAddClassDialog() {
        final Dialog dlg = new Dialog(activity);
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
        TextView txt_title = dlg.findViewById(R.id.txt_title);
        txt_title.setText(getResources().getString(R.string.choose_class));
        ClassListAdapter classListAdapter = new ClassListAdapter(activity, Utils.currentSchool.classes);
        classListAdapter.sel_index = -2;
        listView.setAdapter(classListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sel_class = Utils.currentSchool.classes.get(i);
                dlg.dismiss();
                btn_class.setText(sel_class.name);
                loadExams(3);
            }
        });
        Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setVisibility(View.GONE);
        dlg.show();
    }
    */

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == 0) {
            loadExams(0);
            read_students();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}