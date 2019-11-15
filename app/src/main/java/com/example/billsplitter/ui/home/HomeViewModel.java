package com.example.billsplitter.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.billsplitter.ui.database.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<String>> mfriendsList;
    private MutableLiveData<ArrayList<String>> mGroupsList;

    public HomeViewModel() {
        mfriendsList = new MutableLiveData<>();
        mGroupsList = new MutableLiveData<>();
        setFriendsList();
        setGroupsList();
    }


    public LiveData<ArrayList<String>> getFriendsList(){
        return mfriendsList;
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
        final ArrayList<String> list = new ArrayList<>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot unit : dataSnapshot.getChildren()){
                    Users value = unit.getValue(Users.class);

                    list.add(StringUtils.capitalize(value.name));
                }
                mfriendsList.setValue(list);
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

