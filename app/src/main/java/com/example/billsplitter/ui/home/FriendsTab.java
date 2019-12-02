package com.example.billsplitter.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.billsplitter.AddFriend;
import com.example.billsplitter.HomeActivity;
import com.example.billsplitter.R;
import com.example.billsplitter.SettleExpenses;
import com.example.billsplitter.ui.database.User;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class FriendsTab extends Fragment {
    ArrayList<String> friends;
    ArrayList<String> amounts;

    public FriendsTab() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private HomeViewModel homeViewModel;
    private ListView listView;

    private Integer imgId;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        friends = new ArrayList<>();
        amounts = new ArrayList<>();



        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        View view = inflater.inflate(R.layout.friends_tab, container, false);

        listView = view.findViewById(R.id.friends_list);
        imgId = R.drawable.ic_person_black_24dp;


        //Populating the friends list
        homeViewModel.getFriendsList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                friends = strings;

                updateList();

            }
        });

        homeViewModel.getFriendsAmountList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                amounts = strings;
                updateList();

            }
        });

        return view;
    }

    private void updateList(){

        final CustomListView adapter = new CustomListView(getActivity(), friends, amounts , imgId);
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, strings);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapter.getItem(i);
                Intent intent = new Intent(getContext(), SettleExpenses.class);
                intent.putExtra("UserName",item);
                startActivity(intent);
            }
        });
    }



}
