package com.stockt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InventoryHistoryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory_history.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ADJUSTMENTS = "adjustments";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ITEM_ID = "item_id";
    private static final String COLUMN_ITEM_NAME = "item_name";
    private static final String COLUMN_QUANTITY_CHANGE = "quantity_change";
    private static final String COLUMN_ADJUSTMENT_TIME = "adjustment_time";
    private static final String COLUMN_ADJUSTMENT_REASON = "adjustment_reason";

    public InventoryHistoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ADJUSTMENTS_TABLE = "CREATE TABLE " + TABLE_ADJUSTMENTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ITEM_ID + " INTEGER,"
                + COLUMN_ITEM_NAME + " TEXT,"
                + COLUMN_QUANTITY_CHANGE + " INTEGER,"
                + COLUMN_ADJUSTMENT_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_ADJUSTMENT_REASON + " TEXT" + ")";
        db.execSQL(CREATE_ADJUSTMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADJUSTMENTS);
        onCreate(db);
    }

    public void addAdjustment(int itemId, String itemName, int quantityChange, LocalDateTime adjustmentTime, String adjustmentReason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_ID, itemId);
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_QUANTITY_CHANGE, quantityChange);

        //Format LocalDateTime to string
        if (adjustmentTime != null) {
            String formattedDate = adjustmentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            values.put(COLUMN_ADJUSTMENT_TIME, formattedDate);
        }

        values.put(COLUMN_ADJUSTMENT_REASON, adjustmentReason);
        db.insert(TABLE_ADJUSTMENTS, null, values);
        db.close();
    }

    public List<Adjustment> getAllAdjustments() {
        List<Adjustment> adjustmentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ADJUSTMENTS, null, null, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID));
                    String itemName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME));
                    int quantityChange = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY_CHANGE));
                    String adjustmentTimeString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADJUSTMENT_TIME));
                    LocalDateTime adjustmentTime = LocalDateTime.parse(adjustmentTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    String adjustmentReason = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADJUSTMENT_REASON));

                    Adjustment adjustment = new Adjustment(id, itemId, itemName, quantityChange, adjustmentTime, adjustmentReason);
                    adjustmentList.add(adjustment);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return adjustmentList;
    }
}