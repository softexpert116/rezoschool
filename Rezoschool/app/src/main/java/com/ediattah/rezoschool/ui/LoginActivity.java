package com.ediattah.rezoschool.ui;

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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    Button btn_login, btn_signup, btn_forgot_password;
    EditText edit_phone;
    ProgressDialog progressDialog;
    CountryCodePicker txt_countryCode;
    String country_code, number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        App.hideKeyboard(this);

        getSupportActionBar().hide();

        edit_phone = findViewById(R.id.edit_phone);
        btn_login = findViewById(R.id.btn_login);
        txt_countryCode = findViewById(R.id.txt_countryCode);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.CheckEditTextIsEmptyOrNot(edit_phone)) {
                    country_code = txt_countryCode.getSelectedCountryCode();
                    number = edit_phone.getText().toString().trim();
                    number = number.replace(" ", "");
                    number = number.replace("-", "");
                    sendSMS(country_code + number);
                } else {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.please_input_your_mobile_number), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void sendSMS(final String mobileNumber) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                progressDialog.dismiss();
                Log.d("msg", "onVerificationCompleted");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Log.d("msg", e.getLocalizedMessage());
//                e.printStackTrace();
                Utils.showAlert(LoginActivity.this, getResources().getString(R.string.error), e.getMessage());
            }

            @Override
            public void onCodeSent(String verificationID, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.sms_code_has_been_sent_to_your_phone), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, OTPActivity.class);
                intent.putExtra("country_code", country_code);
                intent.putExtra("number", number);
                intent.putExtra("verificationID", verificationID);
                LoginActivity.this.startActivity(intent);
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+" + mobileNumber, 60, TimeUnit.SECONDS, this, mCallback
        );
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.show();
    }
}