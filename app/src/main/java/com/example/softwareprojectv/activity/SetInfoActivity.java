package com.example.softwareprojectv.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.softwareprojectv.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SetInfoActivity extends AppCompatActivity {
    private Button submitButton,confirmButton;
    private TextView textView;
    private EditText editText;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://softwareprojectv-default-rtdb.firebaseio.com/");
    private DatabaseReference mRef = database.getReference("user_info");

    public static final String USER_NAME="name";
    public static final String USER_EMAIL="email";
    public static final String USER_TYPE="type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_info);
        findSection();
        setListeners();
    }

    private void setListeners() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitInformation();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterInformationToDatabase();
            }
        });
    }

    private void enterInformationToDatabase() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser!=null){
            String id = currentUser.getUid();
            String name = textView.getText().toString();
            String email = currentUser.getEmail();
            String image = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_640.png";
            Map<String,String> userInfo= new HashMap<>();
            userInfo.put("email",email);
            userInfo.put("image",image);
            userInfo.put("name",name);
            userInfo.put("type","normal");
            DatabaseReference infoRef = mRef.child(id);
            infoRef.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SetInfoActivity.this, "Name set successfully", Toast.LENGTH_SHORT).show();
                        goToUserActivity();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SetInfoActivity.this, "Name set Failed", Toast.LENGTH_SHORT).show();
                    goToLoginActivity();
                }
            });

        }
        else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(SetInfoActivity.this, "No user", Toast.LENGTH_SHORT).show();

        }
    }

    private void submitInformation() {
        String name = editText.getText().toString();
        if (!name.isEmpty()){
            final String newName = name.replace(" ","").toLowerCase().trim();
            progressBar.setVisibility(View.VISIBLE);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (checkUserName(newName,dataSnapshot)){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SetInfoActivity.this, "This name is exist", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.VISIBLE);
                        textView.setText(newName);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SetInfoActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(SetInfoActivity.this, "Enter username ", Toast.LENGTH_SHORT).show();
        }
    }
    private void goToLoginActivity() {
        Intent intent= new Intent(SetInfoActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear the activity stack...
        startActivity(intent);
    }
    private void goToUserActivity() {
        Intent intent= new Intent(SetInfoActivity.this, UserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear the activity stack...
        startActivity(intent);
    }

    private boolean checkUserName(String name, DataSnapshot dataSnapshot){
        for (DataSnapshot ds :dataSnapshot.getChildren()){
            String existingName = ds.child(USER_NAME).getValue().toString();
            if (name.equals(existingName)){
                return true;
            }
        }
        return false;
    }

    private void findSection() {
        submitButton= findViewById(R.id.submitButtonId);
        confirmButton= findViewById(R.id.confirmButtonId);
        textView= findViewById(R.id.finalNameTextId);
        editText= findViewById(R.id.setUserNameEditTextId);
        progressBar= findViewById(R.id.setNameProgressId);
        progressBar.setVisibility(View.GONE);
        relativeLayout= findViewById(R.id.layout3);
        relativeLayout.setVisibility(View.GONE);

    }
}