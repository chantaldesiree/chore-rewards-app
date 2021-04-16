package com.example.chorerewards;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

//Allows a user to sign in using google or their email address, or sign up for an account.
public class SignInActivity extends AppCompatActivity  implements View.OnClickListener {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private Button mSignInButton;

    private GoogleSignInClient mSignInClient;

    Button signInGoogle;
    Button signInEmail;
    Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);

        mFirebaseAuth = FirebaseAuth.getInstance();

        signInGoogle = (Button) findViewById(R.id.sign_in_google_button);
        signInGoogle.setOnClickListener(this);
        signInEmail = (Button) findViewById(R.id.sign_in_email_button);
        signInEmail.setOnClickListener(this);
        signUp = (Button) findViewById( R.id.sign_up_button);
        signUp.setOnClickListener(this);

    }

    private void signIn() {
        Intent signInIntent = mSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInEmail()
    {
        Intent intent = new Intent(SignInActivity.this, SignInEmailActivity.class);
        startActivity(intent);
        finish();
    }

    private void signUpEmail()
    {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_google_button:
                signIn();
                break;
            case R.id.sign_in_email_button:
                signInEmail();
                break;
            case R.id.sign_up_button:
                signUpEmail();
                break;
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());

                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignInActivity.this, "Signin Successful!",
                                    Toast.LENGTH_SHORT).show();

                            mFirebaseUser = mFirebaseAuth.getCurrentUser();

                            db.collection("users").document(mFirebaseUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                        } else {
                                            Log.d(TAG, "No such document");
                                            startActivity(new Intent(SignInActivity.this, AccountCreation.class));
                                        }
                                        finish();
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent in signIn()
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
}