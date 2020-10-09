package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.ClassListAdapter;
import com.ediattah.rezoschool.adapter.SchoolListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseSchoolActivity extends AppCompatActivity {
    SchoolListAdapter schoolListAdapter;
    ClassListAdapter classListAdapter;
    ArrayList<School> array_school = new ArrayList();
    ArrayList<Class> array_class = new ArrayList();
    School sel_school = new School();
    ListView list_class;
    String parent_id;
    boolean isNew;
    Class sel_class = new Class();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_school);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        App.hideKeyboard(this);
        ListView list_school = findViewById(R.id.list_school);
        schoolListAdapter = new SchoolListAdapter(this, array_school);
        schoolListAdapter.flag_view = true;
        list_school.setAdapter(schoolListAdapter);
        list_school.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                schoolListAdapter.sel_index = i;
                schoolListAdapter.notifyDataSetChanged();
                sel_school = array_school.get(i);
                updateClassListView();
            }
        });

        list_class = findViewById(R.id.list_class);
        list_class.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                classListAdapter.sel_index = i;
                classListAdapter.notifyDataSetChanged();
                sel_class = sel_school.classes.get(i);
            }
        });

        Button btn_done = findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Student student = new Student(Utils.mUser.getUid(), parent_id, sel_school._id, sel_class.name, isNew, false);
                sel_school.students.add(student);
                Utils.currentSchool = sel_school;
                Utils.currentClass = sel_class;
                Utils.mDatabase.child(Utils.tbl_school).child(sel_school._id).setValue(sel_school);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.successfully_applied_to_school_) + sel_school.number, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        read_schools();
        get_parentId();
    }
    void get_parentId() {
        Utils.mDatabase.child(Utils.tbl_parent_student).orderByChild("student_id").equalTo(Utils.mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        parent_id = datas.child("parent_id").getValue(String.class);
                        isNew = datas.child("isNew").getValue(Boolean.class);
                    }
                }
                if (parent_id == null) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.there_is_no_parent_for_this_student), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void updateClassListView() {
        classListAdapter = new ClassListAdapter(this, sel_school.classes);
        classListAdapter.sel_index = 0;
        list_class.setAdapter(classListAdapter);
        sel_class = sel_school.classes.get(0);
    }
    void read_schools() {
        array_school.clear();
        Utils.mDatabase.child(Utils.tbl_school).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        School school = datas.getValue(School.class);
                        school._id = datas.getKey();
                        array_school.add(school);
                    }
                }
                schoolListAdapter.notifyDataSetChanged();
                if (array_school.size() > 0) {
                    sel_school = array_school.get(0);
                    updateClassListView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}