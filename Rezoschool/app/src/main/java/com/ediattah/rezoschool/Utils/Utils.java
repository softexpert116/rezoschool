package com.ediattah.rezoschool.Utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.ediattah.rezoschool.Model.ChatRoom;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.CLIPBOARD_SERVICE;

public class Utils {
    public static String fbServerKey = "AAAAJ7tB2oI:APA91bFvxlvHaF4rIhvkhMq6BQu6vQGpdGnT_ntT5m26vx2rxM30I7m4VUQXAeOxXz0oKOxAZy2cEphB-SDAF3OIjr0-RujKPxYJ3Gbjp-lkSoKptjDCBSXCMB2SYTXo-2IWArYGpRrW";
    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static StorageReference mStorage = FirebaseStorage.getInstance().getReference();
//    public static FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    public static FirebaseUser mUser = auth.getCurrentUser();
    public static User currentUser = new User();
    public static School currentSchool = new School();
    public static Student currentStudent = new Student();
    public static Class currentClass = new Class();
//    public static Location currentLocation;
//    public static String mFuncUrl = "https://us-central1-taxikini-9a743.cloudfunctions.net";
    public static String tbl_user = "users";
    public static String tbl_school = "schools";
    public static String tbl_tweet = "tweets";
    public static String tbl_library = "library";
    public static String tbl_exam = "exams";
    public static String tbl_absence = "absences";
    public static String tbl_syllabus = "syllabus";
    public static String tbl_chat = "chats";
    public static String tbl_transaction = "transactions";
    public static String tbl_parent_student = "parent_student";
    public static String tbl_group = "groups";

    public static String USER_NAME = "name";
    public static String USER_TYPE = "type";
    public static String USER_EMAIL = "email";
    public static String USER_PHOTO = "photo";
    public static String USER_PHONE = "phone";
    public static String USER_COUNTRY = "country";
    public static String USER_CITY = "city";
    public static String USER_TOKEN = "token";
    public static String USER_ALLOW = "isAllow";
    public static String USER_STATUS = "status";

    public static String SCHOOL_NUMBER = "number";
    public static String SCHOOL_TYPE = "type";
    public static String SCHOOL_PUBLIC = "isPublic";


    public static String SCHOOL = "SCHOOL";
    public static String TEACHER = "TEACHER";
    public static String PARENT = "PARENT";
    public static String STUDENT = "STUDENT";
    public static String PRIMARY = "PRIMARY";
    public static String SECONDARY = "SECONDARY";
    public static DecimalFormat df = new DecimalFormat("0.00");

    public static void copy_text(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
    }
    public static String getDayStrFromInt(int dayOfWeek) {
        String day = "Monday";
        switch (dayOfWeek) {
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
            case 1:
                day = "Sunday";
            default:
        }
        return day;
    }

    public static void shareImage(Context context, String path) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(String.valueOf(path));

        if(fileWithinMyDir.exists()) {
            intentShareFile.setType("application/pdf");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+path));

            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

            context.startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }
    public static String getChatUserId(String roomId) {
        String user_id;
        int index = roomId.indexOf(Utils.mUser.getUid());
        if (index == 0) {
            user_id = roomId.substring(Utils.mUser.getUid().length());
        } else {
            user_id = roomId.substring(0, roomId.length()-Utils.mUser.getUid().length());
        }
        return user_id;
    }
    public static Boolean CheckEditTextIsEmptyOrNot(EditText editText) {

        // Getting values from EditText.
        String text = editText.getText().toString().trim();

        // Checking whether EditText value is empty or not.
        if (TextUtils.isEmpty(text)) {

            // If any of EditText is empty then set variable value as False.
            return false;

        } else {

            // If any of EditText is filled then set variable value as True.
            return true;
        }
    }
    public static long getLastDayOfMonth(Date sel_date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(sel_date);
//        int res = cal.getActualMaximum(Calendar.DATE);
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        cal.set(Calendar.AM_PM, 0);
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date date = cal.getTime();
        return cal.getTimeInMillis();
    }
    public static long getFirstDayOfMonth(Date sel_date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sel_date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.AM_PM, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
//        calendar.set(Calendar.DAY_OF_MONTH, Calendar.SUNDAY);
        Date date = calendar.getTime();
        return calendar.getTimeInMillis();
    }
    public static long getFirstDayOfWeek(Date sel_date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sel_date);
        calendar.set(Calendar.AM_PM, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return calendar.getTimeInMillis();
    }

    public static String convertFromBitmap(Bitmap bm) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    public static void showAlert(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
    public static Uri getLocalBitmapUri(Context context, Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public static String getDeviceToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("DEVICE_TOKEN", "");
    }
//    public static Place getCurrentPlace(Context context) {
//        String name = "", address = "", id = "";
//        try {
//            Geocoder geo = new Geocoder(context, Locale.getDefault());
//            List<Address> addresses = geo.getFromLocation(Util.currentLocation.getLatitude(), Util.currentLocation.getLongitude(), 1);
//            if (!addresses.isEmpty()) {
//                if (addresses.size() > 0) {
//                    name = addresses.get(0).getFeatureName();
//                    address = addresses.get(0).getAddressLine(0);
////                    yourtextboxname.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return Place.builder().setLatLng(new LatLng(Util.currentLocation.getLatitude(), Util.currentLocation.getLongitude())).setName(name).setAddress(address).setId(id).build();
//    }

    public static String getYearMonthString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(date);
    }
    public static String getCurrentDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(new Date(System.currentTimeMillis()));
    }
    public static String getCurrentDateTimeString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return formatter.format(new Date(System.currentTimeMillis()));
    }
    public static String getDateString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }
    public static String getTimeString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        return formatter.format(date);
    }
}
