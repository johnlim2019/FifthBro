package com.example.testbro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Region;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityRegisterUser extends AppCompatActivity implements View.OnClickListener{

    // declaration
    private EditText editTextPassword, editTextEmail, editTextClub, editTextName, editTextPhone;
    private Button register;
    private TextView banner;

    private FirebaseAuth mAuth;
    private DatabaseReference referenceUsers, referenceClubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // initialize
        editTextClub = (EditText) findViewById(R.id.club);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextName = (EditText) findViewById(R.id.name);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextPhone = (EditText) findViewById(R.id.phoneNumber);

        register = (Button) findViewById(R.id.registerUser);
        register.setOnClickListener(this);
        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        referenceClubs = FirebaseDatabase.getInstance().getReference("Clubs");
        referenceUsers = FirebaseDatabase.getInstance().getReference("Users");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.registerUser:
                // register user
                registerUser();
                // after register user, go back to login page
                finish();
                break;
            case R.id.banner:
                // back to main activity
                startActivity(new Intent(ActivityRegisterUser.this, MainActivity.class));
                break;
        }
    }

    private void registerUser() {
        // get inputs
        String name = editTextName.getText().toString().trim();
        String clubName = editTextClub.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String clubID = clubName.toLowerCase().replaceAll("\\s", "");

        // validation
        if (name.isEmpty()) {
            editTextName.setError("Please enter name");
            editTextName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("Please enter email");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter correct email format");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Please enter password");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Please enter password minimum of 6 characters");
            editTextPassword.requestFocus();
            return;
        }
        if (phone.length()!= 8){
            editTextPhone.setError("Invalid phone number");
            editTextPhone.requestFocus();
            return;
        }
        if (clubName.isEmpty()) {
            editTextClub.setError("Please enter club");
            editTextClub.requestFocus();
            return;
        }



        ArrayList<String> clubList = new ArrayList<>();

        // create new mAuth
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // create user class with inputs
                    UserClass userClass = new UserClass(clubID, name, email, phone, clubName);
                    // store created user to database
                    referenceUsers.child(mAuth.getCurrentUser().getUid()).setValue(userClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                // if user is stored,
                                // store all existing clubs into club list, to check if club already exist
                                referenceClubs.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot tempClub : snapshot.getChildren()){
                                            clubList.add(tempClub.getKey());
                                        }
                                        // if club already exist, add current user to the users list of the club
                                        if(clubList.contains(clubID)){
                                            referenceClubs.child(clubID).child("users").child(userClass.getUserID()).setValue(userClass);
                                        }
                                        // if club not exist
                                        else{
                                            // create new club class
                                            ClubClass clubClass = new ClubClass(clubName, clubID);
                                            // store created club class to database
                                            referenceClubs.child(clubID).setValue(clubClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(ActivityRegisterUser.this, "Success", Toast.LENGTH_LONG).show();
                                                    }
                                                    else{
                                                        Toast.makeText(ActivityRegisterUser.this, "Not successful", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ActivityRegisterUser.this, "Error getting clubs", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else{
                                Toast.makeText(ActivityRegisterUser.this, "Failed to store user class", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(ActivityRegisterUser.this, "Failed to create mAuth", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}