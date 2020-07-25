package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.adapter.TeacherListAdapter;
import com.ediattah.rezoschool.adapter.UserListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolAddTeacherActivity extends AppCompatActivity {
    ArrayList<Course> array_course_sel = new ArrayList<>();
    SchoolCourseListAdapter schoolCourseListAdapter;
    EditText edit_course, edit_teacher;
    ArrayList<User> array_user = new ArrayList<>();
    User sel_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_add_teacher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add New Teacher");
        App.hideKeyboard(this);
        edit_teacher = findViewById(R.id.edit_teacher);
        edit_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddTeacherDialog();
            }
        });
        edit_course = findViewById(R.id.edit_course);
        edit_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddCourseDialog();
            }
        });
        Button btn_create = findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courses = edit_course.getText().toString();
                if (edit_teacher.getText().toString().length()*courses.length() == 0) {
                    Utils.showAlert(SchoolAddTeacherActivity.this, "Warning", "Please fill in the blank field");
                    return;
                }
                Teacher teacher = new Teacher(sel_user._id, courses);
                Utils.currentSchool.teachers.add(teacher);
                Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("teachers").setValue(Utils.currentSchool.teachers);
                Toast.makeText(SchoolAddTeacherActivity.this, "Successfully created!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        read_user_teacher();
    }
    void read_user_teacher() {
        Utils.mDatabase.child(Utils.tbl_user).orderByChild(Utils.USER_TYPE).equalTo(Utils.TEACHER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                array_user.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        User user = datas.getValue(User.class);
                        user._id = datas.getKey();
                        boolean flag = false;
                        for (Teacher teacher:Utils.currentSchool.teachers) {
                            if (teacher.uid.equals(user._id)) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            array_user.add(user);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void openAddTeacherDialog() {
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
        txt_title.setText("Choose Teacher");
        UserListAdapter userListAdapter = new UserListAdapter(this, array_user);
        listView.setAdapter(userListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = array_user.get(i);
                sel_user = user;
                edit_teacher.setText(user.name);
                dlg.dismiss();
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
        array_course_sel.clear();
        schoolCourseListAdapter = new SchoolCourseListAdapter(this, Utils.currentSchool.courses, array_course_sel);
        listView.setAdapter(schoolCourseListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Course course = Utils.currentSchool.courses.get(i);
                if (array_course_sel.contains(course)) {
                    array_course_sel.remove(course);
                } else {
                    array_course_sel.add(course);
                }
                schoolCourseListAdapter.notifyDataSetChanged();
            }
        });
        Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courses = "";
                for (Course course: array_course_sel) {
                    if (courses.length() == 0) {
                        courses += course.name;
                    } else {
                        courses += "," + course.name;
                    }
                }
                edit_course.setText(courses);
                dlg.dismiss();
            }
        });
        dlg.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}