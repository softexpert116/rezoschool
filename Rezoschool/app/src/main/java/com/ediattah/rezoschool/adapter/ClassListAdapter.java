package com.ediattah.rezoschool.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.CourseTime;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClassListAdapter extends BaseAdapter {
    public ArrayList<Class> arrayList;
    public int sel_index = -1;
    Context context;

    public ClassListAdapter(Context _context, ArrayList<Class> _arrayList) {
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
        final Class _class = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_class, null);
        }
        RelativeLayout ly_sel = view.findViewById(R.id.ly_sel);
        TextView txt_level = view.findViewById(R.id.txt_level);
        TextView txt_name = view.findViewById(R.id.txt_name);
        TextView txt_courses = view.findViewById(R.id.txt_courses);
        txt_level.setText(_class.level);
        txt_name.setText(_class.name);
        String coursesStr = "";
        for (Course course:_class.courses) {
            if (coursesStr.length() == 0) {
                coursesStr += course.name;
            } else {
                coursesStr += "," + course.name;
            }
        }
        txt_courses.setText(coursesStr);
        Button btn_course_detail = view.findViewById(R.id.btn_course_detail);
        btn_course_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dlg = new Dialog(context);
                Window window = dlg.getWindow();
                View view2 = ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_view_items, null);
                int width = (int)(context.getResources().getDisplayMetrics().widthPixels*0.80);
                int height = (int)(context.getResources().getDisplayMetrics().heightPixels*0.4);
                view.setMinimumWidth(width);
                view.setMinimumHeight(height);
                dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dlg.setContentView(view2);
                window.setGravity(Gravity.CENTER);
                dlg.show();
                LinearLayout ly_course = view2.findViewById(R.id.ly_item);
                ly_course.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(context);
                for (int i = 0; i < _class.courses.size(); i++) {
                    final Course course = _class.courses.get(i);
                    View view1 = inflater.inflate(R.layout.cell_class_course, null);
                    TextView txt_course = view1.findViewById(R.id.txt_course);
                    TextView txt_day = view1.findViewById(R.id.txt_day);
                    TextView txt_time = view1.findViewById(R.id.txt_time);
                    Button btn_remove = view1.findViewById(R.id.btn_remove);
                    btn_remove.setVisibility(View.GONE);
                    txt_course.setText(course.name);
                    CourseTime courseTime = course.times.get(0);
                    txt_day.setText(Utils.getDayStrFromInt(courseTime.dayOfWeek));
                    txt_time.setText(courseTime.start_time + " ~ " + courseTime.end_time);
                    ly_course.addView(view1, i);
                }
                dlg.show();
            }
        });
        Button btn_students = view.findViewById(R.id.btn_students);
        int student_count = 0;
        for (Student student:Utils.currentSchool.students) {
            if (student.class_name.equals(_class.name)) {
                student_count += 1;
            }
        }
        btn_students.setText(context.getResources().getString(R.string.student)+"(" + String.valueOf(student_count) + ")");
        final int finalStudent_count = student_count;
        btn_students.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dlg = new Dialog(context);
                Window window = dlg.getWindow();
                View view2 = ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_view_items, null);
                int width = (int)(context.getResources().getDisplayMetrics().widthPixels*0.90);
                int height = (int)(context.getResources().getDisplayMetrics().heightPixels*0.4);
                view.setMinimumWidth(width);
                view.setMinimumHeight(height);
                dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dlg.setContentView(view2);
                window.setGravity(Gravity.CENTER);
                dlg.show();
                LinearLayout ly_item = view2.findViewById(R.id.ly_item);
                ly_item.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(context);
                for (int i = 0; i < Utils.currentSchool.students.size(); i++) {
                    final Student student = Utils.currentSchool.students.get(i);
                    if (!student.class_name.equals(_class.name)) {
                        continue;
                    }
                    View view1 = inflater.inflate(R.layout.cell_student_accepted, null);
                    final TextView txt_name = view1.findViewById(R.id.txt_name);
                    final TextView txt_class_t = view1.findViewById(R.id.txt_class_t);
                    final TextView txt_class = view1.findViewById(R.id.txt_class);
                    final TextView txt_school_t = view1.findViewById(R.id.txt_school_t);
                    final TextView txt_school = view1.findViewById(R.id.txt_school);
                    final ImageView img_photo = view1.findViewById(R.id.img_photo);
                    Button btn_parent = view1.findViewById(R.id.btn_parent);
                    btn_parent.setVisibility(View.GONE);
                    LinearLayout ly_tool = view1.findViewById(R.id.ly_tool);
                    ly_tool.setVisibility(View.GONE);
                    txt_class_t.setText(context.getResources().getString(R.string.email) + ": ");
                    txt_school_t.setText(context.getResources().getString(R.string.phone) + ": ");
                    Utils.mDatabase.child(Utils.tbl_user).child(student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!=null) {
                                User user = dataSnapshot.getValue(User.class);
                                txt_name.setText(user.name);
                                txt_class.setText(user.email);
                                txt_school.setText(user.phone);
                                Glide.with(context).load(user.photo).apply(new RequestOptions()
                                        .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    ly_item.addView(view1, i);
                }
                if (finalStudent_count > 0) {
                    dlg.show();
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.the_class_has_no_student), Toast.LENGTH_SHORT).show();
                }
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
                        for (int i = 0; i < Utils.currentSchool.classes.size(); i++) {
                            Class aClass = Utils.currentSchool.classes.get(i);
                            if (aClass.name.equals(_class.name)) {
                                Utils.currentSchool.classes.remove(i);
                                break;
                            }
                        }
                        Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("classes").setValue(Utils.currentSchool.classes);
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
        if (sel_index >= 0) {
            btn_remove.setVisibility(View.GONE);
            if (sel_index == i) {
                ly_sel.setBackground(context.getDrawable(R.color.colorPrimary));
            } else {
                ly_sel.setBackground(context.getDrawable(R.color.white));
            }
        } else {
            ly_sel.setBackground(context.getDrawable(R.color.white));
            btn_remove.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
