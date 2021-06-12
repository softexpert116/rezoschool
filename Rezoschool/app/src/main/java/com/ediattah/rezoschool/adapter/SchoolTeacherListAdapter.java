package com.ediattah.rezoschool.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Course;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class SchoolTeacherListAdapter extends BaseAdapter {
    public ArrayList<Teacher> arrayList;
    public int sel_index = 0;
    Context context;
    SchoolTeacherFragment fragment;
    public ValueEventListener listener;

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
        boolean flag_new = true;
        if (teacher.courses.length() > 0) {
            flag_new = false;
        }
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_school_teacher, null);
        }
        Button btn_reset = view.findViewById(R.id.btn_reset);
        if (flag_new) {
//            btn_reset.setBackgroundColor(Color.parseColor("#a0a0a0"));
            btn_reset.setEnabled(false);
        } else {
//            btn_reset.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            btn_reset.setEnabled(true);
        }
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.are_you_going_to_remove_this_item));
                builder.setPositiveButton(context.getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Teacher teacher2 = new Teacher(teacher.uid, "");
                        for (int i = 0; i < Utils.currentSchool.teachers.size(); i++) {
                            Teacher teacher1 = Utils.currentSchool.teachers.get(i);
                            if (teacher1.uid.equals(teacher.uid)) {
                                Utils.currentSchool.teachers.set(i, teacher2);
                                break;
                            }
                        }
                        Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("teachers").setValue(Utils.currentSchool.teachers);
                    }
                });
                builder.setNegativeButton(context.getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        Button btn_syllabus = view.findViewById(R.id.btn_syllabus);
        if (flag_new) {
            btn_syllabus.setText(context.getResources().getString(R.string.add_course));
        } else {
            btn_syllabus.setText(context.getResources().getString(R.string.syllabus));
        }
        final boolean finalFlag_new = flag_new;
        btn_syllabus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalFlag_new) {
                    openAddCourseDialog(teacher);
                } else {
                    Intent intent = new Intent(context, TeacherDetailActivity.class);
                    intent.putExtra("TEACHER", teacher);
                    context.startActivity(intent);
                }
            }
        });
        final RelativeLayout ly_status = view.findViewById(R.id.ly_status);
        TextView txt_course = view.findViewById(R.id.txt_course);
        txt_course.setText(teacher.courses);
        final TextView txt_teacher = view.findViewById(R.id.txt_teacher);
        final Button btn_allow = (Button) view.findViewById(R.id.btn_allow);
        btn_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_allow.getText().toString().equals(context.getResources().getString(R.string.allow))) {
                    Utils.mDatabase.child(Utils.tbl_user).child(teacher.uid).child("isAllow").setValue(true);
                } else {
                    Utils.mDatabase.child(Utils.tbl_user).child(teacher.uid).child("isAllow").setValue(false);
                }
            }
        });
        final CircleImageView img_photo = view.findViewById(R.id.img_photo);
        listener = Utils.mDatabase.child(Utils.tbl_user).orderByKey().equalTo(teacher.uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            User user = childSnapshot.getValue(User.class);
                            txt_teacher.setText(user.name);
                            try {
                                Glide.with(context).load(user.photo).apply(new RequestOptions()
                                        .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (user.isAllow) {
                                btn_allow.setText(context.getResources().getString(R.string.block));
//                                btn_allow.setBackground(context.getDrawable(R.color.colorAccent));
                            } else {
                                btn_allow.setText(context.getResources().getString(R.string.allow));
//                                btn_allow.setBackground(context.getDrawable(R.color.colorPrimaryDark));
                            }
                            if (user.status == 0) {
                                ly_status.setBackground(context.getResources().getDrawable(R.drawable.status_offline));
                            } else {
                                ly_status.setBackground(context.getResources().getDrawable(R.drawable.status_online));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                    }
                });
        Button btn_remove = (Button)view.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.are_you_going_to_remove_this_item));
                builder.setPositiveButton(context.getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        for (int i = 0; i < Utils.currentSchool.teachers.size(); i++) {
                            Teacher teacher1 = Utils.currentSchool.teachers.get(i);
                            if (teacher.uid.equals(teacher1.uid)) {
                                Utils.currentSchool.teachers.remove(i);
                                break;
                            }
                        }
                        Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("teachers").setValue(Utils.currentSchool.teachers);
                        Toast.makeText(context, context.getResources().getString(R.string.successfully_deleted), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(context.getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        RelativeLayout ly_sel = view.findViewById(R.id.ly_sel);
        ly_sel.setBackground(context.getDrawable(R.color.white));
        if (sel_index == i) {
            ly_sel.setBackground(context.getDrawable(R.color.gray_light));
        }
        return view;
    }
    public void openAddCourseDialog(final Teacher teacher) {
        final Dialog dlg = new Dialog(context);
        Window window = dlg.getWindow();
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_choose_item, null);
        int width = (int)(((Activity)context).getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(((Activity)context).getResources().getDisplayMetrics().heightPixels*0.4);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        ListView listView = dlg.findViewById(R.id.listView);
        final ArrayList<Course> array_course_sel = new ArrayList<>();
        final SchoolCourseListAdapter schoolCourseListAdapter = new SchoolCourseListAdapter(context, App.school_courses, array_course_sel);
        listView.setAdapter(schoolCourseListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Course course = App.school_courses.get(i);
                if (array_course_sel.contains(course)) {
                    array_course_sel.remove(course);
                } else {
                    array_course_sel.add(course);
                }
                schoolCourseListAdapter.notifyDataSetChanged();
            }
        });
        Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courses = "";
                for (Course course: array_course_sel) {
                    if (courses.length() == 0) {
                        courses += course.name;
                    } else {
                        courses += "," + course.name;
                    }
                }
                if (courses.length() == 0) {
                    Toast.makeText(context, context.getResources().getString(R.string.please_choose_a_course), Toast.LENGTH_SHORT).show();
                    return;
                }
                teacher.courses = courses;
                for (int i = 0; i < Utils.currentSchool.teachers.size(); i++) {
                    Teacher teacher1 = Utils.currentSchool.teachers.get(i);
                    if (teacher1.uid.equals(teacher.uid)) {
                        Utils.currentSchool.teachers.set(i, teacher);
                        break;
                    }
                }
                Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("teachers").setValue(Utils.currentSchool.teachers);
                dlg.dismiss();
            }
        });
        dlg.show();
    }

}
