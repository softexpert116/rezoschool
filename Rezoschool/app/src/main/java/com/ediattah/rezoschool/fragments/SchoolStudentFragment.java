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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.StudentAcceptedListAdapter;
import com.ediattah.rezoschool.adapter.StudentWaitingListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.StudentDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
    TextView txt_waiting;
    LinearLayout ly_no_items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_school_student, container, false);
        txt_waiting = v.findViewById(R.id.txt_waiting);
        ly_no_items = v.findViewById(R.id.ly_no_items);
        btn_accepted = v.findViewById(R.id.btn_accepted);
        btn_waiting = v.findViewById(R.id.btn_waiting);
        btn_accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_waiting.setTextColor(Color.parseColor("#d0d0d0"));
                btn_accepted.setTextColor(getResources().getColor(R.color.colorText));
                btn_accepted.setBackgroundColor(getResources().getColor(R.color.colorMainBackground));
                btn_waiting.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                flag_accepted = true;
                listView.setAdapter(studentAcceptedListAdapter);
                read_students();
            }
        });
        btn_waiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_accepted.setTextColor(Color.parseColor("#d0d0d0"));
                btn_waiting.setTextColor(getResources().getColor(R.color.colorText));
                btn_accepted.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btn_waiting.setBackgroundColor(getResources().getColor(R.color.colorMainBackground));
                flag_accepted = false;
                listView.setAdapter(studentWaitingListAdapter);
                read_students();
            }
        });

        listView = v.findViewById(R.id.listView);
        studentAcceptedListAdapter = new StudentAcceptedListAdapter(activity, array_student_accepted);
        studentWaitingListAdapter = new StudentWaitingListAdapter(activity, this, array_student_waiting);
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
    public void read_students() {
        array_student_accepted.clear();
        array_student_waiting.clear();
        for (Student student:Utils.currentSchool.students) {
            if (student.isAllow) {
                array_student_accepted.add(student);
            } else {
                array_student_waiting.add(student);
            }
        }
        studentAcceptedListAdapter.notifyDataSetChanged();
        studentWaitingListAdapter.notifyDataSetChanged();
        if (array_student_waiting.size() > 0) {
            txt_waiting.setText(String.valueOf(array_student_waiting.size()));
            txt_waiting.setVisibility(View.VISIBLE);
        } else {
            txt_waiting.setVisibility(View.GONE);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        read_students();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}