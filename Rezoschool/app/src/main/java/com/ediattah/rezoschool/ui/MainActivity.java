package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.fragments.MessageFragment;
import com.ediattah.rezoschool.fragments.ProfileFragment;
import com.ediattah.rezoschool.fragments.StudentSchoolFragment;
import com.ediattah.rezoschool.fragments.TimeslotFragment;
import com.ediattah.rezoschool.fragments.VideoFragment;
import com.ediattah.rezoschool.service.NotificationCallback;
import com.google.android.material.navigation.NavigationView;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.fragments.AlumniFragment;
import com.ediattah.rezoschool.fragments.ParentInvoiceFragment;
import com.ediattah.rezoschool.fragments.ParentStudentFragment;
import com.ediattah.rezoschool.fragments.SchoolCallbackFragment;
import com.ediattah.rezoschool.fragments.SchoolClassFragment;
import com.ediattah.rezoschool.fragments.SchoolFinanceFragment;
import com.ediattah.rezoschool.fragments.TweetsFragment;
import com.ediattah.rezoschool.fragments.LibraryFragment;
import com.ediattah.rezoschool.fragments.SchoolStudentFragment;
import com.ediattah.rezoschool.fragments.SchoolTeacherFragment;
import com.ediattah.rezoschool.fragments.StudentCourseFragment;
import com.ediattah.rezoschool.fragments.TeacherSchoolFragment;
import com.ediattah.rezoschool.fragments.TeacherSyllabusFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageButton;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    NavigationView navigationView;
    FragmentTransaction transaction;
    FrameLayout frameLayout;
    TextView txt_title;
    Toolbar toolbar;
    View header;
    GifImageButton btn_notify;
    Button btn_message, btn_video, btn_video_group;
    LinearLayout ly_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        btn_message = findViewById(R.id.btn_message);
        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFragment(new MessageFragment());
                setTitle(getResources().getString(R.string.menu_message));
                refreshNotifications();
            }
        });
        btn_video_group = findViewById(R.id.btn_video_group);
        btn_video_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFragment(new VideoFragment());
                setTitle(getResources().getString(R.string.menu_video));
                refreshNotifications();
            }
        });
        btn_video = findViewById(R.id.btn_video);
        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> arrayList = App.readPreference_array_String(App.NewVideoCall);
                if (arrayList.size() > 0) {
                    final Dialog dlg = new Dialog(MainActivity.this);
                    Window window = dlg.getWindow();
                    View view2 = getLayoutInflater().inflate(R.layout.dialog_view_items, null);
                    int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
                    int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
                    view.setMinimumWidth(width);
                    view.setMinimumHeight(height);
                    dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dlg.setContentView(view2);
                    window.setGravity(Gravity.CENTER);
                    dlg.show();
                    LinearLayout ly_item = view2.findViewById(R.id.ly_item);
                    ly_item.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                    for (String vtoken:arrayList) {
                        String user_id = vtoken.split(" ")[0];
                        String room = vtoken.split(" ")[1];
                        Utils.mDatabase.child(Utils.tbl_user).child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue()!=null) {
                                    User user = dataSnapshot.getValue(User.class);
                                    View view1 = inflater.inflate(R.layout.cell_video_call, null);
                                    ImageView img_photo = view1.findViewById(R.id.img_photo);
                                    TextView txt_name = view1.findViewById(R.id.txt_name);
                                    TextView txt_email = view1.findViewById(R.id.txt_email);
                                    TextView txt_phone = view1.findViewById(R.id.txt_phone);
                                    Glide.with(MainActivity.this).load(user.photo).apply(new RequestOptions()
                                            .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                                    txt_name.setText(user.name);
                                    txt_email.setText(user.email);
                                    txt_phone.setText(user.phone);
                                    Button btn_join = view1.findViewById(R.id.btn_join);
                                    btn_join.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dlg.dismiss();
                                            App.goToJoinVideoCall(room, MainActivity.this);
                                            ArrayList<String> array_video = App.readPreference_array_String(App.NewVideoCall);
                                            if (array_video.contains(vtoken)) {
                                                array_video.remove(vtoken);
                                                App.setPreference_array_String(App.NewVideoCall, array_video);
                                            }
                                        }
                                    });
                                    Button btn_cancel = view1.findViewById(R.id.btn_cancel);
                                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dlg.dismiss();
                                            ArrayList<String> array_video = App.readPreference_array_String(App.NewVideoCall);
                                            if (array_video.contains(vtoken)) {
                                                array_video.remove(vtoken);
                                                App.setPreference_array_String(App.NewVideoCall, array_video);
                                            }
                                            refreshNotifications();
                                        }
                                    });
                                    ly_item.addView(view1);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    refreshNotifications();
                }
            }
        });
        ly_notification = findViewById(R.id.ly_notification);
        btn_notify = findViewById(R.id.btn_notify);
        btn_notify.setTag("0");
        btn_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_notify.getTag().equals("0")) {
                    ly_notification.setVisibility(View.VISIBLE);
                    btn_notify.setTag("1");
                } else {
                    ly_notification.setVisibility(View.GONE);
                    btn_notify.setTag("0");
                }
            }
        });
        txt_title = findViewById(R.id.txt_title);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // Header View
        header = navigationView.getHeaderView(0);
        setUserProfile();
        setMenuByUserType();

        // First fragment loading...
        selectFragment(new TweetsFragment());
        setTitle(getResources().getString(R.string.menu_home));
        setPermission();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//        {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//            {
//                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
//            }
//        }

        App.notificationCallback = new NotificationCallback() {
            @Override
            public void OnReceivedNotification() {
                refreshNotifications();
            }
        };
    }
    void refreshNotifications() {
        App.cancelAllNotifications();
        boolean flag_notification = false;
        btn_notify.setTag("0");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ly_notification.setVisibility(View.GONE);
                btn_message.setVisibility(View.GONE);
                btn_video.setVisibility(View.GONE);
                btn_video_group.setVisibility(View.GONE);
            }
        });
        String new_message = App.readPreference(App.NewMessage, "");
        if (new_message.equals("true") && !(getFragmentTitle().equals(getResources().getString(R.string.menu_message)))) {
            flag_notification = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_message.setVisibility(View.VISIBLE);
                }
            });
        }

        ArrayList<String> array_video_group = App.readPreference_array_String(App.NewVideoGroup);
        if (array_video_group.size() > 0) {
            flag_notification = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_video_group.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_video_group.setVisibility(View.GONE);
                }
            });
        }

        ArrayList<String> array_video = App.readPreference_array_String(App.NewVideoCall);
        if (array_video.size() > 0) {
            flag_notification = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_video.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_video.setVisibility(View.GONE);
                }
            });
        }
        if (flag_notification) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_notify.setVisibility(View.VISIBLE);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_notify.setVisibility(View.INVISIBLE);
                }
            });
        }
    }
    private void setTitle(String title) {
        txt_title.setText(title);
    }
    private String getFragmentTitle() {
        return txt_title.getText().toString();
    }
    public void setUserProfile() {
        //Navigation Header Profile
        LinearLayout ly_profile = header.findViewById(R.id.ly_profile);
        TextView txt_name = header.findViewById(R.id.txt_name);
        TextView txt_type = header.findViewById(R.id.txt_type);
        txt_name.setText(Utils.currentUser.name);
        txt_type.setText(Utils.currentUser.type);
        LinearLayout ly_status = header.findViewById(R.id.ly_status);
        if (Utils.currentUser.status == 1) {
            ly_status.setBackground(getResources().getDrawable(R.drawable.status_online));
        } else {
            ly_status.setBackground(getResources().getDrawable(R.drawable.status_offline));
        }
        ImageView img_photo = (ImageView)header.findViewById(R.id.img_photo);
        Glide.with(this).load(Utils.currentUser.photo).apply(new RequestOptions()
                .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
        img_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFragment(new ProfileFragment());
                setTitle(getResources().getString(R.string.user_profile));
                closeDrawer();
                getSupportActionBar().hide();
            }
        });
        ImageView img_logout = (ImageView)header.findViewById(R.id.img_lotout);
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                App.setStatus(0);
                Utils.mUser = null;
                Utils.currentUser = new User();
                Utils.currentSchool = new School();
                Utils.currentStudent = new Student();
                Utils.currentClass = new Class();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(loginIntent);
                finish();
            }
        });

    }
    public void setPermission() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ArrayList<String> arrPermissionRequests = new ArrayList<>();
            arrPermissionRequests.add(WRITE_EXTERNAL_STORAGE);
            arrPermissionRequests.add(READ_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(this, arrPermissionRequests.toArray(new String[arrPermissionRequests.size()]), 201);
//            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 201);


            return;
        } else {
            createDirectory();
        }
    }
    void createDirectory() {
        getExternalFilesDir(null);
        File assets = getExternalFilesDir("assets");
        if (!assets.exists()) {
            assets.mkdir();
        }
        App.MY_APP_PATH = assets.getAbsolutePath();
        File f3 = new File(App.MY_APP_PATH, "audio");
        if (!f3.exists()) {
            f3.mkdir();
        }
        App.MY_AUDIO_PATH = f3.getAbsolutePath();

        File pictures = new File(Environment.getExternalStorageDirectory() + File.separator + "Pictures", "rezo");
        if (!pictures.exists()) {
            pictures.mkdir();
        }
        App.MY_IMAGE_PATH = pictures.getAbsolutePath();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 201: {
                if ((grantResults.length > 0) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createDirectory();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT);
                    finish();
                }
                break;
            }
            default:
                Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT);
                finish();
        }
    }

    private void selectFragment(Fragment fragment) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_container, fragment);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNotifications();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you going to finish the app?");
            builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    App.setStatus(0);
                    ActivityCompat.finishAffinity(MainActivity.this);
                    System.exit(0);
                }
            });
            builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    private void setMenuByUserType() {
        Menu nav_Menu = navigationView.getMenu();
        if (Utils.currentUser.type.equals(Utils.SCHOOL)) {


            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_timeslot).setVisible(true);
            nav_Menu.findItem(R.id.nav_class).setVisible(true);
            nav_Menu.findItem(R.id.nav_teacher).setVisible(true);
            nav_Menu.findItem(R.id.nav_student).setVisible(true);
//            nav_Menu.findItem(R.id.nav_callback).setVisible(true);
            nav_Menu.findItem(R.id.nav_finance).setVisible(true);
            nav_Menu.findItem(R.id.nav_child).setVisible(false);
            nav_Menu.findItem(R.id.nav_school).setVisible(false);
            nav_Menu.findItem(R.id.nav_syllabus).setVisible(false);
            nav_Menu.findItem(R.id.nav_library).setVisible(true);
            nav_Menu.findItem(R.id.nav_invoice).setVisible(false);
            nav_Menu.findItem(R.id.nav_myschool).setVisible(false);
            nav_Menu.findItem(R.id.nav_course).setVisible(false);
            nav_Menu.findItem(R.id.nav_alumni).setVisible(false);
            nav_Menu.findItem(R.id.nav_message).setVisible(true);
            nav_Menu.findItem(R.id.nav_video).setVisible(true);

        } else if (Utils.currentUser.type.equals(Utils.TEACHER)) {
            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_timeslot).setVisible(false);
            nav_Menu.findItem(R.id.nav_class).setVisible(false);
            nav_Menu.findItem(R.id.nav_teacher).setVisible(false);
            nav_Menu.findItem(R.id.nav_student).setVisible(false);
//            nav_Menu.findItem(R.id.nav_callback).setVisible(true);
            nav_Menu.findItem(R.id.nav_finance).setVisible(false);
            nav_Menu.findItem(R.id.nav_child).setVisible(false);
            nav_Menu.findItem(R.id.nav_school).setVisible(true);
            nav_Menu.findItem(R.id.nav_syllabus).setVisible(true);
            nav_Menu.findItem(R.id.nav_library).setVisible(true);
            nav_Menu.findItem(R.id.nav_invoice).setVisible(false);
            nav_Menu.findItem(R.id.nav_myschool).setVisible(false);
            nav_Menu.findItem(R.id.nav_course).setVisible(false);
            nav_Menu.findItem(R.id.nav_alumni).setVisible(false);
            nav_Menu.findItem(R.id.nav_message).setVisible(true);
            nav_Menu.findItem(R.id.nav_video).setVisible(true);

        } else if (Utils.currentUser.type.equals(Utils.PARENT)) {
            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_timeslot).setVisible(false);
            nav_Menu.findItem(R.id.nav_class).setVisible(false);
            nav_Menu.findItem(R.id.nav_teacher).setVisible(false);
            nav_Menu.findItem(R.id.nav_student).setVisible(false);
//            nav_Menu.findItem(R.id.nav_callback).setVisible(true);
            nav_Menu.findItem(R.id.nav_finance).setVisible(false);
            nav_Menu.findItem(R.id.nav_child).setVisible(true);
            nav_Menu.findItem(R.id.nav_school).setVisible(false);
            nav_Menu.findItem(R.id.nav_syllabus).setVisible(false);
            nav_Menu.findItem(R.id.nav_library).setVisible(false);
            nav_Menu.findItem(R.id.nav_invoice).setVisible(true);
            nav_Menu.findItem(R.id.nav_myschool).setVisible(false);
            nav_Menu.findItem(R.id.nav_course).setVisible(false);
            nav_Menu.findItem(R.id.nav_alumni).setVisible(false);
            nav_Menu.findItem(R.id.nav_message).setVisible(true);
            nav_Menu.findItem(R.id.nav_video).setVisible(true);

        } else if (Utils.currentUser.type.equals(Utils.STUDENT)) {
            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_timeslot).setVisible(false);
            nav_Menu.findItem(R.id.nav_class).setVisible(false);
            nav_Menu.findItem(R.id.nav_teacher).setVisible(false);
            nav_Menu.findItem(R.id.nav_student).setVisible(false);
//            nav_Menu.findItem(R.id.nav_callback).setVisible(false);
            nav_Menu.findItem(R.id.nav_finance).setVisible(false);
            nav_Menu.findItem(R.id.nav_child).setVisible(false);
            nav_Menu.findItem(R.id.nav_school).setVisible(false);
            nav_Menu.findItem(R.id.nav_syllabus).setVisible(false);
            nav_Menu.findItem(R.id.nav_library).setVisible(true);
            nav_Menu.findItem(R.id.nav_invoice).setVisible(true);
            nav_Menu.findItem(R.id.nav_myschool).setVisible(true);
            nav_Menu.findItem(R.id.nav_course).setVisible(true);
            nav_Menu.findItem(R.id.nav_alumni).setVisible(true);
            nav_Menu.findItem(R.id.nav_message).setVisible(true);
            nav_Menu.findItem(R.id.nav_video).setVisible(true);

        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        getSupportActionBar().show();

        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            selectFragment(new TweetsFragment());
            setTitle(getResources().getString(R.string.menu_home));
        } else if (id == R.id.nav_timeslot) {
            selectFragment(new TimeslotFragment());
            setTitle(getResources().getString(R.string.menu_timeslot));
        } else if (id == R.id.nav_class) {
            selectFragment(new SchoolClassFragment());
            setTitle(getResources().getString(R.string.menu_class));
        } else if (id == R.id.nav_teacher) {
            selectFragment(new SchoolTeacherFragment());
            setTitle(getResources().getString(R.string.menu_teacher));
        } else if (id == R.id.nav_student) {
            selectFragment(new SchoolStudentFragment());
            setTitle(getResources().getString(R.string.menu_student));
        } else if (id == R.id.nav_finance) {
            selectFragment(new SchoolFinanceFragment());
            setTitle(getResources().getString(R.string.menu_finance));
        } else if (id == R.id.nav_library) {
            selectFragment(new LibraryFragment());
            setTitle(getResources().getString(R.string.menu_library));
        } else if (id == R.id.nav_school) {
            selectFragment(new TeacherSchoolFragment());
            setTitle(getResources().getString(R.string.menu_school));
        } else if (id == R.id.nav_syllabus) {
            selectFragment(new TeacherSyllabusFragment());
            setTitle(getResources().getString(R.string.menu_syllabus));
        } else if (id == R.id.nav_child) {
            selectFragment(new ParentStudentFragment());
            setTitle(getResources().getString(R.string.menu_child));
        } else if (id == R.id.nav_invoice) {
            selectFragment(new ParentInvoiceFragment());
            setTitle(getResources().getString(R.string.menu_invoice));
        } else if (id == R.id.nav_myschool) {
            selectFragment(new StudentSchoolFragment());
            setTitle(getResources().getString(R.string.menu_myschool));
        } else if (id == R.id.nav_course) {
            selectFragment(new StudentCourseFragment());
            setTitle(getResources().getString(R.string.menu_timeslot));
        } else if (id == R.id.nav_alumni) {
            selectFragment(new AlumniFragment());
            setTitle(getResources().getString(R.string.menu_alumni));
        } else if (id == R.id.nav_message) {
            selectFragment(new MessageFragment());
            setTitle(getResources().getString(R.string.menu_message));
            refreshNotifications();
        } else if (id == R.id.nav_video) {
            selectFragment(new VideoFragment());
            setTitle(getResources().getString(R.string.menu_video));
        }

        closeDrawer();

        return true;
    }
    public void openDrawer(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }
    public void closeDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}