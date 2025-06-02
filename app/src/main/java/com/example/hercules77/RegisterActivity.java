package com.example.hercules77;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public class RegisterActivity extends AppCompatActivity {
    EditText etUsernameReg, etPasswordReg, etEmailReh;
    Button btnRegister, btnBack;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsernameReg = findViewById(R.id.etUsernameReg);
        etEmailReh = findViewById(R.id.etEmailReg);
        etPasswordReg = findViewById(R.id.etPasswordReg);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);

        db = new DBHelper(this);

        // Tombol register
        btnRegister.setOnClickListener(v -> {
            String user = etUsernameReg.getText().toString();
            String pass = etPasswordReg.getText().toString();
            String email = etEmailReh.getText().toString();

            if (user.isEmpty() || pass.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = db.registerUser(user, pass, email); // ubah parameter
                if (success) {
                    Toast.makeText(this, "Register berhasil!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Username sudah digunakan", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Tombol kembali
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