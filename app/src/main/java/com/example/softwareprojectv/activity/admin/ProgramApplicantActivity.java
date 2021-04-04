package com.example.softwareprojectv.activity.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.softwareprojectv.R;
import com.example.softwareprojectv.Utils;
import com.example.softwareprojectv.adapter.ProgramApplicantsAdapter;
import com.example.softwareprojectv.model.ProgramApplicants;
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

public class ProgramApplicantActivity extends AppCompatActivity {

    private static final String TAG = "AdminProgramFragment";
    //widget
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private List<ProgramApplicants> applicants;
    private ProgramApplicantsAdapter adapter;

    private static final  String M_TITLE = "title";
    private static final String M_PROGRAM_DESCRIPTION = "description";
    private static final String M_START_DATE = "start date";
    private static final String M_END_DATE = "end date";
    private static final String M_START_TIME = "start time";
    private static final String M_END_TIME = "end time";
    private static final String M_IMAGE_URL = "program_images";
    private static final String M_PROGRAM_INFO_APPLICANT_NAME = "applicant_name";
    private static final String M_PROGRAM_INFO_APPLICANT_EMAIL = "applicant_email";
    private static final String M_PROGRAM_INFO_APPLICANT_UID = "applicant_UID";
    private static final String M_PROGRAM_INFO_APPLICANT = "applicants";
    private static final String M_NEW_LINE = "\n";
    private static final String M_INTENT_DATA_KEY = "admin_program_applicant";
    private static  String M_KEY = "";


    //firebase
    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://softwareprojectv-default-rtdb.firebaseio.com/");
    private DatabaseReference mRef = database.getReference("program_info");
    private StorageReference storage = FirebaseStorage.getInstance().getReference("program_image");
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private ChildEventListener mChildListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_applicant);
        findSection();
        setRecyclerView();
        loadData();
        setListeners();
    }

    private void setListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    private void sendEmail() {
        ArrayList<String> email = new ArrayList<>();
        for (ProgramApplicants applicant: applicants) {
            email.add(applicant.getEmail());
        }
        composeEmail(Utils.getUniqueEmail(email));
    }

    private void loadData() {
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String name = dataSnapshot.child(M_PROGRAM_INFO_APPLICANT_NAME).getValue().toString();
                String email = dataSnapshot.child(M_PROGRAM_INFO_APPLICANT_EMAIL).getValue().toString();
                applicants.add(new ProgramApplicants(name,email));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        M_KEY = getIntent().getStringExtra(M_INTENT_DATA_KEY);
        mRef.child(M_KEY).child(M_PROGRAM_INFO_APPLICANT).addChildEventListener(mChildListener);
    }

    private void setRecyclerView() {
        applicants = new ArrayList<>();
        adapter = new ProgramApplicantsAdapter();
        adapter.setApplicantsList(applicants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);
    }

    private void findSection() {
        recyclerView = findViewById(R.id.ProgramApplicantActivityRecyclerViewId);
        floatingActionButton = findViewById(R.id.ProgramApplicantActivityFloatingId);
    }
    public void composeEmail(ArrayList<String> addresses) {
        String address="";
        for (int i = 0; i <addresses.size() ; i++) {
            address+=addresses.get(i);
            if (i<addresses.size()-1){
                address+=",";
            }
        }
        String[] finalString = address.split(",");
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL,finalString);
        try {
            startActivity(emailIntent);
        }catch (Exception e ){

        }
    }

    @Override
    protected void onDestroy() {
        try {
            mRef.child(M_KEY).child(M_PROGRAM_INFO_APPLICANT).removeEventListener(mChildListener);
        }catch (NullPointerException e){

        }catch (Exception e){

        }
        super.onDestroy();
    }
}