package com.example.hercules77;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "UTP.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS user(id TEXT PRIMARY KEY, username TEXT, password TEXT)");

        // Tambahan: Tabel win_history
        db.execSQL("CREATE TABLE IF NOT EXISTS win_history (" +
                "id TEXT PRIMARY KEY, " +
                "idUser TEXT, " +
                "jumlahMenang INTEGER, " +
                "tanggalMenang TEXT, " +
                "buktiGambarUrl TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS win_history");
        onCreate(db);
    }

    // Fungsi insert user
    public boolean insertUser(String id, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("username", username);
        values.put("password", password);
        long result = db.insert("user", null, values);
        return result != -1;
    }

    // Alias: registerUser agar tidak error di RegisterActivity
    public boolean registerUser(String id, String username, String password) {
        return insertUser(id, username, password);
    }

    // Fungsi login
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username = ? AND password = ?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Tambahan: Fungsi insert win_history
    public boolean insertWinHistory(String id, String idUser, int jumlahMenang, String tanggalMenang, String buktiGambarUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("idUser", idUser);
        values.put("jumlahMenang", jumlahMenang);
        values.put("tanggalMenang", tanggalMenang);
        values.put("buktiGambarUrl", buktiGambarUrl);
        long result = db.insert("win_history", null, values);
        return result != -1;
    }

    // Tambahan: Ambil semua data win_history
    public Cursor getAllWinHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM win_history", null);
    }

    // Tambahan: Hapus data win_history berdasarkan id
    public int deleteWinHistory(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("win_history", "id = ?", new String[]{id});
    }
}