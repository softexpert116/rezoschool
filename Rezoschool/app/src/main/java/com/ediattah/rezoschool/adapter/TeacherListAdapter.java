package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.UserModel;
import com.ediattah.rezoschool.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherListAdapter extends BaseAdapter {
    ArrayList<UserModel> arrayList;

    Context context;

    public TeacherListAdapter(Context _context, ArrayList<UserModel> _arrayList) {
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
        final UserModel model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_teacher, null);
        }
        TextView txt_name = view.findViewById(R.id.txt_name);
        txt_name.setText(model.name);
        TextView txt_description = view.findViewById(R.id.txt_description);
        txt_description.setText(model.description);
        CircleImageView img_photo = view.findViewById(R.id.img_photo);
        Glide.with(context).load(model.phone).apply(new RequestOptions()
                .placeholder(R.drawable.ic_teacher).centerCrop().dontAnimate()).into(img_photo);
        return view;
    }
}
