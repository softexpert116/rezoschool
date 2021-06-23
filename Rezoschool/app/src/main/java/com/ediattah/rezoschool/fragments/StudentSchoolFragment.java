package com.ediattah.rezoschool.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.PsychologyResult;
import com.ediattah.rezoschool.Model.PsychologySection;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.ui.BulkSMSActivity;
import com.ediattah.rezoschool.ui.ChooseSchoolActivity;
import com.ediattah.rezoschool.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class StudentSchoolFragment extends Fragment {
    MainActivity activity;
    Button btn_choose;
    TextView txt_status, txt_section, txt_score, txt_result;
    ImageView img_status, img_photo, img_parent_photo;
    User schoolUser;
    TextView txt_school_number, txt_name, txt_email, txt_phone, txt_type, txt_public, txt_class_level, txt_class_name, txt_parent_name, txt_parent_email, txt_parent_phone;
    RelativeLayout ly_status;
    LinearLayout ly_tool;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_student_school, container, false);
        btn_choose = v.findViewById(R.id.btn_choose);
        txt_status = v.findViewById(R.id.txt_status);
        img_status = v.findViewById(R.id.img_status);

        txt_school_number = v.findViewById(R.id.txt_school_number);
        txt_name = v.findViewById(R.id.txt_name);
        txt_email = v.findViewById(R.id.txt_email);
        txt_phone = v.findViewById(R.id.txt_phone);
        txt_type = v.findViewById(R.id.txt_type);
        txt_public = v.findViewById(R.id.txt_public);
        img_photo = v.findViewById(R.id.img_photo);
        ly_status = v.findViewById(R.id.ly_status);
        ly_tool = v.findViewById(R.id.ly_tool);
        txt_class_level = v.findViewById(R.id.txt_class_level);
        txt_class_name = v.findViewById(R.id.txt_class_name);
        img_parent_photo = v.findViewById(R.id.img_parent_photo);
        txt_parent_name = v.findViewById(R.id.txt_parent_name);
        txt_parent_email = v.findViewById(R.id.txt_parent_email);
        txt_parent_phone = v.findViewById(R.id.txt_parent_phone);
        txt_section = v.findViewById(R.id.txt_section);
        txt_score = v.findViewById(R.id.txt_score);
        txt_result = v.findViewById(R.id.txt_result);

        ImageView img_chat = v.findViewById(R.id.img_chat);
        ImageView img_sms = v.findViewById(R.id.img_sms);
        ImageView img_video = v.findViewById(R.id.img_video);
        img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.goToChatPage(activity, Utils.currentSchool.uid);
            }
        });
        img_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, BulkSMSActivity.class);
                intent.putExtra("USER", schoolUser);
                activity.startActivity(intent);
            }
        });
        img_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.goToStartVideoCallPage(schoolUser, activity);
            }
        });
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ChooseSchoolActivity.class);
                activity.startActivity(intent);
            }
        });
        Utils.mDatabase.child(Utils.tbl_parent_student).orderByChild("student_id").equalTo(Utils.mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        String parent_id = datas.child("parent_id").getValue(String.class);
                        Utils.mDatabase.child(Utils.tbl_user).child(parent_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue()!=null) {
                                    User user = dataSnapshot.getValue(User.class);
                                    Glide.with(activity).load(user.photo).apply(new RequestOptions()
                                            .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_parent_photo);
                                    txt_parent_name.setText(user.name);
                                    txt_parent_email.setText(user.email);
                                    txt_parent_phone.setText(user.phone);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Utils.mDatabase.child(Utils.tbl_psychology_result).orderByChild("uid").equalTo(Utils.currentUser._id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        PsychologyResult result = datas.getValue(PsychologyResult.class);
                        result._id = datas.getKey();
                        Utils.mDatabase.child(Utils.tbl_psychology_section).child(result.section_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    PsychologySection section = dataSnapshot.getValue(PsychologySection.class);
                                    txt_section.setText(section.name);
                                    txt_score.setText(String.valueOf(result.score));
                                    txt_result.setText(App.analyzeScoreResult(result.score));
                                    txt_result.setBackgroundColor(App.analyzeScoreColor(activity, result.score));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
            }
        });
        return v;
    }
    void setValues() {
        txt_class_level.setText(Utils.currentClass.level);
        txt_class_name.setText(Utils.currentClass.name);
        txt_school_number.setText(Utils.currentSchool.number);
        txt_type.setText(Utils.currentSchool.type);
        if (Utils.currentSchool.isPublic) {
            txt_public.setText(getResources().getString(R.string._public));
            txt_public.setBackgroundColor(Color.parseColor("#0000ff"));
        } else {
            txt_public.setText(getResources().getString(R.string._public));
            txt_public.setBackgroundColor(Color.parseColor("#7f00ff"));
        }
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.currentSchool.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    User user = dataSnapshot.getValue(User.class);
                    user._id = dataSnapshot.getKey();
                    txt_name.setText(user.name);
                    txt_email.setText(user.email);
                    txt_phone.setText(user.phone);
                    try {
                        Glide.with(activity).load(user.photo).apply(new RequestOptions()
                                .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (user.status == 0) {
                        ly_status.setBackground(activity.getResources().getDrawable(R.drawable.status_offline));
                    } else {
                        ly_status.setBackground(activity.getResources().getDrawable(R.drawable.status_online));
                    }
                    schoolUser = user;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (Utils.currentSchool.number.length() > 0) {
            btn_choose.setBackground(activity.getDrawable(R.drawable.btn_round_gray));
            btn_choose.setEnabled(false);
            txt_public.setVisibility(View.VISIBLE);
            if (Utils.currentStudent.isAllow) {
                txt_status.setText(getResources().getString(R.string.accepted));
                img_status.setImageDrawable(activity.getDrawable(R.drawable.ic_accepted1));
                ly_tool.setVisibility(View.VISIBLE);
            } else {
                txt_status.setText(getResources().getString(R.string.pending));
                img_status.setImageDrawable(activity.getDrawable(R.drawable.ic_pending1));
                ly_tool.setVisibility(View.GONE);
            }
        } else {
            btn_choose.setBackground(activity.getDrawable(R.drawable.btn_round));
            btn_choose.setEnabled(true);
            txt_public.setVisibility(View.GONE);
            ly_tool.setVisibility(View.GONE);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        setValues();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}