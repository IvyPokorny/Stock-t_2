package com.stockt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKey;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create the users table with additional fields
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FULL_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_PHONE_NUMBER + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the old table if it exists and create a new one (for changing columns and otherwise updating the table)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void addUser(String fullName, String email, String password, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, hashPassword(password));
        values.put(COLUMN_PHONE_NUMBER, phoneNumber);
        db.insert(TABLE_USERS, null, values);
        db.close();
        Log.i("UserDatabaseHelper", "addUser: Hashed password=" + hashPassword(password));
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Checks the passwords match
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, hashPassword(password)},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        Log.i("UserDatabaseHelper", "checkUser: Hashed password=" + hashPassword(password));
        return exists;
    }


    public static String hashPassword(String password) {
        String saltText = "ThisIsSaltText";
        byte[] salt = saltText.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(hashedBytes, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Handle error appropriately
        }
    }

//    public String encryptPassword(String password) {
//        listProviders();
//        try {
//            //Generate a secret key for AES
//            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//            keyGen.init(128); //I can also choose 192 or 256 bits
//            SecretKey secretKey = keyGen.generateKey();
//
//            //Initialize the cipher for AES
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//
//            //Encrypt the password
//            byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
//
//            //Return the encrypted password as a Base64-encoded string
//            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


//    public void listProviders() {
//        for (Provider provider : Security.getProviders()) {
//            Log.i("UserDatabaseHelper", "listProviders: " + provider.getName() + ": " + provider.getVersion());
//            for (Provider.Service service : provider.getServices()) {
//                Log.i("UserDatabaseHelper", "listService: " + service.getType() + " " + service.getAlgorithm());
//            }
//        }
//    }
}
