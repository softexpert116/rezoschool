package com.ediattah.rezoschool.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.FeeModel;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.ui.FinanceSettingActivity;

import java.util.ArrayList;

public class FeeStudentListAdapter extends BaseAdapter {
    ArrayList<FeeModel> arrayList;

    FinanceSettingActivity activity;

    public FeeStudentListAdapter(FinanceSettingActivity _activity, ArrayList<FeeModel> _arrayList) {
        activity = _activity;
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
        final FeeModel model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            view = inflater.inflate(R.layout.cell_fee_student, null);
        }
        TextView txt_level = view.findViewById(R.id.txt_student);
        txt_level.setText(String.valueOf(model.student_id));
        TextView txt_fee = view.findViewById(R.id.txt_fee);
        txt_fee.setText(model.fee);
        ImageButton ibtn_edit = view.findViewById(R.id.ibtn_edit);
        ibtn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openAddDialog(false, false);
            }
        });
        ibtn_edit.setFocusable(false);
        ibtn_edit.setFocusableInTouchMode(false);
        return view;
    }
}
