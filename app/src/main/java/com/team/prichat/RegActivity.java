package com.team.prichat;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegActivity extends AppCompatActivity {
    private Button login_btn_2;
    private LinearLayout signup;
    private EditText UserEmail, UserPassword, enctyptKey;
    private FirebaseAuth mAuth;
    private ProgressDialog LoadingProgressBar;
    private DatabaseReference RootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_reg);

           mAuth = FirebaseAuth.getInstance();
           RootRef = FirebaseDatabase.getInstance().getReference();

           InitializeFields();

          login_btn_2.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   SendUserToLogin1Activity();
               }
          });

          signup.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   CreateNewAccount();
               }
          });

    }

    // Authenticating the email and password
    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String pass = UserPassword.getText().toString();
        String encrypt = enctyptKey.getText().toString();


        if(TextUtils.isEmpty(email)){
           Toast.makeText(this, "Please enter your email...", Toast.LENGTH_SHORT).show();
           return;
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this, "Please enter your password...", Toast.LENGTH_SHORT).show();

        }
        if(TextUtils.isEmpty(encrypt)){
            Toast.makeText(this, "Please enter your encryption key...", Toast.LENGTH_SHORT).show();

        }
        else {
                     // Displaying a progressbar while creating account
            LoadingProgressBar.setTitle("Creating New Account");
            LoadingProgressBar.setMessage("Creating your account...");
            LoadingProgressBar.setCanceledOnTouchOutside(false);
            LoadingProgressBar.show();

                 // Anthenicatig the signin with firebase
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                String currentUserID = mAuth.getCurrentUser().getUid();
                                RootRef.child("User").child(currentUserID).setValue("");

                                SigninUserToMainActivity();
                                Toast.makeText(RegActivity.this, "Your account is successfully created", Toast.LENGTH_SHORT).show();
                                LoadingProgressBar.dismiss();
                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(RegActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                                LoadingProgressBar.dismiss();
                            }
                        }
                    });
        }

    }

    private void InitializeFields() {
        login_btn_2 = findViewById(R.id.lbtn2);
        signup = findViewById(R.id.signup_btn);
        UserEmail = findViewById(R.id.email2);
        UserPassword= findViewById(R.id.password2);
        enctyptKey= findViewById(R.id.encryCode);
        LoadingProgressBar = new ProgressDialog(this);

    }

    private void SendUserToLogin1Activity() {

        Intent login2 = new Intent(RegActivity.this, LoginActivity.class);
        startActivity(login2);
    }

    private void SigninUserToMainActivity() {

        Intent login = new Intent(RegActivity.this, MainActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }

}
