package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.fragments.SchoolStudentFragment;

import java.util.ArrayList;

public class StudentAcceptedListAdapter extends BaseAdapter {
    ArrayList<Student> arrayList;

    Context context;
    SchoolStudentFragment fragment;

    public StudentAcceptedListAdapter(Context _context, SchoolStudentFragment _fragment, ArrayList<Student> _arrayList) {
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
            view = inflater.inflate(R.layout.cell_student_accepted, null);
        }
        TextView txt_class = view.findViewById(R.id.txt_class);
        TextView txt_name = view.findViewById(R.id.txt_name);
        ImageView img_chat = (ImageView)view.findViewById(R.id.img_chat);
        img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "click chat", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView img_sms = (ImageView)view.findViewById(R.id.img_sms);
        img_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "click sms", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView img_call = (ImageView)view.findViewById(R.id.img_call);
        img_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "click call", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView img_video = (ImageView)view.findViewById(R.id.img_video);
        img_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "click video", Toast.LENGTH_SHORT).show();
            }
        });
//        txt_name.setText(studentModel.user_id);
//        txt_class.setText(studentModel.class_id);
        return view;
    }
}
