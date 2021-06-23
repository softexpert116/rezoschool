package com.ediattah.rezoschool.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.QA;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.ui.PsychologyAnswerEditActivity;
import com.ediattah.rezoschool.ui.PsychologyQuestionEditActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class PsychologyAnswerEditListAdapter extends BaseAdapter {
    ArrayList<String> arrayList;
    PsychologyAnswerEditActivity context;
    String corrects;

    public PsychologyAnswerEditListAdapter(PsychologyAnswerEditActivity _context, ArrayList<String> _arrayList, String _corrects) {
        context = _context;
        this.arrayList = _arrayList;
        this.corrects = _corrects;
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
        final String model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_psychology_answer_edit, null);
        }
        CheckBox checkBox = view.findViewById(R.id.checkbox);
        checkBox.setText(model);
        ArrayList<String> indexArr = new ArrayList<>(Arrays.asList(corrects.split(",")));
        checkBox.setChecked(indexArr.contains(String.valueOf(i)));
        ImageView img_edit = view.findViewById(R.id.img_edit);
        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.openUpdateAnswerDialog(model, i, checkBox.isChecked());
            }
        });

        return view;
    }
}
