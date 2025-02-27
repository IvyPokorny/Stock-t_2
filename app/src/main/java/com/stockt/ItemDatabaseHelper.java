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
    public void addItem(String name, int quantity, double value, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_VALUE, value);
        values.put(COLUMN_CATEGORY, category);
        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //Variable for all columns required
        String[] columns = {COLUMN_ID, COLUMN_NAME, COLUMN_QUANTITY, COLUMN_VALUE};

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

                    //Check if indices are valid before accessing
                    if (idIndex != -1 && nameIndex != -1 && quantityIndex != -1 && valueIndex != -1) {
                        int id = cursor.getInt(idIndex);
                        String name = cursor.getString(nameIndex);
                        int quantity = cursor.getInt(quantityIndex);
                        double price = cursor.getDouble(valueIndex);
                        Item item = new Item(id, name, quantity, price);
                        itemList.add(item);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return itemList;
    }

    public void deleteItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Uses id to ensure a unique identifier is used to eliminate the correct item
        int itemId = item.getId();
        db.delete("items", "id = ?", new String[]{String.valueOf(itemId)});
        db.close();
    }

    public void updateItemQuantity(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Uses id to ensure a unique identifier is used to update the correct item
        int itemId = item.getId();
        values.put("quantity", item.getQuantity());
        db.update("items", values, "id = ?", new String[]{String.valueOf(itemId)});

        //Send message if, after updated, the count is <= 50
        if (item.getQuantity() <= 50){
            String message = "The item: " + item.getName() + " is low on inventory";
            HomeActivity.sendSms("15551234567", message); //Again, hardcoded to emulated phone number
        }
        db.close();
    }
}
