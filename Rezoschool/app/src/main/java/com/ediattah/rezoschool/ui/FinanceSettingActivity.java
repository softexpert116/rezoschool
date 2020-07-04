package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.FeeModel;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.FeeLevelListAdapter;
import com.ediattah.rezoschool.adapter.FeeStudentListAdapter;

import java.util.ArrayList;

public class FinanceSettingActivity extends AppCompatActivity {
    ArrayList<FeeModel> arrayList = new ArrayList<>();
    ListView list_fee, list_student;
    FeeLevelListAdapter feeLevelListAdapter;
    FeeStudentListAdapter feeStudentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Fee Setting");

        arrayList.add(new FeeModel(1, 1, 1, "CPA1", "$30"));
        arrayList.add(new FeeModel(2, 2, 1, "CPB1", "$40"));
        arrayList.add(new FeeModel(3, 3, 1, "CMA1", "$50"));

        list_fee = findViewById(R.id.list_fee_level);
        list_student = findViewById(R.id.list_fee_student);
        feeLevelListAdapter = new FeeLevelListAdapter(this, arrayList);
        list_fee.setAdapter(feeLevelListAdapter);
        feeStudentListAdapter = new FeeStudentListAdapter(this, arrayList);
        list_student.setAdapter(feeStudentListAdapter);

        ImageButton ibtn_add_fee_level = findViewById(R.id.ibtn_add_fee_level);
        ImageButton ibtn_add_fee_student = findViewById(R.id.ibtn_add_fee_student);
        ibtn_add_fee_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddDialog(true, true);
            }
        });
        ibtn_add_fee_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddDialog(true, false);
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void openAddDialog(boolean flag_add, boolean flag_level) {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_add_fee, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
//        dlg.getWindow().setLayout(width, height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        EditText edit_level = view.findViewById(R.id.edit_level);
        EditText edit_student = view.findViewById(R.id.edit_student);
        EditText edit_fee = view.findViewById(R.id.edit_fee);
        TextView txt_title= view.findViewById(R.id.txt_title);
        Button btn_add = view.findViewById(R.id.btn_add);
        if (flag_add) {
            txt_title.setText("Add Fee");
            btn_add.setText("Add");
        } else {
            txt_title.setText("Edit Fee");
            btn_add.setText("Update");
        }

        dlg.show();
    }
}