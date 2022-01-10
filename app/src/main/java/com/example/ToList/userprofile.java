package com.example.ToList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ToList.model.Items;
import com.example.ToList.model.Shopper;
import com.example.ToList.ui.ItemUpdateAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class userprofile extends AppCompatActivity {
    private TextView Name, email, phone, cuba;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");


    //StorageReference storageReference;
    // CollectionReference collectionReference = db.collection("Users");

    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        phone = findViewById(R.id.profilePhone);
        Name =  findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);


        //connect to Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        firebaseUser = firebaseAuth.getCurrentUser();
        //storageReference = FirebaseStorage.getInstance().getReference();

        /*

        collectionReference.whereEqualTo("userId", userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful())    {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Shopper s = document.toObject(Shopper.class);
                                String doc_UID = document.getId();

                               firestore.collection("Users").document(doc_UID)
                                       .get()
                                       .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                             if (task.isSuccessful())   {
                                                 Shopper user = task.getResult().toObject(Shopper.class);
                                                 phone.setText("01151132661");
                                                 Name.setText(user.getUserName());
                                                 email.setText(user.getEmail());

                                                 Toast.makeText(userprofile.this, "Alhamdullilah"+user.getUserId(), Toast.LENGTH_SHORT).show();
                                             }
                                           }
                                       });


                            }


                        }
                        else
                            Toast.makeText(userprofile.this, "CUBA LAGI", Toast.LENGTH_SHORT).show();

                    }
                });

         */


        collectionReference.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Shopper user = document.toObject(Shopper.class);

                                phone.setText(user.getusername());
                                email.setText(user.getEmail());
                                Name.setText(user.getUserId());

                                Toast.makeText(userprofile.this, "Alhamdullilah"+user.getusername(), Toast.LENGTH_SHORT).show();
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