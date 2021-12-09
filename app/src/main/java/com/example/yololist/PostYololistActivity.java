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
import android.view.View.OnClickListener;

import com.example.yololist.data.model.Items;
import com.example.yololist.data.model.List;
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

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.YololistApi;

public class PostYololistActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "PostYololistActivity";
    private Button saveButton;
    private Button buttonAdd;
    private EditText list_title;
    private EditText textIn;
    private TextView textOut;
    private LinearLayout container;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReferenceL = db.collection("List");
    private CollectionReference collectionReferenceI = db.collection("Items");


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_yololist);

        firebaseAuth = FirebaseAuth.getInstance();
        list_title = findViewById(R.id.post_list_title);
        textOut = findViewById(R.id.textout); //nnt buat loop
        //currentUserTextView = findViewById(R.id.post_username_textview);

        textIn = findViewById(R.id.textin);
        saveButton = findViewById(R.id.post_saveButton);
        saveButton.setOnClickListener(this);
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
            //String currentUserName = YololistApi.getInstance().getUsername();

            if (v.getId() == R.id.post_saveButton)  {
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
            List list = new List();

            /*----
            const racesCollection: AngularFirestoreCollection<Race>;
            return racesCollection.snapshotChanges().map(actions => {
            return actions.map(a => {
            const data = a.payload.doc.data() as Race;
            data.id = a.payload.doc.id;
            return data;
              });
            });
            ---*/

            list.setTitle(title);
            list.setTotitem(totitem);
            list.setTimeAdded(new Timestamp(new Date()));
            list.setUserId(currentUserId);
            //invoke collectionReference
            collectionReferenceL.add(list)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    
                       @Override
                        public void onSuccess(DocumentReference documentReference) {

                            //String itemName  = textOut.getText().toString().trim();

                            //insert loop data item into database
                                Items item = new Items();
                                item.setItemName(title);
                                item.isItemchecked("false");

                                //dptkan document id list
                                DocumentReference document = db.collection("list").document(title);
                                item.setListid(document.getId());

                                collectionReferenceI.add(item)
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