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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;

import java.util.ArrayList;

public class SchoolListAdapter extends BaseAdapter {
    public ArrayList<School> arrayList;
    public int sel_index = 0;
    public boolean flag_view = false;
    Context context;

    public SchoolListAdapter(Context _context, ArrayList<School> _arrayList) {
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
        final School school = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_school, null);
        }
        LinearLayout ly_sel = view.findViewById(R.id.ly_sel);
        if (sel_index == i) {
            ly_sel.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            ly_sel.setBackgroundColor(context.getResources().getColor(R.color.white_background));
        }
        TextView txt_number = view.findViewById(R.id.txt_number);
        txt_number.setText(school.number);
        TextView txt_type = view.findViewById(R.id.txt_type);
        txt_type.setText(school.type);
        TextView txt_public = view.findViewById(R.id.txt_public);
        if (school.isPublic) {
            txt_public.setText(context.getResources().getString(R.string._public));
            txt_public.setBackgroundColor(Color.parseColor("#0000ff"));
        } else {
            txt_public.setText(context.getResources().getString(R.string._private));
            txt_public.setBackgroundColor(Color.parseColor("#7f00ff"));
        }
        Button btn_remove = view.findViewById(R.id.btn_remove);
        if (flag_view) {
            btn_remove.setVisibility(View.GONE);
        }
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.are_you_going_to_remove_this_item));
                builder.setPositiveButton(context.getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
//                        ArrayList<Teacher> teachers = school.teachers;
                        for (int i = 0; i < school.teachers.size(); i++) {
                            Teacher teacher = school.teachers.get(i);
                            if (teacher.uid.equals(Utils.mUser.getUid())) {
                                school.teachers.remove(i);
                                break;
                            }
                        }
                        Utils.mDatabase.child(Utils.tbl_school).child(school._id).child("teachers").setValue(school.teachers);
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

        return view;
    }
}
