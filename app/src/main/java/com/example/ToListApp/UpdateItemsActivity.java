package com.example.ToListApp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ToListApp.model.Items;
import com.example.ToListApp.ui.ItemUpdateAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.YololistApi;

import static android.content.ContentValues.TAG;

public class UpdateItemsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView view_title, view_shopName, view_datego, update_addnewitem, update_addnewqty, view_expenses;
    private EditText inputexpenses;
    private Button buttonAddNewItem, button_addexpenses;
    private TextView goUpdate;
    private TextView update_budget, vItem, textOut;
    private CheckBox checked;
    private CardView box_addexpenses;
    private LinearLayout container;
    private String title, ListID, itemQty, DateAdded, shopName, datePlan, totalbudget, totalexpenses, listdocID;
    private Timestamp dp;
    private long dpDate;
    private int countchecked;

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
        setContentView(R.layout.activity_update_items);;

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
        view_title = findViewById(R.id.view_title);
        view_shopName = findViewById(R.id.view_shopName);
        view_datego = findViewById(R.id.view_datego);
        update_budget = findViewById(R.id.view_budget);
        update_addnewitem = findViewById(R.id.update_addnewitem);
        update_addnewqty = findViewById(R.id.update_addnewqty);
        view_expenses = findViewById(R.id.view_expenses);
        box_addexpenses = findViewById(R.id.box_addexpenses);
        inputexpenses = findViewById(R.id.inputexpenses);
        goUpdate = findViewById(R.id.GoUpdate);
        goUpdate.setOnClickListener(this);
        button_addexpenses = findViewById(R.id.button_addexpenses);
        button_addexpenses.setOnClickListener(this);


        buttonAddNewItem = findViewById(R.id.button_update_add_new_item);
        buttonAddNewItem.setOnClickListener(this);

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

            if (v.getId() == R.id.GoUpdate) {

                //date
                SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    dp = new Timestamp(new SimpleDateFormat("dd/MM/yyyy").parse(datePlan));
                    dpDate = dp.getSeconds()*1000;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Intent intent = new  Intent(UpdateItemsActivity.this, UpdateListActivity.class);

                intent.putExtra("Title", title);
                intent.putExtra("ListID", ListID);
                intent.putExtra("ItemQty", itemQty);
                intent.putExtra("DateAdded", DateAdded);
                intent.putExtra("shopName", shopName);
                intent.putExtra("datePlan", sfd.format(new Date(dpDate)));
                intent.putExtra("totalbudget", ""+totalbudget);
                intent.putExtra("totalexpenses", ""+totalexpenses);

                startActivity(intent);
            } else if (v.getId() == R.id.button_update_add_new_item)  {
                addnewItem(currentUserId);
            } else if (v.getId() == R.id.button_addexpenses)    {
                addexpenses();
            }
        }
    }

    private void addexpenses() {

        String expenses = inputexpenses.getText().toString().trim();
        if (!TextUtils.isEmpty(expenses)) {
            collectionReferenceL.document(listdocID).update("totalexpenses", Float.valueOf(expenses))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UpdateItemsActivity.this, "Expenses has been added", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, e.toString());
                }
            });
        }

    }

    private void addnewItem(String currentUserId) {
        String up_newItem = update_addnewitem.getText().toString().trim();
        int up_newQty = Integer.valueOf(update_addnewqty.getText().toString().trim());

        if (!TextUtils.isEmpty(title)) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("List");

            String key = reference.push().getKey();
            Items item = new Items();
            item.setItemid(key);
            item.setStatus(0);
            item.setItemName(up_newItem);
            item.setItemQty(up_newQty);
            item.setListid(ListID);

            collectionReferenceI.add(item)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                collectionReferenceL.document(listdocID).update("statusList", "In Progress");
                                Toast.makeText(UpdateItemsActivity.this, "Item Saved", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            } else {
                                Toast.makeText(UpdateItemsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, e.toString());
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
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                com.example.ToListApp.model.List list = document.toObject(com.example.ToListApp.model.List.class);
                                listdocID = document.getId();

                                //change float formal to two decimal point
                                DecimalFormat df = new DecimalFormat("0.00");

                                view_title.setText(list.getTitle());
                                view_shopName.setText(list.getShopName());
                                update_budget.setText("RM"+df.format(list.getTotalbudget()));
                                long t = list.getDatePlan().getSeconds()*1000;
                                SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy");
                                view_datego.setText(sfd.format(new Date(t)));

                                if (list.getTotalexpenses() == 0.0)   {
                                    inputexpenses.setHint("Enter expenses");
                                    view_expenses.setText("Go Shopping!");
                                } else {
                                    button_addexpenses.setText("Update");
                                    inputexpenses.setText(""+list.getTotalexpenses());
                                    view_expenses.setText("RM"+df.format(list.getTotalexpenses()));
                                }

                                if (list.getStatusList().equals("Completed"))   {
                                    box_addexpenses.setVisibility(View.VISIBLE);
                                }
                            }
                        }


                        collectionReferenceI.whereEqualTo("listid", ListID)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            allItems.clear();
                                            for (QueryDocumentSnapshot items : queryDocumentSnapshots) {
                                                Items item = items.toObject(Items.class);
                                                allItems.add(item);
                                            }
                                            //invoke recyclerview
                                            itemAdapter = new ItemUpdateAdapter(UpdateItemsActivity.this, allItems);
                                            recyclerView.setAdapter(itemAdapter);
                                            itemAdapter.notifyDataSetChanged();
                                        }

                                    }

                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, e.toString());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });

    }
}