package com.example.chorerewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

//Allows the user to sign in with their email address.
public class SignInEmailActivity extends AppCompatActivity {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;

    EditText etEmailSignIn;
    EditText etPasswordSignIn;
    Button btnSignInEmail;

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_email);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        etEmailSignIn = (EditText) findViewById(R.id.tvRewardToRedeem);
        etPasswordSignIn = (EditText) findViewById(R.id.etPasswordSignIn);
        btnSignInEmail = (Button) findViewById(R.id.btnSignInSubmit);

        btnSignInEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmailSignIn.getText().toString();
                password = etPasswordSignIn.getText().toString();

                Log.d("email", email);
                Log.d("password", password);

                mFirebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignInEmailActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("", "signInWithEmail:success");
                                    mFirebaseUser = mFirebaseAuth.getCurrentUser();
                                    db.collection("users").document(mFirebaseUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                                    startActivity(new Intent(SignInEmailActivity.this, MainActivity.class));
                                                } else {
                                                    Log.d("TAG", "No such document");
                                                    startActivity(new Intent(SignInEmailActivity.this, AccountCreation.class));
                                                }
                                                finish();
                                            } else {
                                                Log.d("TAG", "get failed with ", task.getException());
                                            }
                                        }
                                    });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignInEmailActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignInEmailActivity.this, MainActivity.class));
                                }
                            }
                        });
            }
        });
    }
}