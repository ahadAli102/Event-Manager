package com.example.softwareprojectv.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Toast;

import com.example.softwareprojectv.R;
import com.example.softwareprojectv.adapter.PageAdapter;
import com.example.softwareprojectv.fragment.AdminAboutFragment;
import com.example.softwareprojectv.fragment.AdminActivityFragment;
import com.example.softwareprojectv.fragment.AdminHomeFragment;
import com.example.softwareprojectv.fragment.AdminProgramFragment;
import com.google.android.material.tabs.TabLayout;

public class AdminActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toast.makeText(this, "User Admin", Toast.LENGTH_SHORT).show();
        findSection();
        setViewPager();
    }
    private void findSection() {
        tabLayout = findViewById(R.id.AdminTabLayoutId);
        viewPager = findViewById(R.id.AdminViewPagerID);
    }
    private void setViewPager() {
        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(),0);
        pageAdapter.addFragment(new AdminHomeFragment());
        pageAdapter.addFragment(new AdminActivityFragment());
        pageAdapter.addFragment(new AdminProgramFragment());
        pageAdapter.addFragment(new AdminAboutFragment());
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_activity);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_dashboard);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_abut);
    }
}