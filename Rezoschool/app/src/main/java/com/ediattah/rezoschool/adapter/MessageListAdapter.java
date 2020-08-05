package com.ediattah.rezoschool.adapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.Absence;
import com.ediattah.rezoschool.Model.ChatRoom;
import com.ediattah.rezoschool.Model.Message;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.fragments.ParentStudentFragment;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.ParentViewExamActivity;
import com.ediattah.rezoschool.ui.ParentViewTeacherActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MessageListAdapter extends BaseAdapter {
    ArrayList<ChatRoom> arrayList;
    MainActivity activity;
    public int index_update = -1;

    public MessageListAdapter(MainActivity _activity, ArrayList<ChatRoom> _arrayList) {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ChatRoom chatRoom = arrayList.get(i);
        Message message = chatRoom.messages.get(chatRoom.messages.size()-1);
        String user_id = Utils.getChatUserId(chatRoom._id);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            view = inflater.inflate(R.layout.cell_message, null);
        }
        final LinearLayout ly_status = view.findViewById(R.id.ly_status);
        final TextView txt_user = view.findViewById(R.id.txt_user);
        TextView txt_message = view.findViewById(R.id.txt_message);
        final TextView txt_time = view.findViewById(R.id.txt_time);
        final ImageView img_photo = view.findViewById(R.id.img_photo);
        txt_message.setText(message.message);
        if (message.message.length() == 0) {
            txt_message.setText("[file attached]");
            if (message.file.length() == 0) {
                txt_message.setText("[chat open]");
            }
        }
        txt_message.setTextColor(Color.parseColor("#222222"));
//        if (index_update == i) {
//            if (message.receiver_id.equals(Utils.mUser.getUid()) && !message.seen) {
//                txt_message.setTextColor(Color.parseColor("#A55510"));
//            }
//        }
        if (message.receiver_id.equals(Utils.mUser.getUid()) && !message.seen) {
            txt_message.setTextColor(Color.parseColor("#A55510"));
        }
        txt_time.setText(Utils.getTimeString(new Date(message.timestamp)));
        Utils.mDatabase.child(Utils.tbl_user).child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    User user = dataSnapshot.getValue(User.class);
                    txt_user.setText(user.name);
                    Glide.with(activity).load(user.photo).apply(new RequestOptions()
                            .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                    if (user.status == 0) {
                        ly_status.setBackground(activity.getResources().getDrawable(R.drawable.status_offline));
                    } else {
                        ly_status.setBackground(activity.getResources().getDrawable(R.drawable.status_online));
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
