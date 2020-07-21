package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.Level;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;

public class NewLevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_level);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Create New Level");
        App.hideKeyboard(this);
        final EditText edit_level = (EditText)findViewById(R.id.edit_level);
        Button btn_create = (Button)findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String level_name = edit_level.getText().toString().trim();
                if (level_name.length() == 0) {
                    Utils.showAlert(NewLevelActivity.this, "Warning", "Please fill in the blank field");
                } else {
                    boolean flag = false;
                    for (Level level: Utils.currentSchool.levels) {
                        if (level.name.equals(level_name)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        Utils.showAlert(NewLevelActivity.this, "Warning", "The level name already exists.");
                        return;
                    }
                    Level level = new Level(level_name, "");
                    Utils.currentSchool.levels.add(level);
                    Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("levels").setValue(Utils.currentSchool.levels);
                    Toast.makeText(NewLevelActivity.this, "Successfully created!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}