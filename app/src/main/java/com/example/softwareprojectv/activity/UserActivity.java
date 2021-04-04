package com.example.softwareprojectv.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Toast;

import com.example.softwareprojectv.R;
import com.example.softwareprojectv.adapter.PageAdapter;
import com.example.softwareprojectv.fragment.UserAboutFragment;
import com.example.softwareprojectv.fragment.UserActivityFragment;
import com.example.softwareprojectv.fragment.UserHomeFragment;
import com.example.softwareprojectv.fragment.UserProgramFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ThrowOnExtraProperties;

public class UserActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toast.makeText(this, "User Activity", Toast.LENGTH_SHORT).show();
        tabLayout = findViewById(R.id.tabLayoutId);
        viewPager = findViewById(R.id.viewPagerID);
        setViewPager();
    }
    private void setViewPager() {
        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(),0);
        pageAdapter.addFragment(new UserHomeFragment());
        pageAdapter.addFragment(new UserActivityFragment());
        pageAdapter.addFragment(new UserProgramFragment());
        pageAdapter.addFragment(new UserAboutFragment());
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_activity);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_dashboard);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_abut);
    }
}