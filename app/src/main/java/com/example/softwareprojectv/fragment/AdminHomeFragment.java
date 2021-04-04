package com.example.softwareprojectv.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.softwareprojectv.R;
import com.example.softwareprojectv.activity.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminHomeFragment extends Fragment {
    private Button signOutButton,updateButton;
    private CircleImageView profileImage;
    private EditText editText;
    private ProgressBar progressBar,editProgress;
    private TextView nameText,emailText,editNameText;
    private GoogleSignInClient mGoogleSignInClient;
    // firebase
    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://softwareprojectv-default-rtdb.firebaseio.com/");
    private DatabaseReference mRef = database.getReference("user_info");
    private StorageReference storageReference= FirebaseStorage.getInstance().getReference();

    public static final String M_NAME = "name";
    public static final String M_IMAGE = "image";
    public static final String M_EMAIL = "email";
    public static final String M_PROFILE_IMAGE = "Profile_Image";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_admin_home, container, false);
        signOutButton= view.findViewById(R.id.adminSignOutButtonId);
        profileImage= view.findViewById(R.id.adminProfileImageId);
        nameText= view.findViewById(R.id.adminUserNameId);
        emailText= view.findViewById(R.id.adminUserEmailId);
        editNameText= view.findViewById(R.id.adminEditNameTextId);
        progressBar= view.findViewById(R.id.adminHomeProgressId);
        progressBar.setVisibility(View.GONE);
        editNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editName();
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initGoogleSignInClient();
                signOut();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
        setProfile();
        return view;
    }
    private void initGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    private void signOut() {
        firebaseAuth.signOut();
        mGoogleSignInClient.signOut();
        Intent intent= new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear the activity stack
        startActivity(intent);
    }

    private void uploadImage() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else {
                imagePick();
            }
        }
        else {
            imagePick();
        }
    }

    private void imagePick() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1).start(getContext(),this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {
                progressBar.setVisibility(View.VISIBLE);
                final Uri resultUri = result.getUri();
                final StorageReference image_path= storageReference.child(M_PROFILE_IMAGE)
                        .child(firebaseAuth.getCurrentUser().getUid()).child("profile.jpg");
                image_path.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mRef.child(firebaseAuth.getCurrentUser().getUid())
                                        .child(M_IMAGE).setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getActivity(), "Upload Success", Toast.LENGTH_SHORT).show();
                                            Glide.with(getActivity()).load(resultUri).centerCrop()
                                                    .placeholder(R.drawable.profile).into(profileImage);
                                        }
                                        else {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getActivity(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), ""+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getActivity(), "Error at Crop Image: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void editName() {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();
        final  View view= inflater.inflate(R.layout.custom_edit_name,null);
        builder.setView(view).setTitle("Edit user name").setCancelable(true)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        updateButton= view.findViewById(R.id.updateButtonId);
        editProgress= view.findViewById(R.id.editNameProgressId);
        editProgress.setVisibility(View.GONE);
        editText= view.findViewById(R.id.editUserNameId);
        editText.setText(nameText.getText().toString());
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name= editText.getText().toString();
                name= name.replace(" ","");
                final String newName= name.toLowerCase().trim();
                if(!TextUtils.isEmpty(newName)){
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(checkUserName(newName,snapshot)){
                                editProgress.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "This name is exist", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String currentUser= firebaseAuth.getCurrentUser().getUid();
                                mRef.child(currentUser).child("name").setValue(newName)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    editProgress.setVisibility(View.GONE);
                                                    Toast.makeText(getActivity(), "Name update successfully", Toast.LENGTH_SHORT).show();
                                                    nameText.setText(newName);
                                                }
                                                else {
                                                    editProgress.setVisibility(View.GONE);
                                                    Toast.makeText(getActivity(), "Name update Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            editProgress.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), ""+error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(getActivity(), "Please enter username", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.create().show();
    }

    private void setProfile() {
        FirebaseUser currentUser= firebaseAuth.getCurrentUser();
        mRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name= snapshot.child(M_NAME).getValue().toString();
                String email= snapshot.child(M_EMAIL).getValue().toString();
                String image= snapshot.child(M_IMAGE).getValue().toString();

                Glide.with(getActivity()).load(image).centerCrop()
                        .placeholder(R.drawable.profile).into(profileImage);
                nameText.setText(name);
                emailText.setText(email);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkUserName(String name, DataSnapshot dataSnapshot){
        for(DataSnapshot ds:dataSnapshot.getChildren()){
            String a= ds.child(M_NAME).getValue().toString();
            if(a.equals(name)){
                return true;
            }
        }
        return false;
    }
}