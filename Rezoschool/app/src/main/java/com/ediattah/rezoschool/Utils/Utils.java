package com.ediattah.rezoschool.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.ediattah.rezoschool.Model.School;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static StorageReference mStorage = FirebaseStorage.getInstance().getReference();
//    public static FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    public static FirebaseUser mUser = auth.getCurrentUser();
    public static User currentUser = new User();
    public static School currentSchool = new School();
//    public static Location currentLocation;
//    public static String mFuncUrl = "https://us-central1-taxikini-9a743.cloudfunctions.net";
    public static String tbl_user = "users";
    public static String tbl_school = "schools";
    public static String tbl_tweet = "tweets";
    public static String tbl_library = "library";
//    public static String tbl_comment = "comments";
//    public static String tbl_course = "courses";
//    public static String tbl_class = "classes";

    public static String USER_NAME = "name";
    public static String USER_TYPE = "type";
    public static String USER_EMAIL = "email";
    public static String USER_PHOTO = "photo";
    public static String USER_PHONE = "phone";
    public static String USER_COUNTRY = "country";
    public static String USER_CITY = "city";
    public static String USER_TOKEN = "token";
    public static String USER_SCHOOL_ID = "school_id";

    public static String SCHOOL_NUMBER = "number";
    public static String SCHOOL_TYPE = "type";
    public static String SCHOOL_PUBLIC = "isPublic";


    public static String SCHOOL = "SCHOOL";
    public static String TEACHER = "TEACHER";
    public static String PARENT = "PARENT";
    public static String STUDENT = "STUDENT";
    public static String PRIMARY = "PRIMARY";
    public static String SECONDARY = "SECONDARY";

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
}
