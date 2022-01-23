package com.example.ToList;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ToList.ui.budgetAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import util.YololistApi;

public class TableExpensesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    budgetAdapter adapter;
    private TextView noListEntry;
    private List<com.example.ToList.model.List> allList = allList = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("List");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_expenses);

        recyclerView = findViewById(R.id.recyclerview_monitorbudget);

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
                                com.example.ToList.model.List list = lists.toObject(com.example.ToList.model.List.class);
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