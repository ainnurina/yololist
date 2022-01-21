package com.example.ToList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ToList.model.Items;
import com.example.ToList.ui.ItemUpdateAdapter;
import com.example.ToList.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import util.YololistApi;

public class UpdateListDetails extends AppCompatActivity {
    EditText update_title, update_shopName, update_datego, update_addnewitem, update_expenses, update_budget;
    String title, ListID, itemQty, DateAdded, shopName, datePlan, totalbudget, totalexpenses;
    Button saveButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceL = db.collection("List");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_list_details);

        //get intent
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
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (YololistApi.getInstance() != null) {
                    String currentUserId = YololistApi.getInstance().getUserId();
                    updateList(currentUserId);
                }
            }
        });
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
                                                "shopName", "AeonLah",
                                                "totalexpenses", Float.parseFloat(totalexpenses),
                                                "datePlan", new SimpleDateFormat("dd/MM/yyyy").parse(datePlan)
                                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())    {
                                                    Toast.makeText(UpdateListDetails.this, "Alhamdullilah updated", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(UpdateListDetails.this, MainActivity.class));
                                                    finish();
                                                }
                                                else
                                                    Toast.makeText(UpdateListDetails.this, "CUBA LAGI", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(UpdateListDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();

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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}