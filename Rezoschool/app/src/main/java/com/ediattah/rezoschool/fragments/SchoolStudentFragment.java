package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.StudentAcceptedListAdapter;
import com.ediattah.rezoschool.adapter.StudentWaitingListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.StudentDetailActivity;

import java.util.ArrayList;


public class SchoolStudentFragment extends Fragment {
    MainActivity activity;
    Button btn_accepted, btn_waiting;
    boolean flag_accepted = true;
    ListView listView;
    ArrayList<Student> array_student_accepted = new ArrayList<>();
    ArrayList<Student> array_student_waiting = new ArrayList<>();
    StudentAcceptedListAdapter studentAcceptedListAdapter;
    StudentWaitingListAdapter studentWaitingListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_school_student, container, false);
        btn_accepted = v.findViewById(R.id.btn_accepted);
        btn_waiting = v.findViewById(R.id.btn_waiting);
        btn_accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_waiting.setTextColor(Color.parseColor("#d0d0d0"));
                btn_accepted.setTextColor(Color.parseColor("#ffffff"));
                flag_accepted = true;
                studentAcceptedListAdapter = new StudentAcceptedListAdapter(activity, SchoolStudentFragment.this, array_student_accepted);
                listView.setAdapter(studentAcceptedListAdapter);
            }
        });
        btn_waiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_accepted.setTextColor(Color.parseColor("#d0d0d0"));
                btn_waiting.setTextColor(Color.parseColor("#ffffff"));
                flag_accepted = false;
                studentWaitingListAdapter = new StudentWaitingListAdapter(activity, SchoolStudentFragment.this, array_student_waiting);
                listView.setAdapter(studentWaitingListAdapter);
            }
        });

//        array_student_accepted.add(new Student(1, 1, 1, 1, true, true));
//        array_student_accepted.add(new Student(2, 2, 1, 1, true, true));
//        array_student_accepted.add(new Student(3, 3, 1, 1, true, true));
//        array_student_accepted.add(new Student(4, 4, 1, 1, true, true));
//        array_student_accepted.add(new Student(5, 5, 1, 1, true, true));
//
//        array_student_waiting.add(new Student(6, 5, 1, 1, true, true));


        listView = v.findViewById(R.id.listView);
        studentAcceptedListAdapter = new StudentAcceptedListAdapter(activity, this, array_student_accepted);
        listView.setAdapter(studentAcceptedListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (flag_accepted) {
                    Intent intent = new Intent(activity, StudentDetailActivity.class);
                    intent.putExtra("OBJECT", array_student_accepted.get(i));
                    startActivity(intent);
                } else {

                }
            }
        });
        return v;
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