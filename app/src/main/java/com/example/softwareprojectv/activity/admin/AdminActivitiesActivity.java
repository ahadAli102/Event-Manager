package com.example.softwareprojectv.activity.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.softwareprojectv.R;
import com.example.softwareprojectv.adapter.ImageAdapter;
import com.example.softwareprojectv.model.Image;
import com.example.softwareprojectv.model.UriImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminActivitiesActivity extends AppCompatActivity {
    private EditText nameEditText, descriptionEditText;
    private ImageView blankImage;
    private RecyclerView recyclerView;
    private Button saveButton, uploadButton;
    private int counter = 0;
    public static final int IMAGE_CODE =1;
    private List<Image> imageNameList;
    private List<UriImage> savedImagesUri;
    private ImageAdapter adapter;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_activities);
        findViews();
        initializeSection();
        setListeners();
    }
    private void initializeSection() {
        imageNameList= new ArrayList<>();
        savedImagesUri= new ArrayList<>();
        adapter = new ImageAdapter(imageNameList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void findViews() {
        nameEditText=  findViewById(R.id.AdminActivitiesNoteNameEditTextId);
        descriptionEditText=  findViewById(R.id.AdminActivitiesNoteNameDescriptionTextId);
        saveButton =  findViewById(R.id.AdminActivitiesSaveButtonId);
        uploadButton =  findViewById(R.id.AdminActivitiesUploadButtonId);
        blankImage=  findViewById(R.id.AdminActivitiesBlankImageId);
        recyclerView=  findViewById(R.id.AdminActivitiesUploadRecycleId);
    }

    private void setListeners() {
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.getItemCount()!=0){
                    blankImage.setVisibility(View.GONE);
                }
                else if (adapter.getItemCount()==0){
                    blankImage.setVisibility(View.GONE);
                }
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                startActivityForResult(intent,IMAGE_CODE);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name= nameEditText.getText().toString();
                String description= descriptionEditText.getText().toString();

                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(description)){
                    uploadData(name,description);

                }
                else {
                    Toast.makeText(AdminActivitiesActivity.this, "Fill up all field", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void uploadData(String name, String description) {
        if(imageNameList.isEmpty()){
            Toast.makeText(this, "please insert image", Toast.LENGTH_SHORT).show();
        }
        else{
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploaded 0/"+imageNameList.size());
            progressDialog.setCanceledOnTouchOutside(false); //Remove this line if you want your user to be able to cancel upload
            progressDialog.setCancelable(false);    //Remove this line if you want your user to be able to cancel upload
            progressDialog.show();
            final String key= mRef.push().getKey();
            DatabaseReference dataPath= mRef.child(key);

            Map<String,String> map = new HashMap<>();
            map.put(M_NAME,name);
            map.put(M_DESCRIPTION,description);
            map.put(M_SEARCH,name.toLowerCase());
            dataPath.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull final Task<Void> task) {
                    if(task.isSuccessful()){

                        final StorageReference image_path = storageReference.child(M_ACTIVITIES_IMAGE)
                                .child(key);
                        for(int i=0; i<imageNameList.size();i++){

                            final int finalI = i;
                            image_path.child(imageNameList.get(i).getImageName())
                                    .putFile(imageNameList.get(i).getUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    image_path.child(imageNameList.get(finalI).getImageName()).getDownloadUrl()
                                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    counter++;
                                                    progressDialog.setMessage("Uploaded "+counter+"/"+imageNameList.size());
                                                    if(task.isSuccessful()){
                                                        String url= task.getResult().toString();
                                                        String name = imageNameList.get(finalI).getImageName();
                                                        UriImage uriImage = new UriImage(name,url);
                                                        savedImagesUri.add(uriImage);
                                                    }
                                                    else {
                                                        Toast.makeText(AdminActivitiesActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                                    }

                                                    if(counter== imageNameList.size()){
                                                        storeImageUrl(progressDialog,key);
                                                        counter=0;
                                                    }

                                                }
                                            });

                                }
                            });
                        }
                    }
                    else{
                        Toast.makeText(AdminActivitiesActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void storeImageUrl(ProgressDialog progressDialog, String key) {
        DatabaseReference uriPath=  mRef.child(key).child(M_ACTIVITIES_IMAGE);
        for(int i=0;i<savedImagesUri.size();i++){
            Map<String,String> uriMap= new HashMap<>();
            uriMap.put("link",savedImagesUri.get(i).getUrl());
            uriMap.put("name",savedImagesUri.get(i).getImageName());
            uriPath.push().setValue(uriMap);

        }
        progressDialog.dismiss();
        Toast.makeText(this, "Unload successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageNameList.clear();
        savedImagesUri.clear();
        if (requestCode==IMAGE_CODE && resultCode == Activity.RESULT_OK){
            if (data.getClipData()!=null){
                //multiple image have been selected from gallery
                Toast.makeText(AdminActivitiesActivity.this, "multiple image", Toast.LENGTH_SHORT).show();
                blankImage.setVisibility(View.GONE);
                int totalImage = data.getClipData().getItemCount();
                for (int i = 0; i<totalImage; i++){
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    String imageName = getFileName(uri);
                    Image image = new Image(imageName,uri);
                    imageNameList.add(image);
                    recyclerView.setAdapter(adapter);
                }
            }
            else if(data.getData()!=null){
                //single image has been selected from gallery
                Toast.makeText(AdminActivitiesActivity.this, "single image", Toast.LENGTH_SHORT).show();
                blankImage.setVisibility(View.GONE);
                Uri uri = data.getData();
                String imageName = getFileName(uri);
                Image image = new Image(imageName,uri);
                imageNameList.add(image);
                recyclerView.setAdapter(adapter);
            }
        }
    }
    //get image name....
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
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