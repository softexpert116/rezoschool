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

import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.Level;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;

import java.util.ArrayList;

public class SchoolLevelListAdapter extends BaseAdapter {
    public ArrayList<Level> arrayList;
    public boolean flag_list = false;
    Context context;

    public SchoolLevelListAdapter(Context _context, ArrayList<Level> _arrayList) {
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
        final Level level = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_level, null);
        }
        TextView txt_name = view.findViewById(R.id.txt_name);
        txt_name.setText(level.name);

        Button btn_remove = (Button)view.findViewById(R.id.btn_remove);
        if (flag_list == false) {
            btn_remove.setVisibility(View.VISIBLE);
        } else {
            btn_remove.setVisibility(View.GONE);
        }
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.are_you_going_to_remove_this_item));
                builder.setPositiveButton(context.getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        for (int i = 0; i < Utils.currentSchool.levels.size(); i++) {
                            Level level1 = Utils.currentSchool.levels.get(i);
                            if (level1.name.equals(level.name)) {
                                Utils.currentSchool.levels.remove(i);
                                break;
                            }
                        }
                        Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("levels").setValue(Utils.currentSchool.levels);
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
