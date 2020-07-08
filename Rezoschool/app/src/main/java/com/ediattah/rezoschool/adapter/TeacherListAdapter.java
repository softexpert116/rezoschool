package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.Model.UserModel;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherListAdapter extends BaseAdapter {
    ArrayList<Teacher> arrayList;

    Context context;

    public TeacherListAdapter(Context _context, ArrayList<Teacher> _arrayList) {
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
        final Teacher teacher = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_teacher, null);
        }
        ImageView img_chat = (ImageView)view.findViewById(R.id.img_chat);
        img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.goToChatPage(context, teacher.uid);
//                Toast.makeText(context, "click chat", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView img_sms = (ImageView)view.findViewById(R.id.img_sms);
        img_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "click sms", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView img_call = (ImageView)view.findViewById(R.id.img_call);
        img_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "click call", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView img_video = (ImageView)view.findViewById(R.id.img_video);
        img_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "click video", Toast.LENGTH_SHORT).show();
            }
        });
        final TextView txt_name = view.findViewById(R.id.txt_name);
//        txt_name.setText(model.name);
        TextView txt_course = view.findViewById(R.id.txt_course);
        txt_course.setText(teacher.courses);
        final CircleImageView img_photo = view.findViewById(R.id.img_photo);
        Utils.mDatabase.child(Utils.tbl_user).child(teacher.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    User user = dataSnapshot.getValue(User.class);
                    txt_name.setText(user.name);
                    Glide.with(context).load(user.photo).apply(new RequestOptions()
                            .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
}
