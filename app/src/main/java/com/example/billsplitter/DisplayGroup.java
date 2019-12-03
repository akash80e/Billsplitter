package com.example.billsplitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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
    private ArrayList<String> memberID;
    private CustomListView adapter;

    private Integer imgId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_group);

        groupName = findViewById(R.id.group_name);
        addFriend = findViewById(R.id.addPeople);

        groupsMembers = new ArrayList<>();
        amounts = new ArrayList<>();
        memberID = new ArrayList<>();
        imgId = R.drawable.ic_person_black_24dp;
        allMembers = findViewById(R.id.recycler);

        final String group = getIntent().getStringExtra("groupName");
        groupName.setText(group.toUpperCase());

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
                                            memberID.add(members.getKey());
                                        }
                                    }
                                }

                            }
                        }


                    }
                }
//                ArrayList<Card> final_list = new ArrayList<>();
//                for (int i = 0; i < groupsMembers.size();i++){
//                    final_list.add(new Card(R.drawable.profile_picture,groupsMembers.get(i)));
//                }
//                recyclerView.setHasFixedSize(true);
//              //  layoutManager = new LinearLayoutManager(getApplicationContext());
//                //adapter for getting cardsList
//                adapter = new ViewAdaptor(final_list);
//
//                recyclerView.setLayoutManager(new LinearLayoutManager(DisplayGroup.this));
//                recyclerView.setAdapter(adapter);

               // ArrayList<Card> cardsList =  new ArrayList<>();

                adapter = new CustomListView(DisplayGroup.this, groupsMembers, amounts , imgId);
                allMembers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        allMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),GroupActivitySettleExpense.class);
                intent.putExtra("groupName",group);
                intent.putExtra("members",memberID);
                String item = adapter.getItem(i);
                intent.putExtra("UserName",item);
                startActivity(intent);
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
                        final String friend_id = editText.getText().toString();
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot unit: dataSnapshot.getChildren()){
                                    if( (unit.getKey().equals(my_id)) ||  (memberID.contains(unit.getKey()))){
                                        for(DataSnapshot groups:unit.getChildren()){
                                            if(groups.getKey().equals("group_expenses")){
                                                for(DataSnapshot userGroup:groups.getChildren()){

                                                    if(userGroup.getKey().equals(group)){
                                                        databaseReference.child(unit.getKey()).child("group_expenses").child(group).child(friend_id).setValue("0.0");
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot unit: dataSnapshot.getChildren()){
                                    if(unit.getKey().equals(friend_id)){
                                        for(int i = 0; i < memberID.size(); i++){
                                            databaseReference.child(unit.getKey()).child("group_expenses").child(group).child(memberID.get(i)).setValue("0.0");
                                        }
                                    }
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setView(new_view);
                AlertDialog mDialog = builder.create();
                mDialog.show();

            }

        });

    }
}
