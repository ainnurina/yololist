package com.example.yololist.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yololist.MainActivity;
import com.example.yololist.PostYololistActivity;
import com.example.yololist.R;
import com.example.yololist.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import util.YololistApi;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private Button createAcctButton;
    private AutoCompleteTextView emailAddress;
    private EditText password;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        emailAddress = findViewById(R.id.email);
        password = findViewById(R.id.password);


        loginButton = findViewById(R.id.email_sign_in_button);
        createAcctButton = findViewById(R.id.create_acct_button);

        createAcctButton.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEmailPasswordUser(emailAddress.getText().toString().trim(),
                        password.getText().toString().trim());
            }

            private void loginEmailPasswordUser(String email, String pwd) {
                if (!TextUtils.isEmpty(email)
                        && !TextUtils.isEmpty(pwd)) {

                    firebaseAuth.signInWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    assert user != null;
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
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                }else {
                    Toast.makeText(LoginActivity.this,
                            "Please enter email and password",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience

        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}