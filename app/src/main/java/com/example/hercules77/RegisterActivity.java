package com.example.hercules77;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnRegister, btnBack;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // GANTI ID SESUAI DENGAN XML!
        etUsername = findViewById(R.id.etUsernameReg);
        etPassword = findViewById(R.id.etPasswordReg);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);

        db = new DBHelper(this);

        btnRegister.setOnClickListener(v -> {
            String user = etUsername.getText().toString();
            String pass = etPassword.getText().toString();
            String id = UUID.randomUUID().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = db.registerUser(id, user, pass);
                if (success) {
                    Toast.makeText(this, "Register berhasil!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Username sudah digunakan", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }); // Tombol kembali ke Login
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}