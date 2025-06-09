package com.stockt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.List;
import java.util.ArrayList;
import android.telephony.SmsManager;
import android.util.Log;

public class ItemDatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_VALUE = "value";
    private static final String COLUMN_CATEGORY = "category";

    public ItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_QUANTITY + " INTEGER,"
                + COLUMN_VALUE + " REAL,"
                + COLUMN_CATEGORY + " TEXT" + ")";
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    //Add item given all params
    public void addItem(String name, int quantity, double value, String category, boolean isLogged) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_VALUE, value);
        values.put(COLUMN_CATEGORY, category);
        Log.i("ItemDatabaseHelper", "addItem: NEW & Log=" + String.valueOf(isLogged) + ", name=" + name + ", quantity=" + String.valueOf(quantity));
        long id = db.insert(TABLE_ITEMS, null, values); //Get the newly created ID
        db.close();

        if (isLogged) {
            //Log the action for history using Action class
            List<Object> actionValues = new ArrayList<>();
            actionValues.add((int) id); //Log the item ID
            actionValues.add(name);
            actionValues.add(quantity);
            actionValues.add(value);
            actionValues.add(category);
            HistoryManager.getInstance().logAction(new Action(Action.ActionType.ADD_ITEM, actionValues));
        }
    }

    //Overloaded method to add item with a specific ID (for use with reinstating deleted items)
    public void addItem(int id, String name, int quantity, double value, String category, boolean isLogged) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id); // Set the provided ID
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_VALUE, value);
        values.put(COLUMN_CATEGORY, category);
        Log.i("ItemDatabaseHelper", "addItem: NEW & Log=" + String.valueOf(isLogged) + ", id=" + String.valueOf(id) + ", name=" + name + ", quantity=" + String.valueOf(quantity));
        db.insert(TABLE_ITEMS, null, values); // Insert the item
        db.close();

        if (isLogged) {
            //Log the action for history using Action class
            List<Object> actionValues = new ArrayList<>();
            actionValues.add(id); //Log the provided item ID
            actionValues.add(name);
            actionValues.add(quantity);
            actionValues.add(value);
            actionValues.add(category);
            HistoryManager.getInstance().logAction(new Action(Action.ActionType.ADD_ITEM, actionValues));
        }
    }

    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //Variable for all columns required
        String[] columns = {COLUMN_ID, COLUMN_NAME, COLUMN_QUANTITY, COLUMN_VALUE, COLUMN_CATEGORY};

        //Get query results
        Cursor cursor = db.query(TABLE_ITEMS, columns, null, null, null, null, null);

        //Make sure it queries correctly
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    //Get the column indices
                    int idIndex = cursor.getColumnIndex(COLUMN_ID);
                    int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                    int quantityIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
                    int valueIndex = cursor.getColumnIndex(COLUMN_VALUE);
                    int categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY);

                    //Check if indices are valid before accessing
                    if (idIndex != -1 && nameIndex != -1 && quantityIndex != -1 && valueIndex != -1 && categoryIndex != -1) {
                        int id = cursor.getInt(idIndex);
                        String name = cursor.getString(nameIndex);
                        int quantity = cursor.getInt(quantityIndex);
                        double price = cursor.getDouble(valueIndex);
                        String category = cursor.getString(categoryIndex);
                        Item item = new Item(id, name, quantity, price, category);
                        itemList.add(item);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return itemList;
    }

    public void deleteItem(Item item, boolean isLogged) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Uses id to ensure a unique identifier is used to eliminate the correct item
        int itemId = item.getId();
        Log.i("ItemDatabaseHelper", "deleteItem: NEW & Log=" + String.valueOf(isLogged) + ", id=" + String.valueOf(itemId));
        db.delete(TABLE_ITEMS, "id = ?", new String[]{String.valueOf(itemId)});
        db.close();

        if (isLogged) {
            //Log the action for history using Action class
            List<Object> actionValues = new ArrayList<>();
            actionValues.add(itemId); //Log the item ID
            actionValues.add(item.getName());
            actionValues.add(item.getQuantity());
            actionValues.add(item.getPrice());
            actionValues.add(item.getCategory());
            HistoryManager.getInstance().logAction(new Action(Action.ActionType.DELETE_ITEM, actionValues));
        }
    }

    public void updateItemQuantity(Item item, boolean isLogged) {
        SQLiteDatabase db = this.getWritableDatabase();
        int itemId = item.getId(); //Uses id to find the correct item

        //Fetch the old quantity before updating
        Cursor cursor = db.query(TABLE_ITEMS, new String[]{COLUMN_QUANTITY}, COLUMN_ID + " = ?", new String[]{String.valueOf(itemId)}, null, null, null);
        int oldQuantity = -1; //Default value if item not found
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int quantityIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
                if (quantityIndex != -1) { // Check if the index is valid
                    oldQuantity = cursor.getInt(quantityIndex);
                } else {
                    Log.e("ItemDatabaseHelper", "Column not found: " + COLUMN_QUANTITY);
                }
            }
            cursor.close();
        }

        //Update the item quantity
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, item.getQuantity());
        Log.i("ItemDatabaseHelper", "updateItemQuantity: Log=" + String.valueOf(isLogged) + ", id=" + String.valueOf(itemId) + ", name=" + item.getName() + ", quantity=" + String.valueOf(item.getQuantity()) + ", oldQuantity=" + String.valueOf(oldQuantity));
        db.update(TABLE_ITEMS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(itemId)});

        //Send message if, after updated, the count is <= 50
        if (item.getQuantity() <= 50) {
            String message = "The item: " + item.getName() + " is low on inventory";
            HomeActivity.sendSms("15551234567", message); //Again, hardcoded to emulated phone number
        }

        if (isLogged) {
            //Log the action for history using Action class
            List<Object> actionValues = new ArrayList<>();
            actionValues.add(itemId); //Log the item ID
            actionValues.add(item.getQuantity()); //Log the new quantity
            actionValues.add(oldQuantity); //Log the old quantity
            HistoryManager.getInstance().logAction(new Action(Action.ActionType.UPDATE_ITEM, actionValues));
        }

        db.close();
    }
}
