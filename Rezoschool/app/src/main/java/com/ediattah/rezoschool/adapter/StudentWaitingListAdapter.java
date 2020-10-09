package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.fragments.SchoolStudentFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
        TextView txt_new = view.findViewById(R.id.txt_new);
        final TextView txt_name = view.findViewById(R.id.txt_name);
        Button btn_accept = view.findViewById(R.id.btn_accept);
        Button btn_reject = view.findViewById(R.id.btn_reject);
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                student.isAllow = true;
                for (int j = 0; j < Utils.currentSchool.students.size(); j++) {
                    Student student1 = Utils.currentSchool.students.get(j);
                    if (student1.uid.equals(student.uid)) {
                        Utils.currentSchool.students.set(j, student);
                        break;
                    }
                }
                Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("students").setValue(Utils.currentSchool.students);
                fragment.read_students();
            }
        });
        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.currentSchool.students.remove(student);
                Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("students").setValue(Utils.currentSchool.students);
                fragment.read_students();
            }
        });
        final ImageView img_photo = (ImageView)view.findViewById(R.id.img_photo);

        Utils.mDatabase.child(Utils.tbl_user).child(student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    User user = dataSnapshot.getValue(User.class);
                    txt_name.setText(user.name);
                    Glide.with(context).load(user.photo).apply(new RequestOptions()
                            .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        txt_class.setText(student.class_name);
        if (student.isNew) {
            txt_new.setText(context.getResources().getString(R.string._new));
            txt_new.setBackgroundColor(Color.parseColor("#c3edb9"));
        } else {
            txt_new.setText(context.getResources().getString(R.string.old));
            txt_new.setBackgroundColor(Color.parseColor("#e28e8e"));
        }
        return view;
    }
}
