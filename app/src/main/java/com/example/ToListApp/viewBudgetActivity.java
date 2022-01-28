package com.example.ToListApp;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ToListApp.ui.budgetAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import util.YololistApi;

import static android.content.ContentValues.TAG;

public class viewBudgetActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    budgetAdapter adapter;
    private TextView noListEntry;
    private EditText firstdate, enddate;
    private Button buttonanalyse;
    private List<com.example.ToListApp.model.List> allList = allList = new ArrayList<>();
    private  DatePickerDialog.OnDateSetListener mDateSetListener;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("List");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_budget);

        recyclerView = findViewById(R.id.recyclerview_monitorbudget);
        firstdate = findViewById(R.id.firstdate);
        enddate = findViewById(R.id.enddate);
        buttonanalyse = findViewById(R.id.buttonanalyse);

        //get calendar
        firstdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        viewBudgetActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDataset date: dd/mm/yyyy" + day + "/" + month + "/" + year);

                String date = day + "/" + month + "/" +  year;
                firstdate.setText(date);
            }
        };

        setRecyclerView();
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId", YololistApi.getInstance()
                .getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty())  {
                            allList.clear();
                            for (QueryDocumentSnapshot lists : queryDocumentSnapshots)  {
                                com.example.ToListApp.model.List list = lists.toObject(com.example.ToListApp.model.List.class);
                                allList.add(list);
                            }
                            //Invoke recyler view
                            adapter = new budgetAdapter(getApplicationContext(), allList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else   {
                            noListEntry.setVisibility(View.VISIBLE);
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