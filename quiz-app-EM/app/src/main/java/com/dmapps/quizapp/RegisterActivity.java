package com.dmapps.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    de.hdodenhof.circleimageview.CircleImageView profile;
    private FirebaseAuth mAuth;
    DatabaseReference myRef;
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference storageReference;
    private String filePath;
    Map<String, String> userMap = new HashMap<>();
    EditText name,email,mobile,password,confirmPassword;

    AppCompatButton register;
    AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("database").child("user");

        //upload image
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        profile = findViewById(R.id.profile_image);
        profile.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.con_password);

        avi = findViewById(R.id.avi);
        register =  findViewById(R.id.register);
        register.setOnClickListener(v -> validForm());
    }

    private void validForm() {
        if (name.getText().toString().length()==0) {
            name.setError("Invalid name");
        }
        else if(!isEmailValid(email.getText().toString())) {
            email.setError("Invalid Email");
        }
        else if(mobile.getText().toString().length() < 10 ) {
            mobile.setError("Invalid Mobile Number");
        }
        else if(password.getText().toString().length() < 6) {
            password.setError("Length should be more than six character");
        }
        else if(confirmPassword.getText().toString().length() < 6) {
            confirmPassword.setError("Length should be more than six character");
        }
        else if(!password.getText().toString().equals(confirmPassword.getText().toString())) {
            password.setError("Password should be equal");
        }
        else {
            register.setVisibility(AppCompatButton.GONE);
            avi.setVisibility(AVLoadingIndicatorView.VISIBLE);
            createUser();
        }
    }

    private void createUser() {
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        addUserToDatabase(user.getUid());
                    } else {
                        // If sign in fails, display a message to the user.
                        register.setVisibility(AppCompatButton.VISIBLE);
                        avi.setVisibility(AVLoadingIndicatorView.GONE);
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addUserToDatabase(String uid) {
        userMap.put("name",name.getText().toString());
        userMap.put("email",email.getText().toString());
        userMap.put("password",password.getText().toString());
        userMap.put("mobile",mobile.getText().toString());
        userMap.put("profile",filePath);

        //push data
        myRef.child(uid).setValue(userMap).addOnSuccessListener(aVoid -> {
                    register.setVisibility(AppCompatButton.VISIBLE);
                    avi.setVisibility(AVLoadingIndicatorView.GONE);
            Toast.makeText(getApplicationContext(),"Successfully create an account",Toast.LENGTH_LONG).show();
                    onBackPressed();
        }).addOnFailureListener(e ->{
                    register.setVisibility(AppCompatButton.VISIBLE);
                    avi.setVisibility(AVLoadingIndicatorView.GONE);
                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                }
        );}

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profile.setImageBitmap(photo);
            filePath = encodeImage(photo);
        }
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}