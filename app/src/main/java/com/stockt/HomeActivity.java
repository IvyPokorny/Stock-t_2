package com.stockt;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private Spinner categoryDropdown;
    private EditText searchBar;
    private TextView welcomeMessage;
    private TextView totalItems;
    private TextView stockFlowSummary;
    private TextView lowStock;
    private Button buttonLowStock;
    private FloatingActionButton floatingActionButton;
    private BottomNavigationView bottomNavigationView;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1;
    private static boolean hasPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        //Initialize views
        categoryDropdown = findViewById(R.id.categoryDropdown);
        searchBar = findViewById(R.id.searchBar);
        welcomeMessage = findViewById(R.id.welcomeMessage);
        totalItems = findViewById(R.id.totalItems);
        stockFlowSummary = findViewById(R.id.stockFlowSummary);
        lowStock = findViewById(R.id.lowStock);
        buttonLowStock = findViewById(R.id.buttonLowStock);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Check permission status from SharedPreferences and saves it for use
        SharedPreferences sharedPreferences = getSharedPreferences("StockPrefs", MODE_PRIVATE);
        hasPermission = sharedPreferences.getBoolean("SMSPermission", false);

        //Set up listeners or any additional setup here
        floatingActionButton.setOnClickListener(v -> actionButton());
        buttonLowStock.setOnClickListener(v -> enableLowStockSMS());

    }

    private void actionButton(){
        Intent intent = new Intent(this, InventoryActivity.class);
        startActivity(intent);
    }

    private void enableLowStockSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            //Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        } else {
            //Permission granted, proceed to send SMS
            sendSms("15551234567", "You've given permission to receive texts");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission granted, proceed to send SMS
                //Hard-coded for the phone number of the emulated android
                sendSms("15551234567", "You've given permission to receive texts");
            } else {
                //Permission denied, inform the user that SMS functionality is not available
                permissionSMSDenied();
            }
        }
    }

        //Old version which used Toast for quick verification that it works
//    private void sendSMS() {
//        //Example notification
//        Toast.makeText(this, "SMS permission given", Toast.LENGTH_SHORT).show();
//    }

    public static void sendSms(String phoneNumber, String message) {
        if (hasPermission) {
            SmsManager smsManager = SmsManager.getDefault();
            //Send the message
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            //Log the message
            Log.d("InventoryActivity", "SMS sent to " + phoneNumber + ": " + message);
        }
    }

    private void permissionSMSDenied() {
        //Example notification for verification
        Toast.makeText(this, "NO SMS permission given", Toast.LENGTH_SHORT).show();
    }
}