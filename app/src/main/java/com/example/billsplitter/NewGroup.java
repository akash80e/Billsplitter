package com.example.billsplitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.billsplitter.MainActivity.getNameFromUserID;


public class NewGroup extends AppCompatActivity {

    Button btnCreateGroup;
    Button btnAddFriend;

    EditText edGroupName;
    private DatabaseReference ExpensesTable;

    private ArrayList<String> friendUserIDList;
    private ArrayList<String> userItems = new ArrayList<>();

    private String[] friends;



    private boolean[] checkedItems;

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_appartment:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_house:
                if (checked)
                    // Ninjas rule
                    break;

            case R.id.radio_trip:
                if (checked)
                    // Ninjas rule
                    break;

            case R.id.radio_other:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        btnCreateGroup = findViewById(R.id.create_group_button);

        btnAddFriend = findViewById(R.id.create_add_friend);
        edGroupName = findViewById(R.id.group);

        friendUserIDList = new ArrayList<>();


        friendUserIDList = getIntent().getStringArrayListExtra("friends");
        checkedItems = new boolean[friendUserIDList.size()];


        friends = new String[friendUserIDList.size()];
        for(int i=0; i < friendUserIDList.size();i++){
            friends[i] =   getNameFromUserID(friendUserIDList.get(i)) ;
        }

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewGroup.this);
                builder.setTitle("Select Group Members");
                builder.setMultiChoiceItems(friends, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos, boolean check) {
                        if(check){
                            if(! userItems.contains(friends[pos])){
                                userItems.add(friends[pos]);
                            }
                        }else if(userItems.contains(friends[pos])){
                            userItems.remove(friends[pos]);
                        }
                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String people = "";
                        for (int j = 0; j < userItems.size(); j++){
                            people = people + userItems.get(j);
                        }
                    }
                });
                for (int i=0; i<userItems.size(); i++) {
                    System.out.println(userItems.get(i));
                }
                AlertDialog mDialog = builder.create();
                mDialog.show();
            }
        });

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroupToDb();
            }
        });
    }

    private void addGroupToDb() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ExpensesTable = database.getReference("expenses_data");

        final String groupName = edGroupName.getText().toString();



        ExpensesTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    if (friendUserIDList.contains(unit.getKey())){
                        for(int i=0;i<friendUserIDList.size();i++){
                            ExpensesTable.child(unit.getKey()).child("group_expenses").child(groupName).child(friendUserIDList.get(i)).setValue("0");
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
