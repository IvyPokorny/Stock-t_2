package com.stockt;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import android.util.Log;


public class InventoryActivity extends AppCompatActivity {

    //Declare UI elements
    private Spinner spinnerCategory, spinnerValue;
    private Button buttonSearch, buttonAddItem, buttonHistory, buttonItems, buttonHome, buttonSettings;
    private TableLayout tableLayout, tableSortNav;

    private ItemDatabaseHelper itemDatabaseHelper;

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
            TextView priceTextView = new TextView(this);
            Button deleteButton = new Button(this);

            row.setPadding(3, 3, 3, 3); //Set padding to match existing rows

            nameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1)); //Makes it have the parents layout params
            nameTextView.setGravity(Gravity.CENTER); //Centers text
            nameTextView.setBackgroundColor(getResources().getColor(R.color.nice_gray)); //Sets background color
            nameTextView.setText(item.getName()); //Sets Text
            nameTextView.setTextColor(getResources().getColor(R.color.black)); //Sets text color
            nameTextView.setTextSize(16); //Sets text size

            countEditText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1));
            countEditText.setGravity(Gravity.CENTER);
            countEditText.setBackgroundColor(getResources().getColor(R.color.nice_gray));
            countEditText.setText(String.valueOf(item.getQuantity()));
            countEditText.setTextColor(getResources().getColor(R.color.black));
            countEditText.setTextSize(16);
            countEditText.setInputType(InputType.TYPE_CLASS_NUMBER); //Set input type to number
            countEditText.setOnFocusChangeListener((v, hasFocus) -> { //Adding the quick function here to update the item's quantity
                if (!hasFocus) {
                    //Update quantity in database if focus is lost
                    int newQuantity = Integer.parseInt(countEditText.getText().toString());
                    item.setQuantity(newQuantity); //Add item to java List
                    itemDatabaseHelper.updateItemQuantity(item, true); //Add item to SQLite database
                }
            });

            priceTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1));
            priceTextView.setGravity(Gravity.CENTER);
            priceTextView.setBackgroundColor(getResources().getColor(R.color.nice_gray));
            priceTextView.setText("$" + item.getPrice());
            priceTextView.setTextColor(getResources().getColor(R.color.black));
            priceTextView.setTextSize(16);

            deleteButton.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener(v -> deleteItem(item));

            //Add views to the row
            row.addView(nameTextView);
            row.addView(countEditText);
            row.addView(priceTextView);
            row.addView(deleteButton);

            //Add the row to the table layout
            tableLayout.addView(row);
        }
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
