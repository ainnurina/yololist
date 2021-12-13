package com.example.yololist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yololist.data.model.List;
import com.example.yololist.ui.ListRecyclerAdapter;
import com.example.yololist.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import util.YololistApi;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //variables
    DrawerLayout drawerlayout;
    NavigationView navigationView;
    Button addlistbutton;
    Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;

    private java.util.List<List> allList;
    private RecyclerView recyclerView;
    private ListRecyclerAdapter listRecyclerAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("List");
    private TextView noListEntry;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            setContentView(R.layout.activity_main);


        } else {

            //Toast.makeText(MainActivity.this, "Not log in yet",  Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        addlistbutton = (Button)findViewById(R.id.button_add_list2);

        addlistbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PostYololistActivity.class);
                startActivity(intent);
            }
        });

        /*----------Hooks------------*/
        drawerlayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar3);

        /*----------Tool Bar-----------*/
        setSupportActionBar(toolbar);

        /*---------Navigation Drawer Menu-----*/

        //Hide or show items

        Menu menu = navigationView.getMenu();
        //menu.findItem(R.id.nav_logout).setVisible(false);
        //menu.findItem(R.id.nav_profile).setVisible(false);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        noListEntry = findViewById(R.id.list_no_data);
        allList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                listRecyclerAdapter.deleteItem(viewHolder.getAdapterPosition());

            }
        }).attachToRecyclerView(recyclerView);


        listRecyclerAdapter.setOnItemClickListener(new ListRecyclerAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                List note = documentSnapshot.toObject(List.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                Toast.makeText(MainActivity.this,
                        "Position: "+position+" ID: "+id, Toast.LENGTH_SHORT).show();

            }
        });


        //menu
        if (user != null && firebaseAuth != null)   {
            menu.findItem(R.id.nav_login).setVisible(false);
            menu.findItem(R.id.nav_signup).setVisible(false);
        }


        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerlayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

    }

    @Override
    public void onBackPressed() {
        if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
            drawerlayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                break;

            case R.id.nav_login:
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_profile:
                Intent intent2 = new Intent(MainActivity.this, userprofile.class);
                startActivity(intent2);
                break;
                //Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                //break;

            case R.id.nav_signup:
                Intent intent3 = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent3);
                break;

            case R.id.nav_archive:
                Intent intent4 = new  Intent(MainActivity.this, ArchiveActivity.class);
                startActivity(intent4);
                break;

            case R.id.nav_logout:
                firebaseAuth = FirebaseAuth.getInstance();

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null && firebaseAuth != null)   {
                    firebaseAuth.signOut();

                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    //finish;
                }
                break;
        }
        drawerlayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //showList = findViewById(R.id.button);
    //nameText = findViewById(R.id.textView);

    //showList.setOnClickListener(new View.OnClickListener() {
    //@Override
    //public void onClick(View view) {
    //    nameText.setText("Welcome to Shopping List!");
    //}
    //});


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
                            for (QueryDocumentSnapshot lists : queryDocumentSnapshots)  {
                                List list = lists.toObject(List.class);
                                allList.add(list);
                            }
                            //Invoke recyler view
                            listRecyclerAdapter = new ListRecyclerAdapter(MainActivity.this, allList);
                            recyclerView.setAdapter(listRecyclerAdapter);
                            listRecyclerAdapter.notifyDataSetChanged();
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

    //@Override
   // public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

   // }
}