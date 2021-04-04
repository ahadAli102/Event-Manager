package com.example.softwareprojectv.activity.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.softwareprojectv.R;
import com.example.softwareprojectv.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddAboutActivity extends AppCompatActivity {
    //widget
    private EditText nameEditText, designationEditText,emailEditText;
    private ImageView blankImage;
    private Button saveButton, uploadButton;
    public static final int IMAGE_CODE =1;
    private Uri imageUri;

    //variables
    public static final String M_NAME = "name";
    public static final String M_DESIGNATION = "designation";
    public static final String M_EMAIL = "email";
    public static final String M_URL = "url";

    //firebase
    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://softwareprojectv-default-rtdb.firebaseio.com/");
    private DatabaseReference mRef = database.getReference("about_info");
    private StorageReference storage = FirebaseStorage.getInstance().getReference("about_info_image");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_about);
        findSections();
        setListeners();
    }
    private void setListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase();
            }

        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void findSections() {
        saveButton = findViewById(R.id.AddAboutActivitySaveButtonId);
        uploadButton = findViewById(R.id.AddAboutActivityUploadButtonId);
        nameEditText = findViewById(R.id.AddAboutActivityNameEditTextId);
        designationEditText = findViewById(R.id.AddAboutActivityDesignationEditTextId);
        emailEditText = findViewById(R.id.AddAboutActivityEmailEditTextId);
        blankImage = findViewById(R.id.AddAboutActivityBlankImageId);
    }
    private void saveToDatabase() {
        final String name = nameEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String designation = designationEditText.getText().toString();
        if (name.isEmpty()) {
            nameEditText.setError("Enter name");
            nameEditText.requestFocus();
            return;
        }if (email.isEmpty()) {
            emailEditText.setError("Enter email");
            emailEditText.requestFocus();
            return;
        }if (designation.isEmpty()) {
            designationEditText.setError("Enter designation");
            designationEditText.requestFocus();
            return;
        }
        if(!Utils.validate(email)){
            emailEditText.setError("Invalid Email");
            emailEditText.requestFocus();
            return;
        }
        Toast.makeText(this, "Saving file to database", Toast.LENGTH_LONG).show();
        final StorageReference ref = storage.child(System.currentTimeMillis()+".jpg");
        ref.child(getFileName(imageUri))
                .putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.child(getFileName(imageUri)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Toast.makeText(AddAboutActivity.this, "Inside upload", Toast.LENGTH_SHORT).show();
                        String url= task.getResult().toString();
                        Map<String,String> map = new HashMap<>();
                        map.put(M_NAME,name);
                        map.put(M_EMAIL,email);
                        map.put(M_DESIGNATION,designation);
                        map.put(M_URL,url);
                        String key = mRef.push().getKey();
                        mRef.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    onBackPressed();
                                    Toast.makeText(AddAboutActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(AddAboutActivity.this, "Insert Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddAboutActivity.this, "Insert failed Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMAGE_CODE && resultCode == Activity.RESULT_OK){
            if (data.getData()!=null){
                Toast.makeText(this, "Image get", Toast.LENGTH_SHORT).show();
                imageUri = data.getData();
                Glide.with(this).load(imageUri).into(blankImage);
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = AddAboutActivity.this.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}