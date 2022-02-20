package com.example.ToListApp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ToListApp.model.List;
import com.example.ToListApp.ui.ListRecyclerAdapter;
import com.example.ToListApp.ui.RecyclerViewInterface;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import util.YololistApi;

public class ListHistoryActivity extends AppCompatActivity implements RecyclerViewInterface {
    private RecyclerView recyclerView;
    private java.util.List<List> allListHistory;
    private ListRecyclerAdapter listRecyclerAdapter;
    private RecyclerViewInterface recyclerViewInterface = this::onItemClick;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("List");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_history);

        allListHistory = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereLessThan("datePlan", new Timestamp(new Date()))
                .orderBy("datePlan", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            allListHistory.clear();
                            for (QueryDocumentSnapshot lists : queryDocumentSnapshots) {
                                List list = lists.toObject(List.class);
                                Timestamp t = new Timestamp(new Date());

                                if (list.getUserId().equals(YololistApi.getInstance().getUserId())
                                && list.getStatusList().equals("Completed")) {
                                    allListHistory.add(list);
                                }

                            }
                            //Invoke recyler view
                            listRecyclerAdapter = new ListRecyclerAdapter(getApplicationContext(), allListHistory, recyclerViewInterface);
                            recyclerView.setAdapter(listRecyclerAdapter);
                            listRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(ListHistoryActivity.this, UpdateItemsActivity.class);

        long t = (allListHistory.get(position).getDatePlan().getSeconds()) * 1000;
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy");

        intent.putExtra("Title", allListHistory.get(position).getTitle());
        intent.putExtra("ListID", allListHistory.get(position).getListid());
        intent.putExtra("DateAdded", allListHistory.get(position).getTimeAdded());
        intent.putExtra("shopName", allListHistory.get(position).getShopName());
        intent.putExtra("datePlan", sfd.format(new Date(t)));
        intent.putExtra("totalbudget", "" + allListHistory.get(position).getTotalbudget());
        intent.putExtra("totalexpenses", "" + allListHistory.get(position).getTotalexpenses());

        startActivity(intent);
    }

}
