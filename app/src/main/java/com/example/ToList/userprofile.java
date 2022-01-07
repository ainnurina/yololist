package com.example.ToList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class userprofile extends AppCompatActivity {
    private TextView fullName, email, phone, cuba;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;
    //StorageReference storageReference;
    // CollectionReference collectionReference = db.collection("Users");

    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        phone = (TextView)findViewById(R.id.profilePhone);
        fullName = (TextView)findViewById(R.id.profileName);
        email = (TextView)findViewById(R.id.profileEmail);
        cuba = (TextView)findViewById(R.id.cubaan);



        //connect to Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        //connect to Firebase's Firestore
        firestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        firebaseUser = firebaseAuth.getCurrentUser();
        //storageReference = FirebaseStorage.getInstance().getReference();


        DocumentReference documentReference = firestore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (documentSnapshot.exists())  {
                    phone.setText("kenapa tak muncul");
                    fullName.setText(documentSnapshot.getString("username"));
                    email.setText(documentSnapshot.getString("email"));
                    cuba.setText("ihdsuhufhfuufhsufhsdyww");
                } else  {
                    phone.setText("Not exist"+ userId);
                    Log.d("tag", "onEvent: Document do not exists");
                }

            }
        });

    }

}