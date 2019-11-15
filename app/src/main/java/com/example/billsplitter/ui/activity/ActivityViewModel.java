package com.example.billsplitter.ui.activity;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ActivityViewModel extends ViewModel {

    private MutableLiveData<ArrayList<String>> mlist;

    public ActivityViewModel() {
       mlist = new MutableLiveData<>();

    }


    public LiveData<ArrayList<String>> getList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("activity 1");
        list.add("activity 2");
        list.add("activity 3");
        mlist.setValue(list);
        return mlist;
    }
}