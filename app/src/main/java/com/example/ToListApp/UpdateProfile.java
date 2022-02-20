package com.example.ToListApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ToListApp.model.Shopper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UpdateProfile extends AppCompatActivity {

    private EditText update_profileName, update_profileEmail, update_profilePhone;
    private Button updateProfileButton;
    private String input_profileName, input_profileEmail, input_profilePhone;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String userId;
    private String documentId;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        firebaseUser = firebaseAuth.getCurrentUser();

        update_profileName = findViewById(R.id.update_profileName);
        update_profileEmail = findViewById(R.id.update_profileEmail);
        update_profilePhone = findViewById(R.id.update_profilePhone);
        updateProfileButton = findViewById(R.id.updateProfileButton);

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

    }

    private void updateProfile() {
        input_profileName = update_profileName.getText().toString().trim();
        input_profileEmail = update_profileEmail.getText().toString().trim();
        input_profilePhone = update_profilePhone.getText().toString().trim();

        collectionReference.document(documentId)
                .update("username", input_profileName,
                        "email", input_profileEmail,
                        "phoneno", input_profilePhone)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateProfile.this, "Profile Succesfully Updated", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(UpdateProfile.this, userprofileActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProfile.this, "Profile cannot be updated", Toast.LENGTH_LONG).show();

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Shopper user = document.toObject(Shopper.class);

                                update_profileName.setText(user.getUsername());
                                update_profileEmail.setText(user.getEmail());
                                update_profilePhone.setText(user.getPhoneno());
                                documentId = document.getId();

                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}