package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.StudentAcceptedListAdapter;
import com.ediattah.rezoschool.ui.BulkSMSActivity;
import com.ediattah.rezoschool.ui.MainActivity;

import java.util.ArrayList;

public class AlumniFragment extends Fragment {
    MainActivity activity;
    ArrayList<Student> array_student_accepted = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_alumni, container, false);
        StudentAcceptedListAdapter studentAcceptedListAdapter = new StudentAcceptedListAdapter(activity,  array_student_accepted);
        ListView listView = (ListView)v.findViewById(R.id.listView);
//        array_student_accepted.add(new Student(1, 1, 1, 1, true, true));
//        array_student_accepted.add(new Student(2, 2, 1, 1, true, true));
//        array_student_accepted.add(new Student(3, 3, 1, 1, true, true));
//        array_student_accepted.add(new Student(4, 4, 1, 1, true, true));
//        array_student_accepted.add(new Student(5, 5, 1, 1, true, true));
        listView.setAdapter(studentAcceptedListAdapter);

        Button btn_bulk = (Button)v.findViewById(R.id.btn_bulk_sms);
        btn_bulk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, BulkSMSActivity.class);
                activity.startActivity(intent);
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