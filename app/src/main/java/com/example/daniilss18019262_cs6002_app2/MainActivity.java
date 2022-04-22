package com.example.daniilss18019262_cs6002_app2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText phone, otp;
    Button btngentOTP, btnverify;
    FirebaseAuth mAuth;
    String verificationID;
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phone = findViewById(R.id.phone);
        otp = findViewById(R.id.otp);
        btngentOTP = findViewById(R.id.btnGenerateOTP);
        btnverify = findViewById(R.id.btnVerifyOTP);
        mAuth = FirebaseAuth.getInstance();
        bar = findViewById(R.id.bar);

        btngentOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(phone.getText().toString())){
                    Toast.makeText(MainActivity.this, "Enter Valid Phone No.", Toast.LENGTH_SHORT).show();
                } else {
                    String number = phone.getText().toString();
                    bar.setVisibility(View.VISIBLE);
                    sendVerificationCode(number);
                }

            }
        });

        btnverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(TextUtils.isEmpty(otp.getText().toString())){
                    Toast.makeText(MainActivity.this, "Wrong OTP Entered.", Toast.LENGTH_SHORT).show();
                } else{
                    verifyCode(otp.getText().toString());
                }

            }
        });
    }


    private void sendVerificationCode(String phoneNumber) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+44"+phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

                final String code = credential.getSmsCode();
                if(code!=null)
                {
                    verifyCode(code);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(MainActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s,
                    @NonNull PhoneAuthProvider.ForceResendingToken token)
            {
                super.onCodeSent(s, token);
                verificationID = s;
                Toast.makeText(MainActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
                btnverify.setEnabled(true);
                bar.setVisibility(View.INVISIBLE);
            }
        };

    private void verifyCode(String Code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, Code);
        singbyCredentials(credential);
    }

    private void singbyCredentials(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!=null)
        {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }
    }
}