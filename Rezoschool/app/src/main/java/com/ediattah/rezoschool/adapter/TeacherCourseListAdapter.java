package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;

public class TeacherCourseListAdapter extends BaseAdapter {
    public ArrayList<Course> arrayList;
    ArrayList<Course> array_sel;
    public School school = new School();
    Context context;

    public TeacherCourseListAdapter(Context _context, ArrayList<Course> _arrayList, ArrayList<Course> array_sel) {
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
        txt_name.setText(_course.name);

        Button btn_remove = (Button)view.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you going to remove this item?");
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        String new_courseStr = "";
                        for (int i = 0; i < school.teachers.size(); i++) {
                            Teacher teacher = school.teachers.get(i);
                            if (teacher.uid.equals(Utils.mUser.getUid())) {
                                ArrayList<String> courses_list = new ArrayList<>(Arrays.asList(teacher.courses.split(",")));
                                for (String courseStr: courses_list) {
                                    if (courseStr.equals(_course.name)) {
                                        courses_list.remove(courseStr);
                                        break;
                                    }
                                }
                                Log.d("remove:", courses_list.toString());
                                for (String courseStr: courses_list) {
                                    if (new_courseStr.length() == 0) {
                                        new_courseStr += courseStr;
                                    } else {
                                        new_courseStr += "," + courseStr;
                                    }
                                }
                            }


                        }
                        final String finalNew_courseStr = new_courseStr;
                        Utils.mDatabase.child(Utils.tbl_school).child(school._id).child("teachers").orderByChild("uid").equalTo(Utils.mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                        String key = datas.getKey();
                                        Utils.mDatabase.child(Utils.tbl_school).child(school._id).child("teachers").child(key).child("courses").setValue(finalNew_courseStr);
                                    }
//                                    Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
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
            if (array_sel.contains(_course)) {
                ly_sel.setBackground(context.getDrawable(R.color.colorPrimary));
            }
            btn_remove.setVisibility(View.GONE);
        } else {
            btn_remove.setVisibility(View.VISIBLE);

        }
        return view;
    }
}
