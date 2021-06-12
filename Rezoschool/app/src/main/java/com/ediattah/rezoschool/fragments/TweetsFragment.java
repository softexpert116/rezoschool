package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.Comment;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.ui.ChatActivity;
import com.ediattah.rezoschool.ui.TweetDetailActivity;
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
    LinearLayout ly_header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tweets, container, false);


        ly_header = v.findViewById(R.id.ly_header);
        listView = v.findViewById(R.id.listView);
        tweetListAdapter = new TweetListAdapter(activity, TweetsFragment.this, arrayList);
        listView.setAdapter(tweetListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, TweetDetailActivity.class);
                intent.putExtra("tweet", arrayList.get(position));
                startActivity(intent);
            }
        });


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
// horizontal scroll view
                            final LayoutInflater inflater = LayoutInflater.from(activity);
                            ly_header.removeAllViews();
                            for(Tweet tweet: arrayList) {
                                if (tweet.like < 3)
                                    continue;
                                View view1 = inflater.inflate(R.layout.cell_hview, null);
                                ImageView img_media = view1.findViewById(R.id.img_media);
                                img_media.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(activity, TweetDetailActivity.class);
                                        intent.putExtra("tweet", tweet);
                                        startActivity(intent);
                                    }
                                });
                                TextView txt_description = view1.findViewById(R.id.txt_description);
                                Glide.with(activity).load(tweet.media).apply(new RequestOptions()
                                        .placeholder(R.drawable.default_pic).centerCrop().dontAnimate()).into(img_media);
                                txt_description.setText(tweet.description);
                                ly_header.addView(view1);
                            }
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