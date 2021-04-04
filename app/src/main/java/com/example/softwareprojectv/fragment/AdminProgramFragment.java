package com.example.softwareprojectv.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.softwareprojectv.R;
import com.example.softwareprojectv.activity.admin.AddProgramActivity;
import com.example.softwareprojectv.activity.admin.ProgramApplicantActivity;
import com.example.softwareprojectv.adapter.ProgramAdapter;
import com.example.softwareprojectv.model.Program;
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

public class AdminProgramFragment extends Fragment implements ProgramAdapter.ClickInterface{
    private static final String TAG = "AdminProgramFragment";
    //widget
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private List<Program> programList;
    private View mView;
    private ProgramAdapter programAdapter;

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

    //firebase
    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://softwareprojectv-default-rtdb.firebaseio.com/");
    private DatabaseReference mRef = database.getReference("program_info");
    private StorageReference storage = FirebaseStorage.getInstance().getReference("program_image");
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    private ChildEventListener mChildListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_admin_program, container, false);
        mView = view;
        findSections();
        initializingSections();
        setListeners();
        loadData();
        return view;
    }
    private void loadData() {
        Toast.makeText(getActivity(), "Loading data", Toast.LENGTH_SHORT).show();
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String title = dataSnapshot.child(M_TITLE).getValue().toString();
                String description = dataSnapshot.child(M_PROGRAM_DESCRIPTION).getValue().toString();
                String startTime = dataSnapshot.child(M_START_TIME).getValue().toString();
                String endTime = dataSnapshot.child(M_END_TIME).getValue().toString();
                String startDate = dataSnapshot.child(M_START_DATE).getValue().toString();
                String endDate = dataSnapshot.child(M_END_DATE).getValue().toString();
                String imageUrl = dataSnapshot.child(M_IMAGE_URL).getValue().toString();
                String key = dataSnapshot.getKey();
                Program model = new Program(key,title,description,startDate,endDate,startTime,endTime,imageUrl);
                programList.add(model);
                programAdapter.setProgramList(programList);
                recyclerView.setAdapter(programAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                programAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializingSections() {
        programList = new ArrayList<>();
        programAdapter = new ProgramAdapter(getActivity(),this);
        programAdapter.setProgramList(programList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);

    }

    private void findSections() {
        recyclerView = mView.findViewById(R.id.AdminProgramFragmentRecyclerViewId);
        floatingActionButton = mView.findViewById(R.id.AdminProgramFragmentAddButtonId);
    }

    private void setListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProgramAddActivity();
            }
        });
    }

    private void goToProgramAddActivity() {
        startActivity(new Intent(getActivity(), AddProgramActivity.class));
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
        final Program model = programList.get(finalPosition);
        final String key = model.getKey();
        StorageReference deleteStorageReference = firebaseStorage.getReferenceFromUrl(model.getImageUrl());
        deleteStorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "delete Done", Toast.LENGTH_SHORT).show();
                        programList.remove(finalPosition);
                        programAdapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getActivity(), "small click", Toast.LENGTH_SHORT).show();
        goToProgramApplicantActivity(position);
    }

    private void goToProgramApplicantActivity(int position) {
        Intent intent = new Intent(new Intent(getActivity(), ProgramApplicantActivity.class));
        intent.putExtra(M_INTENT_DATA_KEY,programList.get(position).getKey());
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(int position) {
        Toast.makeText(getActivity(), "Long click", Toast.LENGTH_SHORT).show();
        deleteDataDialog(position);
    }

    /*private void loadCurrentProgramApplicant(final int position) {
        final ArrayList<String> applicantsName = new ArrayList<>();
        final ArrayList<String> applicantsEmail = new ArrayList<>();
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                applicantsName.add(dataSnapshot.child(M_PROGRAM_INFO_APPLICANT_NAME).getValue().toString());
                applicantsEmail.add(dataSnapshot.child(M_PROGRAM_INFO_APPLICANT_EMAIL).getValue().toString());
                Log.d(TAG, "onChildAdded: "+dataSnapshot.child(M_PROGRAM_INFO_APPLICANT_NAME).getValue().toString()
                        +dataSnapshot.child(M_PROGRAM_INFO_APPLICANT_EMAIL).getValue().toString());
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
        Thread myCurrentThread = new Thread(Thread.currentThread());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mRef.child(programList.get(position).getKey()).child(M_PROGRAM_INFO_APPLICANT).addChildEventListener(mChildListener);
            }
        };
        Thread myDataCollectionThread = new Thread(runnable);
        myDataCollectionThread.start();
        try{
            myDataCollectionThread.join();

        }catch (InterruptedException e){
            Log.e(TAG, "loadCurrentProgramApplicant: thread is Interrupted" );
        }
        String programName = programList.get(position).getTitle();
        showApplicantInformationOnAlertDialog(programName,applicantsEmail,applicantsName,position);

    }

    private void showApplicantInformationOnAlertDialog(String programName, ArrayList<String> applicantsEmail, ArrayList<String> applicantsName, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_program_applicant_dialog, null);
        builder.setView(customLayout);
        final TextView titleText = customLayout.findViewById(R.id.custom_program_applicant_title_textViewId);
        final TextView programText = customLayout.findViewById(R.id.custom_program_applicant_information_textViewId);
        titleText.setText(programName);
        Log.d(TAG, "showApplicantInformationOnAlertDialog: list size :"+applicantsName.size());
        for (int i = 0; i <applicantsEmail.size() ; i++) {
            programText.append(applicantsName.get(i)+M_NEW_LINE+applicantsEmail.get(i)+M_NEW_LINE+M_NEW_LINE);
        }
        builder.setPositiveButton("Email",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "You will email them", Toast.LENGTH_SHORT).show();
                mRef.child(programList.get(position).getKey()).child(M_PROGRAM_INFO_APPLICANT).removeEventListener(mChildListener);
                Toast.makeText(getActivity(), "Removed child event listener", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRef.child(programList.get(position).getKey()).child(M_PROGRAM_INFO_APPLICANT).removeEventListener(mChildListener);
                Toast.makeText(getActivity(), "Removed child event listener", Toast.LENGTH_SHORT).show();
            }
        }).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }*/
}