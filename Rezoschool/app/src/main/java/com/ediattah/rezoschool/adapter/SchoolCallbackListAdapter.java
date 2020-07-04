package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.Model.CallbackModel;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.fragments.SchoolCallbackFragment;

import java.util.ArrayList;

public class SchoolCallbackListAdapter extends BaseAdapter {
    ArrayList<CallbackModel> arrayList;

    Context context;
    SchoolCallbackFragment fragment;

    public SchoolCallbackListAdapter(Context _context, SchoolCallbackFragment _fragment, ArrayList<CallbackModel> _arrayList) {
        context = _context;
        fragment = _fragment;
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
        final CallbackModel model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_school_callback, null);
        }
        TextView txt_number = view.findViewById(R.id.txt_number);
        txt_number.setText(model.number);
        TextView txt_message = view.findViewById(R.id.txt_message);
        txt_message.setText(model.message);
        ImageButton ibtn_remove = view.findViewById(R.id.ibtn_remove);
        ibtn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Remove clicked", Toast.LENGTH_SHORT).show();
            }
        });
        ibtn_remove.setFocusable(false);
        ibtn_remove.setFocusableInTouchMode(false);
        return view;
    }
}
