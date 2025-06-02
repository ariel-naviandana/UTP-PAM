package com.example.hercules77;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private SharedPreferences prefs;
    private static final String CLOUD_NAME = "dk7ayxsny"; // Nama cloud Cloudinary
    private static final String UPLOAD_PRESET = "pam-project"; // Upload preset Cloudinary
    ImageView imgProfile;
    TextView txtUsername, txtEmail, txtStatus;
    Button btnBack, btnEditUsername;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgProfile = findViewById(R.id.imgProfile);
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        txtStatus = findViewById(R.id.txtStatus);
        btnBack = findViewById(R.id.btnBack);
        btnEditUsername = findViewById(R.id.btnEditUsername);

        // Inisialisasi SharedPreferences
        prefs = getSharedPreferences("login_session", MODE_PRIVATE);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        loadProfileDataFromFirestore();

        // Tombol kembali
        btnBack.setOnClickListener(v -> finish());

        // Tombol ubah username
        btnEditUsername.setOnClickListener(v -> showEditUsernamePopup());

        imgProfile.setOnClickListener(v -> openGallery());
    }

    // Memuat data profil dari Firestore
    private void loadProfileDataFromFirestore() {
        String username = prefs.getString("username", "Guest");
        if (username.equals("Guest")) {
            // Kalau belum login
            txtUsername.setText("Guest");
            txtEmail.setText("-");
            txtStatus.setText("Status: Tidak diketahui");
            imgProfile.setImageResource(R.drawable.ic_user_icon);
            return;
        }

        // Query Firestore user collection untuk username ini
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener((QuerySnapshot snapshots) -> {
                    if (snapshots.isEmpty()) {
                        Toast.makeText(this, "User tidak ditemukan di Firestore", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (QueryDocumentSnapshot doc : snapshots) {
                        String email = doc.getString("email");
                        String status = doc.getString("status");
                        String fotoProfilUrl = doc.getString("fotoProfilUrl");

                        txtUsername.setText(username);
                        txtEmail.setText(email != null ? email : "-");
                        txtStatus.setText("Status: " + (status != null ? status : "Tidak diketahui"));

                        if (fotoProfilUrl != null && !fotoProfilUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(fotoProfilUrl)
                                    .circleCrop()
                                    .into(imgProfile);
                            // Update prefs juga agar konsisten
                            prefs.edit().putString("fotoProfilUrl", fotoProfilUrl).apply();
                        } else {
                            imgProfile.setImageResource(R.drawable.ic_user_icon);
                        }
                        break; // cukup 1 dokumen
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal ambil data user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Menampilkan popup untuk mengubah username
    private void showEditUsernamePopup() {
        final EditText input = new EditText(this);
        input.setHint("Username baru");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(txtUsername.getText().toString());

        new AlertDialog.Builder(this)
                .setTitle("Ubah Username")
                .setView(input)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String newUsername = input.getText().toString().trim();
                    if (newUsername.isEmpty()) {
                        Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(this, "Memperbarui username...", Toast.LENGTH_SHORT).show();
                    updateUsername(newUsername);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    // Mengupdate username di Firestore
    private void updateUsername(String newUsername) {
        String oldUsername = prefs.getString("username", "");

        db.collection("users")
                .whereEqualTo("username", oldUsername)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots.isEmpty()) {
                        Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (QueryDocumentSnapshot doc : snapshots) {
                        String docId = doc.getId();
                        db.collection("users")
                                .document(docId)
                                .update("username", newUsername)
                                .addOnSuccessListener(aVoid -> {
                                    prefs.edit().putString("username", newUsername).apply();
                                    txtUsername.setText(newUsername);
                                    Toast.makeText(this, "Username berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Gagal update: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        break;
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal akses Firestore", Toast.LENGTH_SHORT).show());
    }

    // Membuka gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Mengambil gambar dari gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImageToCloudinary(imageUri);
        }
    }

    // Mengupload gambar ke Cloudinary
    private void uploadImageToCloudinary(Uri imageUri) {
        try {
            Toast.makeText(this, "Gambar sedang diupload...", Toast.LENGTH_SHORT).show();
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

                        updatePhotoProfilUrlInFirestore(imageUrl);

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

    // Mengupdate foto profil di Firestore
    private void updatePhotoProfilUrlInFirestore(String newImageUrl) {
        String username = prefs.getString("username", "Guest");
        if (username.equals("Guest")) return;

        // Cari dokumen user berdasarkan username
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots.isEmpty()) {
                        runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "User tidak ditemukan", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    for (QueryDocumentSnapshot doc : snapshots) {
                        String docId = doc.getId();
                        db.collection("users")
                                .document(docId)
                                .update("fotoProfilUrl", newImageUrl)
                                .addOnSuccessListener(aVoid -> {
                                    prefs.edit().putString("fotoProfilUrl", newImageUrl).apply();

                                    runOnUiThread(() -> {
                                        Glide.with(ProfileActivity.this)
                                                .load(newImageUrl)
                                                .circleCrop()
                                                .into(imgProfile);
                                        Toast.makeText(ProfileActivity.this, "Foto profil diperbarui", Toast.LENGTH_SHORT).show();
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Gagal memperbarui foto profil", Toast.LENGTH_SHORT).show());
                                });
                        break;
                    }
                })
                .addOnFailureListener(e -> {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Gagal cari user di Firestore", Toast.LENGTH_SHORT).show());
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileDataFromFirestore();
    }
}