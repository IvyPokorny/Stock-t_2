package com.stockt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private ItemDatabaseHelper itemDatabaseHelper;
    private Button resetDatabaseButton;
    private Button buttonItems, buttonHome, buttonSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);

        itemDatabaseHelper = new ItemDatabaseHelper(this);
        resetDatabaseButton = findViewById(R.id.resetDatabaseButton);

        // Initialize bottom buttons
        buttonItems = findViewById(R.id.button5);
        buttonHome = findViewById(R.id.button4);
        buttonSettings = findViewById(R.id.button3);

        // Set up bottom button listeners
        buttonItems.setOnClickListener(v -> openItemsActivity());
        buttonHome.setOnClickListener(v -> openHomeActivity());
        buttonSettings.setOnClickListener(v -> openSettingsActivity());

        resetDatabaseButton.setOnClickListener(v -> showResetDatabaseConfirmation());

    }

    private void showResetDatabaseConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Database")
                .setMessage("Are you sure you want to reset the database? This action cannot be undone.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        resetDatabase();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void resetDatabase() {
        itemDatabaseHelper.close(); // Close the database if it's open
        itemDatabaseHelper = new ItemDatabaseHelper(this); // Re-initialize to clear data
        itemDatabaseHelper.onUpgrade(itemDatabaseHelper.getWritableDatabase(), 1, 2);
        // You can also recreate the database by calling a method to clear items if necessary
        // Show a confirmation message
        Toast.makeText(this, "Database has been reset.", Toast.LENGTH_SHORT).show();
    }

    private void openItemsActivity() {
        Intent intent = new Intent(this, InventoryActivity.class);
        startActivity(intent);
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void openSettingsActivity() {
        recreate();
    }
}