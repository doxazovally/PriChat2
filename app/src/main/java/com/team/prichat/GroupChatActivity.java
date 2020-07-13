package com.team.prichat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    public ImageButton sendmsgbtn;
    public EditText writemsg;
    public ScrollView mScrowview;
    public TextView displayGroupTextInput;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, GroupNameRef, GroupMessageKeyRef;

       // receiving the selected group name in the group chat
    private String currentGroupName, currentUserId, currentUserName, currentDate, currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        // getting the Groupname from the Groupfragnemt and storing it inside the currentGroupName
        currentGroupName = getIntent().getExtras().get("groupName").toString().toUpperCase();
        Toast.makeText(GroupChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
           // with the help of the currentUserId, the user name can be gotten from the database
        currentUserId = mAuth.getCurrentUser().getUid();



        InitializeFields();

        GetUserInfo();

        sendmsgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savemsgbtnInfoToDatabase();

                // enabling the message box to empty when the send button is clicked
                writemsg.setText("");
                
                mScrowview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
                 // retrieving the chat message from the database to the screen when chat is going on
    @Override
    protected void onStart() {
        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void InitializeFields() {
        mToolbar = findViewById(R.id.group_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        sendmsgbtn = findViewById(R.id.send_msg_btn);
        writemsg = findViewById(R.id.write_group_msg);
        displayGroupTextInput = findViewById(R.id.groupchat_text_display);
        mScrowview = findViewById(R.id.chat_scow_view);

    }

          // getting the user's information when he sends a message
    private void GetUserInfo() {
        UserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


            // convert the message to string
    private void savemsgbtnInfoToDatabase() {
        String message = writemsg.getText().toString();
        String messageKey = GroupNameRef.push().getKey();

                // verifying if message box is empty or not
        if(TextUtils.isEmpty(message)){
            Toast.makeText(this, "Messagebox empty...", Toast.LENGTH_SHORT).show();
        }

               // getting the time and date the message was sent ie if the message box is not empty.
        else{
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            // saving the time, date and other message information into the database
            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef = GroupNameRef.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            GroupMessageKeyRef.updateChildren(messageInfoMap);

        }
    }
          // retrieve and display all messages for each specific group
    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            displayGroupTextInput.append(chatName + " :\n" + chatMessage + " :\n" + chatTime + "  " + chatDate + " :\n\n\n");

                // making the message scroll down on its own (Automatically)
            mScrowview.fullScroll(ScrollView.FOCUS_DOWN);
        }

    }




}
