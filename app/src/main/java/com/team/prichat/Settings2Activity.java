package com.team.prichat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings2Activity extends AppCompatActivity {

    private Button UpdateSettingsbtn, backToHomeBtn;
    private EditText username, userStatus;
    private CircleImageView userProfileImg;
    private ProgressDialog loadingBar;

    private String currentUserid;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private Uri ImgUri;

    private static final int GalleryPick = 1;
    private StorageReference UserProfileImagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();
       RootRef = FirebaseDatabase.getInstance().getReference();

       UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images").child("Images");

        InitializeFields();

          // username.setVisibility(View.INVISIBLE);

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        backToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back_btn = new Intent(Settings2Activity.this, MainActivity.class);
                back_btn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(back_btn);
                finish();
            }
        });

        UpdateSettingsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSettings();
            }
        });

        RetriveUserInformation();

        userProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, GalleryPick);
            }
        });
    }

    private void InitializeFields() {
       UpdateSettingsbtn = findViewById(R.id.update_settings_button);
        username = findViewById(R.id.profile_username);
        userStatus = findViewById(R.id.profile_status);
        userProfileImg = findViewById(R.id.profile_image);
        backToHomeBtn = findViewById(R.id.back_to_home_btn);
        loadingBar = new ProgressDialog(this);
    }

         // this method will get the image of the user
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPick && resultCode == RESULT_OK && data!= null){

            ImgUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Profile Image Updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();

                final StorageReference filepath = UserProfileImagesRef.child(currentUserid + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(Settings2Activity.this, "Profile Image uploaded successfully...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();


                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                   final String downloadUrl = uri.toString();

                                    RootRef.child("Users").child(currentUserid).child("Images").setValue(downloadUrl)
                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete (@NonNull Task < Void > task) {
                                                if (task.isSuccessful()) {
                                                    loadingBar.dismiss();
                                                    Toast.makeText(Settings2Activity.this, "Image successfully saved to the database...", Toast.LENGTH_SHORT).show();
                                                }

                                                else {
                                                      String message = task.getException().toString();
                                                      loadingBar.dismiss();
                                                      Toast.makeText(Settings2Activity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                    });
                                }
                            });

                        }

                        else {
                            String message = task.getException().toString();
                            Toast.makeText(Settings2Activity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        }

    }

    private void UpdateSettings() {
        String setUserName = username.getText().toString();
        String setUserStatus = userStatus.getText().toString();
        


        if(TextUtils.isEmpty(setUserName)){

            Toast.makeText(Settings2Activity.this, "please write your username", Toast.LENGTH_SHORT).show();

            return;
        }

        if(TextUtils.isEmpty(setUserStatus)){
            Toast.makeText(Settings2Activity.this, "please write your status", Toast.LENGTH_SHORT).show();

        }

        else {
              HashMap<String, String> profileMap  = new HashMap<>();profileMap.put("Uid", currentUserid);
              profileMap.put("name", setUserName);
              profileMap.put("status", setUserStatus);
              profileMap.put("Images", "");

              RootRef.child("Users").child(currentUserid).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                             //   SigninUserToMainActivity();
                                Toast.makeText(Settings2Activity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                            else {
                                String message = task.getException().toString();
                                Toast.makeText(Settings2Activity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }

    }

    private void RetriveUserInformation() {
          RootRef.child("Users").child(currentUserid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    // if the user has created his profile or not
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("Images")))) {

                                       // retrieving the user's information
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveUserStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveprofileimage = dataSnapshot.child("Images").getValue().toString();

                            username.setText(retrieveUserName);
                            userStatus.setText(retrieveUserStatus);
                            Picasso.get().load(retrieveprofileimage).placeholder(R.drawable.profile).into(userProfileImg);
                        }

                        else {
                                            // username.setVisibility(View.VISIBLE);
                            Toast.makeText(Settings2Activity.this, "Please set and update your profile information..", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }

                });
    }

  //  private void SigninUserToMainActivity() {

     //   Intent login = new Intent(Settings2Activity.this, MainActivity.class);
    //    login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    //    startActivity(login);
   //     finish();
   // }

}
