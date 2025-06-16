package com.stockt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class HistoryActivity extends AppCompatActivity {

    private TextView historyTextView, redoTextView;
    private Button undoButton, redoButton, reloadButton;
    private Button buttonItems, buttonHome, buttonSettings;
    private HistoryManager historyManager;
    private ItemDatabaseHelper itemDatabaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_screen);

        historyTextView = findViewById(R.id.historyTextView);
        redoTextView = findViewById(R.id.redoTextView);
        undoButton = findViewById(R.id.undoButton);
        redoButton = findViewById(R.id.redoButton);
        reloadButton = findViewById(R.id.reloadButton);
        buttonItems = findViewById(R.id.button5);
        buttonHome = findViewById(R.id.button4);
        buttonSettings = findViewById(R.id.button3);

        historyManager = HistoryManager.getInstance(); //Get instance once

        //Example: Adding initial actions to the undo stack
//        List<Object> addItemValues = new ArrayList<>();
//        addItemValues.add("Item A");
//        addItemValues.add(10);
//        historyManager.logAction(new Action(Action.ActionType.ADD_ITEM, addItemValues));

        updateHistoryDisplay();
        itemDatabaseHelper = new ItemDatabaseHelper(this);

        undoButton.setOnClickListener(v -> undoAction());
        redoButton.setOnClickListener(v -> redoAction());
        reloadButton.setOnClickListener(v -> reloadAction());
        buttonItems.setOnClickListener(v -> openItemsActivity());
        buttonHome.setOnClickListener(v -> openHomeActivity());
        buttonSettings.setOnClickListener(v -> openSettingsActivity());
    }

    private void updateHistoryDisplay() {
        List<Action> undoList = historyManager.getUndoStack();
        StringBuilder historyBuilder = new StringBuilder();
        Log.i("HistoryActivity", "updateHistoryDisplay: Size of Undo Stack" + undoList.size());
        for (Action action : undoList) {
            historyBuilder.append(action.getActionType()).append(": ").append(action.getActionValues()).append("\n");
        }
        historyTextView.setText(historyBuilder.toString());

        List<Action> redoList = historyManager.getRedoStack();
        Collections.reverse(redoList); //Reverse so the action to be redone is at the top
        StringBuilder redoBuilder = new StringBuilder();
        Log.i("HistoryActivity", "updateHistoryDisplay: Size of Undo Stack" + redoList.size());
        for (Action action : redoList) {
            redoBuilder.append(action.getActionType()).append(": ").append(action.getActionValues()).append("\n");
        }
        redoTextView.setText(redoBuilder.toString());
    }

    private void undoAction() {
        List<Action> undoList = historyManager.getUndoStack();
        if (!undoList.isEmpty()) {
            Action action = historyManager.undo();

            updateHistoryDisplay();
            handleAction(action, false);
        }
    }


    private void redoAction() {
        List<Action> redoList = historyManager.getRedoStack();
        if (!redoList.isEmpty()) {
            Action action = historyManager.redo();

            updateHistoryDisplay();
            handleAction(action, true);
        }
    }

    private void handleAction(Action action, boolean isRedo) {
        switch (action.getActionType()) {
            case ADD_ITEM:
                //Logic to add item or reverse add (delete)
                if (isRedo) {
                    //Add item back to database
                    List<Object> values = action.getActionValues();
                    int id = (int) values.get(0);
                    String name = (String) values.get(1);
                    int quantity = (int) values.get(2);
                    double value = (double) values.get(3);
                    String category = (String) values.get(4);
                    itemDatabaseHelper.addItem(id, name, quantity, value, category, false);
                } else {
                    //Logic to delete the item from the database
                    List<Object> values = action.getActionValues();
                    int id = (int) values.get(0);
                    String name = (String) values.get(1);
                    int quantity = (int) values.get(2);
                    double value = (double) values.get(3);
                    String category = (String) values.get(4);
                    Item itemToDelete = new Item(id, name, quantity, value, category);
                    itemDatabaseHelper.deleteItem(itemToDelete, false);
                }
                break;
            case UPDATE_ITEM:
                //Logic to update item or reverse update
                List<Object> updateValues = action.getActionValues();
                int itemId = (int) updateValues.get(0);
                int newQuantity = (int) updateValues.get(1);
                int previousQuantity = (int) updateValues.get(2);
                Item itemToUpdate = itemDatabaseHelper.getAllItems().stream()
                        .filter(item -> item.getId() == itemId)
                        .findFirst()
                        .orElse(null);

                if (isRedo) {
                    //Reapply the update
                    if (itemToUpdate != null) {
                        itemToUpdate.setQuantity(newQuantity);
                        itemDatabaseHelper.updateItemQuantity(itemToUpdate, false);
                    }
                } else {
                    //Reverse the update (set to previous quantity)
                    if (itemToUpdate != null) {
                        itemToUpdate.setQuantity(previousQuantity); //Restore previous quantity
                        itemDatabaseHelper.updateItemQuantity(itemToUpdate, false);
                    }
                }
                break;
            case DELETE_ITEM:
                //Logic to delete item or reverse delete (add back)
                List<Object> deleteValues = action.getActionValues();
                int deletedId = (int) deleteValues.get(0);
                String deletedItemName = (String) deleteValues.get(1);
                int deletedItemQuantity = (int) deleteValues.get(2);
                double deletedItemValue = (double) deleteValues.get(3);
                String deletedItemCategory = (String) deleteValues.get(4);
                Item itemToRestore = new Item(deletedId, deletedItemName, deletedItemQuantity, deletedItemValue, deletedItemCategory); //Use a placeholder ID

                if (isRedo) {
                    //Logic to delete the item from the database
                    itemDatabaseHelper.deleteItem(itemToRestore, false);

                } else {
                    //Restore the deleted item
                    itemDatabaseHelper.addItem(deletedId, deletedItemName, deletedItemQuantity, deletedItemValue, deletedItemCategory, false);
                }
                break;
        }
    }

    private void reloadAction() {
        //Logic to open the items list activity but we're already here
        recreate();
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
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}