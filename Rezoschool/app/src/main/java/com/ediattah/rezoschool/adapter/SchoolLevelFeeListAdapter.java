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

import com.ediattah.rezoschool.Model.Level;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;

import java.util.ArrayList;

public class SchoolLevelFeeListAdapter extends BaseAdapter {
    public ArrayList<Level> arrayList;
    public int sel_index = 0;
    Context context;

    public SchoolLevelFeeListAdapter(Context _context, ArrayList<Level> _arrayList) {
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
            view = inflater.inflate(R.layout.cell_level_fee, null);
        }
        TextView txt_name = view.findViewById(R.id.txt_name);
        txt_name.setText(level.name);
        TextView txt_fee = view.findViewById(R.id.txt_fee);
        txt_fee.setVisibility(View.VISIBLE);
        if (level.fee != null) {
            if (level.fee.length() > 0) {
                txt_fee.setText(level.fee + "XOF/month");
            } else {
                txt_fee.setVisibility(View.GONE);
            }
        } else {
            txt_fee.setVisibility(View.GONE);
        }
        RelativeLayout ly_sel = view.findViewById(R.id.ly_sel);
        if (sel_index == i) {
            ly_sel.setBackground(context.getDrawable(R.color.colorPrimaryDark));
        } else {
            ly_sel.setBackground(context.getDrawable(R.color.white));
        }

        return view;
    }
}
