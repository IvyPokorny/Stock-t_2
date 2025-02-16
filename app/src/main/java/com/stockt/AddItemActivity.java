package com.stockt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AddItemActivity extends AppCompatActivity {

    // Declare UI elements
    private TextView textItemName, textItemQuantity, textItemValue, textItemCategory;
    private EditText editTextItemName, editTextItemQuantity, editTextItemValue, editTextItemCategory;
    private Spinner spinnerCategory, spinnerSort;
    private Button buttonSave, buttonSearch, buttonItems, buttonHome, buttonSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item_screen);

        // Initialize UI elements
        textItemName = findViewById(R.id.textItemName);
        textItemQuantity = findViewById(R.id.textItemQuantity);
        textItemValue = findViewById(R.id.textItemValue);
        textItemCategory = findViewById(R.id.textItemCategory);

        editTextItemName = findViewById(R.id.editTextItemName);
        editTextItemQuantity = findViewById(R.id.editTextItemQuantity);
        editTextItemValue = findViewById(R.id.editTextItemValue);
        editTextItemCategory = findViewById(R.id.editTextItemCategory);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerSort = findViewById(R.id.spinnerSort);

        buttonSave = findViewById(R.id.buttonSave);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonItems = findViewById(R.id.buttonItems);
        buttonHome = findViewById(R.id.buttonHome);
        buttonSettings = findViewById(R.id.buttonSettings);

        // Set onClickListener for the buttons
        buttonSave.setOnClickListener(v -> saveItem());
        buttonSearch.setOnClickListener(v -> searchItems());
        buttonItems.setOnClickListener(v -> openItemsActivity());
        buttonHome.setOnClickListener(v -> openHomeActivity());
        buttonSettings.setOnClickListener(v -> openSettingsActivity());

    }

    private void saveItem() {
        String itemName = editTextItemName.getText().toString();
        String itemQuantity = editTextItemQuantity.getText().toString();
        String itemValue = editTextItemValue.getText().toString();
        String itemCategory = editTextItemCategory.getText().toString();

        //Add logic to save these details to a database
    }

    private void searchItems() {
        // Logic to perform a search operation
    }

    private void openItemsActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void openSettingsActivity() {
        // Logic to open the settings activity
    }
}