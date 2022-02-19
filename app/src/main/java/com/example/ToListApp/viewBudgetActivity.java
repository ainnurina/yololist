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
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ToListApp.ui.budgetAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import util.YololistApi;

import static android.content.ContentValues.TAG;

public class viewBudgetActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    budgetAdapter adapter;
    private EditText firstdate, enddate;
    private TextView sumbudget, sumexpenses, qtylistwithinbudget, qtylistoverbudget, largestlistitle;
    private LinearLayout framelayout4, inputbfrlayout, graphtitle;
    private TableLayout outputtable;
    private Button buttonanalyse;
    private GraphView graph;
    private List<com.example.ToListApp.model.List> allList = allList = new ArrayList<>();
    private DatePickerDialog.OnDateSetListener mDateSetListenerFirst;
    private DatePickerDialog.OnDateSetListener mDateSetListenerEnd;

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
        sumbudget = findViewById(R.id.sumbudget);
        sumexpenses = findViewById(R.id.sumexpenses);
        qtylistwithinbudget = findViewById(R.id.qtylistwithinbudget);
        qtylistoverbudget = findViewById(R.id.qtylistoverbudget);
        largestlistitle = findViewById(R.id.largestlistitle);
        framelayout4 = findViewById(R.id.frameLayout4);
        inputbfrlayout = findViewById(R.id.inputbfrlayout);
        outputtable = findViewById(R.id.outputtable);
        graph = (GraphView) findViewById(R.id.graph);
        graphtitle = findViewById(R.id.graphtitle);

        //get calendar
        firstdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendartheme(mDateSetListenerFirst);
            }
        });

        mDateSetListenerFirst = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDataset date: dd/mm/yyyy" + day + "/" + month + "/" + year);

                String date = day + "/" + month + "/" + year;

                firstdate.setText(date);
            }
        };

        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendartheme(mDateSetListenerEnd);
            }
        });

        mDateSetListenerEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDataset date: dd/mm/yyyy" + day + "/" + month + "/" + year);

                String date = day + "/" + month + "/" + year;
                enddate.setText(date);
            }
        };

        buttonanalyse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAnalysisBudget(firstdate, enddate);
            }
        });

        setRecyclerView();
    }

    private void getAnalysisBudget(EditText firstdate, EditText enddate) {
        String startdt = firstdate.getText().toString().trim();
        String enddt = enddate.getText().toString().trim();
        DecimalFormat df = new DecimalFormat("0.00");

        if (startdt.isEmpty()) {
            firstdate.setError("Select first date");
            firstdate.requestFocus();
        }

        if (enddt.isEmpty()) {
            enddate.setError("Select end date");
            enddate.requestFocus();
        }

        if (!startdt.isEmpty() && !enddt.isEmpty()) {
            inputbfrlayout.setVisibility(View.INVISIBLE);
            framelayout4.setVisibility(View.VISIBLE);
            outputtable.setVisibility(View.VISIBLE);

            Timestamp start = null, end = null;
            try {
                start = new Timestamp(new SimpleDateFormat("dd/MM/yyyy").parse(startdt));
                end = new Timestamp(new SimpleDateFormat("dd/MM/yyyy").parse(enddt));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Timestamp finalEnd1 = end;
            Timestamp finalStart = start;
            collectionReference.whereGreaterThanOrEqualTo("datePlan", start).whereLessThanOrEqualTo("datePlan", end)
                    .orderBy("datePlan", Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                allList.clear();

                                int countwithinbudget = 0, countoverbudget = 0;
                                float totsumbudget = 0, totsumexpenses = 0, totgap = 0, gap = 0;
                                String temp = "";
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    com.example.ToListApp.model.List list = document.toObject(com.example.ToListApp.model.List.class);
//                                    Toast.makeText(viewBudgetActivity.this, "Keluar"+list.getTotalexpenses(), Toast.LENGTH_SHORT).show();

                                    if (list.getTotalexpenses() > 0 && list.getUserId().equals(YololistApi.getInstance().getUserId())) {
                                        allList.add(list);
                                    }
                                }

                                graph.removeAllSeries();
                                LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
                                PointsGraphSeries<DataPoint> seriesPoint = new PointsGraphSeries<>();

                                int numDataPoint = allList.size();
                                double calctotalbudget = 0.0, x, y;
                                int i =0, a=1;


                                for (com.example.ToListApp.model.List list : allList) {

                                    if (list.getTotalbudget() >= list.getTotalexpenses()) {
                                        countwithinbudget++;

                                        calctotalbudget = list.getTotalbudget()-list.getTotalexpenses();

                                    } else if (list.getTotalexpenses() >= list.getTotalbudget()) {
                                        countoverbudget++;

                                        totgap = gap;
                                        gap = list.getTotalexpenses() - list.getTotalbudget();

                                        if (gap >= totgap) {
                                            temp = list.getTitle();
                                        }
                                        calctotalbudget = list.getTotalbudget()-list.getTotalexpenses();

                                    }


                                    i = a;
                                    series.appendData(new DataPoint(i, calctotalbudget), true, 100);
                                    seriesPoint.appendData(new DataPoint(i, calctotalbudget), true, 100);
                                    a++;

                                    totsumbudget = totsumbudget + list.getTotalbudget();
                                    totsumexpenses = totsumexpenses + list.getTotalexpenses();

                                }
                                seriesPoint.setShape(PointsGraphSeries.Shape.TRIANGLE);
                                seriesPoint.setSize(12);
                                seriesPoint.setColor(Color.BLACK);
                                graph.addSeries(series);
                                graph.addSeries(seriesPoint);
//                                graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
//                                graph.getViewport().setDrawBorder(true);
                                graphtitle.setVisibility(View.VISIBLE);
                                graph.setVisibility(View.VISIBLE);

//                                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(
//                                        new DataPoint[]{
//                                                new DataPoint(0, 1),
//                                                new DataPoint(1, 5),
//                                                new DataPoint(2, 3),
//                                                new DataPoint(3, 2),
//                                                new DataPoint(4, 6)
//                                        }
//                                );
//                                graph.addSeries(series);


                                sumbudget.setText("" + df.format(totsumbudget));
                                sumexpenses.setText("" + df.format(totsumexpenses));
                                qtylistwithinbudget.setText(":  " + countwithinbudget);
                                qtylistoverbudget.setText(":  " + countoverbudget);
                                largestlistitle.setText(":  " + temp);


                                //Invoke recyler view
                                adapter = new budgetAdapter(getApplicationContext(), allList);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            } else {

                                Toast.makeText(viewBudgetActivity.this, "Not available on that date", Toast.LENGTH_SHORT);
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

    private void calendartheme(DatePickerDialog.OnDateSetListener mDateSetListener) {
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

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}