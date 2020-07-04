package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.SchoolTeacherModel;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.fragments.SchoolTeacherFragment;
import com.ediattah.rezoschool.ui.TeacherDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import de.hdodenhof.circleimageview.CircleImageView;

public class SchoolTeacherListAdapter extends BaseAdapter {
    public ArrayList<Teacher> arrayList;
    public int sel_index = 0;
    Context context;
    SchoolTeacherFragment fragment;

    public SchoolTeacherListAdapter(Context _context, ArrayList<Teacher> _arrayList, int sel_index) {
        context = _context;
        this.arrayList = _arrayList;
        this.sel_index = sel_index;
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
        final Teacher teacher = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_school_teacher, null);
        }
        Button btn_syllabus = view.findViewById(R.id.btn_syllabus);
        btn_syllabus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TeacherDetailActivity.class);
                intent.putExtra("TEACHER", teacher);
                context.startActivity(intent);
            }
        });
        TextView txt_course = view.findViewById(R.id.txt_course);
        txt_course.setText(teacher.courses);
        final TextView txt_teacher = view.findViewById(R.id.txt_teacher);
        final ToggleSwitch toggleSwitch = (ToggleSwitch) view.findViewById(R.id.sw_allow);
        toggleSwitch.setActiveBgColor(Color.parseColor("#0099CC"));
        toggleSwitch.setInactiveBgColor(Color.parseColor("#888888"));
        toggleSwitch.setActiveTextColor(Color.parseColor("#ffffff"));
        toggleSwitch.setInactiveTextColor(Color.parseColor("#ffffff"));
        toggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener(){

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
//                Toast.makeText(context, "switched", Toast.LENGTH_SHORT).show();
                if (position == 0) {
                    Utils.mDatabase.child(Utils.tbl_user).child(teacher.uid).child("isAllow").setValue(true);
                } else {
                    Utils.mDatabase.child(Utils.tbl_user).child(teacher.uid).child("isAllow").setValue(false);
                }
            }
        });
        final CircleImageView img_photo = view.findViewById(R.id.img_photo);
        Utils.mDatabase.child(Utils.tbl_user).orderByKey().equalTo(teacher.uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            User user = childSnapshot.getValue(User.class);
                            txt_teacher.setText(user.name);
                            Glide.with(context).load(user.photo).apply(new RequestOptions()
                                    .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                            ArrayList<String> labels = new ArrayList<>();
                            labels.add("Allow");
                            labels.add("Block");
                            toggleSwitch.setLabels(labels);
                            if (user.isAllow) {
                                toggleSwitch.setCheckedTogglePosition(0);
                            } else {
                                toggleSwitch.setCheckedTogglePosition(1);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                    }
                });

        RelativeLayout ly_sel = view.findViewById(R.id.ly_sel);
        ly_sel.setBackground(context.getDrawable(R.color.white));
        if (sel_index == i) {
            ly_sel.setBackground(context.getDrawable(R.color.colorPrimary));
        } else {
            ly_sel.setBackground(context.getDrawable(R.color.white));
        }
        return view;
    }
}
