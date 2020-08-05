package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.ediattah.rezoschool.Utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ediattah.rezoschool.Model.Tweet;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.TweetListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewRsTweetsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TweetsFragment extends Fragment {
    MainActivity activity;
    ListView listView;
    ArrayList<Tweet> arrayList = new ArrayList<>();
    TweetListAdapter tweetListAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tweets, container, false);
        listView = v.findViewById(R.id.listView);
        tweetListAdapter = new TweetListAdapter(activity, TweetsFragment.this, arrayList);
        listView.setAdapter(tweetListAdapter);


        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, NewRsTweetsActivity.class);
                activity.startActivity(intent);
            }
        });
        read_tweets();
        return v;
    }
    public void read_tweets() {

        Utils.mDatabase.child(Utils.tbl_tweet).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    arrayList.clear();
                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                        Tweet tweet = childSnapshot.getValue(Tweet.class);
                        tweet._id = childSnapshot.getKey();
                        arrayList.add(0, tweet);
                    }
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            tweetListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}