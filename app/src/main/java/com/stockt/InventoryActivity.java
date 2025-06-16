package com.stockt;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.util.List;
import android.util.Log;


public class InventoryActivity extends AppCompatActivity {

    //Declare UI elements
    private Spinner spinnerCategory, spinnerValue;
    private Button buttonSearch, buttonAddItem, buttonHistory, buttonItems, buttonHome, buttonSettings;
    private TableLayout tableLayout, tableSortNav;

    private ItemDatabaseHelper itemDatabaseHelper;
    private InventoryHistoryDatabaseHelper inventoryHistoryHelper;

    //Example item views
    private TextView itemName, itemCount, itemPrice;
    private Button buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_screen);

        //Initialize elements
        spinnerCategory = findViewById(R.id.spinner3);
        spinnerValue = findViewById(R.id.spinner2);
        buttonSearch = findViewById(R.id.button6);
        buttonAddItem = findViewById(R.id.button2);
        buttonHistory = findViewById(R.id.buttonHistory);
        buttonItems = findViewById(R.id.button5);
        buttonHome = findViewById(R.id.button4);
        buttonSettings = findViewById(R.id.button3);

        //Initialize table layout
        tableLayout = findViewById(R.id.tableLayout);
        tableSortNav = findViewById(R.id.tableSortNav);

        itemDatabaseHelper = new ItemDatabaseHelper(this);
        inventoryHistoryHelper = new InventoryHistoryDatabaseHelper(this);

        //Starts the database
        populateItemTable();

        //Set onClickListeners
        buttonSearch.setOnClickListener(v -> searchItems());
        buttonAddItem.setOnClickListener(v -> addItem());
        buttonHistory.setOnClickListener(v -> openHistoryActivity());
        buttonItems.setOnClickListener(v -> openItemsActivity());
        buttonHome.setOnClickListener(v -> openHomeActivity());
        buttonSettings.setOnClickListener(v -> openSettingsActivity());
    }

    private void populateItemTable() {
        List<Item> itemList = itemDatabaseHelper.getAllItems();

        //Clear previous rows
        tableLayout.removeViewsInLayout(1, tableLayout.getChildCount() - 1); //Keep the header row

        //Loop through the items and add them to the table
        for (Item item : itemList) {
            TableRow row = new TableRow(this);
            TextView nameTextView = new TextView(this);
            EditText countEditText = new EditText(this);
            Button submitButton = new Button(this);
            Button deleteButton = new Button(this);

            row.setPadding(3, 3, 3, 3); //Set padding to match existing rows

            //Configure the item name TextView
            nameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1)); //Makes it have the parents layout params
            nameTextView.setGravity(Gravity.CENTER); //Centers text
            nameTextView.setBackgroundColor(getResources().getColor(R.color.nice_gray)); //Sets background color
            nameTextView.setText(item.getName()); //Sets Text
            nameTextView.setTextColor(getResources().getColor(R.color.black)); //Sets text color
            nameTextView.setTextSize(16); //Sets text size

            //Set OnClickListener to open SingleItemGraphActivity
            nameTextView.setOnClickListener(v -> {
                Intent intent = new Intent(this, SingleItemGraphActivity.class);
                intent.putExtra("ITEM_ID", item.getId()); //Pass the item ID
                startActivity(intent); //Start the activity
            });

            //Configure the countEditText
            countEditText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1));
            countEditText.setGravity(Gravity.CENTER);
            countEditText.setBackgroundColor(getResources().getColor(R.color.nice_gray));
            countEditText.setText(String.valueOf(item.getQuantity()));
            countEditText.setTextColor(getResources().getColor(R.color.black));
            countEditText.setTextSize(16);
            countEditText.setInputType(InputType.TYPE_CLASS_NUMBER); //Set input type to number
            //Eliminated change listener

            //Configure the submit Button
            submitButton.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1));
            submitButton.setBackgroundColor(getResources().getColor(R.color.nice_gray));
            submitButton.setText("Submit");
            submitButton.setEnabled(false); //Initially disabled

            //Add a listener to the count EditText
            countEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    //No action needed
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //Check if the new count is different from the item's current quantity
                    try {
                        int newQuantity = Integer.parseInt(s.toString());
                        if (newQuantity != item.getQuantity()) {
                            submitButton.setEnabled(true); //Enable the button
                            submitButton.setBackgroundColor(getResources().getColor(R.color.nice_teal)); //Change color to green
                        } else {
                            submitButton.setEnabled(false); //Disable the button
                            submitButton.setBackgroundColor(getResources().getColor(R.color.nice_gray)); //Reset color
                        }
                    } catch (NumberFormatException e) {
                        submitButton.setEnabled(false); //Disable the button if input is invalid
                        submitButton.setBackgroundColor(getResources().getColor(R.color.nice_gray)); //Reset color
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //No action needed
                }
            });

            //Add a listener to the submit Button
            submitButton.setOnClickListener(v -> showReasonDropdown(item, countEditText, submitButton));

            //Configure the delete Button
            deleteButton.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener(v -> deleteItem(item));

            //Add views to the row
            row.addView(nameTextView);
            row.addView(countEditText);
            row.addView(submitButton);
            row.addView(deleteButton);

            //Add the row to the table layout
            tableLayout.addView(row);
        }
    }

    //Method to show a dropdown for selecting the reason for inventory adjustment
    private void showReasonDropdown(Item item, EditText countEditText, Button submitButton) {
        //Create an AlertDialog with a dropdown
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Reason");

        String[] reasons = {"Sold", "Store-Use", "Diseased/Dead", "Other", "Received Shipment"};

        builder.setItems(reasons, (dialog, which) -> {
            String selectedReason = reasons[which];

            //Update the inventory count in the database
            try {
                int oldQuantity = item.getQuantity();
                int newQuantity = Integer.parseInt(countEditText.getText().toString());
                item.setQuantity(newQuantity); //Update item in the Java list
                itemDatabaseHelper.updateItemQuantity(item, true); //Update item in the SQLite database

                //Update Inventory History in the SQLite database
                inventoryHistoryHelper.addAdjustment(item.getId(), item.getName(), (newQuantity - oldQuantity), LocalDateTime.now(), selectedReason);

                //Log or process the selected reason
                Toast.makeText(this, "Reason: " + selectedReason, Toast.LENGTH_SHORT).show();

                //Reset the button state after submission
                submitButton.setEnabled(false);
                submitButton.setBackgroundColor(getResources().getColor(R.color.nice_gray));
            } catch (NumberFormatException e) {
                //Handle invalid input gracefully
                Toast.makeText(this, "Invalid quantity!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        //Show the dialog
        builder.create().show();
    }


    private void deleteItem(Item item) {
        //Logic to delete the item from the database and refresh the table
        Log.d("InventoryActivity", "Deleting item with ID: " + item.getId());
        itemDatabaseHelper.deleteItem(item, true); //Delete from database
        populateItemTable(); //Refresh the table
    }

    private void searchItems() {
        //Logic to search for items in the inventory
    }

    private void addItem() {
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);
    }

    private void openHistoryActivity() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    private void openItemsActivity() {
        //Logic to open the items list activity but we're already here
        recreate();
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
