package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ediattah.rezoschool.Model.SchoolTeacherModel;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.SchoolTeacherListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewTeacherActivity;
import com.ediattah.rezoschool.ui.TeacherDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.ediattah.rezoschool.App.array_course;
import static com.ediattah.rezoschool.App.array_teacher;

public class SchoolTeacherFragment extends Fragment {
    MainActivity activity;
    ListView listView;
    SchoolTeacherListAdapter schoolTeacherListAdapter;
//    ArrayList<Teacher> arrayList = new ArrayList<>();
    int sel_index = 0;
    CircleImageView img_photo;
    TextView txt_name;
    String sel_userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_school_teacher, container, false);
        listView = v.findViewById(R.id.listView);
        img_photo = v.findViewById(R.id.img_photo);
        txt_name = v.findViewById(R.id.txt_name);
        schoolTeacherListAdapter = new SchoolTeacherListAdapter(activity, Utils.currentSchool.teachers, sel_index);
        listView.setAdapter(schoolTeacherListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                schoolTeacherListAdapter.sel_index = i;
                schoolTeacherListAdapter.notifyDataSetChanged();
                chooseTeacher(Utils.currentSchool.teachers.get(i));
//                Toast.makeText(activity, "Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView img_chat = v.findViewById(R.id.img_chat);
        img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sel_userId != null) {
                    App.goToChatPage(activity, sel_userId);
                }
//                Toast.makeText(activity, "click chat", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView img_sms = v.findViewById(R.id.img_sms);
        img_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "click sms", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView img_call = v.findViewById(R.id.img_call);
        img_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "click call", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView img_video = v.findViewById(R.id.img_video);
        img_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "click video", Toast.LENGTH_SHORT).show();
            }
        });
        if (Utils.currentSchool.teachers.size() > 0) {
            chooseTeacher(Utils.currentSchool.teachers.get(0));
        }
        return v;
    }
    void chooseTeacher(Teacher teacher) {
        Utils.mDatabase.child(Utils.tbl_user).orderByKey().equalTo(teacher.uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            User user = childSnapshot.getValue(User.class);
                            sel_userId = childSnapshot.getKey();
                            txt_name.setText(user.name);
                            Glide.with(activity).load(user.photo).apply(new RequestOptions()
                                    .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                    }
                });
    }
    void teacher_update_listener() {
        Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("teachers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Utils.currentSchool.teachers.clear();
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        Teacher teacher = datas.getValue(Teacher.class);
                        Utils.currentSchool.teachers.add(teacher);
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        schoolTeacherListAdapter.arrayList = Utils.currentSchool.teachers;
                        schoolTeacherListAdapter.notifyDataSetChanged();
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
        teacher_update_listener();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}