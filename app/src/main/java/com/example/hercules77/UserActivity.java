package com.example.hercules77;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import android.Manifest;

public class UserActivity extends AppCompatActivity {
    DBHelper db;
    RecyclerView recyclerView;
    ArrayList<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        db = new DBHelper(this);

        // Cek dan minta permission WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            loadUsers();
        }
    }

    // Tangani hasil permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin penyimpanan diberikan", Toast.LENGTH_SHORT).show();
                loadUsers();
            } else {
                Toast.makeText(this, "Izin penyimpanan ditolak, fitur download tidak dapat digunakan", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Fungsi untuk memuat data user dari database
    private void loadUsers() {
        userList.clear();

        Cursor cursor = db.getAllUsers();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            String fotoProfilUrl = cursor.getString(cursor.getColumnIndexOrThrow("fotoProfilUrl"));

            userList.add(new User(id, username, email, status, fotoProfilUrl));
        }
        cursor.close();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new UserAdapter(this, userList, db));
    }
}