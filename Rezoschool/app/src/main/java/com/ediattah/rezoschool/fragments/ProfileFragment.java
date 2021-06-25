package com.ediattah.rezoschool.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.Util;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Ministry;
import com.ediattah.rezoschool.Model.Tweet;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewRsTweetsActivity;
import com.ediattah.rezoschool.ui.RegisterActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    MainActivity activity;
    private Uri imgUri;
    ProgressDialog progressDialog;
    ImageView img_photo;
    RelativeLayout ly_status;
    TextView txt_available, txt_unavailable;
    MaterialSpinner spinner_area;
    Ministry cur_ministry;
    LinearLayout ly_name, ly_firstname, ly_lastname, ly_birthday;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        App.hideKeyboard(activity);
        final FloatingActionButton fab = v.findViewById(R.id.fab);
        final FloatingActionButton fab1 = v.findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openDrawer();
            }
        });
        fab.setTag("edit");
        fab.setImageDrawable(activity.getResources().getDrawable(android.R.drawable.ic_menu_edit));
        ly_status = v.findViewById(R.id.ly_status);
        ly_birthday = v.findViewById(R.id.ly_birthday);
        ly_name = v.findViewById(R.id.ly_name);
        ly_firstname = v.findViewById(R.id.ly_firstname);
        ly_lastname = v.findViewById(R.id.ly_lastname);
        txt_available = v.findViewById(R.id.txt_available);
        txt_unavailable = v.findViewById(R.id.txt_unavailable);
        spinner_area = v.findViewById(R.id.spinner_area);
        spinner_area.setItems(Utils.array_school_area);
        txt_available.setEnabled(false);
        txt_unavailable.setEnabled(false);

        img_photo = (ImageView)v.findViewById(R.id.img_photo);
        img_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity, ProfileFragment.this);
            }
        });
        TextView txt_type = v.findViewById(R.id.txt_type);
        txt_type.setText(Utils.currentUser.type);
        LinearLayout ly_school = (LinearLayout) v.findViewById(R.id.ly_school);
        LinearLayout ly_ministry_type = (LinearLayout) v.findViewById(R.id.ly_ministry_type);
//        final EditText edit_username = (EditText)v.findViewById(R.id.edit_username_sms);
//        final EditText edit_password = (EditText)v.findViewById(R.id.edit_password_sms);
//        final EditText edit_senderID = (EditText)v.findViewById(R.id.edit_senderID_sms);
        final EditText edit_birthday = (EditText)v.findViewById(R.id.edit_birthday);
        final EditText edit_lastname = (EditText)v.findViewById(R.id.edit_lastname);
        final EditText edit_firstname = (EditText)v.findViewById(R.id.edit_firstname);
        final EditText edit_name = (EditText)v.findViewById(R.id.edit_name);
        final EditText edit_email = (EditText)v.findViewById(R.id.edit_email);
        EditText edit_country = (EditText)v.findViewById(R.id.edit_country);
        final EditText edit_city = (EditText)v.findViewById(R.id.edit_city);
        EditText edit_phone = (EditText)v.findViewById(R.id.edit_phone);
        final CountryCodePicker countryCodePicker = (CountryCodePicker)v.findViewById(R.id.txt_countryCode);
        final EditText edit_school_number = (EditText)v.findViewById(R.id.edit_school_number);
        final RadioButton radio_primary = (RadioButton)v.findViewById(R.id.radio_primary);
        final RadioButton radio_secondary = (RadioButton)v.findViewById(R.id.radio_secondary);
        final RadioButton radio_private = (RadioButton)v.findViewById(R.id.radio_private);
        final RadioButton radio_public = (RadioButton)v.findViewById(R.id.radio_public);
        final RadioButton radio_supervisor = (RadioButton)v.findViewById(R.id.radio_supervisor);
        final RadioButton radio_middle_staff = (RadioButton)v.findViewById(R.id.radio_middle_staff);
        final RadioButton radio_filed_agent = (RadioButton)v.findViewById(R.id.radio_filed_agent);
        final ImageView img_status_edit = v.findViewById(R.id.img_edit_status);
        img_status_edit.setTag("edit");
        img_status_edit.setImageDrawable(activity.getResources().getDrawable(android.R.drawable.ic_menu_edit));
        img_status_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (img_status_edit.getTag().equals("edit")) {
                    img_status_edit.setTag("save");
                    img_status_edit.setImageDrawable(activity.getResources().getDrawable(android.R.drawable.ic_menu_save));
                    txt_available.setEnabled(true);
                    txt_unavailable.setEnabled(true);
                    txt_available.setVisibility(View.VISIBLE);
                    txt_unavailable.setVisibility(View.VISIBLE);
                    if (Utils.currentUser.status == 1) {
                        ly_status.setBackground(activity.getResources().getDrawable(R.drawable.status_online));
                        txt_available.setBackgroundColor(activity.getResources().getColor(R.color.colorMainBackground));
                        txt_unavailable.setBackgroundColor(activity.getResources().getColor(R.color.white));
                    } else if (Utils.currentUser.status == 0) {
                        ly_status.setBackground(activity.getResources().getDrawable(R.drawable.status_offline));
                        txt_available.setBackgroundColor(activity.getResources().getColor(R.color.white));
                        txt_unavailable.setBackgroundColor(activity.getResources().getColor(R.color.colorMainBackground));
                    }
                } else {
                    txt_available.setEnabled(false);
                    txt_unavailable.setEnabled(false);
                    img_status_edit.setTag("edit");
                    img_status_edit.setImageDrawable(activity.getResources().getDrawable(android.R.drawable.ic_menu_edit));
                    txt_unavailable.setBackgroundColor(activity.getResources().getColor(R.color.white));
                    txt_available.setBackgroundColor(activity.getResources().getColor(R.color.white));
                    if (Utils.currentUser.status == 1) {
                        txt_unavailable.setVisibility(View.GONE);
                    } else {
                        txt_available.setVisibility(View.GONE);
                    }
                }
            }
        });
        txt_available.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.setStatus(1);
                ly_status.setBackground(activity.getResources().getDrawable(R.drawable.status_online));
                txt_available.setBackgroundColor(activity.getResources().getColor(R.color.colorMainBackground));
                txt_unavailable.setBackgroundColor(activity.getResources().getColor(R.color.white));
            }
        });
        txt_unavailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.setStatus(0);
                ly_status.setBackground(activity.getResources().getDrawable(R.drawable.status_offline));
                txt_unavailable.setBackgroundColor(activity.getResources().getColor(R.color.colorMainBackground));
                txt_available.setBackgroundColor(activity.getResources().getColor(R.color.white));
            }
        });
        edit_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar myCalender = Calendar.getInstance();
                int year = myCalender.get(Calendar.YEAR);
                int month = myCalender.get(Calendar.MONTH);
                int dayOfMonth = myCalender.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String birthday = String.valueOf(year) + "-" + String.valueOf(month+1) + "-" + String.valueOf(dayOfMonth);
                        edit_birthday.setText(birthday);
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myDateListener, year, month, dayOfMonth);
                datePickerDialog.setTitle("Choose Date");
                datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                datePickerDialog.show();
            }
        });

//        edit_username.setText(Utils.currentUser.username);
//        edit_password.setText(Utils.currentUser.password);
//        edit_senderID.setText(Utils.currentUser.senderID);
        edit_name.setText(Utils.currentUser.name);
        edit_firstname.setText(Utils.currentUser.firstname);
        edit_lastname.setText(Utils.currentUser.lastname);
        edit_birthday.setText(Utils.currentUser.birthday);
        edit_email.setText(Utils.currentUser.email);
        edit_phone.setText(Utils.currentUser.phone);
        edit_country.setText(Utils.currentUser.country);
        edit_city.setText(Utils.currentUser.city);
        if (Utils.currentUser.status == 1) {
            ly_status.setBackground(activity.getResources().getDrawable(R.drawable.status_online));
            txt_available.setVisibility(View.VISIBLE);
            txt_unavailable.setVisibility(View.GONE);
        } else if (Utils.currentUser.status == 0) {
            ly_status.setBackground(activity.getResources().getDrawable(R.drawable.status_offline));
            txt_unavailable.setVisibility(View.VISIBLE);
            txt_available.setVisibility(View.GONE);
        }
        Glide.with(activity).load(Utils.currentUser.photo).apply(new RequestOptions()
                .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
        ly_name.setVisibility(View.GONE);
        ly_firstname.setVisibility(View.VISIBLE);
        ly_lastname.setVisibility(View.VISIBLE);
        ly_birthday.setVisibility(View.GONE);

        if (Utils.currentUser.type.equals(Utils.STUDENT)) {
            ly_birthday.setVisibility(View.VISIBLE);
        }
        if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
            ly_name.setVisibility(View.VISIBLE);
            ly_firstname.setVisibility(View.GONE);
            ly_lastname.setVisibility(View.GONE);
            ly_school.setVisibility(View.VISIBLE);
            edit_school_number.setText(Utils.currentSchool.number);
            radio_public.setChecked(Utils.currentSchool.isPublic);
            radio_private.setChecked(!Utils.currentSchool.isPublic);
            if (Utils.currentSchool.type.equals(Utils.PRIMARY)) {
                radio_primary.setChecked(true);
            } else {
                radio_secondary.setChecked(true);
            }
//            int area_index = Utils.array_school_area.indexOf(Utils.currentSchool.area);
            spinner_area.setSelectedIndex(Utils.array_school_area.indexOf(Utils.currentSchool.area));
        } else if (Utils.currentUser.type.equals(Utils.MINISTRY)) {

            Utils.mDatabase.child(Utils.tbl_ministry).orderByChild("uid").equalTo(Utils.currentUser._id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            cur_ministry = childSnapshot.getValue(Ministry.class);
                            cur_ministry._id = childSnapshot.getKey();
                            ly_ministry_type.setVisibility(View.VISIBLE);
                            if (cur_ministry.type.equals(Utils.SUPERVISOR)) {
                                radio_supervisor.setChecked(true);
                            } else if (cur_ministry.type.equals(Utils.MIDDLE_STAFF)) {
                                radio_middle_staff.setChecked(true);
                            } else if (cur_ministry.type.equals(Utils.FILED_AGENT)) {
                                radio_filed_agent.setChecked(true);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                }
            });
        }

//        edit_username.setEnabled(false);
//        edit_password.setEnabled(false);
//        edit_senderID.setEnabled(false);
        edit_firstname.setEnabled(false);
        edit_lastname.setEnabled(false);
        edit_birthday.setEnabled(false);
        edit_name.setEnabled(false);
        edit_email.setEnabled(false);
        edit_city.setEnabled(false);
        edit_school_number.setEnabled(false);
        radio_primary.setClickable(false);
        radio_secondary.setClickable(false);
        radio_private.setClickable(false);
        radio_public.setClickable(false);
        radio_supervisor.setClickable(false);
        radio_middle_staff.setClickable(false);
        radio_filed_agent.setClickable(false);
        spinner_area.setEnabled(false);
        img_photo.setEnabled(false);
        img_status_edit.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fab.getTag().equals("edit")) {
                    fab.setTag("save");
                    fab.setImageDrawable(activity.getResources().getDrawable(android.R.drawable.ic_menu_save));

//                    edit_username.setEnabled(true);
//                    edit_password.setEnabled(true);
//                    edit_senderID.setEnabled(true);
                    edit_firstname.setEnabled(true);
                    edit_lastname.setEnabled(true);
                    edit_birthday.setEnabled(true);
                    edit_name.setEnabled(true);
                    edit_email.setEnabled(true);
                    edit_city.setEnabled(true);
                    edit_school_number.setEnabled(true);
                    radio_primary.setClickable(true);
                    radio_secondary.setClickable(true);
                    radio_private.setClickable(true);
                    radio_public.setClickable(true);
                    radio_supervisor.setClickable(true);
                    radio_middle_staff.setClickable(true);
                    radio_filed_agent.setClickable(true);
                    img_photo.setEnabled(true);
                    spinner_area.setEnabled(true);
                    img_status_edit.setVisibility(View.VISIBLE);

                } else {
                    if (img_status_edit.getTag().equals("save")) {
                        Utils.showAlert(activity, getResources().getString(R.string.warning),getResources().getString(R.string.please_save_available_status));
                        return;
                    }
                    fab.setTag("edit");
                    fab.setImageDrawable(activity.getResources().getDrawable(android.R.drawable.ic_menu_edit));

//                    edit_username.setEnabled(false);
//                    edit_password.setEnabled(false);
//                    edit_senderID.setEnabled(false);
                    edit_firstname.setEnabled(false);
                    edit_lastname.setEnabled(false);
                    edit_birthday.setEnabled(false);
                    edit_name.setEnabled(false);
                    edit_email.setEnabled(false);
                    edit_city.setEnabled(false);
                    edit_school_number.setEnabled(false);
                    radio_primary.setClickable(false);
                    radio_secondary.setClickable(false);
                    radio_private.setClickable(false);
                    radio_public.setClickable(false);
                    radio_supervisor.setClickable(false);
                    radio_middle_staff.setClickable(false);
                    radio_filed_agent.setClickable(false);
                    spinner_area.setEnabled(false);
                    img_photo.setEnabled(false);
                    img_status_edit.setVisibility(View.GONE);



//                    final String username_sms = edit_username.getText().toString().trim();
//                    final String password_sms = edit_password.getText().toString().trim();
//                    final String senderID_sms = edit_senderID.getText().toString().trim();
                    final String birthday = edit_birthday.getText().toString().trim();
                    final String firstname = edit_firstname.getText().toString().trim();
                    final String lastname = edit_lastname.getText().toString().trim();
                    String name = edit_name.getText().toString().trim();
                    final String email = edit_email.getText().toString().trim();
                    final String city = edit_city.getText().toString().trim();
                    if (email.length()*city.length() == 0) {
                        Utils.showAlert(activity, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                        return;
                    }
                    if (Utils.currentUser.type.equals(Utils.TEACHER) || Utils.currentUser.type.equals(Utils.PARENT) || Utils.currentUser.type.equals(Utils.MINISTRY) || Utils.currentUser.type.equals(Utils.STUDENT)) {
                        if (firstname.length()*lastname.length() == 0) {
                            Utils.showAlert(activity, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                            return;
                        }
                    }
                    if (Utils.currentUser.type.equals(Utils.STUDENT)) {
                        if (birthday.length() == 0) {
                            Utils.showAlert(activity, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                            return;
                        }
                    }
                    if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
                        if (name.length() == 0) {
                            Utils.showAlert(activity, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                            return;
                        }
                    }
                    String school_number = "";
                    final String type;
                    final boolean isPublic;
                    if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
                        school_number = edit_school_number.getText().toString().trim();
                        if (school_number.length() == 0) {
                            Utils.showAlert(activity, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                            return;
                        }
                    }

                    if (name.length() == 0) {
                        name = firstname + " " + lastname;
                    }

                    if (radio_primary.isChecked()) {
                        type = Utils.PRIMARY;
                    } else {
                        type = Utils.SECONDARY;
                    }
                    if (radio_private.isChecked()) {
                        isPublic = false;
                    } else {
                        isPublic = true;
                    }
                    String ministry_type = Utils.SUPERVISOR;
                    if (radio_supervisor.isChecked()) {
                        ministry_type = Utils.SUPERVISOR;
                    } else if (radio_middle_staff.isChecked()) {
                        ministry_type = Utils.MIDDLE_STAFF;
                    } else if (radio_filed_agent.isChecked()) {
                        ministry_type = Utils.FILED_AGENT;
                    }
                    String area = Utils.array_school_area.get(spinner_area.getSelectedIndex());
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setMessage(getResources().getString(R.string.please_wait));
                    progressDialog.show();
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType("image/jpeg")
                            .build();
                    Long tsLong = System.currentTimeMillis();
                    String ts = tsLong.toString();
                    final StorageReference file_refer = Utils.mStorage.child("users/"+ts);

                    // Listen for state changes, errors, and completion of the upload.
                    final String school_number1 = school_number;
                    if (imgUri == null) {
                        updateProfile("", firstname, lastname, name, email, birthday, city, school_number1, type, area, isPublic, ministry_type);
                        return;
                    }
                    String finalMinistry_type = ministry_type;
                    String finalName = name;
                    file_refer.putFile(imgUri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Handle successful uploads on complete
                            file_refer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String downloadUrl = uri.toString();
                                    updateProfile(downloadUrl, firstname, lastname, finalName, email, birthday, city, school_number1, type, area, isPublic, finalMinistry_type);
                                }
                            });
                        }

                    });
                }
            }
        });

        return v;
    }
    void updateProfile(String downloadUrl, String firstname, String lastname, String name, String email, String birthday, String city, String school_number, String type, String area, boolean isPublic, String ministry_type) {
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_NAME).setValue(name);
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_FIRSTNAME).setValue(firstname);
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_LASTNAME).setValue(lastname);
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_BIRTHDAY).setValue(birthday);
        if (downloadUrl.length() > 0) {
            Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_PHOTO).setValue(downloadUrl);
            Utils.currentUser.photo = downloadUrl;
        }
//        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_USERNAME).setValue(username_sms);
//        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_PASSWORD).setValue(password_sms);
//        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_SENDERID).setValue(senderID_sms);
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_EMAIL).setValue(email);
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_CITY).setValue(city);
        Utils.currentUser.name = name; Utils.currentUser.firstname = firstname; Utils.currentUser.lastname = lastname; Utils.currentUser.birthday = birthday; Utils.currentUser.email = email; Utils.currentUser.city = city;
//        Utils.currentUser.username = username_sms; Utils.currentUser.password = password_sms; Utils.currentUser.senderID = senderID_sms;
        if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
            Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child(Utils.SCHOOL_NUMBER).setValue(school_number);
            Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child(Utils.SCHOOL_TYPE).setValue(type);
            Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child(Utils.SCHOOL_AREA).setValue(area);
            Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child(Utils.SCHOOL_PUBLIC).setValue(isPublic);
            Utils.currentSchool.number = school_number; Utils.currentSchool.type = type; Utils.currentSchool.area = area; Utils.currentSchool.isPublic = isPublic;
        } else if (Utils.currentUser.type.equals(Utils.MINISTRY)) {
            Utils.mDatabase.child(Utils.tbl_ministry).child(cur_ministry._id).child(Utils.MINISTRY_TYPE).setValue(ministry_type);
        }
        progressDialog.dismiss();
        activity.setUserProfile();
        Toast.makeText(activity, getResources().getString(R.string.successfully_updated), Toast.LENGTH_LONG).show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgUri = result.getUri();
//                img_photo.setImageURI(result.getUri());
                Glide.with(activity).load(result.getUri())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.profile).centerCrop().dontAnimate()).into(img_photo);
                img_photo.setBackground(activity.getDrawable(R.drawable.app_icon));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(activity, getResources().getString(R.string.cropping_failed) + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
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