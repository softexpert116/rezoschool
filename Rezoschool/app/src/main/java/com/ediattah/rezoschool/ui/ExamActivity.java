package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.ClassListAdapter;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.adapter.UserListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ExamActivity extends AppCompatActivity {
    ArrayList<User> array_user = new ArrayList<>();
    User sel_user;
    Class sel_class;
    Course sel_course;
    EditText edit_class, edit_student, edit_course, edit_title, edit_date, edit_num1, edit_num2, edit_coef;
    //    Course sel_course;
    Date sel_date;
    String parent_mobile = "", school_name = "", title = "";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.view_exams));
        progressDialog = new ProgressDialog(this);

        App.hideKeyboard(this);
        edit_class = findViewById(R.id.edit_class);
        edit_student = findViewById(R.id.edit_student);
        edit_course = findViewById(R.id.edit_course);
        edit_coef = findViewById(R.id.edit_coef);
        edit_title = findViewById(R.id.edit_title);
        edit_date = findViewById(R.id.edit_date);
        edit_num1 = findViewById(R.id.edit_num1);
        edit_num2 = findViewById(R.id.edit_num2);
        Button btn_view = findViewById(R.id.btn_view);
        Button btn_update = findViewById(R.id.btn_update);
        edit_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddClassDialog();
            }
        });
        edit_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sel_class == null) {
                    return;
                }
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(ExamActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myDateListener, year, month, date);
                datePickerDialog.setTitle(getResources().getString(R.string.choose_date));
                datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                datePickerDialog.show();
            }
        });
        if (Utils.currentSchool._id.equals("")) {
            Utils.showAlert(this, getResources().getString(R.string.warning), getResources().getString(R.string.please_select_school_in_school_menu));
        }
//        read_students();
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sel_user == null || edit_course.getText().toString().length() == 0 || sel_date == null || edit_title.getText().toString().trim().length() == 0 || edit_num1.getText().toString().trim().length() == 0 || edit_num2.getText().toString().trim().length() == 0) {
                    Utils.showAlert(ExamActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                } else {
                    progressDialog.setMessage("Processing...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    title = edit_title.getText().toString().trim();
                    String num1 = edit_num1.getText().toString().trim();
                    String num2 = edit_num2.getText().toString().trim();
                    String course_name = edit_course.getText().toString().trim();
                    String course_coef = edit_coef.getText().toString().trim();

                    Utils.mDatabase.child(Utils.tbl_exam).orderByChild("school_id").equalTo(Utils.currentSchool._id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            progressDialog.dismiss();
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
                                            Toast.makeText(ExamActivity.this, getResources().getString(R.string.successfully_updated), Toast.LENGTH_SHORT).show();
                                            sendExamResult(exam);
                                            return;
                                        } else {
                                        }
                                    } else {
                                    }
                                }
                            }
                            Exam exam = new Exam("", sel_date, Integer.valueOf(num1), Integer.valueOf(num2), Utils.currentSchool.uid, sel_user._id, course_name, course_coef, title, sel_date.getTime());
                            Utils.mDatabase.child(Utils.tbl_exam).push().setValue(exam);
                            Toast.makeText(ExamActivity.this, getResources().getString(R.string.successfully_updated), Toast.LENGTH_SHORT).show();
                            sendExamResult(exam);
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
                    Utils.showAlert(ExamActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_select_student_and_date));
                } else {
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Utils.mDatabase.child(Utils.tbl_exam).orderByChild("uid").equalTo(sel_user._id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressDialog.dismiss();
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
                                    Toast.makeText(ExamActivity.this, getResources().getString(R.string.no_exam_result), Toast.LENGTH_SHORT).show();
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
    }
    void sendExamResult(Exam exam) {

        Utils.mDatabase.child(Utils.tbl_user).child(Utils.currentSchool.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            User school = dataSnapshot.getValue(User.class);
                            school_name = school.name;
                            String text = "";
                            text = "Exam Alert of " + sel_user.name + "\n" + "School: " + school_name + "\n" + "Class: " + sel_class.name + "\n" + "Course: " + sel_course.name + "x" + sel_course.coef + "\n" +
                                    "Title: " + title + "\n" +  "Exam Result: " + exam.num1 + "/" + exam.num2 + "\n" + "Date: " + Utils.getDateString(exam.date);
                            ArrayList<String> array_phone = new ArrayList<>();
                            array_phone.add(parent_mobile);
//                            array_phone.add("2250505632490");
                            App.sendEdiaSMS(ExamActivity.this, Utils.currentUser.username, Utils.currentUser.password, Utils.currentUser.senderID, array_phone, text, "text");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                    }
                });
    }
    public void read_students() {
        if (sel_class == null) {
            return;
        }
        array_user.clear();
        for (Student student: Utils.currentSchool.students) {
            if (student.isAllow) {
                if (!student.class_name.equals(sel_class.name)) {
                    continue;
                }
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
    public void openAddClassDialog() {
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
        TextView txt_title = dlg.findViewById(R.id.txt_title);
        txt_title.setText(getResources().getString(R.string.choose_class));
        ClassListAdapter classListAdapter = new ClassListAdapter(this, Utils.currentSchool.classes);
        classListAdapter.sel_index = -2;
        listView.setAdapter(classListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sel_class = Utils.currentSchool.classes.get(i);
                dlg.dismiss();
                edit_class.setText(sel_class.name);
                sel_user = null;
                edit_student.setText("");
                read_students();
            }
        });
        Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setVisibility(View.GONE);
        dlg.show();
    }
    public void openAddStudentDialog() {
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
        TextView txt_title = dlg.findViewById(R.id.txt_title);
        txt_title.setText(getResources().getString(R.string.select_students));
        UserListAdapter userListAdapter = new UserListAdapter(this, array_user);
        listView.setAdapter(userListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sel_user = array_user.get(i);
                dlg.dismiss();
                edit_student.setText(sel_user.name);
                // get parent info from student ID
                Utils.mDatabase.child(Utils.tbl_parent_student).orderByChild("student_id").equalTo(sel_user._id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            String parent_id = childSnapshot.child("parent_id").getValue(String.class);
                            Utils.mDatabase.child(Utils.tbl_user).child(parent_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        User parent = dataSnapshot.getValue(User.class);
                                        parent_mobile = parent.phone;
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w( "loadPost:onCancelled", databaseError.toException());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }
        });
        Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setVisibility(View.GONE);
        dlg.show();
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
        final ArrayList<Course> array_course_sel = new ArrayList<>();
        final SchoolCourseListAdapter schoolCourseListAdapter = new SchoolCourseListAdapter(this, App.school_courses, null);
        schoolCourseListAdapter.flag_timeslot = true;
        listView.setAdapter(schoolCourseListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sel_course = App.school_courses.get(i);
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}