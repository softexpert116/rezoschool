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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.ui.BulkSMSActivity;
import com.ediattah.rezoschool.ui.SchoolAddTeacherActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.SchoolTeacherListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class SchoolTeacherFragment extends Fragment {
    MainActivity activity;
    ListView listView;
    SchoolTeacherListAdapter schoolTeacherListAdapter;
//    ArrayList<Teacher> arrayList = new ArrayList<>();
    int sel_index = 0;
    CircleImageView img_photo;
    TextView txt_name;
    String sel_userId;
    LinearLayout ly_no_items;
    RelativeLayout ly_bottom;
    User sel_user;
    ValueEventListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_school_teacher, container, false);
        listView = v.findViewById(R.id.listView);
        ly_no_items = v.findViewById(R.id.ly_no_items);
        ly_bottom = v.findViewById(R.id.ly_bottom);
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
            }
        });
        ImageView img_chat = v.findViewById(R.id.img_chat);
        img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sel_userId != null) {
                    App.goToChatPage(activity, sel_userId);
                }
            }
        });
        ImageView img_sms = v.findViewById(R.id.img_sms);
        img_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, BulkSMSActivity.class);
                intent.putExtra("USER", sel_user);
                activity.startActivity(intent);
            }
        });
        ImageView img_video = v.findViewById(R.id.img_video);
        img_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.goToStartVideoCallPage(sel_user, activity);
            }
        });

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SchoolAddTeacherActivity.class);
                activity.startActivity(intent);
            }
        });
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
                            sel_user = user;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                    }
                });
    }
    void teacher_update_listener() {
        listener = Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("teachers").addValueEventListener(new ValueEventListener() {
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
                        if (Utils.currentSchool.teachers.size() == 0) {
                            ly_no_items.setVisibility(View.VISIBLE);
                        } else {
                            ly_no_items.setVisibility(View.GONE);
                        }
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
        if (Utils.currentSchool.teachers.size() > 0) {
            chooseTeacher(Utils.currentSchool.teachers.get(0));
            ly_bottom.setVisibility(View.VISIBLE);
        } else {
            ly_bottom.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (schoolTeacherListAdapter.listener != null) {
            Utils.mDatabase.removeEventListener(schoolTeacherListAdapter.listener);
        }
        if (listener != null) {
            Utils.mDatabase.removeEventListener(listener);
        }
    }


    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}