package com.example.hercules77;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    EditText etUsername, etPassword;
    Button btnLogin, btnToRegister;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnToRegister = findViewById(R.id.btnToRegister);

        // Inisialisasi Firebase Authentication dan Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Tombol login
        btnLogin.setOnClickListener(v -> {
            String inputUsername = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (inputUsername.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan password wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cari email dari username di Firestore
            db.collection("users")
                    .whereEqualTo("username", inputUsername)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            String email = querySnapshot.getDocuments().get(0).getString("email");

                            mAuth.signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener(authResult -> {
                                        String userId = mAuth.getCurrentUser().getUid();

                                        db.collection("users").document(userId).get()
                                                .addOnSuccessListener(documentSnapshot -> {
                                                    if (documentSnapshot.exists()) {
                                                        String username = documentSnapshot.getString("username");
                                                        String status = documentSnapshot.getString("status");
                                                        String fotoProfilUrl = documentSnapshot.getString("fotoProfilUrl");

                                                        if (status == null) status = "aktif"; // Status default

                                                        // Jika status banned, tolak login
                                                        if ("banned".equalsIgnoreCase(status)) {
                                                            Toast.makeText(this, "Akun Anda telah diblokir", Toast.LENGTH_SHORT).show();
                                                            mAuth.signOut();
                                                            return;
                                                        }

                                                        // Simpan data login ke SharedPreferences
                                                        SharedPreferences prefs = getSharedPreferences("login_session", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = prefs.edit();
                                                        editor.putString("username", username);
                                                        editor.putString("email", email);
                                                        editor.putString("status", status);
                                                        editor.putString("fotoProfilUrl", fotoProfilUrl);
                                                        editor.apply();

                                                        Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show();

                                                        // Akses admin hanya jika username == "admin"
                                                        if ("admin".equalsIgnoreCase(username)) {
                                                            startActivity(new Intent(this, HomeAdminActivity.class));
                                                        } else {
                                                            startActivity(new Intent(this, HomeActivity.class));
                                                        }
                                                        finish();
                                                    } else {
                                                        Toast.makeText(this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(this, "Gagal ambil data user: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                                );
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Login gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        } else {
                            Toast.makeText(this, "Username tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Gagal cari username: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        // Tombol Register
        btnToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }
}