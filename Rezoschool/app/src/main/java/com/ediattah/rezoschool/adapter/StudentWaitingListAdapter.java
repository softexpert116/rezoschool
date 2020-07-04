package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.fragments.SchoolStudentFragment;

import java.util.ArrayList;

public class StudentWaitingListAdapter extends BaseAdapter {
    ArrayList<Student> arrayList;

    Context context;
    SchoolStudentFragment fragment;

    public StudentWaitingListAdapter(Context _context, SchoolStudentFragment _fragment, ArrayList<Student> _arrayList) {
        context = _context;
        fragment = _fragment;
        this.arrayList = _arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Student student = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_student_waiting, null);
        }
        TextView txt_class = view.findViewById(R.id.txt_class);
        TextView txt_name = view.findViewById(R.id.txt_name);
        Button btn_accept = view.findViewById(R.id.btn_accept);
        Button btn_reject = view.findViewById(R.id.btn_reject);
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
//        txt_name.setText(studentModel.user_id);
//        txt_class.setText(studentModel.class_id);
        return view;
    }
}
