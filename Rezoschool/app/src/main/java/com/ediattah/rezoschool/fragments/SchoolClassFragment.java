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
import android.widget.Button;
import android.widget.ListView;

import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.Level;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SchoolLevelListAdapter;
import com.ediattah.rezoschool.ui.NewLevelActivity;
import com.ediattah.rezoschool.ui.TimeslotActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.ClassListAdapter;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewClassActivity;
import com.ediattah.rezoschool.ui.NewCourseActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolClassFragment extends Fragment {

    MainActivity activity;
    Button btn_class, btn_level;
    int flag = 2;
    ListView listView;
    ClassListAdapter classListAdapter;

    SchoolLevelListAdapter schoolLevelListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_class_school, container, false);
        btn_class = v.findViewById(R.id.btn_class);
        btn_level = v.findViewById(R.id.btn_level);
        setTabWithFlag(flag);

        classListAdapter = new ClassListAdapter(activity, Utils.currentSchool.classes);
        schoolLevelListAdapter = new SchoolLevelListAdapter(activity, Utils.currentSchool.levels);

        btn_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 0;
                setTabWithFlag(flag);
                classListAdapter = new ClassListAdapter(activity, Utils.currentSchool.classes);
                listView.setAdapter(classListAdapter);
            }
        });

        btn_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 2;
                setTabWithFlag(flag);
                listView.setAdapter(schoolLevelListAdapter);
            }
        });
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                switch (flag) {
                    case 0:
                        intent = new Intent(activity, NewClassActivity.class);
                        break;
                    default:
                        intent = new Intent(activity, NewLevelActivity.class);
                }
                activity.startActivity(intent);
            }
        });

        listView = v.findViewById(R.id.listView);
        listView.setAdapter(schoolLevelListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (flag == 2) {
                    Level sel_level = Utils.currentSchool.levels.get(i);
                    ArrayList<Class> array_class = new ArrayList<>();
                    for (Class _class:Utils.currentSchool.classes) {
                        if (_class.level.equals(sel_level.name)) {
                            array_class.add(_class);
                        }
                    }
                    classListAdapter = new ClassListAdapter(activity, array_class);
                    listView.setAdapter(classListAdapter);
                    flag = 0;
                    setTabWithFlag(flag);
                } else if (flag == 0) {
                    Class sel_class = classListAdapter.arrayList.get(i);
                    Intent intent = new Intent(activity, TimeslotActivity.class);
                    intent.putExtra("sel_class", sel_class);
                    activity.startActivity(intent);
                }
            }
        });
        return v;
    }
    void setTabWithFlag(int flag) {
        btn_level.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btn_class.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        if (flag == 0) {
            btn_level.setTextColor(Color.parseColor("#d0d0d0"));
            btn_class.setTextColor(getResources().getColor(R.color.colorText));
            btn_class.setBackgroundColor(getResources().getColor(R.color.colorMainBackground));
        } else {
            btn_class.setTextColor(Color.parseColor("#d0d0d0"));
            btn_level.setTextColor(getResources().getColor(R.color.colorText));
            btn_level.setBackgroundColor(getResources().getColor(R.color.colorMainBackground));
        }
    }

    void level_update_listener() {
        Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("levels").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Utils.currentSchool.levels.clear();
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        Level level = datas.getValue(Level.class);
                        Utils.currentSchool.levels.add(level);
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        schoolLevelListAdapter.arrayList = Utils.currentSchool.levels;
                        schoolLevelListAdapter.notifyDataSetChanged();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void class_update_listener() {
        Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("classes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Utils.currentSchool.classes.clear();
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        Class _class = datas.getValue(Class.class);
                        Utils.currentSchool.classes.add(_class);
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        classListAdapter.arrayList = Utils.currentSchool.classes;
                        classListAdapter.notifyDataSetChanged();
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
        class_update_listener();
        level_update_listener();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}