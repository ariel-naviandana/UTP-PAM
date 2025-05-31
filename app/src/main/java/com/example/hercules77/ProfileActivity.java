package com.example.hercules77;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import okhttp3.*;

public class ProfileActivity extends AppCompatActivity {
    ImageView imgProfile;
    TextView txtUsername, txtEmail, txtStatus;
    Button btnBack, btnEdit;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private SharedPreferences prefs;
    private DBHelper db;
    private static final String CLOUD_NAME = "dk7ayxsny"; // Nama cloud Cloudinary
    private static final String UPLOAD_PRESET = "pam-project"; // Nama preset Cloudinary

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgProfile = findViewById(R.id.imgProfile);
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        txtStatus = findViewById(R.id.txtStatus);
        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);

        // Inisialisasi DBHelper dan SharedPreferences
        db = new DBHelper(this);
        prefs = getSharedPreferences("login_session", MODE_PRIVATE);
        String username = prefs.getString("username", "Guest");
        String email = prefs.getString("email", "-");
        String status = prefs.getString("status", "tidak diketahui");
        String fotoProfilUrl = prefs.getString("fotoProfilUrl", "");

        // Set data ke TextView
        txtUsername.setText(username);
        txtEmail.setText(email);
        txtStatus.setText("Status: " + status);

        if (fotoProfilUrl != null && !fotoProfilUrl.isEmpty()) {
            Glide.with(this)
                    .load(fotoProfilUrl)
                    .circleCrop()
                    .into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.ic_user_icon);
        }

        // Tombol kembali
        btnBack.setOnClickListener(v -> finish());

        // Tombol edit
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Membuka gallery
        imgProfile.setOnClickListener(v -> openGallery());
    }

    // Fungsi untuk membuka gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Fungsi untuk menangani hasil dari gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImageToCloudinary(imageUri);
        }
    }

    // Fungsi untuk upload gambar ke Cloudinary
    private void uploadImageToCloudinary(Uri imageUri) {
        try {
            Toast.makeText(ProfileActivity.this, "Gambar sedang diupload...", Toast.LENGTH_SHORT).show();
            File file = FileUtils.getFileFromUri(this, imageUri);

            CloudinaryUploader.uploadImage(file, UPLOAD_PRESET, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Gagal upload gambar", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Upload gagal", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);
                        String imageUrl = json.getString("secure_url");

                        // Simpan URL ke SQLite & SharedPreferences
                        String username = prefs.getString("username", "Guest");
                        int userId = db.getUserIdByUsername(username);
                        db.updateUserPhoto(userId, imageUrl);

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("fotoProfilUrl", imageUrl);
                        editor.apply();

                        runOnUiThread(() -> {
                            Glide.with(ProfileActivity.this)
                                    .load(imageUrl)
                                    .circleCrop()
                                    .into(imgProfile);
                            Toast.makeText(ProfileActivity.this, "Foto profil diperbarui", Toast.LENGTH_SHORT).show();
                        });

                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Gagal parsing data Cloudinary", Toast.LENGTH_SHORT).show());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal membaca file gambar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String username = prefs.getString("username", "Guest");
        String email = prefs.getString("email", "-");
        txtUsername.setText(username);
        txtEmail.setText(email);
    }
}
