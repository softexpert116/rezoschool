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
import com.ediattah.rezoschool.Model.Ministry;
import com.ediattah.rezoschool.Model.PsychologySubmit;
import com.ediattah.rezoschool.Model.Quarter;
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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.CLIPBOARD_SERVICE;

public class Utils {
    public static String fbServerKey = "AAAAJ7tB2oI:APA91bEKxDcr5ZBvXwhtcx9p-_tHAfaEk3QUDbgB-lOVKfkl5Nvam1YTmsTH8Irx5u7yJaI8IROTOJTwKT1ZbjeUpbHyre52xd3qa8K_ezPeH_roYbGsJKT_D3Qq9-o6lyMq0E5IInwy";
    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static StorageReference mStorage = FirebaseStorage.getInstance().getReference();
//    public static FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    public static FirebaseUser mUser = auth.getCurrentUser();
    public static User currentUser = new User();
    public static School currentSchool = new School();
    public static Student currentStudent = new Student();
    public static Class currentClass = new Class();
    public static Ministry currentMinistry = new Ministry();
    public static PsychologySubmit currentPsychologySubmit = new PsychologySubmit();
//    public static Location currentLocation;
//    public static String mFuncUrl = "https://us-central1-taxikini-9a743.cloudfunctions.net";
    public static String tbl_user = "users";
    public static String tbl_school = "schools";
    public static String tbl_tweet = "tweets";
    public static String tbl_report = "reports";
    public static String tbl_library = "library";
    public static String tbl_exam = "exams";
    public static String tbl_absence = "absences";
    public static String tbl_syllabus = "syllabus";
    public static String tbl_chat = "chats";
    public static String tbl_transaction = "transactions";
    public static String tbl_parent_student = "parent_student";
    public static String tbl_group = "groups";
    public static String tbl_psychology_section = "psychology_section";
    public static String tbl_psychology_result = "psychology_result";
    public static String tbl_psychology_submit = "psychology_submit";
    public static String tbl_ministry = "ministry";

    public static String USER_USERNAME = "username";
    public static String USER_PASSWORD = "password";
    public static String USER_SENDERID = "senderID";
    public static String USER_NAME = "name";
    public static String USER_FIRSTNAME = "firstname";
    public static String USER_LASTNAME = "lastname";
    public static String USER_TYPE = "type";
    public static String USER_EMAIL = "email";
    public static String USER_BIRTHDAY = "birthday";
    public static String USER_PHOTO = "photo";
    public static String USER_PHONE = "phone";
    public static String USER_COUNTRY = "country";
    public static String USER_CITY = "city";
    public static String USER_TOKEN = "token";
    public static String USER_ALLOW = "isAllow";
    public static String USER_STATUS = "status";

    public static String SCHOOL_NUMBER = "number";
    public static String SCHOOL_TYPE = "type";
    public static String SCHOOL_AREA = "area";
    public static String SCHOOL_PUBLIC = "isPublic";
    public static String MINISTRY_TYPE = "type";

    public static ArrayList<String> array_school_area = new ArrayList<>();
    public static ArrayList<String> array_account_type = new ArrayList<>();


    public static String SUPERVISOR = "SUPERVISOR";
    public static String MIDDLE_STAFF = "MIDDLE STAFF";
    public static String FILED_AGENT = "FILED AGENT";

    public static String SCHOOL = "SCHOOL";
    public static String TEACHER = "TEACHER";
    public static String PARENT = "PARENT";
    public static String STUDENT = "STUDENT";
    public static String MINISTRY = "MINISTRY STAFF";
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

    public static Quarter getCurrentQuarter(Date sel_date) {
        int m = sel_date.getMonth();
        int y = sel_date.getYear() + 1900;
        long first_time = 0, last_time = 0;
        String name = "1";
        if (m >= 0 && m <= 2) {
            first_time = getTimestampFromDateString("01-01-" + String.valueOf(y));
            last_time = getTimestampFromDateString("31-03-" + String.valueOf(y));
            name = "1";
        } else if (m >= 3 && m <= 5) {
            first_time = getTimestampFromDateString("01-04-" + String.valueOf(y));
            last_time = getTimestampFromDateString("30-06-" + String.valueOf(y));
            name = "2";
        } else if (m >= 6 && m <= 8) {
            first_time = getTimestampFromDateString("01-07-" + String.valueOf(y));
            last_time = getTimestampFromDateString("31-09-" + String.valueOf(y));
            name = "3";
        } else if (m >= 9 && m <= 11) {
            first_time = getTimestampFromDateString("01-10-" + String.valueOf(y));
            last_time = getTimestampFromDateString("31-12-" + String.valueOf(y));
            name = "4";
        }
        Quarter quarter = new Quarter(name, first_time, last_time);
        return quarter;
    }
    public static long getTimestamp() {
        return System.currentTimeMillis()/1000;
    }
    public static long getTimestampFromDateString(String dateStr) {
        String str_date= dateStr; //"13-09-2011";
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        try {
            date = (Date)formatter.parse(str_date);
        } catch (Exception e) {

        }
        System.out.println("Today is " +date.getTime());
        return date.getTime();
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
    public static String getDateStringFromTimestamp(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String dateString = formatter.format(new Date(timestamp));
        return dateString;
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
    public static boolean checkZeroArray(ArrayList<Float> array) {
        boolean flag = true;
        for (Float val:array) {
            if (val > 0) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}
