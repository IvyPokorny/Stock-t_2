package com.stockt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private TextView editText;
    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText reenterPasswordText;
    private EditText phoneNumberText;
    private EditText addressText;
    private Button buttonSignUp;
    private Button buttonClose;
    private UserDatabaseHelper userDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        //Initialize elements of xml
        editText = findViewById(R.id.editTextText);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        reenterPasswordText = findViewById(R.id.reenterPasswordText);
        phoneNumberText = findViewById(R.id.phoneNumberText);
        //addressText = findViewById(R.id.addressText);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonClose = findViewById(R.id.buttonClose);

        userDatabaseHelper = new UserDatabaseHelper(this);

        //Set up listeners
        buttonSignUp.setOnClickListener(v -> signUp());
        buttonClose.setOnClickListener(v -> finish()); //Close the activity

    }

    private void signUp() {
        //Code for signing up
        String fullName = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String checkPass = reenterPasswordText.getText().toString();
        String phoneNumber = phoneNumberText.getText().toString();

        if (!fullName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !phoneNumber.isEmpty()) { //Checks forms are filled
            if (password.equals(checkPass)) { //Checks passwords match
                userDatabaseHelper.addUser(fullName, email, password, phoneNumber);
                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
                finish(); //Close sign-up activity after registration
            } else {
                //Toast for notifying user of fault
                Toast.makeText(this, "Please make sure the passwords match", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Toast for notifying user of fault
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}