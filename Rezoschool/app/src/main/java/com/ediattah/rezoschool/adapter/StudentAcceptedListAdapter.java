package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.fragments.SchoolStudentFragment;
import com.ediattah.rezoschool.ui.BulkSMSActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentAcceptedListAdapter extends BaseAdapter {
    public ArrayList<Student> arrayList;

    Context context;

    public StudentAcceptedListAdapter(Context _context, ArrayList<Student> _arrayList) {
        context = _context;
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
        final TextView txt_school = view.findViewById(R.id.txt_school);
        final TextView txt_name = view.findViewById(R.id.txt_name);
        final TextView txt_new = view.findViewById(R.id.txt_new);
        final ImageView img_photo = (ImageView)view.findViewById(R.id.img_photo);
        ImageView img_chat = (ImageView)view.findViewById(R.id.img_chat);
        img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.goToChatPage(context, student.uid);
            }
        });
        ImageView img_sms = (ImageView)view.findViewById(R.id.img_sms);
        final User[] sel_user = new User[1];
        img_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BulkSMSActivity.class);
                intent.putExtra("USER", sel_user[0]);
                context.startActivity(intent);
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
        final RelativeLayout ly_status = view.findViewById(R.id.ly_status);
        Utils.mDatabase.child(Utils.tbl_user).child(student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    User user = dataSnapshot.getValue(User.class);
                    txt_name.setText(user.name);
                    Glide.with(context).load(user.photo).apply(new RequestOptions()
                            .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                    if (user.status == 0) {
                        ly_status.setBackground(context.getResources().getDrawable(R.drawable.status_offline));
                    } else {
                        ly_status.setBackground(context.getResources().getDrawable(R.drawable.status_online));
                    }
                    sel_user[0] = user;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Utils.mDatabase.child(Utils.tbl_school).child(student.school_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    School school = dataSnapshot.getValue(School.class);
                    txt_school.setText(school.number);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        txt_class.setText(student.class_name);
        if (student.isNew) {
            txt_new.setText("NEW");
            txt_new.setBackgroundColor(Color.parseColor("#c3edb9"));
        } else {
            txt_new.setText("OLD");
            txt_new.setBackgroundColor(Color.parseColor("#e28e8e"));
        }
        return view;
    }
}
