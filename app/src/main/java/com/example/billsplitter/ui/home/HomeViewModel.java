package com.example.billsplitter.ui.home;

import java.io.Console;
import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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


    private void setFriendsList(){
        System.out.println("Hello");
        ArrayList<String> list = new ArrayList<>();
        list.add("Akash");
        list.add("Hardik");
        list.add("Kethan");
        list.add("Zeel");
        mfriendsList.setValue(list);
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

