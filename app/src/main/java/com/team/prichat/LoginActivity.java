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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout login1;
    private TextView create_acc, forget_pass;
    private EditText UserEmail, UserPassword, userPhone, encryptKey;
    private ProgressDialog progressbar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        InitializeFields();

        login1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              AllowUserSignin();
            }
        });

        create_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               SendUserToRegActivity();
            }
        });

       // userPhone.setOnClickListener(new View.OnClickListener() {
       //     @Override
         //   public void onClick(View view) {
         //       Intent phoneNo = new Intent(LoginActivity.this, Phone_login.class);
         //       startActivity(phoneNo);
       //     }
      //  });

    }

    private void AllowUserSignin() {
        String email = UserEmail.getText().toString();
        String pass = UserPassword.getText().toString();
       // String encrypt = encryptKey.getText().toString();


        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter your email...", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Please enter your password...", Toast.LENGTH_SHORT).show();
        }

       // if(TextUtils.isEmpty(encrypt)){
      //      Toast.makeText(this,"Please enter your encrypt key...", Toast.LENGTH_SHORT).show();
       //}
        else {
            progressbar.setTitle("Signing in");
            progressbar.setMessage("Please hold on while we sign you in");
            progressbar.setCanceledOnTouchOutside(true);
            progressbar.show();
        }

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(LoginActivity.this, "Login successful...", Toast.LENGTH_SHORT).show();
                           SigninUserToMainActivity();
                            progressbar.dismiss();
                        }
                        else {
                            String message = task.getException().toString();
                            Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            progressbar.dismiss();
                        }
                    }
                });

    }

    private void InitializeFields() {
        login1 = findViewById(R.id.lbtn);
        create_acc = findViewById(R.id.Cacc);
        forget_pass = findViewById(R.id.Fpass);
        UserEmail = findViewById(R.id.email);
        UserPassword = findViewById(R.id.password);
        //userPhone = findViewById(R.id.phoneNum);
      //  encryptKey = findViewById(R.id.encryCode);
        progressbar = new ProgressDialog(this);

    }

              // checking if a user is already logged in
    private void SigninUserToMainActivity() {
        Intent login = new Intent(LoginActivity.this, MainActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }

    private void SendUserToRegActivity() {
        Intent login = new Intent(LoginActivity.this, RegActivity.class);
        startActivity(login);
    }

}
