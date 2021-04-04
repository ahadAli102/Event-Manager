package com.example.softwareprojectv.activity.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.softwareprojectv.R;
import com.example.softwareprojectv.adapter.ShowActivitiesAdapter;
import com.example.softwareprojectv.model.Activities;
import com.example.softwareprojectv.model.ShowActivitiesModel;

import java.util.ArrayList;
import java.util.List;

public class ShowActivitiesActivity extends AppCompatActivity {
    public static final String INTENT_DATA_KEY = "get passing object";
    private Activities model;
    private List<ShowActivitiesModel> modelList;
    private ShowActivitiesAdapter adapter;

    //widget
    private TextView titleTextView,descriptionTextView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_activities);
        findSections();
        initializerSection();
    }
    private void initializerSection() {
        model = (Activities) getIntent().getSerializableExtra(INTENT_DATA_KEY);
        modelList = new ArrayList();
        for (int i = 0; i < model.getImageNameList().size(); i++) {
            ShowActivitiesModel showActivitiesModel = new ShowActivitiesModel(model.getImageNameList().get(i)
                    ,model.getImageLinkList().get(i));
            modelList.add(showActivitiesModel);
        }
        titleTextView.setText(model.getActivityName());
        descriptionTextView.setText(model.getActivityDescription());
        adapter = new ShowActivitiesAdapter(this);
        adapter.setModelList(modelList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

    }

    private void findSections() {
        titleTextView = findViewById(R.id.ShowActivitiesActivityTextViewId);
        descriptionTextView = findViewById(R.id.ShowActivitiesActivityTextViewId1);
        recyclerView = findViewById(R.id.ShowActivitiesActivityRecyclerViewId);
    }
}