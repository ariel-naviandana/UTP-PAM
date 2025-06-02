package com.example.hercules77;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {
    EditText editUsername, editEmail;
    Button btnSave;
    SharedPreferences prefs;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        btnSave = findViewById(R.id.btnSave);

        // Inisialisasi DBHelper dan SharedPreferences
        db = new DBHelper(this);
        prefs = getSharedPreferences("login_session", MODE_PRIVATE);

        // Mengisi form dengan data user yang sedang login
        String currentUsername = prefs.getString("username", "");
        User user = db.getUserData(currentUsername);
        if (user != null) {
            editUsername.setText(user.getUsername());
            editEmail.setText(user.getEmail());
        }

        // Tombol simpan
        btnSave.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean updated = db.updateUserProfile(user.getId(), newUsername, newEmail);
            if (updated) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username", newUsername);
                editor.putString("email", newEmail);
                editor.apply();

                Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
            }
        });
    }
}