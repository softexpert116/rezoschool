package com.ediattah.rezoschool.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ediattah.rezoschool.Utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ediattah.rezoschool.Model.Library;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.LibraryListAdapter;
import com.ediattah.rezoschool.ui.LibraryDetailActivity;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewLibraryActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LibraryFragment extends Fragment {
    MainActivity activity;
    ListView listView;
    ListView listView1;
    LibraryListAdapter libraryListAdapter;
    ArrayList<Library> arrayList = new ArrayList<>();
    ArrayList<Library> arrayList1 = new ArrayList<>();
    EditText edit_category;
    LinearLayout ly_no_items;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_library, container, false);
        listView = v.findViewById(R.id.listView);
        ly_no_items = v.findViewById(R.id.ly_no_items);
        edit_category = (EditText)v.findViewById(R.id.edit_category);
        edit_category.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edit_category.getText().toString().trim().length() == 0) {
                    arrayList1 = new ArrayList<>(arrayList);
                    libraryListAdapter.arrayList = arrayList1;
                    libraryListAdapter.notifyDataSetChanged();
                    if (arrayList1.size() == 0) {
                        ly_no_items.setVisibility(View.VISIBLE);
                    } else {
                        ly_no_items.setVisibility(View.GONE);
                    }
                } else {
                    filter_list();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        libraryListAdapter = new LibraryListAdapter(activity, this, arrayList1);
        libraryListAdapter.isSchool = true;
        listView.setAdapter(libraryListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, LibraryDetailActivity.class);
                intent.putExtra("OBJECT", arrayList1.get(i));
                startActivity(intent);
            }
        });
        FloatingActionButton fab = v.findViewById(R.id.fab);
        if (Utils.currentUser.type.equals(Utils.STUDENT)) {
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, NewLibraryActivity.class);
                activity.startActivity(intent);
            }
        });
        return v;
    }
    void filter_list() {
        String category = edit_category.getText().toString().trim();
        arrayList1.clear();
        for (Library library:arrayList) {
            if (library.category.toLowerCase().contains(category.toLowerCase())) {
                arrayList1.add(library);
            }
        }
        if (arrayList1.size() == 0) {
            ly_no_items.setVisibility(View.VISIBLE);
        } else {
            ly_no_items.setVisibility(View.GONE);
        }
        libraryListAdapter.arrayList = arrayList1;
        libraryListAdapter.notifyDataSetChanged();
    }
    public void read_library() {
        if (Utils.currentSchool._id.length() == 0) {
            Utils.showAlert(activity, "Warning", "Please select a school");
            return;
        }
        Utils.mDatabase.child(Utils.tbl_library).orderByChild("school_id").equalTo(Utils.currentSchool._id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.getValue()!= null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        Library library = datas.getValue(Library.class);
                        library._id = datas.getKey();
                        if (library.isAllow) {
                            arrayList.add(library);
                        }
                    }
                    filter_list();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void openAddDialog() {
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_choose_category, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        Button btn1 = (Button)view.findViewById(R.id.btn_category1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_category.setText("Category 1");
                dlg.dismiss();
            }
        });
        Button btn2 = (Button)view.findViewById(R.id.btn_category2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_category.setText("Category 2");
                dlg.dismiss();
            }
        });
        Button btn3 = (Button)view.findViewById(R.id.btn_category3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_category.setText("Category 3");
                dlg.dismiss();
            }
        });
        Button btn4 = (Button)view.findViewById(R.id.btn_category4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_category.setText("Category 4");
                dlg.dismiss();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        read_library();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}