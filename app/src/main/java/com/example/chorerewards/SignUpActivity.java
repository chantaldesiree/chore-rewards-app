package com.example.chorerewards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

//Allows the user to sign up for an account.
public class SignUpActivity extends AppCompatActivity {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth mFirebaseAuth;

    EditText etEmail;
    EditText etPassword;
    EditText etPasswordConfirm;
    Button btnSignUpSubmit;

    String email;
    String password;
    String passwordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPasswordSignIn);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        btnSignUpSubmit = findViewById(R.id.btnSignUpSubmit);

        btnSignUpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                passwordConfirm = etPasswordConfirm.getText().toString();

                if (inputsValid())
                {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, (OnCompleteListener<AuthResult>) task -> {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "createUserWithEmail:success");
                                    signInEmailActivity();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    signInActivity();
                                }
                            });
                }

            }
        });
    }

    public void signInEmailActivity()
    {
        Intent intent = new Intent(SignUpActivity.this, SignInEmailActivity.class);
        startActivity(intent);
        finish();
    }

    public void signInActivity()
    {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean inputsValid()
    {
        Log.d("email", email);
        Log.d("password", password);
        Log.d("passwordConfirm", passwordConfirm);
        Log.d("equal", String.valueOf(passwordConfirm == password));

        if (email.length() > 0 && password.length() > 0 && passwordConfirm.length() > 0)
        {
            if (password.equals(passwordConfirm))
            {
                return true;
            }
        }
        return false;
    }
}