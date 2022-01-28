//LOGIN activity

package com.example.ToListApp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ToListApp.MainActivity;
import com.example.ToListApp.R;
import com.example.ToListApp.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import util.YololistApi;

import static android.content.ContentValues.TAG;


public class LoginActivity<mDatabaseReference> extends AppCompatActivity {

    private Button loginButton;
    private Button createAcctButton;
    private AutoCompleteTextView emailAddress;
    private EditText passwordText;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseReference;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        emailAddress = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);


        loginButton = findViewById(R.id.email_sign_in_button);
        createAcctButton = findViewById(R.id.create_acct_button);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        createAcctButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email2 = emailAddress.getText().toString().trim();
                String password2 = passwordText.getText().toString().trim();
                if (email2.isEmpty()) {
                    emailAddress.setError("Email is empty");
                    emailAddress.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email2).matches()) {
                    emailAddress.setError("Enter the valid email");
                    emailAddress.requestFocus();
                    return;
                }
                if (password2.isEmpty()) {
                    passwordText.setError("Password is empty");
                    passwordText.requestFocus();
                    return;
                }
                if (password2.length() < 6) {
                    passwordText.setError("Length of password is more than 6");
                    passwordText.requestFocus();
                    return;
                }

                loginEmailPasswordUser(email2, password2);

            }

            private void loginEmailPasswordUser(String email, String pwd) {
                if (!TextUtils.isEmpty(email)
                        && !TextUtils.isEmpty(pwd)) {

                    firebaseAuth.signInWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (!task.isSuccessful()) {

                                        Log.e(TAG, "Sign-in Failed: " + task.getException().getMessage());
                                        // Or if you don't use Log:
                                        // System.out.println("Sign-in Failed: " + task.getException().getMessage());
                                        Toast.makeText(LoginActivity.this, "Error Login", Toast.LENGTH_LONG).show();

                                    } else {

                                        CheckUserExists();

                                    }


                                }
                            });

                }
            }
        });
    }


    private void CheckUserExists() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            throw new AssertionError();
        }
        String currentUserId = user.getUid();

        collectionReference
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {

                        if (e != null)  {

                        }
                        assert queryDocumentSnapshots != null;
                        if (!queryDocumentSnapshots.isEmpty())  {

                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)   {

                                YololistApi yololistApi = YololistApi.getInstance();
                                yololistApi.setUsername(snapshot.getString("username"));
                                yololistApi.setUserId(snapshot.getString("userId"));


                                //Go to ListActivity
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();

                            }


                        }

                    }
                });
    }
}