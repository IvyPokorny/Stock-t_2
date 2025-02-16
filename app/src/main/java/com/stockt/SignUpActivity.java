package com.stockt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText editText;
    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText reenterPasswordText;
    private EditText phoneNumberText;
    private EditText addressText;
    private Button buttonSignUp;
    private Button buttonClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        // Initialize elements of xml
        editText = findViewById(R.id.editTextText);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        reenterPasswordText = findViewById(R.id.reenterPasswordText);
        phoneNumberText = findViewById(R.id.phoneNumberText);
        addressText = findViewById(R.id.addressText);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonClose = findViewById(R.id.buttonClose);

        // Set up listeners
        buttonSignUp.setOnClickListener(v -> signUp());
        buttonClose.setOnClickListener(v -> finish()); // Close the activity

    }

    private void signUp() {
        // Code for signing up goes here
    }
}