package com.ediattah.rezoschool;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.Message;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.Model.UserModel;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.ui.ChatActivity;
import com.ediattah.rezoschool.ui.LoginActivity;
import com.ediattah.rezoschool.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class App extends Application {

    public static String En = "english";
    public static String Fr = "french";
    public static String De = "deutsch";

    public static App app;
    public static SharedPreferences prefs;
    private static Context mContext;

    public static String TIME_FORMAT = "h:mm a"; //, dd/MM/yyyy";
    public static String DATE_FORMAT = "dd/MM/yyyy";
    public static boolean relogin = false;

    public static String TEACHER = "TEACHER";
    public static String SCHOOL = "SCHOOL";
    public static String STUDENT = "STUDENT";
    public static String PARENT = "PARENT";
    public static String SCHOOL_PRIMARY = "PRIMARY";
    public static String SCHOOL_SECONDARY = "SECONDARY";
    public static String SCHOOL_PRIVATE = "PRIVATE";
    public static String SCHOOL_PUBLIC = "PUBLIC";

    private static final int MAX_SMS_MESSAGE_LENGTH = 160;

    public static ArrayList<Course> array_course = new ArrayList<>();
    public static ArrayList<UserModel> array_teacher = new ArrayList<>();
    public static ArrayList<UserModel> array_school = new ArrayList<>();
//    public static ArrayList<ParseUser> arrayOnlineUser = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        app = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);


//        array_teacher.add(new UserModel(1, "Ivan", "", "iva@gmail.com", "pass", "+225", "CoteDiVoire", "12345678", "Advijan", TEACHER, "English, French", "1234",SCHOOL_PRIVATE, SCHOOL_PRIMARY));
//        array_teacher.add(new UserModel(2, "Bruw", "", "bre@gmail.com", "pass", "+225", "CoteDiVoire", "12345678", "Advijan", TEACHER,  "Mathematics, Physics", "1234",SCHOOL_PRIVATE, SCHOOL_PRIMARY));
//        array_teacher.add(new UserModel(3, "Volic", "", "vol@gmail.com", "pass", "+225", "CoteDiVoire", "12345678", "Advijan", TEACHER, "English, Mathematics", "1234",SCHOOL_PRIVATE, SCHOOL_PRIMARY));
//        array_teacher.add(new UserModel(4, "Olga", "", "ol@gmail.com", "pass", "+225", "CoteDiVoire", "12345678", "Advijan", TEACHER, "English", "1234",SCHOOL_PRIVATE, SCHOOL_PRIMARY));
//
//        array_school.add(new UserModel(1, "School A", "", "", "", "+225", "CoteDiVoire", "12345678", "Advijan", SCHOOL, "English, French", "1234", SCHOOL_PRIVATE, SCHOOL_PRIMARY));
//        array_school.add(new UserModel(2, "School B", "", "", "", "+225", "CoteDiVoire", "12345678", "Advijan", SCHOOL,  "Mathematics, Physics", "1235",SCHOOL_PUBLIC, SCHOOL_SECONDARY));
//        array_school.add(new UserModel(3, "School C", "", "", "", "+225", "CoteDiVoire", "12345678", "Advijan", SCHOOL, "English, Mathematics", "1236",SCHOOL_PRIVATE, SCHOOL_PRIMARY));
//        array_school.add(new UserModel(4, "School D", "", "", "", "+225", "CoteDiVoire", "12345678", "Advijan", SCHOOL, "English", "1237",SCHOOL_PRIVATE, SCHOOL_PRIMARY));
//
//        array_course.add(new Course("English"));
//        array_course.add(new Course("Mathematics"));
//        array_course.add(new Course("Physics"));
    }

    public static void getCurrentSchool() {
        Utils.mDatabase.child(Utils.tbl_school).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        School school = datas.getValue(School.class);
                        if (school.uid.equals(Utils.mUser.getUid())) {
                            Utils.currentSchool = school;
                            Utils.currentSchool._id = datas.getKey();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static void getCurrentStudent() {
        Utils.mDatabase.child(Utils.tbl_student).orderByChild("uid").equalTo(Utils.mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        Utils.currentStudent = datas.getValue(Student.class);
                        Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentStudent.school_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue()!=null) {
                                    Utils.currentSchool = dataSnapshot.getValue(School.class);
                                    Utils.currentSchool._id = dataSnapshot.getKey();
                                    for (Class _class:Utils.currentSchool.classes) {
                                        if (_class.name.equals(Utils.currentStudent.class_name)) {
                                            Utils.currentClass = _class;
                                            break;
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static void goToChatPage(final Context context, final String user_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want to open chat with this person?");
        builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
//                        ArrayList<Teacher> teachers = school.teachers;
                String myUid = Utils.mUser.getUid();
                final String roomId;
                int compare = myUid.compareTo(user_id);
                if (compare < 0) {
                    roomId = myUid + user_id;
                } else {
                    roomId = user_id + myUid;
                }

                Utils.mDatabase.child(Utils.tbl_chat).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            Utils.mDatabase.child(Utils.tbl_chat).child(roomId).push().setValue(new Message());
                        }
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("roomId", roomId);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//                Utils.mDatabase.child(Utils.tbl_chat).child(school._id).child("teachers").setValue(school.teachers);
//                Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
                                    Toast.makeText(activity, "You are not allowed yet.", Toast.LENGTH_LONG).show();
//                                    Utils.showAlert(activity, "Warning", "You are not allowed yet.");
                                    Intent intent = new Intent(activity, LoginActivity.class);
                                    activity.startActivity(intent);
                                } else {
                                    Utils.currentUser._id = Utils.mUser.getUid();
//token update
                                    Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_TOKEN).setValue(Utils.getDeviceToken(activity));
                                    // go to main page
                                    if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
                                        getCurrentSchool();
                                    } else if (Utils.currentUser.type.equals(Utils.STUDENT)) {
                                        getCurrentStudent();
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
}