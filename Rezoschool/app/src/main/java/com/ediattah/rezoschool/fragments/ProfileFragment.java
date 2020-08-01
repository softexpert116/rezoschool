package com.ediattah.rezoschool.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.ediattah.rezoschool.Model.Tweet;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewRsTweetsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    MainActivity activity;
    private Uri imgUri;
    ProgressDialog progressDialog;
    ImageView img_photo;
    RelativeLayout ly_status;
    TextView txt_available, txt_unavailable;

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
        txt_available = v.findViewById(R.id.txt_available);
        txt_unavailable = v.findViewById(R.id.txt_unavailable);
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
        edit_name.setText(Utils.currentUser.name);
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
        if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
            ly_school.setVisibility(View.VISIBLE);
            edit_school_number.setText(Utils.currentSchool.number);
            radio_public.setChecked(Utils.currentSchool.isPublic);
            radio_private.setChecked(!Utils.currentSchool.isPublic);
            if (Utils.currentSchool.type.equals(Utils.PRIMARY)) {
                radio_primary.setChecked(true);
            } else {
                radio_secondary.setChecked(true);
            }
        } else {
            ly_school.setVisibility(View.GONE);
        }

        edit_name.setEnabled(false);
        edit_email.setEnabled(false);
        edit_city.setEnabled(false);
        edit_school_number.setEnabled(false);
        radio_primary.setEnabled(false);
        radio_secondary.setEnabled(false);
        radio_private.setEnabled(false);
        radio_public.setEnabled(false);
        img_photo.setEnabled(false);
        img_status_edit.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fab.getTag().equals("edit")) {
                    fab.setTag("save");
                    fab.setImageDrawable(activity.getResources().getDrawable(android.R.drawable.ic_menu_save));

                    edit_name.setEnabled(true);
                    edit_email.setEnabled(true);
                    edit_city.setEnabled(true);
                    edit_school_number.setEnabled(true);
                    radio_primary.setEnabled(true);
                    radio_secondary.setEnabled(true);
                    radio_private.setEnabled(true);
                    radio_public.setEnabled(true);
                    img_photo.setEnabled(true);
                    img_status_edit.setVisibility(View.VISIBLE);

                } else {
                    fab.setTag("edit");
                    fab.setImageDrawable(activity.getResources().getDrawable(android.R.drawable.ic_menu_edit));

                    edit_name.setEnabled(false);
                    edit_email.setEnabled(false);
                    edit_city.setEnabled(false);
                    edit_school_number.setEnabled(false);
                    radio_primary.setEnabled(false);
                    radio_secondary.setEnabled(false);
                    radio_private.setEnabled(false);
                    radio_public.setEnabled(false);
                    img_photo.setEnabled(false);
                    img_status_edit.setVisibility(View.GONE);



                    final String name = edit_name.getText().toString().trim();
                    final String email = edit_email.getText().toString().trim();
                    final String city = edit_city.getText().toString().trim();
                    if (name.length()*email.length()*city.length() == 0) {
                        Utils.showAlert(activity, "Warning", "Please fill in blank field!");
                        return;
                    }
                    String school_number = "";
                    final String type;
                    final boolean isPublic;
                    if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
                        school_number = edit_school_number.getText().toString().trim();
                        if (school_number.length() == 0) {
                            Utils.showAlert(activity, "Warning", "Please fill in blank field!");
                            return;
                        }
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
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setMessage("Please Wait...");
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
                        updateProfile("", name, email, city, school_number1, type, isPublic);
                        return;
                    }
                    file_refer.putFile(imgUri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Handle successful uploads on complete
                            file_refer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String downloadUrl = uri.toString();
                                    updateProfile(downloadUrl, name, email, city, school_number1, type, isPublic);
                                }
                            });
                        }

                    });
                }
            }
        });

        return v;
    }
    void updateProfile(String downloadUrl, String name, String email, String city, String school_number, String type, boolean isPublic) {
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_NAME).setValue(name);
        if (downloadUrl.length() > 0) {
            Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_PHOTO).setValue(downloadUrl);
            Utils.currentUser.photo = downloadUrl;
        }
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_EMAIL).setValue(email);
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_CITY).setValue(city);
        Utils.currentUser.name = name; Utils.currentUser.email = email; Utils.currentUser.city = city;
        if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
            Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child(Utils.SCHOOL_NUMBER).setValue(school_number);
            Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child(Utils.SCHOOL_TYPE).setValue(type);
            Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child(Utils.SCHOOL_PUBLIC).setValue(isPublic);
            Utils.currentSchool.number = school_number; Utils.currentSchool.type = type; Utils.currentSchool.isPublic = isPublic;
        }
        progressDialog.dismiss();
        activity.setUserProfile();
        Toast.makeText(activity, "Successfully updated!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(activity, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
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