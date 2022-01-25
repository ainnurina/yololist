package com.example.ToList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ToList.model.Items;
import com.example.ToList.ui.ItemUpdateAdapter;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.YololistApi;

public class YoloListActivity extends AppCompatActivity implements View.OnClickListener {

    TextView view_title, view_shopName, view_datego, update_addnewitem, view_expenses;
    Button goUpdate, buttonAddNewItem;
    TextView update_budget, vItem, textOut;
    private CheckBox checked;
    private LinearLayout container;
    String title, ListID, itemQty, DateAdded, shopName, datePlan, totalbudget, totalexpenses;
    Timestamp dp;
    long dpDate;

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
        view_title = findViewById(R.id.view_title);
        view_shopName = findViewById(R.id.view_shopName);
        view_datego = findViewById(R.id.view_datego);
        update_budget = findViewById(R.id.view_budget);
        update_addnewitem = findViewById(R.id.update_addnewitem);
        view_expenses = findViewById(R.id.view_expenses);
        goUpdate = findViewById(R.id.GoUpdate);
        goUpdate.setOnClickListener(this);


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

                Intent intent = new  Intent(YoloListActivity.this, UpdateListDetails.class);

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
            item.setStatus(0);
            item.setItemName(up_newItem);
            item.setListid(ListID);

            collectionReferenceI.add(item)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(YoloListActivity.this, "Item Saved", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            } else {
                                Toast.makeText(YoloListActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(YoloListActivity.this, "Masuk dari reminder"+ListID, Toast.LENGTH_SHORT).show();
                        //settexttitle
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                com.example.ToList.model.List list = document.toObject(com.example.ToList.model.List.class);

                                view_title.setText(list.getTitle());
                                view_shopName.setText(list.getShopName());
                                update_budget.setText("RM"+list.getTotalbudget());
                                long t = list.getDatePlan().getSeconds()*1000;
                                SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy");
                                view_datego.setText(sfd.format(new Date(t)));

                                if (Float.parseFloat(""+list.getTotalexpenses()) == 0)   {
                                    view_expenses.setText("0");
                                } else {
                                    view_expenses.setText("RM"+list.getTotalexpenses());
                                }
                            }
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