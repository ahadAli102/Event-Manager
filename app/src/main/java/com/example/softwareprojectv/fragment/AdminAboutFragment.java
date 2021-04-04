package com.example.softwareprojectv.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.softwareprojectv.R;
import com.example.softwareprojectv.Utils;
import com.example.softwareprojectv.activity.admin.AddAboutActivity;
import com.example.softwareprojectv.adapter.AdminAboutAdapter;
import com.example.softwareprojectv.model.AdminAbout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class AdminAboutFragment extends Fragment implements AdminAboutAdapter.ClickInterface {
    // firebase
    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://softwareprojectv-default-rtdb.firebaseio.com/");
    private DatabaseReference mRef = database.getReference("about_info");
    private StorageReference storage = FirebaseStorage.getInstance().getReference("about_info_image");
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    //widget
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private List<AdminAbout> aboutList;
    private View mView;
    private AdminAboutAdapter adminAboutAdapter;

    //variables
    public static final String M_NAME = "name";
    public static final String M_DESIGNATION = "designation";
    public static final String M_EMAIL = "email";
    public static final String M_URL = "url";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_about, container, false);
        mView = view;
        findSections();
        initializingSections();
        setListeners();
        loadData();
        return view;
    }

    private void insertSortedData() {
        Toast.makeText(getActivity(), "Inside sorted data: "+aboutList.size(), Toast.LENGTH_SHORT).show();
        aboutList = new ArrayList<>((Utils.getSortedAboutList(aboutList)));
        adminAboutAdapter.setAboutList(aboutList);
        recyclerView.setAdapter(adminAboutAdapter);
        Toast.makeText(getActivity(), "Sorted data complete "+aboutList.size(), Toast.LENGTH_SHORT).show();
    }

    private void loadData() {
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Toast.makeText(getActivity(), "fetching", Toast.LENGTH_SHORT).show();
                String key = dataSnapshot.getKey();
                //Toast.makeText(getActivity(), ""+key, Toast.LENGTH_SHORT).show();
                String name= dataSnapshot.child(M_NAME).getValue().toString();
                String email= dataSnapshot.child(M_EMAIL).getValue().toString();
                String designation= dataSnapshot.child(M_DESIGNATION).getValue().toString();
                String url = dataSnapshot.child(M_URL).getValue().toString();
                AdminAbout model = new AdminAbout(name,email,designation,url);
                model.setKey(dataSnapshot.getKey());
                aboutList.add(model);
                adminAboutAdapter.setAboutList(aboutList);
                recyclerView.setAdapter(adminAboutAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                adminAboutAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(getActivity(),"onChild remove ", Toast.LENGTH_SHORT).show();
                adminAboutAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                adminAboutAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Child error : "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        Toast.makeText(getActivity(), "Inside data: "+aboutList.size(), Toast.LENGTH_SHORT).show();

    }

    private void initializingSections() {
        aboutList = new ArrayList<>();
        adminAboutAdapter = new AdminAboutAdapter(getActivity(),this);
        adminAboutAdapter.setAboutList(aboutList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);

    }

    private void findSections() {
        recyclerView = mView.findViewById(R.id.aboutAdminRecyclerViewId);
        floatingActionButton = mView.findViewById(R.id.aboutAdminAddButtonId);
    }

    private void setListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAboutAddActivity();
            }
        });
    }

    private void goToAboutAddActivity() {
        startActivity(new Intent(getActivity(), AddAboutActivity.class));
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getActivity(), "On click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongItemClick(int position) {
        Toast.makeText(getActivity(), "On long click", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getActivity(), "Inside delete", Toast.LENGTH_SHORT).show();
        final AdminAbout model = aboutList.get(finalPosition);
        final String key = model.getKey();
        StorageReference deleteStorageReference = firebaseStorage.getReferenceFromUrl(model.getImageUrl());
        deleteStorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "delete Done", Toast.LENGTH_SHORT).show();
                        aboutList.remove(finalPosition);
                        adminAboutAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "delete error "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}