package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.Model.VideoGroup;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.VideoGroupListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewRsTweetsActivity;
import com.ediattah.rezoschool.ui.NewVideoGroupActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VideoFragment extends Fragment {
    MainActivity activity;
    Button btn_my, btn_other;
    public boolean flag_my = true;
    VideoGroupListAdapter videoGroupListAdapter;
    ArrayList<VideoGroup> array_my = new ArrayList<>();
    ArrayList<VideoGroup> array_other = new ArrayList<>();
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        listView = v.findViewById(R.id.listView);
        videoGroupListAdapter = new VideoGroupListAdapter(activity, this, array_my);
        listView.setAdapter(videoGroupListAdapter);
        btn_my = v.findViewById(R.id.btn_my);
        btn_other = v.findViewById(R.id.btn_other);
        btn_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTab(0);
            }
        });
        btn_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTab(1);
            }
        });
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, NewVideoGroupActivity.class);
                activity.startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                if (flag_my) {
                    builder.setMessage(getResources().getString(R.string.are_you_going_to_start_a_group_video_call));
                } else {
                    builder.setMessage(getResources().getString(R.string.are_you_going_to_join_a_group_video_call));
                }
                builder.setPositiveButton(getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        if (flag_my) {
                            App.goToStartG_VideoCallPage(array_my.get(i), activity);
                        } else {
                            App.goToJoinVideoCall(array_my.get(i).name, activity);
                        }
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        ArrayList<String> array_video_group = App.readPreference_array_String(App.NewVideoGroup);
        if (array_video_group.size() > 0) {
            setTab(1);
        } else {
            setTab(0);
        }
        return v;
    }
    void setTab(int index) {
        if (index == 0) {
            btn_other.setTextColor(Color.parseColor("#d0d0d0"));
            btn_my.setTextColor(getResources().getColor(R.color.colorText));
            btn_my.setBackgroundColor(getResources().getColor(R.color.colorMainBackground));
            btn_other.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            flag_my = true;

            videoGroupListAdapter = new VideoGroupListAdapter(activity, VideoFragment.this, array_my);
        } else {
            btn_my.setTextColor(Color.parseColor("#d0d0d0"));
            btn_other.setTextColor(getResources().getColor(R.color.colorText));
            btn_my.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btn_other.setBackgroundColor(getResources().getColor(R.color.colorMainBackground));
            flag_my = false;

            videoGroupListAdapter = new VideoGroupListAdapter(activity, VideoFragment.this, array_other);
        }
        listView.setAdapter(videoGroupListAdapter);
    }
    public void read_videoGroup() {
        array_my.clear();
        array_other.clear();
        Utils.mDatabase.child(Utils.tbl_group).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        VideoGroup videoGroup = datas.getValue(VideoGroup.class);
                        videoGroup._id = datas.getKey();
                        if (videoGroup.creator_id.equals(Utils.mUser.getUid())) {
                            array_my.add(videoGroup);
                        } else {
                            for (String uid:videoGroup.member_ids) {
                                if (uid.equals(Utils.mUser.getUid())) {
                                    array_other.add(videoGroup);
                                    break;
                                }
                            }
                        }
                    }
                }
                videoGroupListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        read_videoGroup();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}