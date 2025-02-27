package com.stockt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.telephony.SmsManager;
import android.util.Log;

public class MainActivityy extends AppCompatActivity {

    private TextView editTextTitle;
    private EditText usernameText;
    private EditText passwordText;
    private Button buttonLogin;
    private Button buttonClose;
    private Button buttonSignUp;
    private UserDatabaseHelper userDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Enable edge-to-edge support
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        //        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        //        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        //Adds system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userDatabaseHelper = new UserDatabaseHelper(this);

        //Initialize elements
        editTextTitle = findViewById(R.id.editTextTitle);
        usernameText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonClose = findViewById(R.id.buttonClose);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        //Set up button listeners
        buttonLogin.setOnClickListener(v -> login());
        buttonClose.setOnClickListener(v -> finish()); //Close the app
        buttonSignUp.setOnClickListener(v -> openSignUpPage());
    }

    private void login() {
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        //Handle login logic here
        if (userDatabaseHelper.checkUser(username, password)) {
            //Proceed to home screen
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            //Handle invalid login, simple Toast for verification
            Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSignUpPage() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}