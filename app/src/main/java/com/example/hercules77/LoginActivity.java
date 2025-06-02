package com.example.hercules77;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

public class LoginActivity extends AppCompatActivity {
    EditText etUsername, etPassword;
    Button btnLogin, btnToRegister;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnToRegister = findViewById(R.id.btnToRegister);

        db = new DBHelper(this);

        // Tombol login
        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString();
            String pass = etPassword.getText().toString();

            if (db.checkUser(user, pass)) {
                Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show();

                // Ambil data user lengkap
                User userData = db.getUserData(user);

                SharedPreferences prefs = getSharedPreferences("login_session", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("username", userData.username);
                editor.putString("email", userData.email);
                editor.putString("status", userData.status);
                editor.putString("fotoProfilUrl", userData.fotoProfilUrl);
                editor.apply();

                // Navigasi ke Home sesuai user
                if (user.equals("admin")) {
                    Intent intent = new Intent(this, HomeAdminActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "Login gagal. Coba lagi!", Toast.LENGTH_SHORT).show();
            }
        });

        // Tombol register
        btnToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}