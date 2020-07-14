package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.ChatRoom;
import com.ediattah.rezoschool.Model.Comment;
import com.ediattah.rezoschool.Model.Message;
import com.ediattah.rezoschool.Model.Tweet;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.ChatListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    String roomId;
    User user;
    ListView listView;
    ChatListAdapter chatListAdapter;
    ArrayList<Message> arrayList = new ArrayList<>();
    private Uri imgUri;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Chat");
        App.hideKeyboard(this);
        roomId = getIntent().getStringExtra("roomId");
        user_id = Utils.getChatUserId(roomId);
//        load_user(user_id);

        listView = findViewById(R.id.listView);
        chatListAdapter = new ChatListAdapter(this, arrayList);
        listView.setAdapter(chatListAdapter);

        Button btn_file = findViewById(R.id.btn_file);
        btn_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ChatActivity.this);
            }
        });
        Button btn_send = findViewById(R.id.btn_send);
        final EditText edit_message = findViewById(R.id.edit_message);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = edit_message.getText().toString().trim();
                if (msg.length() == 0) {
                    return;
                }
                Message message = new Message("", Utils.mUser.getUid(), user_id, msg, "", System.currentTimeMillis());
                Utils.mDatabase.child(Utils.tbl_chat).child(roomId).push().setValue(message);
                edit_message.setText("");
            }
        });
        load_user(user_id);
        readMessages();
    }

    void readMessages() {
        arrayList.clear();
        Utils.mDatabase.child(Utils.tbl_chat).child(roomId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue()!=null) {
                    Message message = dataSnapshot.getValue(Message.class);
                    message._id = dataSnapshot.getKey();
                    arrayList.add(message);
                    chatListAdapter.notifyDataSetChanged();
                    listView.smoothScrollToPosition(arrayList.size()-1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue()!=null) {
                    Message message = dataSnapshot.getValue(Message.class);
                    message._id = dataSnapshot.getKey();
                    arrayList.add(message);
                    chatListAdapter.notifyDataSetChanged();
                    listView.smoothScrollToPosition(arrayList.size()-1);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    Message message = dataSnapshot.getValue(Message.class);
                    message._id = dataSnapshot.getKey();
                    for (int i = 0; i < arrayList.size(); i++) {
                        Message message1 = arrayList.get(i);
                        if (message1._id.equals(message._id)) {
                            arrayList.remove(i);
                            chatListAdapter.notifyDataSetChanged();
                            listView.smoothScrollToPosition(i-1);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void load_user(String user_id) {
        Utils.mDatabase.child(Utils.tbl_user).child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    user = dataSnapshot.getValue(User.class);
                    setTitle("Chat With " + user.name);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
//                imgUri = result.getUri();
                uploadPictureToFirebase(result.getUri());
//                Glide.with(EventCreateActivity.this).load(result.getUri()).centerCrop().placeholder(R.drawable.profile).dontAnimate().into(img_event);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }
    void uploadPictureToFirebase(Uri imgUri) {
// Create the file metadata
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        final StorageReference file_refer = Utils.mStorage.child("chats/"+ts);

        // Listen for state changes, errors, and completion of the upload.
        file_refer.putFile(imgUri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                file_refer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.dismiss();
                        String downloadUrl = uri.toString();
                        Message message = new Message("", Utils.mUser.getUid(), user_id, "", downloadUrl, System.currentTimeMillis());
                        Utils.mDatabase.child(Utils.tbl_chat).child(roomId).push().setValue(message);
                    }
                });
            }

        });
    }
    public void delete_message(Message message) {
        Utils.mDatabase.child(Utils.tbl_chat).child(roomId).child(message._id).setValue(null);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}