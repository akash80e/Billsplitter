package com.example.billsplitter.ui.home;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.billsplitter.R;
import com.google.android.material.tabs.TabLayout;


public class HomeFragment extends Fragment {

    TabPagerAdapter myTabAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_home);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = root.findViewById(R.id.viewPager);
        tabLayout = root.findViewById(R.id.tabLayout);

        setPagerAdapter();
        setTabLayout();

        return root;
    }
    private void setPagerAdapter() {

        androidx.fragment.app.FragmentManager fragManager = getChildFragmentManager();
        myTabAdapter = new TabPagerAdapter(fragManager);
        viewPager.setAdapter(myTabAdapter);
        System.out.println("setPagerAdapter");
    }

    private void setTabLayout() {
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Friends");
        tabLayout.getTabAt(1).setText("Groups");
        System.out.println("setPagerAdapter");
    }

}