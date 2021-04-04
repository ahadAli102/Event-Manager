package com.example.softwareprojectv.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.softwareprojectv.R;
import com.example.softwareprojectv.Utils;
import com.example.softwareprojectv.adapter.ProgramAdapter;
import com.example.softwareprojectv.model.Program;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProgramFragment extends Fragment implements ProgramAdapter.ClickInterface{
    //widget
    private RecyclerView recyclerView;
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

    //firebase
    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://softwareprojectv-default-rtdb.firebaseio.com/");
    private DatabaseReference mRef = database.getReference("program_info");
    private StorageReference storage = FirebaseStorage.getInstance().getReference("program_image");
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_program, container, false);
        mView = view;
        findSections();
        initializingSections();
        loadData();
        return mView;
    }
    private void loadData() {
        Toast.makeText(getActivity(), "Loading data", Toast.LENGTH_SHORT).show();
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try{
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
                catch (Exception e){
                    Toast.makeText(getActivity(), "Exception on loading data: "+e.getMessage(), Toast.LENGTH_SHORT).show();

                }

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
        recyclerView = mView.findViewById(R.id.UserProgramFragmentRecyclerViewId);
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getActivity(), "You will be shown details", Toast.LENGTH_SHORT).show();
        showAlertDialogButtonInformation(getView(),position);
    }

    @Override
    public void onLongItemClick(int position) {
        Toast.makeText(getActivity(), "You will apply here", Toast.LENGTH_LONG).show();
        showAlertDialogButtonApply(getView(),position);
    }

    private void putApplicantInformation(final String applicantName, final String applicantEmail, int position) {
        if(applicantName.isEmpty() || applicantEmail.isEmpty()){
            Toast.makeText(getActivity(), "Applicant information is empty", Toast.LENGTH_SHORT).show();
            return;
        }if(applicantName.equals("") || applicantEmail.equals("")){
            Toast.makeText(getActivity(), "Applicant information is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Utils.validate(applicantEmail)){
            Toast.makeText(getActivity(), "Invalid Email", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getActivity(), "Email is valid", Toast.LENGTH_SHORT).show();
        String applicantKey = mRef.child(programList.get(position).getKey()).child(M_PROGRAM_INFO_APPLICANT).push().getKey();
        Map<String,String> applicantMap= new HashMap<>();
        applicantMap.put(M_PROGRAM_INFO_APPLICANT_UID,firebaseAuth.getCurrentUser().getUid());
        applicantMap.put(M_PROGRAM_INFO_APPLICANT_NAME,applicantName);
        applicantMap.put(M_PROGRAM_INFO_APPLICANT_EMAIL,applicantEmail);
        mRef.child(programList.get(position).getKey()).child(M_PROGRAM_INFO_APPLICANT).child(applicantKey).setValue(applicantMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Your application is successfully stored", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getActivity(), "Exception : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void showAlertDialogButtonInformation(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_program_dialog_layout, null);
        builder.setView(customLayout);
        TextView titleText = customLayout.findViewById(R.id.custom_program_title_text_id);
        TextView descriptionText = customLayout.findViewById(R.id.custom_program_description_text_id);
        ImageView imageView = customLayout.findViewById(R.id.custom_program_image_id);
        titleText.setText(programList.get(position).getTitle());
        descriptionText.setText(programList.get(position).getProgramName());
        Glide.with(getContext()).load(programList.get(position).getImageUrl()).into(imageView);
        builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showAlertDialogButtonApply(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_program_apply_dialog_layout, null);
        builder.setView(customLayout);
        final EditText nameEditText = customLayout.findViewById(R.id.custom_program_apply_name_text_id);
        final EditText emailEditText = customLayout.findViewById(R.id.custom_program_apply_email_text_id);
        builder.setPositiveButton("Apply",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Processing Apply", Toast.LENGTH_SHORT).show();
                putApplicantInformation(nameEditText.getText().toString(),emailEditText.getText().toString(),position);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }
}