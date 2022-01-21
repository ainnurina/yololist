package com.example.ToList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ToList.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.YololistApi;

public class RegisterActivity extends AppCompatActivity {
    private Button createAcctButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private EditText userNameEditText;
    private EditText confirmpasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        createAcctButton = findViewById(R.id.create_acct_button);
        progressBar = findViewById(R.id.create_acct_progress);
        emailEditText = findViewById(R.id.email_account);
        passwordEditText = findViewById(R.id.password_account);
        userNameEditText = findViewById(R.id.profileEmail);
        confirmpasswordEditText = findViewById(R.id.confirmpassword);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    /* user is already loggedin */
                } else {
                    /* no user yet... */

                }

                createAcctButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString().trim()).matches())
                        {
                            emailEditText.setError("Enter the valid email address");
                            emailEditText.requestFocus();
                            return;
                        }

                        if (passwordEditText.getText().toString().trim().length()<6)    {
                            passwordEditText.setError("Length of the password should be more than 6");
                            passwordEditText.requestFocus();
                            return;
                        }

                        if (!(passwordEditText.getText().toString().trim().equals(confirmpasswordEditText.getText().toString().trim()))) {
                            passwordEditText.setError("Password and confirm password not equivalent");
                            passwordEditText.requestFocus();
                            return;

                        }

                        if  (!TextUtils.isEmpty(emailEditText.getText().toString())
                                && !TextUtils.isEmpty(passwordEditText.getText().toString())
                                && !TextUtils.isEmpty(userNameEditText.getText().toString())) {

                            String email = emailEditText.getText().toString().trim();
                            String password = passwordEditText.getText().toString().trim();
                            String username = userNameEditText.getText().toString().trim();
                            createUserEmailAccount(email, password, username);

                        }else {
                            Toast.makeText(RegisterActivity.this,
                                    "Empty Fields Not Allowed",
                                    Toast.LENGTH_LONG)
                                    .show();
                            return;
                        }



                    }
                });

            }

            private void createUserEmailAccount(String email, String password, String username) {
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)) {


                    //progressBar.setVisibility(View.VISIBLE);

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //we take user to AddYololistActivity

                                        currentUser = firebaseAuth.getCurrentUser();
                                        String currentUserId = currentUser.getUid();

                                        //Create a user Map so we can create a user in the User Collection
                                        Map<String, String> userObj = new HashMap<>();
                                        userObj.put("userId", currentUserId);
                                        userObj.put("username", username);
                                        userObj.put("email", email);
                                        //userObj.put("password", password)

                                        //save ato our firestore database
                                        collectionReference.add(userObj)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        documentReference.get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if (Objects.requireNonNull(task.getResult()).exists()){
                                                                            //progressBar.setVisibility(View.INVISIBLE);
                                                                            String name = task.getResult()
                                                                                    .getString("username");

                                                                            String mail = task.getResult().getString("email");


                                                                            YololistApi yololistApi = YololistApi.getInstance(); //Global API
                                                                            yololistApi.setUserId(currentUserId);
                                                                            yololistApi.setUsername(name);
                                                                            yololistApi.setEmail(mail);

                                                                            Toast.makeText(RegisterActivity.this, "Successful Registered", Toast.LENGTH_SHORT).show();

                                                                            Intent intent = new Intent(RegisterActivity.this,
                                                                                    LoginActivity.class);
                                                                            intent.putExtra("username", name);
                                                                            intent.putExtra("userId", currentUserId);
                                                                            startActivity(intent);

                                                                        }else{
                                                                            //progressBar.setVisibility(View.INVISIBLE);

                                                                        }

                                                                    }
                                                                });

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });

                                    } else {
                                        //something went wrong
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                } else {

                }
            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

    }

}