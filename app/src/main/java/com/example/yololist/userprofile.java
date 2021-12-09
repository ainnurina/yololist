package com.example.yololist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yololist.data.model.Shopper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class userprofile extends AppCompatActivity {
    private TextView fullName, email, phone;
    private FirebaseAuth fAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //private CollectionReference collectionReference = db.collection("Users");

    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        phone = (TextView)findViewById(R.id.profilePhone);
        fullName = (TextView)findViewById(R.id.profileName);
        email = (TextView)findViewById(R.id.profileEmail);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                phone.setText("kenapa tak muncul");
                fullName.setText(documentSnapshot.getString("username"));
                email.setText(documentSnapshot.getString("email"));
            }
        });

    }
}