package com.example.billsplitter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.billsplitter.ui.database.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Serializable {

    private static final int RC_SIGN_IN = 123;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userTable = database.getReference("users/");
    DatabaseReference expensesDataTable = database.getReference("expenses_data/");
    String UserID;
    static HashMap<String, String> userMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userMap = new HashMap<>();
        createSignInIntent();
    }

    public void createSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                //Signed In

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, HomeActivity.class);

                SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("UserName", user.getDisplayName());
                ed.putString("UserEmail", user.getEmail());

                String[] temp = user.getEmail().split("@");
                UserID = temp[0];
                ed.putString("UserId", UserID);
                ed.apply();

                addUserToTheDatabaseIfNotExists(user);

                startActivity(intent);
                finish();

            } else {
                //sign in failed
                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addUserToDatabase(FirebaseUser userDetails) {
        User user = new User(userDetails.getDisplayName(), userDetails.getEmail(), UserID, userMap);

        userTable.child(UserID).setValue(user);
    }

    private void createGroupExpensesTable(FirebaseUser userDetails) {
        expensesDataTable.child(UserID).child("group_expenses");
        expensesDataTable.child(UserID).child("individual_expenses");
    }



    private void addUserToTheDatabaseIfNotExists(final FirebaseUser userDetails) {
        userTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isUserPresent = false;
                userMap.clear();
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    User existingUserDetails = unit.getValue(User.class);

                    //LookUp Table
                    userMap.put(unit.getKey(), unit.child("name").getValue().toString());
                    System.out.println("LoopUP TAble");
                    System.out.println(userMap.get(unit.getKey()));

                    if(existingUserDetails.email.equalsIgnoreCase(userDetails.getEmail())) {
                        isUserPresent = true;
                    }
                }

                if (!isUserPresent) {
                    addUserToDatabase(userDetails);
                    createGroupExpensesTable(userDetails);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public static String getNameFromUserID(String userID)
    {

        return userMap.get(userID);

    }
}
