package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Library;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.fragments.LibraryFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class LibraryListAdapter extends BaseAdapter {
    ArrayList<Library> arrayList;
    public boolean isSchool = false;
    Context context;
    LibraryFragment fragment;

    public LibraryListAdapter(Context _context, LibraryFragment _fragment, ArrayList<Library> _arrayList) {
        context = _context;
        this.fragment = _fragment;
        this.arrayList = _arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Library model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_library, null);
        }
        TextView txt_title = view.findViewById(R.id.txt_title);
        txt_title.setText(model.title);
        TextView txt_category = view.findViewById(R.id.txt_category);
        txt_category.setText(model.category);
        Button btn_remove = view.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you going to remove this item?");
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Utils.mDatabase.child(Utils.tbl_library).child(model._id).setValue(null);
                        Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
                        fragment.read_library();
                    }
                });
                builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        TextView txt_description = view.findViewById(R.id.txt_description);
        txt_description.setText(model.description);
        ToggleSwitch toggleSwitch = (ToggleSwitch) view.findViewById(R.id.sw_public);
        if (!Utils.currentUser.type.equals(Utils.SCHOOL)) {
            toggleSwitch.setVisibility(View.GONE);
        }
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Public");
        labels.add("Private");
        toggleSwitch.setLabels(labels);
        if (model.isPublic) {
            toggleSwitch.setCheckedTogglePosition(0);
        } else {
            toggleSwitch.setCheckedTogglePosition(1);
        }
        toggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener(){
            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if (position == 0) {
                    Utils.mDatabase.child(Utils.tbl_library).child(model._id).child("isPublic").setValue(true);
                } else {
                    Utils.mDatabase.child(Utils.tbl_library).child(model._id).child("isPublic").setValue(false);
                }
//                Toast.makeText(context, "switched", Toast.LENGTH_SHORT).show();
            }
        });

        if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
            btn_remove.setVisibility(View.VISIBLE);
            toggleSwitch.setVisibility(View.VISIBLE);
        } else {
            btn_remove.setVisibility(View.GONE);
            toggleSwitch.setVisibility(View.GONE);
        }
//        LabeledSwitch sw_public = view.findViewById(R.id.sw_public);
//        sw_public.setLabelOff("Private");
//        sw_public.setLabelOn("Public");
//        sw_public.setOn(model.isPublic);
//        sw_public.setOnToggledListener(new OnToggledListener() {
//            @Override
//            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
//                boolean ss = isOn;
//            }
//        });
        return view;
    }
}
