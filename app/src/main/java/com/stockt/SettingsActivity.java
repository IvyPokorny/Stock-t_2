package com.stockt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import java.time.LocalDateTime;

public class SettingsActivity extends AppCompatActivity {

    private ItemDatabaseHelper itemDatabaseHelper;
    private InventoryHistoryDatabaseHelper inventoryHistoryHelper;
    private Button resetDatabaseButton, fillDatabaseButton;
    private Button buttonItems, buttonHome, buttonSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);

        itemDatabaseHelper = new ItemDatabaseHelper(this);
        inventoryHistoryHelper = new InventoryHistoryDatabaseHelper(this);
        resetDatabaseButton = findViewById(R.id.resetDatabaseButton);
        fillDatabaseButton = findViewById(R.id.fillDatabaseButton);


        //Initialize bottom buttons
        buttonItems = findViewById(R.id.button5);
        buttonHome = findViewById(R.id.button4);
        buttonSettings = findViewById(R.id.button3);

        //Set up bottom button listeners
        buttonItems.setOnClickListener(v -> openItemsActivity());
        buttonHome.setOnClickListener(v -> openHomeActivity());
        buttonSettings.setOnClickListener(v -> openSettingsActivity());

        resetDatabaseButton.setOnClickListener(v -> showResetDatabaseConfirmation());
        fillDatabaseButton.setOnClickListener(v -> fillDatabase());

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
        itemDatabaseHelper.close(); //Close the database if it's open
        itemDatabaseHelper = new ItemDatabaseHelper(this); //Re-initialize to clear data
        itemDatabaseHelper.onUpgrade(itemDatabaseHelper.getWritableDatabase(), 1, 2);

        inventoryHistoryHelper.close(); //Close the database if it's open
        inventoryHistoryHelper = new InventoryHistoryDatabaseHelper(this); //Re-initialize to clear data
        inventoryHistoryHelper.onUpgrade(inventoryHistoryHelper.getWritableDatabase(), 1, 2);

        //Reset the history
        HistoryManager.getInstance().resetHistory();

        //Show a confirmation message
        Toast.makeText(this, "Database has been reset.", Toast.LENGTH_SHORT).show();
    }

    private void fillDatabase(){
        itemDatabaseHelper.addItem("Roses", 40, 1.50, "Flowers", true);
        itemDatabaseHelper.addItem("Tulips", 30, 1.20, "Flowers", true);
        itemDatabaseHelper.addItem("Lilies", 60, 2.00, "Flowers", true);
        itemDatabaseHelper.addItem("Daisies", 25, 0.80, "Flowers", true);
        itemDatabaseHelper.addItem("Sunflowers", 10, 1.00, "Flowers", true);
        itemDatabaseHelper.addItem("Apples", 50, 0.50, "Fruits", true);
        itemDatabaseHelper.addItem("Bananas", 20, 0.30, "Fruits", true);
        itemDatabaseHelper.addItem("Carrots", 15, 0.60, "Vegetables", true);
        itemDatabaseHelper.addItem("Potatoes", 80, 0.40, "Vegetables", true);

        inventoryHistoryHelper.addAdjustment(1, "Tulips", -4, LocalDateTime.parse("2025-05-01T12:13:23"), "Sold");
        inventoryHistoryHelper.addAdjustment(1, "Tulips", -6, LocalDateTime.parse("2025-05-01T14:13:23"), "Sold");
        inventoryHistoryHelper.addAdjustment(1, "Tulips", -9, LocalDateTime.parse("2025-05-01T16:13:23"), "Sold");
        inventoryHistoryHelper.addAdjustment(1, "Tulips", -9, LocalDateTime.parse("2025-05-05T09:13:23"), "Sold");
        inventoryHistoryHelper.addAdjustment(1, "Tulips", -3, LocalDateTime.parse("2025-05-05T11:13:23"), "Sold");
        inventoryHistoryHelper.addAdjustment(1, "Tulips", -4, LocalDateTime.parse("2025-05-05T12:13:23"), "Sold");
        inventoryHistoryHelper.addAdjustment(1, "Tulips", -5, LocalDateTime.parse("2025-05-05T13:13:23"), "Sold");
        inventoryHistoryHelper.addAdjustment(1, "Tulips", -2, LocalDateTime.parse("2025-05-05T14:13:23"), "Sold");
        inventoryHistoryHelper.addAdjustment(1, "Tulips", -1, LocalDateTime.parse("2025-05-05T15:13:23"), "Sold");
        inventoryHistoryHelper.addAdjustment(1, "Tulips", -5, LocalDateTime.parse("2025-05-09T09:13:23"), "Sold");
        inventoryHistoryHelper.addAdjustment(1, "Tulips", -2, LocalDateTime.parse("2025-05-09T10:13:23"), "Sold");
        inventoryHistoryHelper.addAdjustment(1, "Tulips", -1, LocalDateTime.parse("2025-05-15T12:13:23"), "Sold");
        inventoryHistoryHelper.addAdjustment(1, "Tulips", -4, LocalDateTime.parse("2025-05-15T13:13:23"), "Sold");
        inventoryHistoryHelper.addAdjustment(1, "Tulips", -5, LocalDateTime.parse("2025-05-15T13:43:23"), "Sold");


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