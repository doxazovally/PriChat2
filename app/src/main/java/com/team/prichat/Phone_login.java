package com.team.prichat;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Phone_login extends AppCompatActivity {

    private LinearLayout sendVerificationCode, verify;
    private EditText phoneNum, inputverifiCode;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();

        sendVerificationCode = findViewById(R.id.send_sms);
        verify = findViewById(R.id.send_sms_verif);
        phoneNum = findViewById(R.id.phone_no);
        inputverifiCode = findViewById(R.id.verification_code);
        loadingBar = new ProgressDialog(this);


        sendVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                      // getting phone number
                String phoneNumber = phoneNum.getText().toString();

                if(TextUtils.isEmpty(phoneNumber)){

                    Toast.makeText(Phone_login.this, "Phone number required", Toast.LENGTH_SHORT).show();

                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please wait while we authenticate your phone number...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                }

                else {
                         // verifying the phone number
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            Phone_login.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });

         verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode.setVisibility(View.INVISIBLE);
                phoneNum.setVisibility(View.INVISIBLE);

                String verificationcode = inputverifiCode.getText().toString();

                if (TextUtils.isEmpty(verificationcode)) {

                    Toast.makeText(Phone_login.this, "Verification code needed...", Toast.LENGTH_SHORT).show();
                }

                else {

                    loadingBar.setTitle("Verification Code");
                    loadingBar.setMessage("Verifying your verification code...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
         });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                loadingBar.dismiss();

                Toast.makeText(Phone_login.this, "Invalid Verification code OR Phone Number", Toast.LENGTH_SHORT).show();

                sendVerificationCode.setVisibility(View.VISIBLE);
                phoneNum.setVisibility(View.VISIBLE);

                verify.setVisibility(View.INVISIBLE);
                inputverifiCode.setVisibility(View.INVISIBLE);

            }

            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
              //  Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                loadingBar.dismiss();

                Toast.makeText(Phone_login.this, "Verification code has been sent to you, please check and verify",
                        Toast.LENGTH_SHORT).show();

                sendVerificationCode.setVisibility(View.INVISIBLE);
                phoneNum.setVisibility(View.INVISIBLE);

                verify.setVisibility(View.VISIBLE);
                inputverifiCode.setVisibility(View.VISIBLE);

            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
          mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            loadingBar.dismiss();
                            Toast.makeText(Phone_login.this, "Congratulations... Login Successful", Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();

                        }
                        else {
                            String message = task.getException().toString();
                            Toast.makeText(Phone_login.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }

                    }
                });
    }

    private void sendUserToMainActivity() {
            Intent mainIntent = new Intent(Phone_login.this, MainActivity.class);
            startActivity(mainIntent);
            finish();

    }


}
