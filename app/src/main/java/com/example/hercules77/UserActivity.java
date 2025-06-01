package com.example.hercules77;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 101;
    RecyclerView recyclerView;
    ArrayList<User> userList = new ArrayList<>();
    FirebaseFirestore db;
    UserAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();
        adapter = new UserAdapter(this, userList);
        recyclerView.setAdapter(adapter);

        checkStoragePermission();
        loadUsersFromFirestore();
    }

    // Memeriksa izin penyimpanan
    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    // Menangani hasil permintaan izin penyimpanan
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin penyimpanan diterima", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(this, "Izin penyimpanan ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Memuat data user dari Firestore
    private void loadUsersFromFirestore() {
        userList.clear();

        db.collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String documentId = document.getId();
                        int id = documentId.hashCode();
                        String username = document.getString("username");
                        String email = document.getString("email");
                        String status = document.getString("status");
                        String fotoProfilUrl = document.getString("fotoProfilUrl");

                        userList.add(new User(documentId, id, username, email, status, fotoProfilUrl));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal memuat data user: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
