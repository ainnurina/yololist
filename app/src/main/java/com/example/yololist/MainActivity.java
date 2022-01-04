package com.example.yololist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yololist.data.model.List;
import com.example.yololist.ui.ListRecyclerAdapter;
import com.example.yololist.ui.RecyclerViewInterface;
import com.example.yololist.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

import util.YololistApi;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface, NavigationView.OnNavigationItemSelectedListener {

    //variables
    DrawerLayout drawerlayout;
    NavigationView navigationView;
    Button addlistbutton;
    Toolbar toolbar;
    SearchView searchText;
    ArrayList<List> arrayList;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;

    private java.util.List<List> allList;
    private RecyclerView recyclerView;
    private ListRecyclerAdapter listRecyclerAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("List");
    private TextView noListEntry;
    private RecyclerViewInterface recyclerViewInterface = this::onItemClick;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;


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
        allList = new ArrayList<>();

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

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_archive).setVisible(false);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        noListEntry = findViewById(R.id.list_no_data);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*------Edit & remove----
        List list_edit = (List)getIntent().getSerializableExtra("EDIT");
        if (list_edit != null)  {
            btn.setText("UPDATE");
            edit_name.setText(list_edit.getName());
            edit_position.setText(list_edit.getPosition());
            btn_open.setVisibility(View.GONE);

        } else  {

        }
        --*/



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
                            listRecyclerAdapter = new ListRecyclerAdapter(getApplicationContext(), allList, recyclerViewInterface);
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

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, YoloListActivity.class);

        intent.putExtra("Title", allList.get(position).getTitle());
        intent.putExtra("ListID", allList.get(position).getListid());
        intent.putExtra("ItemQty", allList.get(position).getTotitem());
        intent.putExtra("DateAdded", allList.get(position).getTimeAdded());

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        searchText = (SearchView) findViewById(R.id.searchTitle);

        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listRecyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}