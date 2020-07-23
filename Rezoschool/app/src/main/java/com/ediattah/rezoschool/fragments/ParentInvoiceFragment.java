package com.ediattah.rezoschool.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Transaction;
import com.ediattah.rezoschool.Model.TransactionModel;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SchoolFinanceListAdapter;
import com.ediattah.rezoschool.httpsModule.RestClient;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewPaymentActivity;
import com.ediattah.rezoschool.ui.NewRsTweetsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ParentInvoiceFragment extends Fragment {
    MainActivity activity;
    ListView listView;
    SchoolFinanceListAdapter schoolFinanceListAdapter;
    ArrayList<Transaction> arrayList = new ArrayList<>();
    LinearLayout ly_no_items;
    Transaction sel_transaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_parent_invoice, container, false);
        listView = v.findViewById(R.id.listView);
        ly_no_items = v.findViewById(R.id.ly_no_items);

        schoolFinanceListAdapter = new SchoolFinanceListAdapter(activity, arrayList);
        listView.setAdapter(schoolFinanceListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (Utils.currentUser.type.equals(Utils.PARENT)) {
                    Transaction transaction = arrayList.get(i);
                    if (transaction.status.equals("200")) {
                        sel_transaction = transaction;
                        openCompleteDialog(transaction.pay_link);
                    }
                }
            }
        });
        read_transactions();
        return v;
    }
    private void payment_status_ediaRequest() {
        final JSONObject object = new JSONObject();
        try {
            object.put("command", "get payment request status");
            object.put("merchantid", sel_transaction.merchantid);
            object.put("uniqueid", sel_transaction.uniqueid);
            object.put("transactionid", sel_transaction.transactionid);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        final RestClient restClient = RestClient.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = restClient.postRequest(App.ediapayUrl+"api_payment", object);
                JSONObject jsonObject = null;
                String _error = null;
                int pay_code = 0;
                try {
                    jsonObject = new JSONObject(response);
                    pay_code = jsonObject.getInt("code");

                    switch (pay_code) {
                        case 200:
                            _error = "Payment queued";
                            break;
                        case 100:
                            _error = "Payment success";

                            final int finalPay_code = pay_code;
                            Utils.mDatabase.child(Utils.tbl_transaction).orderByChild("transaction_id").equalTo(sel_transaction.transactionid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue()!=null) {
                                        for (DataSnapshot datas:dataSnapshot.getChildren()) {
                                            Transaction transaction1 = datas.getValue(Transaction.class);
                                            transaction1.status = String.valueOf(finalPay_code);
                                            Utils.mDatabase.child(Utils.tbl_transaction).child(datas.getKey()).setValue(transaction1);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            break;
                        default:
                            _error = jsonObject.getString("error");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void openCompleteDialog(final String pay_link) {
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final View view = getLayoutInflater().inflate(R.layout.dialog_ediapay_link, null);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        Button btn_complete = (Button)view.findViewById(R.id.btn_complete);
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.openUrl(pay_link, activity);
                dlg.dismiss();
            }
        });
        dlg.show();
        dlg.getWindow().setLayout((int)(width*0.95f), ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    void read_transactions() {
        if (Utils.currentUser.type.equals(Utils.PARENT)) {
            Utils.mDatabase.child(Utils.tbl_transaction).orderByChild("parent_id").equalTo(Utils.mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    arrayList.clear();
                    if (dataSnapshot.getValue()!=null) {
                        for (DataSnapshot datas:dataSnapshot.getChildren()) {
                            Transaction transaction = datas.getValue(Transaction.class);
                            transaction._id = datas.getKey();
                            arrayList.add(transaction);
                        }
                    }
                    order_transactions();
                    schoolFinanceListAdapter.flag_parent = true;
                    schoolFinanceListAdapter.notifyDataSetChanged();
                    if (arrayList.size() == 0) {
                        ly_no_items.setVisibility(View.VISIBLE);
                    } else {
                        ly_no_items.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (Utils.currentUser.type.equals(Utils.STUDENT)){
            Utils.mDatabase.child(Utils.tbl_transaction).orderByChild("student_id").equalTo(Utils.mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    arrayList.clear();
                    if (dataSnapshot.getValue()!=null) {
                        for (DataSnapshot datas:dataSnapshot.getChildren()) {
                            Transaction transaction = datas.getValue(Transaction.class);
                            transaction._id = datas.getKey();
                            arrayList.add(transaction);
                        }
                    }
                    order_transactions();
                    schoolFinanceListAdapter.notifyDataSetChanged();
                    if (arrayList.size() == 0) {
                        ly_no_items.setVisibility(View.VISIBLE);
                    } else {
                        ly_no_items.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    void order_transactions() {
        Collections.sort(arrayList, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction rhs, Transaction lhs) {
                return lhs.transactionid.compareTo(rhs.transactionid);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (sel_transaction != null) {
            payment_status_ediaRequest();
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}