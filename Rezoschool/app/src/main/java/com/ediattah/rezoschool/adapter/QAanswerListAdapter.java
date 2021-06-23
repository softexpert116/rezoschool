package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ediattah.rezoschool.R;

import java.util.ArrayList;

public class QAanswerListAdapter extends BaseAdapter {
    public boolean isSingleChoice = false;
    public boolean clickEnabled = true;
    public ArrayList<String> arrayList;
    public ArrayList<String> sel_arrayList = new ArrayList<String>();
    Context context;

    public QAanswerListAdapter(Context _context, ArrayList<String> _arrayList) {
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
        final String answer = arrayList.get(i);
        String indexStr = String.valueOf(i);
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_qa_answer, null);
        }
        CheckBox checkBox = view.findViewById(R.id.chkBox);
        checkBox.setText(answer);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (isSingleChoice) {
                        sel_arrayList.clear();
                    }
                    sel_arrayList.add(indexStr);
                } else {
                    sel_arrayList.remove(indexStr);
                }
                notifyDataSetChanged();
            }
        });
        checkBox.setClickable(clickEnabled);
        checkBox.setChecked(sel_arrayList.contains(indexStr));
        return view;
    }
}
