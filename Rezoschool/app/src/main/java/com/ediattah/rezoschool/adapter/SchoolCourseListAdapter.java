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

import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.CourseTime;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolCourseListAdapter extends BaseAdapter {
    public ArrayList<Course> arrayList;
    ArrayList<Course> array_sel;

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
        txt_name.setText(_course.name);

        Button btn_remove = (Button)view.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you going to remove this item?");
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        for (int i = 0; i < Utils.currentSchool.courses.size(); i++) {
                            Course course = Utils.currentSchool.courses.get(i);
                            if (course.name.equals(_course.name)) {
                                Utils.currentSchool.courses.remove(i);
                                break;
                            }
                        }
                        Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("courses").setValue(Utils.currentSchool.courses);
                        Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
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
        LinearLayout ly_time = view.findViewById(R.id.ly_time);
        if (_course.times.size() == 0) {
            ly_time.setVisibility(View.GONE);
        } else {
            ly_time.setVisibility(View.VISIBLE);
            ly_time.removeAllViews();
            for (CourseTime courseTime:_course.times) {
                LinearLayout ly_row = new LinearLayout(context);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (ly_time.getChildCount() > 0) {
                    lp.setMargins(0, 10, 0, 0);
                }
                ly_row.setLayoutParams(lp);
                ly_row.setPadding(10, 5, 10, 5);
                ly_row.setOrientation(LinearLayout.VERTICAL);
                ly_row.setBackgroundColor(Color.parseColor("#A9F1D3"));
                TextView txt_day = new TextView(context);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp1.setMargins(0, 5, 0, 0);
                txt_day.setLayoutParams(lp1);
//                txt_day.setPadding(0, 5, 0, 5);
                txt_day.setText(Utils.getDayStrFromInt(courseTime.dayOfWeek));
                txt_day.setTextColor(Color.parseColor("#D81B60"));
                ly_row.addView(txt_day);
                TextView txt_start_time = new TextView(context);
                txt_start_time.setText(courseTime.start_time + "~" + courseTime.end_time);
                ly_row.addView(txt_start_time);
                ly_time.addView(ly_row);
            }
        }
        return view;
    }
}
