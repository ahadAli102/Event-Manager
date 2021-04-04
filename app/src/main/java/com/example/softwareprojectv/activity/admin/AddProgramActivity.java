package com.example.softwareprojectv.activity.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.softwareprojectv.R;
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

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class AddProgramActivity extends AppCompatActivity {

    //widget
    private TextView startTimeText,endTimeText,startDateText,endDateText;
    private Button startTimeButton,endTimeButton,startDateButton,endDateButton,conformButton,pickButton;
    private ImageView imageView;
    private EditText nameEditText,descEditText;
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private DatePicker dp;
    //variable
    static String SELECTED_DATE="";
    static String SELECTED_TIME="";
    public static final int IMAGE_CODE =1;
    private Uri imageUri;
    private static final  String M_TITLE = "title";
    private static final String M_PROGRAM_DESCRIPTION = "description";
    private static final String M_START_DATE = "start date";
    private static final String M_END_DATE = "end date";
    private static final String M_START_TIME = "start time";
    private static final String M_END_TIME = "end time";
    private static final String M_IMAGE_URL = "program_images";

    //firebase
    private FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://softwareprojectv-default-rtdb.firebaseio.com/");
    private DatabaseReference mRef = database.getReference("program_info");
    private StorageReference storage = FirebaseStorage.getInstance().getReference("program_image");
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_program);
        findSections();
        setListeners();
    }

    private void setListeners() {
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(startDateText);
                startDateText.setText(SELECTED_DATE);
            }
        });
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(endDateText);
                endDateText.setText(SELECTED_DATE);
            }
        });
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime(startTimeText);
                startTimeText.setText(SELECTED_TIME);
            }
        });
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime(endTimeText);
                endTimeText.setText(SELECTED_TIME);
            }
        });
        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        conformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allAreOk()){
                    saveToDatabase();
                }
                else{
                    Toast.makeText(AddProgramActivity.this, "Please enter all information correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean allAreOk() {
        if (endDateText.getText().toString().equals("pick date")){
            return false;
        }if (startDateText.getText().toString().equals("pick date")){
            return false;
        }if (endTimeText.getText().toString().equals("pick time")){
            return false;
        }if (endTimeText.getText().toString().equals("pick time")){
            return false;
        }
        return true;
    }

    private void findSections() {
        startTimeText = findViewById(R.id.AddProgramActivityProgramPickStartTimeTextId);
        endTimeText = findViewById(R.id.AddProgramActivityProgramPickEndTimeTextId);
        startDateText = findViewById(R.id.AddProgramActivityProgramPickStartDateTextId);
        endDateText = findViewById(R.id.AddProgramActivityProgramPickEndDateTextId);
        startDateButton = findViewById(R.id.AddProgramActivityProgramPickStartDateButtonId);
        endDateButton = findViewById(R.id.AddProgramActivityProgramPickEndDateId);
        startTimeButton = findViewById(R.id.AddProgramActivityProgramPickStartTimeButtonId);
        endTimeButton = findViewById(R.id.AddProgramActivityProgramPickEndTimeId);
        conformButton = findViewById(R.id.AddProgramActivityProgramConformButtonId);
        nameEditText = findViewById(R.id.AddProgramActivityProgramNameEditTextId);
        descEditText = findViewById(R.id.AddProgramActivityProgramDescriptionEditTextId);
        pickButton = findViewById(R.id.AddProgramActivityProgramPickImageButtonId);
        imageView = findViewById(R.id.AddProgramActivityProgramPickImageViewId);
    }
    private void saveToDatabase() {
        final String title = nameEditText.getText().toString();
        if (title.isEmpty()) {
            nameEditText.setError("Enter name");
            nameEditText.requestFocus();
            return;
        }
        final String description = descEditText.getText().toString();
        if (description.isEmpty()) {
            descEditText.setError("Enter description");
            descEditText.requestFocus();
            return;
        }
        final StorageReference ref = storage.child(""+System.currentTimeMillis()+"");
        ref.child(getFileName(imageUri))
                .putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.child(getFileName(imageUri)).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Toast.makeText(AddProgramActivity.this, "Inside upload", Toast.LENGTH_SHORT).show();
                        String url= task.getResult().toString();
                        Map<String,String> map = new HashMap<>();
                        map.put(M_TITLE,title);
                        map.put(M_PROGRAM_DESCRIPTION,description);
                        map.put(M_START_DATE,startDateText.getText().toString());
                        map.put(M_END_DATE,endDateText.getText().toString());
                        map.put(M_START_TIME,startTimeText.getText().toString());
                        map.put(M_END_TIME,endTimeText.getText().toString());
                        map.put(M_IMAGE_URL,url);
                        String key = mRef.push().getKey();
                        mRef.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(AddProgramActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(AddProgramActivity.this, "Insert Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddProgramActivity.this, "Insert failed Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void pickTime(final TextView textView){

        final int[] c = {0};
        final String[] time = {""};
        TimePicker timePicker = new TimePicker(AddProgramActivity.this);
        int hour = timePicker.getCurrentHour();
        int minute =timePicker.getCurrentMinute();
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Time time = new Time(i, i1, 0);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mma");
                String s = simpleDateFormat.format(time);
                textView.setText(s);
            }
        },hour,minute,true);
        timePickerDialog.show();

    }
    private void pickDate(final TextView textView){
        dp = new DatePicker(AddProgramActivity.this);
        int year = dp.getYear();
        int month = dp.getMonth()+1;
        final int day = dp.getDayOfMonth();
        datePickerDialog = new DatePickerDialog(AddProgramActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                SELECTED_DATE =  (i2+"-"+(i1+1)+"-"+i);
                textView.setText(SELECTED_DATE);
            }
        },year,month,day);
        datePickerDialog.show();
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
                Toast.makeText(AddProgramActivity.this, "Image get", Toast.LENGTH_SHORT).show();
                imageUri = data.getData();
                Glide.with(AddProgramActivity.this).load(imageUri).centerCrop().into(imageView);
            }
        }
    }
    /*public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }*/
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