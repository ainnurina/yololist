package com.example.yololist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yololist.data.model.List;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.YololistApi;

public class PostYololistActivity extends AppCompatActivity {

    private static final String TAG = "PostYololistActivity";
    private Button saveButton;
    private Button buttonAdd;
    private EditText list_title;
    private EditText textIn;
    private LinearLayout container;

    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("List");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_yololist);

        firebaseAuth = FirebaseAuth.getInstance();
        list_title = findViewById(R.id.post_list_title);
        //currentUserTextView = findViewById(R.id.post_username_textview);

        textIn = findViewById(R.id.textin);
        saveButton = findViewById(R.id.post_saveButton);
        buttonAdd = findViewById(R.id.post_add_new_item);
        container = findViewById(R.id.container_item);


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.row, null);
                TextView textOut = addView.findViewById(R.id.textout);
                textOut.setText(textIn.getText().toString());

                Button buttonRemove = (Button) addView.findViewById(R.id.remove);
                buttonRemove.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((LinearLayout) addView.getParent()).removeView(addView);
                    }
                });

                container.addView(addView);
            }
        });


        if (YololistApi.getInstance() != null) {
            currentUserId = YololistApi.getInstance().getUserId();
            currentUserName = YololistApi.getInstance().getUsername();
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }
            }
        };

    }
    public void onClick(View v) {
        switch (v.getId())  {
            case R.id.post_saveButton:
                //saveList
                saveList();
                break;
        }
    }

    private void saveList() {
        String title = list_title.getText().toString().trim();

        /*--date--*/
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Map map = new HashMap();
        map.put("timestamp", ServerValue.TIMESTAMP);
        ref.child("yourNode").updateChildren(map);

        /*--Loop item--*/
        //count total item & pass at new collection
        int totitem = 0; //untuk count total

        if (!TextUtils.isEmpty(title)) {
            List list = new List();
            list.setTitle(title);
            list.setTotitem(totitem);
            list.setTimeAdded(new Timestamp(new Date()));
            list.setUserId(currentUserId);

            //invoke collectionReference
            collectionReference.add(list)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            startActivity(new Intent(PostYololistActivity.this, YoloListActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "onFailure: " + e.getMessage());

                        }
                    });

        }else   {

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null)   {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }

    }
}