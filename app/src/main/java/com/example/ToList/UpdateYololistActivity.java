package com.example.ToList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ToList.model.Items;
import com.example.ToList.ui.ItemAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import util.YololistApi;

public class UpdateYololistActivity extends AppCompatActivity {

    EditText vTitle, vItem;
    String title, ListID, itemQty, DateAdded;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReferenceL = db.collection("List");
    private CollectionReference collectionReferenceI = db.collection("Items");

    private List<Items> allItems;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_yololist);

        title = getIntent().getStringExtra("Title");
        ListID = getIntent().getStringExtra("ListID");
        itemQty = getIntent().getStringExtra("ItemQty");
        DateAdded = getIntent().getStringExtra("DateAdded");

        vTitle = findViewById(R.id.textTitle);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        allItems = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewItem);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        //kena compare listid this dgn listid dlm firestore
        // listid = listid dlm db
        //String userid = YololistApi.g;
        //String LID = ;

        collectionReferenceL.whereEqualTo("listid", getIntent().getStringExtra("ListID")).whereEqualTo("userId", YololistApi.getInstance()
                .getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //settexttitle
                        vTitle.setText(title);


                        collectionReferenceI.whereEqualTo("listid", getIntent().getStringExtra("ListID"))
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                                            for (QueryDocumentSnapshot items : queryDocumentSnapshots) {
                                                Items item = items.toObject(Items.class);
                                                allItems.add(item);
                                            }
                                            //invoke recyclerview
                                            itemAdapter = new ItemAdapter(UpdateYololistActivity.this, allItems);
                                            recyclerView.setAdapter(itemAdapter);
                                            itemAdapter.notifyDataSetChanged();
                                        }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UpdateYololistActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}