package com.example.billsplitter.ui.home;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.billsplitter.ui.database.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.billsplitter.MainActivity.getNameFromUserID;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<String>> mfriendsList;
    private MutableLiveData<ArrayList<String>> mfriendsAmountList;
    private MutableLiveData<ArrayList<String>> mGroupsList;
    private ArrayList<String> friends;
    private ArrayList<String> amount;
    private Context context;

    public HomeViewModel() {
        mfriendsList = new MutableLiveData<>();
        mGroupsList = new MutableLiveData<>();
        mfriendsAmountList = new MutableLiveData<>();
        context = getApplicationContext();
        setFriendsList();
        setGroupsList();

    }


    public LiveData<ArrayList<String>> getFriendsList(){
        return mfriendsList;
    }

    public LiveData<ArrayList<String>> getFriendsAmountList(){
        return mfriendsAmountList;
    }

    public LiveData<ArrayList<String>> getGroupsList(){
        return mGroupsList;
    }


    /*
    * This method is used to fetch the friends list from the database and set them
    * to the arraylist to use later
    * */
    private void setFriendsList(){
        System.out.println("Hello");
        friends = new ArrayList<>();
        amount = new ArrayList<>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);
        final String userID = sp.getString("UserId", null);
        System.out.println(userID);
        DatabaseReference ref = database.getReference("expenses_data/");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friends.clear();
                amount.clear();
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    if(unit.getKey().equals(userID)){
                        System.out.println(unit.child("individual_expenses"));

                        for(DataSnapshot child : unit.child("individual_expenses").getChildren()){
                            friends.add(getNameFromUserID(child.getKey()));

                            amount.add(child.getValue().toString());
                        }
                    }

                }
                mfriendsList.setValue(friends);
                mfriendsAmountList.setValue(amount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void setGroupsList(){
        ArrayList<String> list = new ArrayList<>();
        list.add("Group 1");
        list.add("Group 2");
        list.add("Group 3");
        list.add("Group 4");
        mGroupsList.setValue(list);
    }
}

