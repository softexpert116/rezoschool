package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.firebase.database.ValueEventListener;

public class OTPActivity extends AppCompatActivity {
    Button otpBtn;
    EditText edit_code;
    String country_code, number;
    String verificationID;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);
        country_code = getIntent().getExtras().getString("country_code");
        number = getIntent().getExtras().getString("number");
        verificationID = getIntent().getExtras().getString("verificationID");

        getSupportActionBar().hide();
        edit_code = (EditText)findViewById(R.id.edit_code);

        otpBtn = findViewById(R.id.btnOTP);
        otpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_code = edit_code.getText().toString();
                if (input_code.length() == 0 || input_code.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please input the security number", Toast.LENGTH_LONG).show();

                } else if (input_code.length() < 6) {

                    Toast.makeText(getApplicationContext(), "Please match length of security number", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, input_code);
                        signInWithPhone(credential);
//                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                Intent mainIntent = new Intent(OTPActivity.this, MainActivity.class);
//                OTPActivity.this.startActivity(mainIntent);
//                finishAffinity();

            }
        });
    }
    public void signInWithPhone(PhoneAuthCredential credential)
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Utils.auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Utils.mUser = Utils.auth.getCurrentUser();
                            Utils.mDatabase.child(Utils.tbl_user).orderByChild(Utils.USER_PHONE).equalTo(country_code+number)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            progressDialog.dismiss();
                                            if (dataSnapshot.getValue() != null) {
                                                App.goToMainPage(OTPActivity.this);
                                            } else {
                                                // go to register page
                                                goToRegisterActivity();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            progressDialog.dismiss();
                                            // Getting Post failed, log a message
                                            goToRegisterActivity();

                                            Log.w( "loadPost:onCancelled", databaseError.toException());
                                            // ...
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
//                            Utils.showAlert(OTPActivity.this, "Warning", task.getException().getMessage());
                            Toast.makeText(OTPActivity.this, "OTP verification failed! Please try again..", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
    void goToRegisterActivity() {
        Intent intent = new Intent(OTPActivity.this, RegisterActivity.class);
        intent.putExtra("country_code", country_code);
        intent.putExtra("number", number);
        startActivity(intent);
        finish();
    }
}