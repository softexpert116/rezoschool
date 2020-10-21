package com.ediattah.rezoschool.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.Exam;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.Model.Tweet;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.adapter.UserListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewCourseActivity;
import com.ediattah.rezoschool.ui.NewSyllabusActivity;
import com.ediattah.rezoschool.ui.StudentDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ExamFragment extends Fragment {
    MainActivity mainActivity;
    ArrayList<User> array_user = new ArrayList<>();
    User sel_user;
    EditText edit_student, edit_course, edit_title, edit_date, edit_num1, edit_num2, edit_coef;
//    Course sel_course;
    Date sel_date;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exam, container, false);
        App.hideKeyboard(mainActivity);
        edit_student = v.findViewById(R.id.edit_student);
        edit_course = v.findViewById(R.id.edit_course);
        edit_coef = v.findViewById(R.id.edit_coef);
        edit_title = v.findViewById(R.id.edit_title);
        edit_date = v.findViewById(R.id.edit_date);
        edit_num1 = v.findViewById(R.id.edit_num1);
        edit_num2 = v.findViewById(R.id.edit_num2);
        Button btn_view = v.findViewById(R.id.btn_view);
        Button btn_update = v.findViewById(R.id.btn_update);
        edit_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddStudentDialog();
            }
        });
        edit_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddCourseDialog();
            }
        });
        sel_date = Calendar.getInstance().getTime();
        edit_date.setText(new SimpleDateFormat(App.DATE_FORMAT).format(sel_date));
        edit_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar myCalender = Calendar.getInstance();
                int year = myCalender.get(Calendar.YEAR);
                int month = myCalender.get(Calendar.MONTH);
                int date = myCalender.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        if (datePicker.isShown()) {
                            myCalender.set(Calendar.YEAR, i);
                            myCalender.set(Calendar.MONTH, i1);
                            myCalender.set(Calendar.DAY_OF_MONTH, i2);
                            sel_date = myCalender.getTime();
                            edit_date.setText(new SimpleDateFormat(App.DATE_FORMAT).format(sel_date));
                        }
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(mainActivity, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myDateListener, year, month, date);
                datePickerDialog.setTitle(getResources().getString(R.string.choose_date));
                datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                datePickerDialog.show();
            }
        });
        if (Utils.currentSchool._id.equals("")) {
            Utils.showAlert(mainActivity, getResources().getString(R.string.warning), getResources().getString(R.string.please_select_school_in_school_menu));
        }
        read_students();
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sel_user == null || edit_course.getText().toString().length() == 0 || sel_date == null || edit_title.getText().toString().trim().length() == 0 || edit_num1.getText().toString().trim().length() == 0 || edit_num2.getText().toString().trim().length() == 0) {
                    Utils.showAlert(mainActivity, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                } else {
                    String title = edit_title.getText().toString().trim();
                    String num1 = edit_num1.getText().toString().trim();
                    String num2 = edit_num2.getText().toString().trim();
                    String course_name = edit_course.getText().toString().trim();
                    String course_coef = edit_coef.getText().toString().trim();

                    Utils.mDatabase.child(Utils.tbl_exam).orderByChild("school_id").equalTo(Utils.currentSchool._id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!=null) {
                                for (DataSnapshot datas:dataSnapshot.getChildren()) {
                                    Exam exam = datas.getValue(Exam.class);
                                    exam._id = datas.getKey();
                                    assert exam != null;
                                    if (exam.uid.equals(sel_user._id)) {
                                        String date_str = Utils.getDateString(exam.date);
                                        String date = Utils.getDateString(sel_date);
                                        if (date_str.equals(date)) {
                                            Utils.mDatabase.child(Utils.tbl_exam).child(exam._id).child("num1").setValue(Integer.valueOf(num1));
                                            Utils.mDatabase.child(Utils.tbl_exam).child(exam._id).child("num2").setValue(Integer.valueOf(num2));
                                            Utils.mDatabase.child(Utils.tbl_exam).child(exam._id).child("course_name").setValue(course_name);
                                            Utils.mDatabase.child(Utils.tbl_exam).child(exam._id).child("course_coef").setValue(course_coef);
                                            Utils.mDatabase.child(Utils.tbl_exam).child(exam._id).child("title").setValue(title);
                                            Toast.makeText(mainActivity, getResources().getString(R.string.successfully_updated), Toast.LENGTH_SHORT).show();
                                            return;
                                        } else {
                                        }
                                    } else {
                                    }
                                }
                            }
                            Utils.mDatabase.child(Utils.tbl_exam).push().setValue(new Exam("", sel_date, Integer.valueOf(num1), Integer.valueOf(num2), Utils.currentSchool._id, sel_user._id, course_name, course_coef, title));
                            Toast.makeText(mainActivity, getResources().getString(R.string.successfully_updated), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sel_user == null || sel_date == null) {
                    Utils.showAlert(mainActivity, getResources().getString(R.string.warning), getResources().getString(R.string.please_select_student_and_date));
                } else {
                    Utils.mDatabase.child(Utils.tbl_exam).orderByChild("uid").equalTo(sel_user._id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                Boolean flag = false;
                                for(DataSnapshot datas: dataSnapshot.getChildren()){
                                    Exam exam = datas.getValue(Exam.class);
                                    String date = Utils.getDateString(sel_date);
                                    String exam_date = Utils.getDateString(exam.date);
                                    if (date.equals(exam_date) && exam.school_id.equals(Utils.currentSchool._id)) {
                                        edit_num1.setText(String.valueOf(exam.num1));
                                        edit_num2.setText(String.valueOf(exam.num2));
                                        edit_course.setText(exam.course_name);
                                        edit_coef.setText(exam.course_coef);
                                        edit_title.setText(exam.title);
                                        flag = true;
                                        break;
                                    }
                                }
                                if (!flag) {
                                    edit_num1.setText("");
                                    edit_num2.setText("");
                                    edit_course.setText("");
                                    edit_coef.setText("");
                                    edit_title.setText("");
                                    Toast.makeText(mainActivity, getResources().getString(R.string.no_exam_result), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                }

            }
        });
        return v;
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
        final Dialog dlg = new Dialog(mainActivity);
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
        UserListAdapter userListAdapter = new UserListAdapter(mainActivity, array_user);
        listView.setAdapter(userListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sel_user = array_user.get(i);
                dlg.dismiss();
                edit_student.setText(sel_user.name);
            }
        });
        Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setVisibility(View.GONE);
        dlg.show();
    }
    public void openAddCourseDialog() {
        final Dialog dlg = new Dialog(mainActivity);
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
        final SchoolCourseListAdapter schoolCourseListAdapter = new SchoolCourseListAdapter(mainActivity, App.school_courses, null);
        schoolCourseListAdapter.flag_timeslot = true;
        listView.setAdapter(schoolCourseListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Course sel_course = App.school_courses.get(i);
                edit_course.setText(sel_course.name);
                edit_coef.setText(sel_course.coef);
                dlg.dismiss();

            }
        });
        Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setVisibility(View.GONE);
        dlg.show();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }
}