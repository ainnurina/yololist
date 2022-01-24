package com.example.ToList;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import util.YololistApi;

public class UpdateListDetails extends AppCompatActivity {
    EditText update_title, update_shopName, update_datego, update_addnewitem, update_expenses, update_budget;
    String title, ListID, itemQty, DateAdded, shopName, datePlan, totalbudget, totalexpenses;
    Button saveButton;
    Date newdate;

    private FirebaseFirestore db;
    private CollectionReference collectionReferenceL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_list_details);

        db = FirebaseFirestore.getInstance();
        collectionReferenceL = db.collection("List");

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
        update_budget = findViewById(R.id.update_budget);
        update_addnewitem = findViewById(R.id.update_addnewitem);
        update_expenses = findViewById(R.id.update_expenses);
        update_datego = findViewById(R.id.update_datego);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        update_datego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        UpdateListDetails.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        update_datego.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

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
        totalbudget = update_budget.getText().toString().trim();
        totalexpenses = update_expenses.getText().toString().trim();
        datePlan = update_datego.getText().toString().trim();

        try {
            newdate = new SimpleDateFormat("dd/MM/yyyy").parse(datePlan);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*--Loop item--*/
        //count total item & pass at new collection

        if (!TextUtils.isEmpty(title)) {

            collectionReferenceL.whereEqualTo("listid", ListID).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    com.example.ToList.model.List list = document.toObject(com.example.ToList.model.List.class);

                                    String doc_id = document.getId();

                                    db.collection("List")
                                            .document(doc_id)
                                            .update("title", title,
                                                    "shopName", shopName,
                                                    "totalbudget", Float.parseFloat(totalbudget),
                                                    "totalexpenses", Float.parseFloat(totalexpenses),
                                                    "datePlan", newdate)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(UpdateListDetails.this, "Succesfully Updated", Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(UpdateListDetails.this, YoloListActivity.class);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UpdateListDetails.this, "Error Occured " + e, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }else{
                                    Toast.makeText(UpdateListDetails.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
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