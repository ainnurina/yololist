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
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.yololist.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //variables
    DrawerLayout drawerlayout;
    NavigationView navigationView;
    Button addlistbutton;
    Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;


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
        menu.findItem(R.id.nav_profile).setVisible(false);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

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
}