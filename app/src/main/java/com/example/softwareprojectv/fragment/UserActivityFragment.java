package com.example.softwareprojectv.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.softwareprojectv.R;
import com.example.softwareprojectv.activity.common.ShowActivitiesActivity;
import com.example.softwareprojectv.adapter.ActivitiesAdapter;
import com.example.softwareprojectv.adapter.UserActivitiesAdapter;
import com.example.softwareprojectv.model.Activities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UserActivityFragment extends Fragment implements UserActivitiesAdapter.ClickInterface  {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private ProgressBar progressBar;
    private UserActivitiesAdapter adapter;
    private Button allButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Activities> activitiesList;
    private List<Activities> searchList;

    // firebase
    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://softwareprojectv-default-rtdb.firebaseio.com/");
    DatabaseReference mRef = database.getReference("admin_activities");
    private final StorageReference storageReference= FirebaseStorage.getInstance().getReference();

    //variables
    public static final String M_ACTIVITIES_IMAGE = "activities_image";
    public static final String M_NAME = "name";
    public static final String M_DESCRIPTION = "description";
    public static final String M_SEARCH = "search";
    public static final String INTENT_DATA_KEY = "get passing object";
    private static final String TAG ="AdminActivityFragment" ;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_activity, container, false);
        mView = view;
        findSections();
        initializerSections();
        setListeners();
        setRecycle();
        return view;
    }
    private void setRecycle() {
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                List<String> imageNameList = new ArrayList<>();
                List<String> imageLinkList = new ArrayList<>();
                String total = String.valueOf(dataSnapshot.child(M_ACTIVITIES_IMAGE).getChildrenCount());
                String activityId = dataSnapshot.getKey();
                String activityName = dataSnapshot.child(M_NAME).getValue().toString();
                String activityDescription = dataSnapshot.child(M_DESCRIPTION).getValue().toString();
                Activities user = new Activities(activityId, activityName, activityDescription, total);
                Log.d(TAG, "onChildAdded: activity name : "+activityName);
                for (DataSnapshot ds : dataSnapshot.child(M_ACTIVITIES_IMAGE).getChildren()){
                    String imageName = ds.child("name").getValue().toString();
                    String imageUrl = ds.child("link").getValue().toString();
                    imageLinkList.add(imageUrl);
                    imageNameList.add(imageName);
                    Log.d(TAG, "onChildAdded: name : "+imageName+" url : "+imageUrl);
                }
                user.setImageLinkList(imageLinkList);
                user.setImageNameList(imageNameList);
                user.setTotalImage(""+imageLinkList.size());
                activitiesList.add(user);
                progressBar.setVisibility(View.GONE);
                adapter.getUserList(activitiesList);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                searchList.clear();
                for (Activities activities : activitiesList ) {
                    if (activities.getActivityName().toLowerCase().equals(query.toLowerCase())){
                        searchList.add(activities);
                    }
                }
                progressBar.setVisibility(View.GONE);
                adapter.getUserList(searchList);
                recyclerView.setAdapter(adapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.getUserList(activitiesList);
                recyclerView.setAdapter(adapter);
            }
        });

    }

    private void initializerSections() {
        adapter= new UserActivitiesAdapter(this,getContext());
        activitiesList= new ArrayList<>();
        searchList= new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private void findSections() {
        recyclerView= mView.findViewById(R.id.UserActivityFragmentListRecycleId);
        searchView= mView.findViewById(R.id.UserActivityFragmentSearchViewId);
        progressBar= mView.findViewById(R.id.UserActivityFragmentListProgressId);
        allButton = mView.findViewById(R.id.UserActivityFragmentAllButtonId);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), ShowActivitiesActivity.class);
        intent.putExtra(INTENT_DATA_KEY,activitiesList.get(position));
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(int position) {
    }
}