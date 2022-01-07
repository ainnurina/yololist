package com.example.ToList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.ToList.model.Items;
import com.example.ToList.model.List;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

import util.YololistApi;

public class PostYololistActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "PostYololistActivity";
    private Button saveButton;
    private Button buttonAdd;
    private EditText list_title;
    private EditText textIn;
    private LinearLayout container;
    private TextView textOut;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;


    private CollectionReference collectionReference = db.collection("List");
    private CollectionReference collectionReferenceI = db.collection("Items");

    private ArrayList<String> values = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_yololist);

        firebaseAuth = FirebaseAuth.getInstance();
        list_title = findViewById(R.id.post_list_title);

        textIn = findViewById(R.id.textin);
        saveButton = findViewById(R.id.post_saveButton);
        saveButton.setOnClickListener(this);
        buttonAdd = findViewById(R.id.post_add_new_item);
        container = findViewById(R.id.container_item);



        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View addView = layoutInflater.inflate(R.layout.row, null);

                textOut = addView.findViewById(R.id.textout);
                textOut.setText(textIn.getText().toString());
                values.add(textIn.getText().toString());


                Button buttonRemove = (Button) addView.findViewById(R.id.remove);
                View finalAddView = addView;
                String tempS = textOut.getText().toString().trim();
                //String name = textIn.getText().toString().trim();
                buttonRemove.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View v) {

                        for (int i = 0; i <values.size(); i++) {
                            if ((tempS).equals(values.get(i))) {
                                values.remove(i);
                                ((LinearLayout) finalAddView.getParent()).removeView(finalAddView);
                                break;
                            }
                        }

                    }
                });

                container.addView(addView);
            }
        });


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

    @Override
    public void onClick(View v) {

        if (YololistApi.getInstance() != null) {
            String currentUserId = YololistApi.getInstance().getUserId();
            String currentUserName = YololistApi.getInstance().getUsername();

            if (v.getId() == R.id.post_saveButton) {
                saveList(currentUserId);
            }
        }
    }


    private void saveList(String currentUserId) {
        String title = list_title.getText().toString().trim();

        /*--Loop item--*/
        //count total item & pass at new collection
        int totitem = 0; //untuk count total

        if (!TextUtils.isEmpty(title)) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("List");


            String key = reference.push().getKey();
            List list = new List();
            list.setListid(key);
            list.setTitle(title);
            list.setTotitem(values.size());
            list.setTimeAdded(new Timestamp(new Date()));
            list.setUserId(currentUserId);

            //invoke collectionReference
            collectionReference.add(list)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            //count addview column
                            String[] itemName = new String[0];

                            for (int i = 0; i < values.size(); i++) {
                                DatabaseReference referenceI = FirebaseDatabase.getInstance().getReference().child("Items");


                                String keyItem = referenceI.push().getKey();
                                //itemName[i] = textOut.getText().toString().trim();

                                Items item = new Items();
                                item.setItemid(keyItem);
                                item.setItemName(values.get(i));
                                //item.setItemName(itemName);
                                item.isItemchecked(false);


                                item.setListid(key);

                                collectionReferenceI.add(item)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(PostYololistActivity.this, "List has been added", Toast.LENGTH_SHORT).show();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Log.d(TAG, "onFailure: " + e.getMessage());

                                            }
                                        });

                            }

                            //ltk sini
                            startActivity(new Intent(PostYololistActivity.this, MainActivity.class)); // mcm mana nk bg dia show sekali je
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "onFailure: " + e.getMessage());

                        }
                    });

        } else {

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
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }

    }
}