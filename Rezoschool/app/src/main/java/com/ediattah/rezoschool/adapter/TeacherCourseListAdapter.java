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
    Context context;

    public TeacherCourseListAdapter(Context _context, ArrayList<Course> _arrayList) {
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
        final Course _course = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_course, null);
        }
        TextView txt_name = view.findViewById(R.id.txt_name);
        txt_name.setText(_course.name);

        Button btn_remove = (Button)view.findViewById(R.id.btn_remove);
        RelativeLayout ly_sel = view.findViewById(R.id.ly_sel);
        ly_sel.setBackground(context.getDrawable(R.color.white));
        btn_remove.setVisibility(View.GONE);
        return view;
    }
}
