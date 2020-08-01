package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ediattah.rezoschool.Model.ChatRoom;
import com.ediattah.rezoschool.Model.Message;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.MessageListAdapter;
import com.ediattah.rezoschool.ui.ChatActivity;
import com.ediattah.rezoschool.ui.MainActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class MessageFragment extends Fragment {
    MainActivity activity;
    ArrayList<ChatRoom> arrayList = new ArrayList<>();
    MessageListAdapter messageListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        ListView listView = v.findViewById(R.id.listView);
        messageListAdapter = new MessageListAdapter(activity, arrayList);
        listView.setAdapter(messageListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                messageListAdapter.index_update = -1;
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra("roomId", arrayList.get(i)._id);
                startActivity(intent);
            }
        });
        readMessages();
        return v;
    }

    void readMessages() {
        arrayList.clear();
        Utils.mDatabase.child(Utils.tbl_chat).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue()!=null) {
                    boolean flag = dataSnapshot.getKey().contains(Utils.mUser.getUid());
                    if (flag) {
                        ChatRoom chatRoom = new ChatRoom();
                        chatRoom._id = dataSnapshot.getKey();
                        for (DataSnapshot datas:dataSnapshot.child("messages").getChildren()) {
                            Message message = datas.getValue(Message.class);
                            chatRoom.messages.add(message);
                        }
                        arrayList.add(chatRoom);
                        messageListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue()!=null) {
                    boolean flag = dataSnapshot.getKey().contains(Utils.mUser.getUid());
                    if ( flag) {
                        ChatRoom chatRoom = new ChatRoom();
                        chatRoom._id = dataSnapshot.getKey();
                        for (DataSnapshot datas:dataSnapshot.child("messages").getChildren()) {
                            Message message = datas.getValue(Message.class);
                            chatRoom.messages.add(message);
                        }
                        int index_update = 0;
                        for (int i = 0; i < arrayList.size(); i++) {
                            ChatRoom room = arrayList.get(i);
                            if (room._id.equals(chatRoom._id)) {
                                arrayList.remove(i);
                                index_update = i;
                                break;
                            }
                        }
                        arrayList.add(index_update, chatRoom);
                        messageListAdapter.index_update = index_update;
                        messageListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (messageListAdapter!=null) {
            messageListAdapter.notifyDataSetChanged();
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}