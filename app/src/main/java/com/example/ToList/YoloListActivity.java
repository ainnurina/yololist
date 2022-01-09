package com.example.ToList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ToList.model.Items;
import com.example.ToList.ui.ItemUpdateAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import util.YololistApi;

import static android.content.ContentValues.TAG;

public class YoloListActivity extends AppCompatActivity implements View.OnClickListener {

    EditText update_title, update_shopName, update_datego, update_addnewitem, update_expenses;
    Button saveButton;
    TextView update_budget, vItem;
    String title, ListID, itemQty, DateAdded, shopName, datePlan, totalbudget, totalexpenses;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReferenceL = db.collection("List");
    private CollectionReference collectionReferenceI = db.collection("Items");

    private ArrayList<String> values = new ArrayList<String>();

    private List<Items> allItems;
    private RecyclerView recyclerView;
    private ItemUpdateAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yolo_list);;

        title = getIntent().getStringExtra("Title");
        ListID = getIntent().getStringExtra("ListID");
        itemQty = getIntent().getStringExtra("ItemQty");
        DateAdded = getIntent().getStringExtra("DateAdded");
        shopName = getIntent().getStringExtra("shopName");
        datePlan = getIntent().getStringExtra("datePlan");
        totalbudget = getIntent().getStringExtra("totalbudget");
        totalexpenses = getIntent().getStringExtra("totalexpenses");


        update_title = findViewById(R.id.update_title);
        update_shopName = findViewById(R.id.update_shopName);
        update_datego = findViewById(R.id.update_datego);
        update_budget = findViewById(R.id.update_budget);
        update_addnewitem = findViewById(R.id.update_addnewitem);
        update_expenses = findViewById(R.id.update_expenses);
        saveButton = findViewById(R.id.update_savebutton);
        saveButton.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        allItems = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewItem);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onClick(View v) {

        if (YololistApi.getInstance() != null) {
            String currentUserId = YololistApi.getInstance().getUserId();
            String currentUserName = YololistApi.getInstance().getUsername();

            if (v.getId() == R.id.post_saveButton) {
                updateList(currentUserId);
            }
        }
    }

    private void updateList(String currentUserId) {

        title = update_title.getText().toString().trim();
        shopName = update_shopName.getText().toString().trim();
        datePlan = update_datego.getText().toString().trim();
        totalbudget = update_budget.getText().toString().trim();
        totalexpenses = update_expenses.getText().toString().trim();

        /*--Loop item--*/
        //count total item & pass at new collection
        int totitem = 0; //untuk count total

        if (!TextUtils.isEmpty(title)) {
            com.example.ToList.model.List list = new com.example.ToList.model.List();
            list.setTitle(title);
            list.setTotitem(values.size());
            list.setShopName(shopName);

            try {
                list.setDatePlan(new SimpleDateFormat("dd/MM/yyyy").parse(datePlan));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            list.setTotalbudget(Float.parseFloat("totbudget"));
            list.setTotalexpenses(Float.parseFloat("totalexpenses"));

            //invoke collectionReference
            collectionReferenceL.add(list)
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
                                item.Itemchecked(false);


                                item.setListid(key);

                                collectionReferenceI.add(item)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(YoloListActivity.this, "List has been updated", Toast.LENGTH_SHORT).show();


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
                            startActivity(new Intent(YoloListActivity.this, MainActivity.class));
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

                        update_title.setText(title);
                        update_shopName.setText(shopName);
                        update_datego.setText(datePlan);
                        update_budget.setText(totalbudget);

                        if (totalexpenses.equalsIgnoreCase("null"))   {
                            update_expenses.setText("0");
                        } else {
                            update_expenses.setText(totalexpenses);
                        }



                        collectionReferenceI.whereEqualTo("listid", getIntent().getStringExtra("ListID"))
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            for (QueryDocumentSnapshot items : queryDocumentSnapshots) {
                                                Items item = items.toObject(Items.class);
                                                allItems.add(item);
                                            }
                                            //invoke recyclerview
                                            itemAdapter = new ItemUpdateAdapter(YoloListActivity.this, allItems);
                                            recyclerView.setAdapter(itemAdapter);
                                            itemAdapter.notifyDataSetChanged();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

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