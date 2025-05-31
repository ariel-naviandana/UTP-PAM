package com.example.hercules77;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "user_db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "users";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Membuat tabel users
        String createTable = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "email TEXT, " +
                "password TEXT, " +
                "status TEXT DEFAULT 'aktif', " +
                "fotoProfilUrl TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Fungsi untuk menambahkan user baru
    public boolean registerUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Cek apakah username sudah digunakan
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=?", new String[]{username});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("email", email);
        values.put("status", "aktif");
        values.put("fotoProfilUrl", ""); // kosong saat awal

        long result = db.insert("users", null, values);
        return result != -1;
    }

    // Fungsi untuk memeriksa apakah username dan password sesuai
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                " WHERE username = ? AND password = ?", new String[]{username, password});
        boolean found = cursor.getCount() > 0;
        cursor.close();
        return found;
    }

    // Update user status
    public boolean updateUserStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        int result = db.update("users", values, "id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Update foto profil
    public boolean updateUserPhoto(int id, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fotoProfilUrl", url);
        int result = db.update("users", values, "id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Mendapatkan semua data user
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id, username, email, status, fotoProfilUrl FROM " + TABLE_NAME, null);
    }

    // Mendapatkan data user berdasarkan username
    public User getUserData(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        Cursor cursor = db.rawQuery("SELECT id, username, email, status, fotoProfilUrl FROM " + TABLE_NAME + " WHERE username = ?", new String[]{username});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                String uname = cursor.getString(1);
                String email = cursor.getString(2);
                String status = cursor.getString(3);
                String fotoProfilUrl = cursor.getString(4);
                user = new User(id, uname, email, status, fotoProfilUrl);
            }
            cursor.close();
        }
        return user;
    }

    // Mendapatkan ID user berdasarkan username
    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE username=?", new String[]{username});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }

    // Mengupdate data user
    public boolean updateUserProfile(int id, String username, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        int result = db.update("users", values, "id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }
}