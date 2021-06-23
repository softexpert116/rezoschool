package com.ediattah.rezoschool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetViewListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.Message;
import com.ediattah.rezoschool.Model.Ministry;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.Transaction;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.Model.VideoGroup;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.TeacherListAdapter;
import com.ediattah.rezoschool.httpsModule.RestClient;
import com.ediattah.rezoschool.service.MenuRefreshCallback;
import com.ediattah.rezoschool.service.NotificationCallback;
import com.ediattah.rezoschool.ui.BulkSMSActivity;
import com.ediattah.rezoschool.ui.ChatActivity;
import com.ediattah.rezoschool.ui.CourseCalendarActivity;
import com.ediattah.rezoschool.ui.LoginActivity;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewPaymentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class App extends Application implements LifecycleObserver {

    public static String En = "english";
    public static String Fr = "french";
    public static String De = "deutsch";

    public static App app;
    public static SharedPreferences prefs;
    private static Context mContext;

    public static String TIME_FORMAT = "h:mm a"; //, dd/MM/yyyy";
    public static String DATE_FORMAT = "dd/MM/yyyy";
    public static String NewMessage = "NewMessage";
    public static String NewVideoGroup = "NewVideoGroup";
    public static String NewVideoCall = "NewVideoMessage";
    public static ArrayList<Course> school_courses = new ArrayList<>();
//    public static ArrayList<JSONObject> array_videoCall = new ArrayList<>();

    public static String PUSH_CHAT = "PUSH_CHAT";
    public static String PUSH_VIDEO = "PUSH_VIDEO";
    public static String PUSH_VIDEO_GROUP = "PUSH_VIDEO_GROUP";

    public static String MY_APP_PATH = "";
    public static String MY_IMAGE_PATH = "";
    public static String MY_AUDIO_PATH = "";
    public static String ediapayUrl = "https://api.ediapay.com/api/";
    public static String ediaSMSUrl = "https://smpp1.valorisetelecom.com/api/api_http.php";


    private static final int MAX_SMS_MESSAGE_LENGTH = 160;
    public static NotificationCallback notificationCallback;
    public static MenuRefreshCallback menuRefreshCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        app = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        getFCMToken();
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Utils.array_account_type = new ArrayList<>();
        Utils.array_account_type.add(Utils.SCHOOL);
        Utils.array_account_type.add(Utils.TEACHER);
        Utils.array_account_type.add(Utils.PARENT);
        Utils.array_account_type.add(Utils.STUDENT);
        Utils.array_account_type.add(Utils.MINISTRY);

        Utils.array_school_area = new ArrayList<>();
        Utils.array_school_area.add("DREN");
        Utils.array_school_area.add("DDEN");
        Utils.array_school_area.add("IEP");

    }
    void getFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        setPreference("DEVICE_TOKEN", token);
                    }
                });
    }
    public static void sendEdiaSMS(Context context, String username, String password, String sender, ArrayList<String> array_receivers, String text, String type) {
        ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setMessage(context.getResources().getString(R.string.sending_sms));
        mDialog.setCancelable(false);
        mDialog.show();
        String to = TextUtils.join(";", array_receivers);
        String datetime = Utils.getCurrentDateTimeString();
//        final JSONObject object = new JSONObject();
//        try {
//            object.put("username", username);
//            object.put("password", password);
//            object.put("sender", sender);
//            object.put("to", to);
//            object.put("text", text);
//            object.put("type", type);
//            object.put("datetime", datetime);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("sender", sender));
        nameValuePairs.add(new BasicNameValuePair("to", to));
        nameValuePairs.add(new BasicNameValuePair("text", text));
        nameValuePairs.add(new BasicNameValuePair("type", type));
        nameValuePairs.add(new BasicNameValuePair("datetime", datetime));

        final RestClient restClient = RestClient.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = null;
                try {
                    response = restClient.postRequest1(App.ediaSMSUrl, nameValuePairs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.contains("OK:")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, context.getResources().getString(R.string.successfully_sent), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (response.contains("ERROR:")) {
                    final String error = response.substring(7);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showAlert(context, context.getResources().getString(R.string.error), error);
                        }
                    });
                }
                mDialog.dismiss();

            }
        }).start();
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        Log.d("MyApp", "App in background");
//        Toast.makeText(getApplicationContext(), "Background", Toast.LENGTH_SHORT).show();
        setStatus(0);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        Log.d("MyApp", "App in foreground");
//        Toast.makeText(getApplicationContext(), "Foreground", Toast.LENGTH_SHORT).show();
        setStatus(1);
    }
    public static void setStatus(int status) {
        if (Utils.mUser != null) {
            Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_STATUS).setValue(status);
            Utils.currentUser.status = status;
        }
    }

    public static void getSchoolInfo() {
        Utils.mDatabase.child(Utils.tbl_school).orderByChild("uid").equalTo(Utils.mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        School school = datas.getValue(School.class);
                        school._id = datas.getKey();
                        Utils.currentSchool = school;
                        school_courses.clear();
                        for (Class _class:Utils.currentSchool.classes) {
                            for (Course _course:_class.courses) {
                                school_courses.add(_course);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static void getMinistryInfo() {
        Utils.mDatabase.child(Utils.tbl_ministry).orderByChild("uid").equalTo(Utils.mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        Ministry ministry = datas.getValue(Ministry.class);
                        ministry._id = datas.getKey();
                        Utils.currentMinistry = ministry;
                    }
                    menuRefreshCallback.OnRefresh();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static void getStudentInfo() {
        Utils.mDatabase.child(Utils.tbl_school).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        School school = datas.getValue(School.class);
                        school._id = datas.getKey();
                        for (Student student:school.students) {
                            if (student.uid.equals(Utils.mUser.getUid())) {
                                student.school_id = school._id;
                                Utils.currentSchool = school;
                                Utils.currentStudent = student;
                                for (Class _class:school.classes) {
                                    if (_class.name.equals(student.class_name)) {
                                        Utils.currentClass = _class;
                                        break;
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static void goToJoinVideoCall(String room, Context context) {
        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setWelcomePageEnabled(false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        JitsiMeetConferenceOptions options
                = new JitsiMeetConferenceOptions.Builder()
                .setRoom(room)
                .setFeatureFlag("recording.enabled",false)
                .setFeatureFlag("chat.enabled", false)
                .setFeatureFlag("pip.enabled",false)
                .setFeatureFlag("add-people.enabled",false)
                .setFeatureFlag("calendar.enabled",false)
                .setFeatureFlag("conference-timer.enabled",false)
                .setFeatureFlag("close-captions.enabled",false)
                .setFeatureFlag("invite.enabled",false)
                .setFeatureFlag("live-streaming.enabled",false)
                .setFeatureFlag("meeting-name.enabled",false)
                .setFeatureFlag("meeting-password.enabled",false)
                .setFeatureFlag("raise-hand.enabled",false)
                .setFeatureFlag("server-url-change.enabled",false)
                .setFeatureFlag("tile-view.enabled",false)
                .setFeatureFlag("video-share.enabled",false)
                .build();
        // Launch the new activity with the given options. The launch() method takes care
        // of creating the required Intent and passing the options.
        JitsiMeetActivity.launch(context, options);
    }
    public static void goToStartVideoCallPage(User user, Context context) {
        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setWelcomePageEnabled(false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        String room = getTimestampString();
        JitsiMeetConferenceOptions options
                = new JitsiMeetConferenceOptions.Builder()
                .setRoom(room)
                .setFeatureFlag("recording.enabled",false)
                .setFeatureFlag("chat.enabled", false)
                .setFeatureFlag("pip.enabled",false)
                .setFeatureFlag("add-people.enabled",false)
                .setFeatureFlag("calendar.enabled",false)
                .setFeatureFlag("conference-timer.enabled",false)
                .setFeatureFlag("close-captions.enabled",false)
                .setFeatureFlag("invite.enabled",false)
                .setFeatureFlag("live-streaming.enabled",false)
                .setFeatureFlag("meeting-name.enabled",false)
                .setFeatureFlag("meeting-password.enabled",false)
                .setFeatureFlag("raise-hand.enabled",false)
                .setFeatureFlag("server-url-change.enabled",false)
                .setFeatureFlag("tile-view.enabled",false)
                .setFeatureFlag("video-share.enabled",false)
                .build();
        // Launch the new activity with the given options. The launch() method takes care
        // of creating the required Intent and passing the options.
        JitsiMeetActivity.launch(context, options);
//        HashMap hashMap_record = new HashMap<String, String>();           //some random data
//        hashMap_record.put("caller", Utils.mUser.getUid());
//        hashMap_record.put("receiver", user._id);
//        Utils.mDatabase.child(Utils.tbl_video_call).child(room).setValue(hashMap_record);
        sendPushMessage(user.token, context.getResources().getString(R.string.video_call_from_) + " " + Utils.currentUser.name, context.getString(R.string.please_join_in_room_) + " '" + room + "'", "", room, context, App.PUSH_VIDEO, Utils.mUser.getUid());
    }
    public static void goToStartG_VideoCallPage(VideoGroup videoGroup, Context context) {
        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setWelcomePageEnabled(false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

                JitsiMeetConferenceOptions options
                        = new JitsiMeetConferenceOptions.Builder()
                        .setRoom(videoGroup.room)
                        .setFeatureFlag("recording.enabled",false)
                        .setFeatureFlag("chat.enabled", false)
                        .setFeatureFlag("pip.enabled",false)
                        .setFeatureFlag("add-people.enabled",false)
                        .setFeatureFlag("calendar.enabled",false)
                        .setFeatureFlag("conference-timer.enabled",false)
                        .setFeatureFlag("close-captions.enabled",false)
                        .setFeatureFlag("invite.enabled",false)
                        .setFeatureFlag("live-streaming.enabled",false)
                        .setFeatureFlag("meeting-name.enabled",false)
                        .setFeatureFlag("meeting-password.enabled",false)
                        .setFeatureFlag("raise-hand.enabled",false)
                        .setFeatureFlag("server-url-change.enabled",false)
                        .setFeatureFlag("tile-view.enabled",false)
                        .setFeatureFlag("video-share.enabled",false)
                        .build();
                // Launch the new activity with the given options. The launch() method takes care
                // of creating the required Intent and passing the options.
                JitsiMeetActivity.launch(context, options);
                for (String uid:videoGroup.member_ids) {
                    Utils.mDatabase.child(Utils.tbl_user).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!=null) {
                                User user = dataSnapshot.getValue(User.class);
                                sendPushMessage(user.token, context.getResources().getString(R.string.video_call_from_) + " " + Utils.currentUser.name, context.getString(R.string.please_join_in_group_) + " '" + videoGroup.name + "'", "", videoGroup.room, context, PUSH_VIDEO_GROUP, Utils.mUser.getUid());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
//            }
//        });
    }
    public static void goToChatPage(final Context context, final String user_id) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setMessage("Do you want to open chat with this person?");
//        builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog,int id) {
//                        ArrayList<Teacher> teachers = school.teachers;
                String myUid = Utils.mUser.getUid();
                final String roomId;
                int compare = myUid.compareTo(user_id);
                final String user1, user2;
                if (compare < 0) {
                    user1 = myUid;
                    user2 = user_id;
                } else {
                    user1 = user_id;
                    user2 = myUid;
                }
                roomId = user1 + user2;

                Utils.mDatabase.child(Utils.tbl_chat).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            Utils.mDatabase.child(Utils.tbl_chat).child(roomId).child("messages").push().setValue(new Message());
                            Utils.mDatabase.child(Utils.tbl_chat).child(roomId).child("isTyping").child(user1).setValue(false);
                            Utils.mDatabase.child(Utils.tbl_chat).child(roomId).child("isTyping").child(user2).setValue(false);
                        }
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("roomId", roomId);
                        context.startActivity(intent);

                        setPreference(App.NewMessage, "");
//                        ArrayList<String> array_message = App.readPreference_array_String(App.NewMessage);
//                        if (array_message.contains(user_id)) {
//                            array_message.remove(user_id);
//                            App.setPreference_array_String(App.NewMessage, array_message);
//                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//            }
//        });
//        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
    }
    public static void goToMainPage(final Activity activity) {
        //get user info into model
        String phone_num = Utils.mUser.getPhoneNumber();
        if (phone_num.length() == 0) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
            activity.finish();
            return;
        }
        phone_num = phone_num.substring(1);
        Utils.mDatabase.child(Utils.tbl_user).orderByChild(Utils.USER_PHONE).equalTo(phone_num)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                Utils.currentUser = datas.getValue(User.class);
                                if (!Utils.currentUser.isAllow) {
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(activity, activity.getResources().getString(R.string.you_are_not_allowed), Toast.LENGTH_LONG).show();
//                                    Utils.showAlert(activity, "Warning", "You are not allowed yet.");
                                    Intent intent = new Intent(activity, LoginActivity.class);
                                    activity.startActivity(intent);
                                } else {
                                    Utils.currentUser._id = Utils.mUser.getUid();
//status update
                                    setStatus(1);
//token update
                                    String token = Utils.getDeviceToken(activity);
                                    Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_TOKEN).setValue(token);
                                    // go to main page
                                    if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
                                        getSchoolInfo();
                                    } else if (Utils.currentUser.type.equals(Utils.STUDENT)) {
                                        getStudentInfo();
                                    } else if (Utils.currentUser.type.equals(Utils.MINISTRY)) {
                                        getMinistryInfo();
                                    }
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    activity.startActivity(intent);
                                }

                            }
                            activity.finish();
                        } else {
                            Utils.auth.signOut();
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });
    }

    //----------------------------------fcm push notification----------------------------------
    @SuppressLint("StaticFieldLeak")
    public static void sendPushMessage(final String token, final String title, final String body, final String icon, final String room, final Context context, String push_type, String user_id) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    JSONObject root = new JSONObject();
//                    JSONObject notification = new JSONObject();
//                    notification.put("body", body);
//                    notification.put("title", title);
//                    notification.put("icon", icon);

                    JSONObject data = new JSONObject();
                    JSONObject subData = new JSONObject();
                    subData.put("push_type", push_type);
                    subData.put("user_id", user_id);
                    subData.put("body", body);
                    subData.put("title", title);
                    subData.put("room", room);
                    data.put("message", "");
//                    root.put("notification", notification);
                    root.put("data", subData);
                    root.put("to", token);
//                    root.put("registration_ids", recipients);

                    String result = postToFCM(root.toString());
                    Log.d("Main Activity", "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    int success, failure;
                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");
//                    Toast.makeText(context, "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(context, "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    static String postToFCM(String bodyString) throws IOException {
        final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + Utils.fbServerKey)
                .build();
        OkHttpClient mClient = new OkHttpClient();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }
//---------------------------------------------------------------------------------

    public static String sendEdiaSMS1(Context context, String username, String password, String sender, ArrayList<String> array_receivers, String text, String type) throws IOException {
        String to = TextUtils.join(";", array_receivers);
        String datetime = Utils.getCurrentDateTimeString();
//        final String urlSMS = "https://smpp1.valorisetelecom.com/api/api_http.php?username=" + username + "&password=" + password + "&sender=" + sender + "&to=" + to + "&text=" + text + "&type=" + type + "&datetime=" + datetime;
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", username)
                .addFormDataPart("password", password)
                .addFormDataPart("sender", sender)
                .addFormDataPart("to", to)
                .addFormDataPart("text", text)
                .addFormDataPart("type", type)
                .addFormDataPart("datetime", datetime)
                .build();
        Request request = new Request.Builder()
                .url(ediaSMSUrl)
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        OkHttpClient mClient = new OkHttpClient();
//        Response response = mClient.newCall(request).execute();
        Response response = null;
        try {
            response = mClient.newCall(request).execute();

            // Do something with the response.
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.body().string();
    }

//    http://smpp1.valorisetelecom.com/api/api_http.php?
//    username=test2&password=7357378ujs&sender=TEST2
//&to=22564906603;22564906604;22564906605&text=Hello%20world
//&type=text&datetime=2020-08-06%2002%3A58%3A00

    public static String getApplicationName() {
        ApplicationInfo applicationInfo = mContext.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : mContext.getString(stringId);
    }
    public static void sendSMS(String phoneNo, String msg) {
        SmsManager manager = SmsManager.getDefault();
        PendingIntent piSend = PendingIntent.getBroadcast(mContext, 0, new Intent("SMS_SENT"), 0);
        PendingIntent piDelivered = PendingIntent.getBroadcast(mContext, 0, new Intent("SMS_DELIVERED"), 0);
        if(msg.length() > MAX_SMS_MESSAGE_LENGTH)
        {
            ArrayList<String> messagelist = manager.divideMessage(msg);

            manager.sendMultipartTextMessage(phoneNo, null, messagelist, null, null);
        }
        else
        {
            manager.sendTextMessage(phoneNo, null, msg, piSend, piDelivered);
        }
//        sms.sendTextMessage(phoneNo, null, msg, piSend, piDelivered);
//        Toast.makeText(mContext, mContext.getString(R.string.sms_success_message), Toast.LENGTH_LONG).show();
//
//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
//            Toast.makeText(mContext, mContext.getString(R.string.sms_success_message),
//                    Toast.LENGTH_LONG).show();
//        } catch (Exception ex) {
//            Toast.makeText(mContext,ex.getMessage().toString(),
//                    Toast.LENGTH_LONG).show();
//            ex.printStackTrace();
//        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    public static void setPreferences(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setPreference(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(key, value)
                .commit();
    }
    public static String readPreference(String key, String defaultValue) {
        String value = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(key, defaultValue);
        return value;
    }
    public static void removePreference(String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }

    //    public static void setPreference_JsonObject(String key, JSONObject jsonObject) {
//        Gson gson = new Gson();
//        String json = gson.toJson(jsonObject);
//        setPreference(key, json);
//    }
//    public static JSONObject readPreference_JsonObject(String key) {
//        String json = readPreference(key, "");
//        Type type = new TypeToken<JSONObject>(){}.getType();
//        Gson gson = new Gson();
//        JSONObject jsonObject = gson.fromJson(json, type);
//        return jsonObject;
//    }
//    public static String getSelectedLang() {
//        String lang = En;
//        JSONObject jsonObject = App.readPreference_JsonObject(App.MY_INFO);
//        if (jsonObject != null) {
//            lang = getJsonValue(jsonObject, "lang");
//        }
//        return lang;
//    }
//    public static Configuration getSelectedConfiguration() {
//        String lang = En;
//        JSONObject jsonObject = App.readPreference_JsonObject(App.MY_INFO);
//        if (jsonObject != null) {
//            lang = getJsonValue(jsonObject, "lang");
//        }
//        Configuration config = new Configuration();
//        if (lang.equals(App.Fr)) {
//            config.locale = Locale.FRENCH;
//        } else if (lang.equals(App.De)) {
//            config.locale = Locale.GERMAN;
//        } else {
//            config.locale = Locale.ENGLISH;
//        }
//        return config;
//    }
    public static String getJsonValue(JSONObject jsonObject, String key) {
        String value = "";
        try {
            value = jsonObject.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    public static void setPreference_array_String(String key, ArrayList<String> list) {
        Set<String> tasksSet = new HashSet<String>(list);
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putStringSet(key, tasksSet)
                .commit();
    }

    public static ArrayList<String> readPreference_array_String(String key) {
        Set<String> tasksSet = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getStringSet(key, new HashSet<String>());
        ArrayList<String> tasksList = new ArrayList<String>(tasksSet);
        return tasksList;
    }
    public static void showToast(String string) {
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();

    }
    public static void cancelAllNotifications() {
        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void social_share(Context context, String url) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
    }
    public void generateHashkey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.ujs.acer.Oyoo",  PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                String hashKey = Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                Log.e("hashkey -------------", hashKey); /// CkLR2IIEs9xCrDGJbKOQ/Jr3exE=
// release key hash: w6gx2BgXV0ybPMNC4PfbKnfpu50=
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {
        }
    }
    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int count = listAdapter.getCount();
        int totalHeight = 0;
        for (int i = 0; i < count; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        params.height *= 1.5;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    public static void DialNumber(String number, Context context)
    {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
//            intent.setData(Uri.parse("tel:" + number));
            Uri uri = ussdToCallableUri(number);
            intent.setData(uri);
//            context.startActivity(intent);
        } catch (SecurityException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT);
        }
    }

    private static Uri ussdToCallableUri(String ussd) {

        String uriString = "";

        if(!ussd.startsWith("tel:"))
            uriString += "tel:";

        for(char c : ussd.toCharArray()) {

            if(c == '#')
                uriString += Uri.encode("#");
            else
                uriString += c;
        }

        return Uri.parse(uriString);
    }

    public static void openUrl (String url, Context context) {
        if (!URLUtil.isValidUrl(url)) {
            Toast.makeText(context, "Invalid url", Toast.LENGTH_SHORT);
            return;
        }
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static String getTimestampString()
    {
        long tsLong = System.currentTimeMillis();
        String ts =  Long.toString(tsLong);
        return ts;
    }
    public static int getPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = mContext.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr = mContext.obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary});
        int primaryColor = arr.getColor(0, -1);
        return primaryColor;
    }
    public static String analyzeScoreResult(int score) {
        String result;
        if (score <= 5) {
            result = "Bad";
        } else if (score <= 10) {
            result = "Good";
        } else {
            result = "Excellent";
        }
        return result;
    }
    public static int analyzeScoreColor(Context context, int score) {
        int color;
        if (score <= 5) {
            color = context.getResources().getColor(R.color.colorText);
        } else if (score <= 10) {
            color = context.getResources().getColor(R.color.colorPrimary);
        } else {
            color = context.getResources().getColor(R.color.colorAccent);
        }
        return color;
    }
}