package com.example.billsplitter.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ActivityViewModel extends ViewModel {

    private MutableLiveData<ArrayList<String>> PaidByList;
    private MutableLiveData<ArrayList<String>> ItemList;
    private MutableLiveData<ArrayList<String>> AmountList;
    private ArrayList<String> PaidBy;
    private ArrayList<String> Item;
    private ArrayList<String> Amount;
    private Context context;

    public ActivityViewModel() {
       PaidByList = new MutableLiveData<>();
        ItemList = new MutableLiveData<>();
        AmountList = new MutableLiveData<>();
        PaidBy = new ArrayList<>();
        Item = new ArrayList<>();
        Amount = new ArrayList<>();
        context = getApplicationContext();
        getActivity();

    }


    public LiveData<ArrayList<String>> getPaidByList() {

        return PaidByList;
    }
    public LiveData<ArrayList<String>> getItemList() {

        return ItemList;
    }
    public LiveData<ArrayList<String>> getAmountList() {

        return AmountList;
    }

    private void getActivity(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);
        final String userID = sp.getString("UserId", null);
        DatabaseReference ref = database.getReference("activity/");


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot unit : dataSnapshot.getChildren()){
                    if (unit.getKey().equals(userID)){
                        for (DataSnapshot expenses : unit.getChildren()){


                            PaidBy.add(expenses.child("paidBy").getValue().toString());
                            Item.add(expenses.child("desc").getValue().toString());
                            Amount.add(expenses.child("amount").getValue().toString());
                        }
                    }


                }
                PaidByList.setValue(PaidBy);
                ItemList.setValue(Item);
                AmountList.setValue(Amount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}