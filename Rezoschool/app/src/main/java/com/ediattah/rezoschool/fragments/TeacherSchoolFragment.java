package com.ediattah.rezoschool.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.CourseTime;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.adapter.SchoolListAdapter;
import com.ediattah.rezoschool.adapter.TeacherCourseListAdapter;
import com.ediattah.rezoschool.ui.CourseDetailActivity;
import com.ediattah.rezoschool.ui.CourseStudentActivity;
import com.ediattah.rezoschool.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class TeacherSchoolFragment extends Fragment {
    MainActivity activity;
    ListView list_school, list_course;
    SchoolListAdapter schoolListAdapter;
    TeacherCourseListAdapter teacherCourseListAdapter;
    ArrayList<School> array_school = new ArrayList<>();
    ArrayList<Course> array_course = new ArrayList<>();
    TextView txt_school_course;

    public int sel_index = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_teacher_school, container, false);
        txt_school_course = v.findViewById(R.id.txt_school_course);
        Button btn_add_course = v.findViewById(R.id.btn_add_course);
        btn_add_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddCourseDialog(array_school.get(schoolListAdapter.sel_index));
            }
        });
        Button btn_add_school = v.findViewById(R.id.btn_add_school);
        btn_add_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddSchoolDialog();
            }
        });
        list_school = v.findViewById(R.id.list_school);
        list_course = v.findViewById(R.id.list_course);

        schoolListAdapter = new SchoolListAdapter(activity, array_school);
        list_school.setAdapter(schoolListAdapter);
        teacherCourseListAdapter = new TeacherCourseListAdapter(activity, array_course);
        list_course.setAdapter(teacherCourseListAdapter);
        list_school.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                schoolListAdapter.sel_index = i;
                Utils.currentSchool = array_school.get(i);
//                Toast.makeText(activity, "Item Clicked", Toast.LENGTH_SHORT).show();
                schoolListAdapter.notifyDataSetChanged();
                course_update_listener(array_school.get(i));
//                courseListAdapter.notifyDataSetChanged();
            }
        });
        list_course.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, CourseDetailActivity.class);
                intent.putExtra("COURSE", array_course.get(i));
                intent.putExtra("SCHOOL", array_school.get(schoolListAdapter.sel_index));
                startActivity(intent);
            }
        });
        school_update_listener();
        return v;
    }
    public void openAddCourseDialog(final School school) {
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
        final LinearLayout ly_no_items = dlg.findViewById(R.id.ly_no_items);
        TextView txt_title = dlg.findViewById(R.id.txt_title);
        txt_title.setText("Choose Courses");
        ListView listView = dlg.findViewById(R.id.listView);
        final ArrayList<Course> array_all_course = new ArrayList<>();
        final ArrayList<Course> array_course_sel = new ArrayList<>();
        final SchoolCourseListAdapter courseAdapter = new SchoolCourseListAdapter(activity, array_all_course, array_course_sel);
        listView.setAdapter(courseAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Course course = array_all_course.get(i);
                if (array_course_sel.contains(course)) {
                    array_course_sel.remove(course);
                } else {
                    array_course_sel.add(course);
                }
                courseAdapter.notifyDataSetChanged();
            }
        });
        final Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        for(Course course: school.courses){
            boolean flag = false;
            for (Course course1:array_course) {
                if (course1.name.equals(course.name)) {
                    flag = true;
                }
            }
            if (!flag) {
                array_all_course.add(course);
            }
        }
        activity.runOnUiThread(new Runnable() {
            public void run() {
                courseAdapter.arrayList = array_all_course;
                courseAdapter.notifyDataSetChanged();
                if (array_all_course.size() == 0) {
                    btn_choose.setEnabled(false);
                    ly_no_items.setVisibility(View.VISIBLE);
                } else {
                    btn_choose.setEnabled(true);
                    ly_no_items.setVisibility(View.GONE);
                }
            }
        });
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String add_courseStr = "";
                for (Course course: array_course) {
                    if (add_courseStr.length() == 0) {
                        add_courseStr += course.name;
                    } else {
                        add_courseStr += "," + course.name;
                    }
                }
                for (Course course: array_course_sel) {
                    if (add_courseStr.length() == 0) {
                        add_courseStr += course.name;
                    } else {
                        add_courseStr += "," + course.name;
                    }
                }
                final String finalAdd_courseStr = add_courseStr;
                Utils.mDatabase.child(Utils.tbl_school).child(school._id).child("teachers").orderByChild("uid").equalTo(Utils.mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                String key = datas.getKey();
                                Utils.mDatabase.child(Utils.tbl_school).child(school._id).child("teachers").child(key).child("courses").setValue(finalAdd_courseStr);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//                boolean flag = false;
//                for (School school1:array_school) {
//                    if (school._id.equals(school1._id)) {
//                        flag = true;
//                    }
//                }
//                if (!flag) {
//                    Teacher teacher = new Teacher(Utils.mUser.getUid(), "");
//                    school.teachers.add(teacher);
//                    Utils.mDatabase.child(Utils.tbl_school).child(school._id).child("teachers").setValue(school.teachers);
//                }
                dlg.dismiss();
            }
        });
        dlg.show();
    }
    public void openAddSchoolDialog() {
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
        TextView txt_title = dlg.findViewById(R.id.txt_title);
        txt_title.setText("Choose School");
        final LinearLayout ly_no_items = dlg.findViewById(R.id.ly_no_items);
        ListView listView = dlg.findViewById(R.id.listView);
        final ArrayList<School> array_all_school = new ArrayList<>();
        final SchoolListAdapter schoolAdapter = new SchoolListAdapter(activity, array_all_school);
        schoolAdapter.flag_view = true;
        listView.setAdapter(schoolAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                schoolAdapter.sel_index = i;
                schoolAdapter.notifyDataSetChanged();
            }
        });
        final Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        Utils.mDatabase.child(Utils.tbl_school).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    array_all_school.clear();
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        School school = datas.getValue(School.class);
                        school._id = datas.getKey();
                        boolean flag = false;
                        for (School school1:array_school) {
                            if (school1._id.equals(school._id)) {
                                flag = true;
                            }
                        }
                        if (!flag) {
                            array_all_school.add(school);
                        }
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        schoolAdapter.arrayList = array_all_school;
                        schoolAdapter.notifyDataSetChanged();
                        if (array_all_school.size() == 0) {
                            btn_choose.setEnabled(false);
                            ly_no_items.setVisibility(View.VISIBLE);
                        } else {
                            btn_choose.setEnabled(true);
                            ly_no_items.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                School school = array_all_school.get(schoolAdapter.sel_index);
                boolean flag = false;
                for (School school1:array_school) {
                    if (school._id.equals(school1._id)) {
                        flag = true;
                    }
                }
                if (!flag) {
                    Teacher teacher = new Teacher(Utils.mUser.getUid(), "");
                    school.teachers.add(teacher);
                    Utils.mDatabase.child(Utils.tbl_school).child(school._id).child("teachers").setValue(school.teachers);
                }
                dlg.dismiss();
            }
        });
        dlg.show();
    }
    void school_update_listener() {
        Utils.mDatabase.child(Utils.tbl_school).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                array_school.clear();
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        School school = datas.getValue(School.class);
                        school._id = datas.getKey();
                        for (Teacher teacher:school.teachers) {
                            if (teacher.uid.equals(Utils.mUser.getUid())) {
                                array_school.add(school);
                            }
                        }
                    }
                    if (array_school.size() > 0) {
                        Utils.currentSchool = array_school.get(0);
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        schoolListAdapter.arrayList = array_school;
                        if (schoolListAdapter.sel_index == array_school.size()) {
                            schoolListAdapter.sel_index = array_school.size()-1;
                        }
                        if (array_school.size() > 0) {
                            course_update_listener(array_school.get(schoolListAdapter.sel_index));
                        }
                        schoolListAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void course_update_listener(final School school) {
        txt_school_course.setText("My Courses in school " + school.number);
        Utils.mDatabase.child(Utils.tbl_school).child(school._id).child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                array_course.clear();
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        Course course = datas.getValue(Course.class);
                        for (Teacher teacher:school.teachers) {
                            if (teacher.uid.equals(Utils.mUser.getUid())) {
                                ArrayList<String> arrayStrList = new ArrayList<String>(Arrays.asList(teacher.courses.split(",")));
                                if (arrayStrList.contains(course.name)) {
                                    array_course.add(course);
                                }
                            }
                        }
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        teacherCourseListAdapter.arrayList = array_course;
                        teacherCourseListAdapter.notifyDataSetChanged();
                    }
                });
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