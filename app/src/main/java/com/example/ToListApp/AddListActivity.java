
package com.example.ToListApp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ToListApp.model.Items;
import com.example.ToListApp.model.List;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import util.YololistApi;

public class AddListActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "AddListActivity";
    private Button saveButton;
    private Button buttonAdd;
    private EditText list_title;
    private EditText textIn;
    private LinearLayout container;
    private TextView textOut, textOutQty;

    private EditText etDate;
    private EditText shopName;
    private EditText budget;
    private EditText txtqty;
    DatePickerDialog.OnDateSetListener setListener;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;


    private CollectionReference collectionReference = db.collection("List");
    private CollectionReference collectionReferenceI = db.collection("Items");

    private ArrayList<String> values = new ArrayList<String>();
    private ArrayList<String> valuesQty = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        firebaseAuth = FirebaseAuth.getInstance();
        list_title = findViewById(R.id.post_list_title);
        etDate = findViewById(R.id.et_date);
        shopName = findViewById(R.id.post_list_shopName);
        budget = findViewById(R.id.post_list_budget);
        textIn = findViewById(R.id.textin);
        txtqty = findViewById(R.id.txtqty);
        saveButton = findViewById(R.id.post_saveButton);
        saveButton.setOnClickListener(this);
        buttonAdd = findViewById(R.id.post_add_new_item);
        container = findViewById(R.id.container_item);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        //to get date input in calendar
        etDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddListActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        etDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        //to add new item in the list
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View addView = layoutInflater.inflate(R.layout.item_row, null);

                textOut = addView.findViewById(R.id.textout);
                textOut.setText(textIn.getText().toString());
                values.add(textIn.getText().toString());

                textOutQty = addView.findViewById(R.id.inputqty);
                textOutQty.setText(txtqty.getText().toString());
                valuesQty.add(txtqty.getText().toString());

                Button buttonRemove = (Button) addView.findViewById(R.id.remove);
                View finalAddView = addView;
                String tempS = textOut.getText().toString().trim();
                //String name = textIn.getText().toString().trim();
                buttonRemove.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        for (int i = 0; i <values.size(); i++) {
                            if ((tempS).equals(values.get(i))) {
                                values.remove(i);
                                valuesQty.remove(i);
                                ((LinearLayout) finalAddView.getParent()).removeView(finalAddView);
                                break;
                            }
                        }

                    }
                });

                container.addView(addView);

            }
        });


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }
            }
        };

    }

    @Override
    public void onClick(View v) {

        if (YololistApi.getInstance() != null) {
            String currentUserId = YololistApi.getInstance().getUserId();

            if (v.getId() == R.id.post_saveButton) {
                addList(currentUserId);
            }
        }
    }


    private void addList(String currentUserId) {
        String title = list_title.getText().toString().trim();
        String shopnamelocation = shopName.getText().toString().trim();
        String dateplan = etDate.getText().toString().trim();
        String totbudget = budget.getText().toString().trim();


        /*--Loop item--*/
        //count total item & pass at new collection
        int totitem = 0; //untuk count total

        if (!TextUtils.isEmpty(title)) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("List");
            String key = reference.push().getKey();

            //insert list into db
            List list = new List();
            list.setListid(key);
            list.setTitle(title);
            list.setShopName(shopnamelocation);
            list.setTotalbudget(Float.parseFloat(totbudget));
            list.setTotalexpenses(Float.parseFloat("0.00"));
            list.setStatusList("In Progress");
            list.setUserId(currentUserId);
            list.setTimeAdded(new Timestamp(new Date()));
            try {
                list.setDatePlan(new Timestamp(new SimpleDateFormat("dd/MM/yyyy").parse(dateplan)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //invoke collectionReference
            collectionReference.add(list)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            //count addview column
                            String[] itemName = new String[0];

                            for (int i = 0; i < values.size(); i++) {
                                DatabaseReference referenceI = FirebaseDatabase.getInstance().getReference().child("Items");

                                String keyItem = referenceI.push().getKey();

                                Items item = new Items();
                                item.setItemid(keyItem);
                                item.setItemName(values.get(i));
                                item.setItemQty(Integer.valueOf(valuesQty.get(i)));
                                item.setStatus(0);
                                item.setListid(key);

                                collectionReferenceI.add(item)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                
                                                Intent intent = new Intent(AddListActivity.this, ReminderBroadcast.class);
                                                intent.putExtra("ListID", key);
                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(AddListActivity.this, 0, intent, 0);

                                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


                                                Timestamp remindDatePlan;
                                                long timeAddInMillis = 0;
                                                try {
                                                    remindDatePlan = new Timestamp(new SimpleDateFormat("dd/MM/yyyy").parse(dateplan));
                                                    timeAddInMillis = remindDatePlan.getSeconds()*1000;
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                                Toast.makeText(AddListActivity.this, "List has been added"+timeAddInMillis, Toast.LENGTH_SHORT).show();

                                                long twentyhoursInMillis = 72000000; // 1 seconds = 1000 Milis
                                                
                                                alarmManager.set(AlarmManager.RTC_WAKEUP,
                                                        timeAddInMillis + twentyhoursInMillis,
                                                        pendingIntent);

                                                Toast.makeText(AddListActivity.this, "List has been added"+(timeAddInMillis + twentyhoursInMillis), Toast.LENGTH_SHORT).show();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Log.d(TAG, "onFailure: " + e.getMessage());

                                            }
                                        });

                            }

                            startActivity(new Intent(AddListActivity.this, MainActivity.class)); // mcm mana nk bg dia show sekali je
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
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }

    }
}