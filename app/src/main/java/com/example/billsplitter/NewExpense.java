package com.example.billsplitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.billsplitter.ui.home.FriendsTab;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.seismic.ShakeDetector;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.example.billsplitter.MainActivity.checkUser;
import static com.example.billsplitter.MainActivity.getIdFromUserName;
import static com.example.billsplitter.MainActivity.getNameFromUserID;

public class NewExpense extends AppCompatActivity implements ShakeDetector.Listener {
    private Button paid;
    private Button split, addItem;

    private boolean[] checkedItems;
    private String selected;
    private String friendPhoneNumber;
    private EditText etDescription;
    private EditText etAmount;

    private ArrayList<String> userItems = new ArrayList<>();

    private ArrayList<String> PaidList;
    private boolean paidByYou = true;

    private String[] listItems;

    private DatabaseReference ExpenseTable;
    private DatabaseReference FriendsAndGroups;
    private String UserID;
    private String you;
    private String friend;

    private ArrayAdapter<String> adapter;

    private String selectedFriendOrGroup;
    private Spinner sList;

    private String PaidBy;
    private String amountFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_expense);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector shakeDetector = new ShakeDetector(NewExpense.this);
        shakeDetector.start(sensorManager);

        etAmount = findViewById(R.id.amount);
        etDescription = findViewById(R.id.describeitem);
        paid = findViewById(R.id.paidByButton);
        addItem = findViewById(R.id.additem);

        Button imageUpload = findViewById(R.id.upload_image);

        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        UserID = sp.getString("UserId", null);

        ArrayList<String> f= getIntent().getStringArrayListExtra("friends");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FriendsAndGroups = database.getReference("/expenses_data");

        ArrayList<String> friendsAndGroups = new ArrayList<>();
        for (int i=0;i<f.size();i++)
        {
            if (getNameFromUserID(f.get(i)).equals(""))
            {
                friendsAndGroups.add(f.get(i));
            }
            else {
                friendsAndGroups.add(getNameFromUserID(f.get(i)));
            }
        }
        PaidList = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                NewExpense.this, android.R.layout.simple_list_item_1, friendsAndGroups);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sList =  findViewById(R.id.list_friends);
        sList.setAdapter(adapter);

        sList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFriendOrGroup = sList.getSelectedItem().toString();
                PaidList.clear();
                PaidList.add("You");
                if (!checkUser(selectedFriendOrGroup))
                {
                    FriendsAndGroups.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot unit : dataSnapshot.child(UserID).child("group_expenses").getChildren()) {
                                if (unit.getKey().equals(selectedFriendOrGroup)){
                                    for (DataSnapshot members : unit.getChildren()){

                                        if (!members.getKey().equals(UserID)){
                                            PaidList.add(getNameFromUserID(members.getKey()));
                                        }

                                        System.out.println("**************" + members.getKey());
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                else
                {
                    PaidList.add(selectedFriendOrGroup);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedFriendOrGroup = sList.getSelectedItem().toString();


                System.out.println(UserID);
                System.out.println(sList.getSelectedItem());


                listItems = new String[PaidList.size()];
                for(int i=0;i<PaidList.size();i++){
                    listItems[i] = PaidList.get(i);
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(NewExpense.this);
                builder.setTitle("Paid by");
                builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected = listItems[which];
                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        /*String people = "";
                        for (int j = 0; j < userItems.size(); j++){
                            people = people + userItems.get(j);
                        }

                        System.out.println(selected);
                        if (selected.equals("Friend")){
                            paidByYou = false;
                            paid.setText(R.string.paid_by_friend);
                        }
                        else{
                            paid.setText(R.string.paid_by_you);
                        }*/
                    }
                });
                AlertDialog mDialog = builder.create();
                mDialog.show();
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = etDescription.getText().toString();
                String amount = etAmount.getText().toString();
                Toast.makeText(getApplicationContext(), selected, Toast.LENGTH_SHORT).show();
                if (checkUser(selectedFriendOrGroup)){
                    addExpenseToDb(desc, amount, getIdFromUserName(selectedFriendOrGroup) );
                }
                else {
                    addExpenseToDb(desc, amount,"");
                }

                //String friendUserName = etUserName.getText().toString();
                //addExpenseToDb(desc, amount, friendUserName);
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                String filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
                //image_name_tv.setText(filePath);

                try {
                    if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {

                    } else {
                        throw new FileNotFoundException();
                    }
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == 2 && resultCode == Activity.RESULT_OK){

            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getApplicationContext().getContentResolver().query(contactUri, projection,
                    null, null, null);

            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                System.out.println(numberIndex);
                System.out.println(nameIndex);
                String number = cursor.getString(0);
                //String name = cursor.getString(1);
                friendPhoneNumber = number;
                //friendName = name;
                System.out.println("Friend Phone Number " + friendPhoneNumber);
            }
            cursor.close();
        }
    }

    public String getPath(Uri uri) {
        int column_index;
        String imagePath;
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        imagePath = cursor.getString(column_index);
        return cursor.getString(column_index);
    }


    private void addExpenseToDb(String desc, String amount, final String friendID){
        Double splitAmount;
        int number;

        final String groupName = sList.getSelectedItem().toString();

         PaidBy = selected;
        if (PaidBy.equals("You")){
            PaidBy = UserID;
        }
        else {
            PaidBy = getIdFromUserName(selected);
        }

        boolean groupExpense = true;
        if (friendID.equals("")){
            number = PaidList.size();
            splitAmount = Double.parseDouble(amount)/number;
             amountFinal = String.valueOf(splitAmount);
        }
        else {
            groupExpense = false;
            number = 2;
            splitAmount = Double.parseDouble(amount)/number;
            amountFinal = String.valueOf(splitAmount);

            if (paidByYou){
                you = amountFinal;
                friend = "-" + amountFinal;
            }
            else {
                friend = amountFinal;
                you = "-" + amountFinal;
            }
        }


        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        ExpenseTable = database.getReference("expenses_data/");

        if (groupExpense){
            ExpenseTable.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot unit : dataSnapshot.getChildren()) {

                        if (unit.getKey().equals(PaidBy)){

                            for (DataSnapshot group : unit.child("group_expenses").child(groupName).getChildren())
                            {
                                String preValue = group.getValue().toString();
                                Double value = Double.parseDouble(preValue);
                                value = value + Double.parseDouble(amountFinal);

                                ExpenseTable.child(unit.getKey()).child("group_expenses").child(groupName).child(group.getKey()).setValue(value);

                            }
                        }
                        else if (PaidBy.contains(getNameFromUserID(unit.getKey()))) {

                            for (DataSnapshot group : unit.child("group_expenses").child(groupName).getChildren())
                            {
                                String preValue = group.getValue().toString();
                                Double value = Double.parseDouble(preValue);
                                value = value - Double.parseDouble(amountFinal);

                                ExpenseTable.child(unit.getKey()).child("group_expenses").child(groupName).child(group.getKey()).setValue(value);
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
        else {
            ExpenseTable.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot unit : dataSnapshot.getChildren()) {

                        if (unit.getKey().equals(UserID)){
                            String preValue = unit.child("individual_expenses").child(friendID).getValue().toString();
                            Double value = Double.parseDouble(preValue);
                            value = value + Double.parseDouble(you);

                            ExpenseTable.child(unit.getKey()).child("individual_expenses").child(friendID).setValue(String.valueOf(value));
                        }
                        else if (unit.getKey().equals(friendID)){
                            String preValue = unit.child("individual_expenses").child(UserID).getValue().toString();
                            Double value = Double.parseDouble(preValue);
                            value = value + Double.parseDouble(friend);
                            ExpenseTable.child(unit.getKey()).child("individual_expenses").child(UserID).setValue(value);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    @Override
    public void hearShake() {
        Intent intent = new Intent(NewExpense.this, HomeActivity.class);
        startActivity(intent);
    }


}

