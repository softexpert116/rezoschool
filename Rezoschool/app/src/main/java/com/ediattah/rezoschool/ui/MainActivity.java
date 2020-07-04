package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.fragments.ProfileFragment;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    NavigationView navigationView;
    FragmentTransaction transaction;
    FrameLayout frameLayout;
    TextView txt_title;
    Toolbar toolbar;
    View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



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


    }
    private void setTitle(String title) {
        txt_title.setText(title);
    }
    public void setUserProfile() {
        //Navigation Header Profile
        LinearLayout ly_profile = header.findViewById(R.id.ly_profile);
        TextView txt_name = header.findViewById(R.id.txt_name);
        TextView txt_type = header.findViewById(R.id.txt_type);
        txt_name.setText(Utils.currentUser.name);
        txt_type.setText(Utils.currentUser.type);
        ImageView img_photo = (ImageView)header.findViewById(R.id.img_photo);
        Glide.with(this).load(Utils.currentUser.photo).apply(new RequestOptions()
                .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
        img_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFragment(new ProfileFragment());
                setTitle(getResources().getString(R.string.user_profile));
                closeDrawer();
            }
        });
        ImageView img_logout = (ImageView)header.findViewById(R.id.img_lotout);
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(loginIntent);
                finish();
            }
        });

    }
    private void selectFragment(Fragment fragment) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_container, fragment);
        transaction.commit();
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
            nav_Menu.findItem(R.id.nav_class).setVisible(true);
            nav_Menu.findItem(R.id.nav_teacher).setVisible(true);
            nav_Menu.findItem(R.id.nav_student).setVisible(true);
            nav_Menu.findItem(R.id.nav_callback).setVisible(true);
            nav_Menu.findItem(R.id.nav_finance).setVisible(true);
            nav_Menu.findItem(R.id.nav_child).setVisible(false);
            nav_Menu.findItem(R.id.nav_school).setVisible(false);
            nav_Menu.findItem(R.id.nav_syllabus).setVisible(false);
            nav_Menu.findItem(R.id.nav_library).setVisible(true);
            nav_Menu.findItem(R.id.nav_invoice).setVisible(false);
            nav_Menu.findItem(R.id.nav_course).setVisible(false);
            nav_Menu.findItem(R.id.nav_alumni).setVisible(false);

        } else if (Utils.currentUser.type.equals(Utils.TEACHER)) {
            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_class).setVisible(false);
            nav_Menu.findItem(R.id.nav_teacher).setVisible(false);
            nav_Menu.findItem(R.id.nav_student).setVisible(false);
            nav_Menu.findItem(R.id.nav_callback).setVisible(true);
            nav_Menu.findItem(R.id.nav_finance).setVisible(false);
            nav_Menu.findItem(R.id.nav_child).setVisible(false);
            nav_Menu.findItem(R.id.nav_school).setVisible(true);
            nav_Menu.findItem(R.id.nav_syllabus).setVisible(true);
            nav_Menu.findItem(R.id.nav_library).setVisible(true);
            nav_Menu.findItem(R.id.nav_invoice).setVisible(false);
            nav_Menu.findItem(R.id.nav_course).setVisible(false);
            nav_Menu.findItem(R.id.nav_alumni).setVisible(false);

        } else if (Utils.currentUser.type.equals(Utils.PARENT)) {
            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_class).setVisible(false);
            nav_Menu.findItem(R.id.nav_teacher).setVisible(false);
            nav_Menu.findItem(R.id.nav_student).setVisible(false);
            nav_Menu.findItem(R.id.nav_callback).setVisible(true);
            nav_Menu.findItem(R.id.nav_finance).setVisible(false);
            nav_Menu.findItem(R.id.nav_child).setVisible(true);
            nav_Menu.findItem(R.id.nav_school).setVisible(false);
            nav_Menu.findItem(R.id.nav_syllabus).setVisible(false);
            nav_Menu.findItem(R.id.nav_library).setVisible(false);
            nav_Menu.findItem(R.id.nav_invoice).setVisible(true);
            nav_Menu.findItem(R.id.nav_course).setVisible(false);
            nav_Menu.findItem(R.id.nav_alumni).setVisible(false);

        } else if (Utils.currentUser.type.equals(Utils.STUDENT)) {
            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            nav_Menu.findItem(R.id.nav_class).setVisible(false);
            nav_Menu.findItem(R.id.nav_teacher).setVisible(false);
            nav_Menu.findItem(R.id.nav_student).setVisible(false);
            nav_Menu.findItem(R.id.nav_callback).setVisible(false);
            nav_Menu.findItem(R.id.nav_finance).setVisible(false);
            nav_Menu.findItem(R.id.nav_child).setVisible(false);
            nav_Menu.findItem(R.id.nav_school).setVisible(false);
            nav_Menu.findItem(R.id.nav_syllabus).setVisible(false);
            nav_Menu.findItem(R.id.nav_library).setVisible(true);
            nav_Menu.findItem(R.id.nav_invoice).setVisible(true);
            nav_Menu.findItem(R.id.nav_course).setVisible(true);
            nav_Menu.findItem(R.id.nav_alumni).setVisible(true);

        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            selectFragment(new TweetsFragment());
            setTitle(getResources().getString(R.string.menu_home));
        } else if (id == R.id.nav_class) {
            selectFragment(new SchoolClassFragment());
            setTitle(getResources().getString(R.string.menu_class));
        } else if (id == R.id.nav_teacher) {
            selectFragment(new SchoolTeacherFragment());
            setTitle(getResources().getString(R.string.menu_teacher));
        } else if (id == R.id.nav_student) {
            selectFragment(new SchoolStudentFragment());
            setTitle(getResources().getString(R.string.menu_student));
        } else if (id == R.id.nav_callback) {
            selectFragment(new SchoolCallbackFragment());
            setTitle(getResources().getString(R.string.menu_callback));
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
        } else if (id == R.id.nav_course) {
            selectFragment(new StudentCourseFragment());
            setTitle(getResources().getString(R.string.menu_course));
        } else if (id == R.id.nav_alumni) {
            selectFragment(new AlumniFragment());
            setTitle(getResources().getString(R.string.menu_alumni));
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