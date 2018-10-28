package com.fusion.kim.alc30;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private Button mSignUpBtn;
    private EditText mUserNameInput, mEmailInput, mPasswordInput, mConfPasswordInput;

    private ProgressDialog mSignupPD;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setTitle("Account Creation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserRef.keepSynced(true);

        mUserNameInput = findViewById(R.id.input_register_name);
        mEmailInput = findViewById(R.id.input_register_email);
        mPasswordInput = findViewById(R.id.input_register_password);
        mConfPasswordInput = findViewById(R.id.input_confirm_password);

        mSignUpBtn = findViewById(R.id.btn_register);

        mSignupPD = new ProgressDialog(this);
        mSignupPD.setTitle("Creating User");
        mSignupPD.setMessage("Processing...");
        mSignupPD.setCancelable(false);
        mSignupPD.setIndeterminate(true);



        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String userName = mUserNameInput.getText().toString().trim();
                final String email = mEmailInput.getText().toString().trim();
                final String pass = mPasswordInput.getText().toString().trim();
                final String confPass = mPasswordInput.getText().toString().trim();

                if (TextUtils.isEmpty(userName)){

                    mUserNameInput.setError("Please Enter a Username");
                    return;

                }

                if (TextUtils.isEmpty(email)){

                    mEmailInput.setError("Please Enter an Email Address");
                    return;

                }

                if (TextUtils.isEmpty(pass)){

                    mPasswordInput.setError("Please Enter a Password");
                    return;

                }

                if (TextUtils.isEmpty(confPass)){

                    mConfPasswordInput.setError("Please Confirm your Password");
                    return;

                }

                if (!pass.equals(confPass)){

                    mConfPasswordInput.setError("Passwords do not match!");
                    return;

                }

                if (!TextUtils.isEmpty(email)  && !TextUtils.isEmpty(pass) &&
                        !TextUtils.isEmpty(confPass) && pass.equals(confPass)){

                    mSignupPD.show();

                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                Map userMap = new HashMap();
                                userMap.put("userName", userName);
                                userMap.put("emailAddress", email);
                                userMap.put("password", pass);

                                mUserRef.child(task.getResult().getUser().getUid()).
                                        setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){

                                            mSignupPD.dismiss();

                                            Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(mainIntent);
                                            finish();

                                        }

                                    }
                                });

                            } else {

                                mSignupPD.dismiss();

                                Toast.makeText(SignUpActivity.this, "Failed to create account. " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                            }

                        }
                    });

                }

            }
        });
    }


}