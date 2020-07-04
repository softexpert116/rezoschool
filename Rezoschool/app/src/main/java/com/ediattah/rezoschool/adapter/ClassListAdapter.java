package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.fragments.SchoolClassFragment;

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
        TextView txt_start_time = view.findViewById(R.id.txt_start_time);
        TextView txt_end_time = view.findViewById(R.id.txt_end_time);
        TextView txt_courses = view.findViewById(R.id.txt_courses);
        txt_level.setText(_class.level);
        txt_name.setText(_class.name);
        txt_start_time.setText(_class.start_time);
        txt_end_time.setText(_class.end_time);
        txt_courses.setText(_class.courses);
        Button btn_remove = (Button)view.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you going to remove this item?");
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        for (int i = 0; i < Utils.currentSchool.classes.size(); i++) {
                            Class aClass = Utils.currentSchool.classes.get(i);
                            if (aClass.name.equals(aClass.name)) {
                                Utils.currentSchool.classes.remove(i);
                                break;
                            }
                        }
                        Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("classes").setValue(Utils.currentSchool.classes);
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
