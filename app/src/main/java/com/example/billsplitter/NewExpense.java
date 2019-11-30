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
import android.widget.Button;
import android.widget.EditText;
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

public class NewExpense extends AppCompatActivity implements ShakeDetector.Listener {
    private Button paid;
    private Button split, addItem;

    private boolean[] checkedItems;
    private String selected;
    private String friendPhoneNumber;
    private String friendName;
    private EditText etDescription;
    private EditText etAmount;
    private EditText etUserName;
    private ArrayList<String> userItems = new ArrayList<>();
    private boolean paidByYou = true;
    private ArrayList<String> users;
    private String[] listItems;

    private DatabaseReference ExpenseTable;
    private DatabaseReference UserTable;
    private String UserID;
    private String you;
    private String friend;
    private Double value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_expense);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector shakeDetector = new ShakeDetector(NewExpense.this);
        shakeDetector.start(sensorManager);

        etAmount = findViewById(R.id.amount);
        etDescription = findViewById(R.id.describeitem);
        etUserName = findViewById(R.id.friend_username);
        paid = findViewById(R.id.paidByButton);
        addItem = findViewById(R.id.additem);

        Button imageUpload = findViewById(R.id.upload_image);

        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

        listItems = getResources().getStringArray(R.array.person);


     /*   friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, 2);
                paid.setEnabled(true);

            }
        });
*/

        paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                        String people = "";
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
                        }
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
                String friendUserName = etUserName.getText().toString();
                addExpenseToDb(desc, amount, friendUserName);
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
        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);


        Double splitAmount = Double.parseDouble(amount)/2;
       final String amountFinal = String.valueOf(splitAmount);
       if (paidByYou){
            you = amountFinal;
            friend = "-" + amountFinal;
       }
       else {
           friend = amountFinal;
           you = "-" + amountFinal;
       }

        final String user_id = sp.getString("UserId", null);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        System.out.println(user_id);
        System.out.println(friendID);

        ExpenseTable = database.getReference("expenses_data/");
        ExpenseTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot unit : dataSnapshot.getChildren()) {

                    if (unit.getKey().equals(user_id)){
                        if(unit.child("individual_expenses").child(friendID).getValue() != null) {
                            String preValue = unit.child("individual_expenses").child(friendID).getValue().toString();
                            value = Double.parseDouble(preValue);
                            value = value + Double.parseDouble(you);
                        }
                        else{
                            value = Double.parseDouble(you);
                        }
                        ExpenseTable.child(unit.getKey()).child("individual_expenses").child(friendID).setValue(String.valueOf(value));
                    }
                    else if (unit.getKey().equals(friendID)){
                        if(unit.child("individual_expenses").child(user_id).getValue() != null) {
                            String preValue = unit.child("individual_expenses").child(user_id).getValue().toString();
                            value = Double.parseDouble(preValue);
                            value = value + Double.parseDouble(friend);
                        }
                        else{
                            value = Double.parseDouble(friend);
                        }
                        ExpenseTable.child(unit.getKey()).child("individual_expenses").child(user_id).setValue(value);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void hearShake() {
        Intent intent = new Intent(NewExpense.this, HomeActivity.class);
        startActivity(intent);
    }
/*
    private void getUserIDFromEmail(String Email) {
        final String FriendEmail = Email;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        UserTable = database.getReference("users/");

        UserTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot unit : dataSnapshot.getChildren()) {
                    System.out.println("Akash User");
                    System.out.println(unit.getValue());
                    if (unit.child("email").getValue().equals(FriendEmail)) {
                        System.out.println("Found");
                        UserID = unit.getKey();
                        System.out.println(unit.getKey());

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
}

