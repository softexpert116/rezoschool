package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.ChatRoom;
import com.ediattah.rezoschool.Model.Message;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ChatListAdapter extends BaseAdapter {
    ArrayList<Message> arrayList;
    Context context;
    public int index_update = -1;

    public ChatListAdapter(Context _context, ArrayList<Message> _arrayList) {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Message message = arrayList.get(i);
        LayoutInflater inflater = LayoutInflater.from(context);
        if (message.sender_id.equals(Utils.mUser.getUid())) {
            view = inflater.inflate(R.layout.cell_chat_right, null);
        } else {
            view = inflater.inflate(R.layout.cell_chat_left, null);
        }
        RelativeLayout ly_cover = view.findViewById(R.id.ly_cover);
        TextView txt_message = view.findViewById(R.id.txt_message);
        final TextView txt_time = view.findViewById(R.id.txt_time);
        final ImageView img_photo = view.findViewById(R.id.img_photo);
        final ImageView img_pic = view.findViewById(R.id.img_pic);
        if (message.message.length()>0) {
            txt_message.setVisibility(View.VISIBLE);
            img_pic.setVisibility(View.GONE);
            txt_message.setText(message.message);
        } else {
            if (message.file.length() == 0) {
                ly_cover.setVisibility(View.GONE);
                return view;
            }
            txt_message.setVisibility(View.GONE);
            img_pic.setVisibility(View.VISIBLE);
            Glide.with(context).load(message.file).apply(new RequestOptions()
                    .placeholder(R.drawable.default_pic).centerCrop().dontAnimate()).into(img_pic);
        }
        txt_time.setText(Utils.getTimeString(new Date(message.timestamp)));
        Utils.mDatabase.child(Utils.tbl_user).child(message.sender_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (context != null) {
                        Glide.with(context).load(user.photo).apply(new RequestOptions()
                                .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
}
