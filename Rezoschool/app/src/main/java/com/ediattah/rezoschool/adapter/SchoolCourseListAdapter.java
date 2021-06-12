package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.CourseTime;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewSyllabusActivity;
import com.ediattah.rezoschool.ui.TimeslotActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolCourseListAdapter extends BaseAdapter {
    public ArrayList<Course> arrayList;
    ArrayList<Course> array_sel;
    public boolean flag_class = false;
    public boolean flag_syllabus = false;
    public boolean flag_timeslot = false;
    public CourseTime sel_courseTime;
    Context context;

    public SchoolCourseListAdapter(Context _context, ArrayList<Course> _arrayList, ArrayList<Course> array_sel) {
        context = _context;
        this.arrayList = _arrayList;
        this.array_sel = array_sel;
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
        final Course _course = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_course, null);
        }
        TextView txt_name = view.findViewById(R.id.txt_name);
        txt_name.setText(_course.name + " x " + _course.coef);

        Button btn_remove = (Button)view.findViewById(R.id.btn_remove);

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.are_you_going_to_remove_this_item));
                builder.setPositiveButton(context.getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        TimeslotActivity activity = (TimeslotActivity)context;
                        for (int i = 0; i < activity.sel_class.courses.size(); i++) {
                            Course course = activity.sel_class.courses.get(i);
                            if (course.name.equals(_course.name)) {
                                activity.sel_class.courses.remove(i);
                                break;
                            }
                        }
                        for (int i = 0; i < Utils.currentSchool.classes.size(); i++) {
                            Class _class = Utils.currentSchool.classes.get(i);
                            if (_class.name.equals(activity.sel_class.name)) {
                                Utils.currentSchool.classes.set(i, activity.sel_class);
                                break;
                            }
                        }
                        Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("classes").setValue(Utils.currentSchool.classes);
                        Toast.makeText(context, context.getResources().getString(R.string.successfully_deleted), Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
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
        if (array_sel != null) {
            for (Course course:array_sel) {
                if (course.name.equals(_course.name)) {
                    ly_sel.setBackground(context.getDrawable(R.color.gray_light));
                    break;
                }
            }
            btn_remove.setVisibility(View.GONE);
        } else {
            btn_remove.setVisibility(View.VISIBLE);
        }
        if (flag_timeslot) {
            btn_remove.setVisibility(View.GONE);
        }
        LinearLayout ly_time = view.findViewById(R.id.ly_time);
        if (_course.times.size() == 0) {
            ly_time.setVisibility(View.GONE);
        } else {
            ly_time.setVisibility(View.VISIBLE);
            ly_time.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context);
            int j = 0;
            while (j < _course.times.size()) {
                View view1 = inflater.inflate(R.layout.cell_timeslot, null);
                final LinearLayout ly1 = view1.findViewById(R.id.ly1);
                final LinearLayout ly2 = view1.findViewById(R.id.ly2);

                TextView txt_day1 = view1.findViewById(R.id.txt_day1);
                TextView txt_day2 = view1.findViewById(R.id.txt_day2);
                TextView txt_time1 = view1.findViewById(R.id.txt_time1);
                TextView txt_time2 = view1.findViewById(R.id.txt_time2);

                final CourseTime courseTime1 = _course.times.get(j);
                CourseTime courseTime2 = null;
                txt_day1.setText(Utils.getDayStrFromInt(courseTime1.dayOfWeek));
                txt_time1.setText(courseTime1.start_time + "~" + courseTime1.end_time);
                if (_course.times.size() > j+1) {
                    courseTime2 = _course.times.get(j+1);
                    txt_day2.setText(Utils.getDayStrFromInt(courseTime2.dayOfWeek));
                    txt_time2.setText(courseTime2.start_time + "~" + courseTime2.end_time);
                } else {
                    ly2.setVisibility(View.INVISIBLE);
                }
                if (flag_syllabus) {
                    final NewSyllabusActivity activity = (NewSyllabusActivity)context;
                    ly1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.refresh_courseTime(_course.name, courseTime1);
                        }
                    });
                    final CourseTime finalCourseTime2 = courseTime2;
                    ly2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.refresh_courseTime(_course.name, finalCourseTime2);
                        }
                    });
                }
                if (flag_class) {
                    ly1.setTag("0");
                    ly1.setBackground(context.getResources().getDrawable(R.drawable.frame_round));
                    ly2.setTag("0");
                    ly2.setBackground(context.getResources().getDrawable(R.drawable.frame_round));
                    if (array_sel.size() > 0) {
                        for (Course course:array_sel) {
                            if (course.name.equals(_course.name)) {
                                CourseTime sel_courseTime = course.times.get(0);
                                if (courseTime1.equals(sel_courseTime)) {
                                    ly1.setTag("1");
                                    ly1.setBackground(context.getResources().getDrawable(R.drawable.frame_round_dark));
                                    break;
                                }
                                if (courseTime2!=null) {
                                    if (courseTime2.equals(sel_courseTime)) {
                                        ly2.setTag("1");
                                        ly2.setBackground(context.getResources().getDrawable(R.drawable.frame_round_dark));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    ly1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ArrayList<CourseTime> arrayList = new ArrayList<>();
                            arrayList.add(courseTime1);
                            Course course = new Course(_course.name, _course.coef, arrayList);
                            boolean flag = false;
                            int flag_index = 0;
                            for (int k = 0; k < array_sel.size(); k++) {
                                Course sel_course = array_sel.get(k);
                                if (sel_course.name.equals(course.name)) {
                                    flag = true;
                                    flag_index = k;
                                    break;
                                }
                            }
                            if (ly1.getTag().equals("1")) {
                                if (flag) {
                                    array_sel.remove(flag_index);
                                }
                            } else {
                                if (!flag) {
                                    array_sel.add(course);
                                } else {
                                    array_sel.set(flag_index, course);
                                }
                            }
                            notifyDataSetChanged();
                        }
                    });
                    final CourseTime finalCourseTime = courseTime2;
                    ly2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ArrayList<CourseTime> arrayList = new ArrayList<>();
                            arrayList.add(finalCourseTime);
                            Course course = new Course(_course.name, _course.coef, arrayList);
                            boolean flag = false;
                            int flag_index = 0;
                            for (int k = 0; k < array_sel.size(); k++) {
                                Course sel_course = array_sel.get(k);
                                if (sel_course.name.equals(course.name)) {
                                    flag = true;
                                    flag_index = k;
                                    break;
                                }
                            }
                            if (ly2.getTag().equals("1")) {
                                if (flag) {
                                    array_sel.remove(flag_index);
                                }
                            } else {
                                if (!flag) {
                                    array_sel.add(course);
                                } else {
                                    array_sel.set(flag_index, course);
                                }
                            }
                            notifyDataSetChanged();
                        }
                    });
                }
                ly_time.addView(view1);
                j +=2;
            }
        }
        return view;
    }
}
