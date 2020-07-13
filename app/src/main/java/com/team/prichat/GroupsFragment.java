package com.team.prichat;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {
    private View groupFrag;
    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private ArrayList<String> list_of_group = new ArrayList<>();

    private DatabaseReference groupref;


    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        groupFrag = inflater.inflate(R.layout.fragment_groups, container, false);

        groupref = FirebaseDatabase.getInstance().getReference().child("Groups");

        InitializeField();

        RetrieveAndDisplayGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long Id) {

                         // currentGroupName: the name a user clicks will be stored there, and it will be gotten using the Adaptedview, passing in the position
                String currentGroupName = adapterView.getItemAtPosition(position).toString();

                       // A user is then sent to the GroupChatActivity, getcontext() is used becus it is from a fragment
                Intent groupChat = new Intent(getContext(), GroupChatActivity.class);

                     // sending the name stored in the currentGroupName to the GroupChat
                groupChat.putExtra("groupName", currentGroupName);
                startActivity(groupChat);

            }
        });

        return groupFrag;
    }

    private void InitializeField() {

        listView = groupFrag.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_group);
        listView.setAdapter(arrayAdapter);
    }

    private void RetrieveAndDisplayGroups() {

             // Connecting to the database to have access to the groups
        groupref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   // Avoiding duplicate when group is created
                Set<String> set = new HashSet<>();

                // retrieving each group line be line
                Iterator iterator = dataSnapshot.getChildren().iterator();

                //  reading each group line by line
                while (iterator.hasNext()){

                    //  Avoiding duplication of group.  *get key gets the group names
                         // set.add holds all the group names.
                    set.add(((DataSnapshot) iterator.next()).getKey());
                }

                list_of_group.clear();

                // displaying group names
                list_of_group.addAll(set);

                  // To see the changes of the groups on the screen
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
