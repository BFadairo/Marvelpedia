package com.example.android.marvelpedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private final String LOG_TAG = LoginActivity.class.getSimpleName();
    // UI references.
    @BindView(R.id.email)
    AutoCompleteTextView mEmailView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.login_progress)
    ProgressBar mProgressView;
    @BindView(R.id.email_sign_in_button)
    Button mSignIn;
    @BindView(R.id.email_register_button)
    Button mRegister;
    private FirebaseAuth mAuth;
    private String userEmail, password, requiredError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);
        initializeRegisterButton();
        initializeSignInButton();
        retrieveLoginRelatedStrings();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void showProgressBar() {
        mProgressView.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mProgressView.setVisibility(View.GONE);
    }

    private void createNewUser(String email, String password) {
        Log.d(LOG_TAG, "CreateAccount:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressBar();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "Create User: Success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(LOG_TAG, "Create user failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        hideProgressBar();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailView.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(requiredError);
            valid = false;
        } else {
            mEmailView.setError(null);
        }

        String password = mPasswordView.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(requiredError);
            valid = false;
        } else {
            mPasswordView.setError(null);
        }

        return valid;
    }

    private void signIn(String email, String password) {
        Log.d(LOG_TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressBar();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        hideProgressBar();
                    }
                });
    }

    private void getEmailAndPassword() {
        userEmail = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();
    }

    private void initializeRegisterButton() {
        mRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getEmailAndPassword();
                createNewUser(userEmail, password);
            }
        });
    }

    private void initializeSignInButton() {
        mSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getEmailAndPassword();
                signIn(userEmail, password);
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent loginSuccess = new Intent(this, MainActivity.class);
            startActivity(loginSuccess);
        }
    }

    private void retrieveLoginRelatedStrings() {
        requiredError = getResources().getString(R.string.error_field_required);
    }
}

