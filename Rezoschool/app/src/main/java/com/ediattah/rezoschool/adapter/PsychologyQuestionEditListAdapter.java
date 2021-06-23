package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.PsychologySection;
import com.ediattah.rezoschool.Model.QA;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.fragments.PsychologyEditFragment;
import com.ediattah.rezoschool.ui.PsychologyQuestionEditActivity;

import java.util.ArrayList;

public class PsychologyQuestionEditListAdapter extends BaseAdapter {
    public ArrayList<QA> arrayList;
    PsychologyQuestionEditActivity context;

    public PsychologyQuestionEditListAdapter(PsychologyQuestionEditActivity _context, ArrayList<QA> _arrayList) {
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
        final QA model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_psychology_question_edit, null);
        }
        TextView txt_question = view.findViewById(R.id.txt_question);
        txt_question.setText((i+1) +". "+ model.question);
        TextView txt_singleChoice = view.findViewById(R.id.txt_singleChoice);
        if (model.isSingleChoice) {
            txt_singleChoice.setVisibility(View.VISIBLE);
        } else {
            txt_singleChoice.setVisibility(View.GONE);
        }
        TextView txt_no_answer = view.findViewById(R.id.txt_no_answer);
        if (model.corrects == "") {
            txt_no_answer.setVisibility(View.VISIBLE);
        } else {
            txt_no_answer.setVisibility(View.GONE);
        }
        ImageView img_edit_question = view.findViewById(R.id.img_edit_question);
        img_edit_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.openUpdateQuestionDialog(model, i);
            }
        });

        return view;
    }
}
