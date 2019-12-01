package com.example.billsplitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.billsplitter.ui.home.CustomListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DisplayGroup extends AppCompatActivity {

    private TextView groupName;
    private ListView allMembers;
    private Button addFriend;
    private DatabaseReference databaseReference;
    private ArrayList<String> groupsMembers;
    private ArrayList<String> amounts;
    private Integer imgId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_group);

        groupName = findViewById(R.id.group_name);
        allMembers = findViewById(R.id.groupMembers);
        addFriend = findViewById(R.id.addPeople);

        groupsMembers = new ArrayList<>();
        amounts = new ArrayList<>();
        imgId = R.drawable.ic_person_black_24dp;

        final String group = getIntent().getStringExtra("groupName");
        groupName.setText(group);

        databaseReference = FirebaseDatabase.getInstance().getReference("expenses_data");
        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        final String my_id = sp.getString("UserId", null);



        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot unit: dataSnapshot.getChildren()){
                    if(unit.getKey().equals(my_id)){
                        for(DataSnapshot groups:unit.getChildren()){
                            if(groups.getKey().equals("group_expenses")){
                                for(DataSnapshot userGroup:groups.getChildren()){

                                    if(userGroup.getKey().equals(group)){
                                        for(DataSnapshot members:userGroup.getChildren()){
                                            groupsMembers.add(MainActivity.getNameFromUserID(members.getKey()));
                                            amounts.add(members.getValue().toString());
                                        }
                                    }
                                }

                            }
                        }


                    }
                }
                final CustomListView adapter = new CustomListView(DisplayGroup.this, groupsMembers, amounts , imgId);
                allMembers.setAdapter(adapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DisplayGroup.this);
                builder.setTitle("Enter the username");
                final Context context = builder.getContext();
                final LayoutInflater inflater = LayoutInflater.from(context);
                final View new_view = inflater.inflate(R.layout.add_new_friend, null, false);
//
                final EditText editText = new_view.findViewById(R.id.newFriend);

                builder.setPositiveButton("Add Friend", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String friend_id = editText.getText().toString();

                    }
                });
                builder.setView(new_view);
                AlertDialog mDialog = builder.create();
                mDialog.show();
            }
        });
    }
}
