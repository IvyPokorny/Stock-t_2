package com.stockt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class InventoryActivity extends AppCompatActivity {

    // Declare UI elements
    private Spinner spinnerCategory, spinnerValue;
    private Button buttonSearch, buttonAddItem, buttonItems, buttonHome, buttonSettings;
    private TableLayout tableLayout;

    // Example item views
    private TextView itemName, itemCount, itemPrice;
    private Button buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_screen); // Ensure this matches your XML filename

        // Initialize elements
        spinnerCategory = findViewById(R.id.spinner3);
        spinnerValue = findViewById(R.id.spinner2);
        buttonSearch = findViewById(R.id.button6);
        buttonAddItem = findViewById(R.id.button2);

        // Initialize table layout
        tableLayout = findViewById(R.id.tableItems); // Ensure your XML has this ID for the TableLayout

        // Initialize item views
        itemName = findViewById(R.id.itemName1);
        itemCount = findViewById(R.id.itemCount1);
        itemPrice = findViewById(R.id.itemPrice1);
        buttonDelete = findViewById(R.id.button);

        buttonItems = findViewById(R.id.button5);
        buttonHome = findViewById(R.id.button4);
        buttonSettings = findViewById(R.id.button3);

        // Set onClickListeners
        buttonSearch.setOnClickListener(v -> searchItems());
        buttonAddItem.setOnClickListener(v -> addItem());
        buttonDelete.setOnClickListener(v -> deleteItem());
        buttonItems.setOnClickListener(v -> openItemsActivity());
        buttonHome.setOnClickListener(v -> openHomeActivity());
        buttonSettings.setOnClickListener(v -> openSettingsActivity());
    }

    private void searchItems() {
        // Logic to search for items in the inventory
    }

    private void addItem() {
        // Logic to add a new item to the inventory
    }

    private void deleteItem() {
        // Logic to delete the selected item from the inventory
        // For example, remove the item from the UI or database
    }

    private void openItemsActivity() {
        // Logic to open the items list activity
    }

    private void openHomeActivity() {
        // Logic to open the home activity
    }

    private void openSettingsActivity() {
        // Logic to open the settings activity
    }
}
