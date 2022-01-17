package com.example.ToList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ToList.model.Items;
import com.example.ToList.ui.ItemUpdateAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import util.YololistApi;

import static android.content.ContentValues.TAG;

public class YoloListActivity extends AppCompatActivity implements View.OnClickListener {

    EditText update_title, update_shopName, update_datego, update_addnewitem, update_expenses;
    Button saveButton, buttonAddNewItem;
    TextView update_budget, vItem, textOut;
    private CheckBox checked;
    private LinearLayout container;
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

        //intent
        title = getIntent().getStringExtra("Title");
        ListID = getIntent().getStringExtra("ListID");
        itemQty = getIntent().getStringExtra("ItemQty");
        DateAdded = getIntent().getStringExtra("DateAdded");
        shopName = getIntent().getStringExtra("shopName");
        datePlan = getIntent().getStringExtra("datePlan");
        totalbudget = getIntent().getStringExtra("totalbudget");
        totalexpenses = getIntent().getStringExtra("totalexpenses");

        //get element user interface id
        update_title = findViewById(R.id.update_title);
        update_shopName = findViewById(R.id.update_shopName);
        update_datego = findViewById(R.id.update_datego);
        update_budget = findViewById(R.id.update_budget);
        update_addnewitem = findViewById(R.id.update_addnewitem);
        update_expenses = findViewById(R.id.update_expenses);
        saveButton = findViewById(R.id.update_savebutton);
        saveButton.setOnClickListener(this);
        buttonAddNewItem = findViewById(R.id.button_update_add_new_item);
        buttonAddNewItem.setOnClickListener(this);

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

            if (v.getId() == R.id.update_savebutton) {
                updateList(currentUserId);
            } else if (v.getId() == R.id.button_update_add_new_item)  {
                addnewItem(currentUserId);
            }
        }
    }

    private void addnewItem(String currentUserId) {
        String up_newItem = update_addnewitem.getText().toString().trim();

        if (!TextUtils.isEmpty(title)) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("List");

            String key = reference.push().getKey();
            Items item = new Items();
            item.setItemid(key);
            item.setItemStatus("not yet purchased");
            item.setItemName(up_newItem);
            item.setListid(ListID);

            collectionReferenceI.add(item)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            //int totalQtyNow = Integer.parseInt(itemQty)+1; //kena update dalam list
                            Toast.makeText(YoloListActivity.this, "Item successful added.."+itemQty, Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(YoloListActivity.this, MainActivity.class)); // mcm mana nk bg dia show sekali jefinish();
                            overridePendingTransition( 0, 0);
                            startActivity(getIntent());
                            overridePendingTransition( 0, 0);
                            finish();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(YoloListActivity.this, "Item cannot added on database.", Toast.LENGTH_SHORT).show();
                }
            });
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


            collectionReferenceL.whereEqualTo("listid", ListID).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())    {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    com.example.ToList.model.List list = document.toObject(com.example.ToList.model.List.class);

                                    String doc_id = document.getId();

                                    DocumentReference listRef = collectionReferenceL.document(doc_id);
                                    try {
                                        listRef.update(
                                                "title", title,
                                                "shopName", shopName,
                                                "totalexpenses", Float.parseFloat(totalexpenses),
                                                "datePlan", new SimpleDateFormat("dd/MM/yyyy").parse(datePlan)
                                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())    {
                                                    ///update item dari sini!


                                                    //update item ke sini!
                                                    Toast.makeText(YoloListActivity.this, "Alhamdullilah updated", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(YoloListActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                                else
                                                    Toast.makeText(YoloListActivity.this, "CUBA LAGI", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(YoloListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        collectionReferenceL.whereEqualTo("listid", ListID)
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



                        collectionReferenceI.whereEqualTo("listid", ListID)
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