package com.example.billsplitter.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.billsplitter.R;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class FriendsTab extends Fragment {

    public FriendsTab() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private HomeViewModel homeViewModel;

    private Integer imgId;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {


        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        View view = inflater.inflate(R.layout.friends_tab, container, false);

        final ListView listView = view.findViewById(R.id.friends_list);
        imgId = R.drawable.profile_picture;

        final ArrayList<String> subtitle = new ArrayList<>();
        subtitle.add("owes");
        subtitle.add("owes");
        subtitle.add("owes");
        subtitle.add("owes");

        //Populating the friends list
        homeViewModel.getFriendsList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                CustomListView adapter = new CustomListView(getActivity(), strings, subtitle , imgId);
                //ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                listView.setAdapter(adapter);
            }
        });

        return view;
    }
}