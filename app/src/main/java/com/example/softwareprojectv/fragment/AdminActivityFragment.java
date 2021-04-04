package com.example.softwareprojectv.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.softwareprojectv.R;
import com.example.softwareprojectv.activity.admin.AdminActivitiesActivity;
import com.example.softwareprojectv.activity.common.ShowActivitiesActivity;
import com.example.softwareprojectv.adapter.ActivitiesAdapter;
import com.example.softwareprojectv.model.Activities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class AdminActivityFragment extends Fragment implements ActivitiesAdapter.ClickInterface {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ProgressBar progressBar;
    private ActivitiesAdapter adapter;
    private FloatingActionButton actionButton;
    private Button allButton;

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
        View view = inflater.inflate(R.layout.fragment_admin_activity, container, false);
        mView = view;
        findSections();
        initializerSections();
        setListeners();
        setRecycle();
        return view;
    }
    private void setRecycle() {
        /*mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){

                    String total= String.valueOf(ds.child(M_ACTIVITIES_IMAGE).getChildrenCount());
                    String activityId= ds.getKey().toString();
                    String activityName= ds.child(M_NAME).getValue().toString();
                    String activityDescription = ds.child(M_DESCRIPTION).getValue().toString();
                    Activities user= new Activities(activityId,activityName,activityDescription,total);
                    activitiesList.add(user);
                }
                progressBar.setVisibility(View.GONE);
                adapter.getUserList(activitiesList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), ""+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });*/
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
                removeChild(dataSnapshot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeChild(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        try{
            String name = activitiesList.get(getDataPosition(key)).getActivityName();
            activitiesList.remove(getDataPosition(key));
            adapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), name+"\'s data is removed", Toast.LENGTH_SHORT).show();
        }catch (Exception ex){

        }
    }

    private int getDataPosition(String key) {
        int position =0;
        for (Activities activity: activitiesList) {
            position++;
            if(activity.getActivityId()==key){
                return position;
            }
        }
        return activitiesList.size();
    }

    private void setListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                searchList.clear();
                /*String currentUser= firebaseAuth.getCurrentUser().getUid();
                mRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            String value = ds.child("search").getValue().toString();
                            if (value.equals(query)){
                                String total= String.valueOf(ds.child("images").getChildrenCount());
                                String noteId= ds.getKey().toString();
                                String name= ds.child("name").getValue().toString();
                                String description = ds.child("description").getValue().toString();
                                User user= new User(noteId,name,description,total);
                                activitiesList.add(user);
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        adapter.getUserList(searchList);
                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(getActivity(), ""+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });*/
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
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AdminActivitiesActivity.class));
            }
        });
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllData();
            }
        });

    }

    private void setAllData() {
        adapter.getUserList(activitiesList);
        recyclerView.setAdapter(adapter);
    }

    private void initializerSections() {
        adapter= new ActivitiesAdapter(this);
        activitiesList= new ArrayList<>();
        searchList= new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private void findSections() {
        recyclerView= mView.findViewById(R.id.AdminActivityFragmentListRecycleId);
        searchView= mView.findViewById(R.id.AdminActivityFragmentSearchViewId);
        progressBar= mView.findViewById(R.id.AdminActivityFragmentListProgressId);
        actionButton = mView.findViewById(R.id.AdminActivityFragmentActionButton);
        allButton = mView.findViewById(R.id.AdminActivityFragmentAllButtonId);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), ShowActivitiesActivity.class);
        intent.putExtra(INTENT_DATA_KEY,activitiesList.get(position));
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(int position) {
        deleteDataDialog(position);
    }
    private void deleteDataDialog(final int finalPosition) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Delete conformation").setMessage("Do you want to delete this information")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteData(finalPosition);
                    }
                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setCancelable(true).create().show();
    }

    private void deleteData(final int finalPosition) {
        final int[] c = {0};
        final Activities model = activitiesList.get(finalPosition);
        final String uid= model.getActivityId();
        StorageReference deletePath= storageReference.child(M_ACTIVITIES_IMAGE).child(uid);
        for(int i=0;i<model.getImageNameList().size();i++){
            deletePath.child(model.getImageNameList().get(i)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    c[0]++;
                    if(task.isSuccessful()){
                        if(c[0] ==model.getImageNameList().size()){
                            mRef.child(uid).removeValue();
                            Toast.makeText(getActivity(), "Delete Complete", Toast.LENGTH_SHORT).show();
                            activitiesList.remove(finalPosition);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    else {
                        Toast.makeText(getActivity(), ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}