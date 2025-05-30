package com.example.hercules77;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.hercules77.databinding.ActivityBannerFormBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class BannerFormActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityBannerFormBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imageUri;
    private String imageUrl;
    private String bannerId;
    private boolean isCloudinaryInitialized = false;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBannerFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        initCloudinary();

        binding.btnBack.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
        binding.btnUploadImage.setOnClickListener(this);

        bannerId = getIntent().getStringExtra("BANNER_ID");
        if (bannerId != null) {
            binding.title.setText("Edit Banner Promo");
            loadBannerData();
        } else {
            binding.title.setText("Tambah Banner Promo");
        }

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                Glide.with(this).load(imageUri).into(binding.ivPreview);
            }
        });
    }

    private void initCloudinary() {
        if (!isCloudinaryInitialized) {
            try {
                Map<String, String> config = new HashMap<>();
                config.put("cloud_name", "dto6d9tbe");
                MediaManager.init(this, config);
                isCloudinaryInitialized = true;
            } catch (IllegalStateException e) {
                Log.e("Cloudinary", "MediaManager already initialized");
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnBack) {
            finish();
        } else if (id == R.id.btnSave) {
            saveBanner();
        } else if (id == R.id.btnUploadImage) {
            openImagePicker();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Pilih Gambar"));
    }

    private void uploadImageToCloudinary(Runnable onSuccess, Runnable onError) {
        if (imageUri == null) {
            onSuccess.run();
            return;
        }

        binding.btnUploadImage.setEnabled(false);
        binding.btnUploadImage.setText("Mengunggah...");

        MediaManager.get().upload(imageUri)
                .unsigned("ecokids")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("Cloudinary", "Mulai mengupload gambar");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        imageUrl = (String) resultData.get("secure_url");
                        binding.btnUploadImage.setEnabled(true);
                        binding.btnUploadImage.setText("Unggah Gambar");
                        Toast.makeText(BannerFormActivity.this, "Gambar berhasil diunggah", Toast.LENGTH_SHORT).show();
                        onSuccess.run();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        binding.btnUploadImage.setEnabled(true);
                        binding.btnUploadImage.setText("Unggah Gambar");
                        Toast.makeText(BannerFormActivity.this, "Gagal mengunggah gambar: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                        onError.run();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        binding.btnUploadImage.setEnabled(true);
                        binding.btnUploadImage.setText("Unggah Gambar");
                        onError.run();
                    }
                })
                .dispatch();
    }

    private void saveBanner() {
        String judulBanner = binding.etJudulBanner.getText().toString().trim();
        String deskripsi = binding.etDeskripsi.getText().toString().trim();
        String linkPromo = binding.etLinkPromo.getText().toString().trim();

        if (judulBanner.isEmpty() || deskripsi.isEmpty()) {
            Toast.makeText(this, "Judul banner dan deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImageToCloudinary(
                () -> {
                    Map<String, Object> bannerData = new HashMap<>();
                    bannerData.put("judulBanner", judulBanner);
                    bannerData.put("deskripsi", deskripsi);
                    bannerData.put("linkPromo", linkPromo);
                    bannerData.put("gambarBannerUrl", imageUrl != null ? imageUrl : "");

                    if (bannerId != null) {
                        db.collection("banners").document(bannerId)
                                .set(bannerData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Banner berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Gagal memperbarui banner", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        db.collection("banners").add(bannerData)
                                .addOnSuccessListener(documentReference -> {
                                    bannerData.put("id", documentReference.getId());
                                    db.collection("banners").document(documentReference.getId())
                                            .set(bannerData)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "Banner berhasil disimpan", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Gagal menyimpan banner", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Gagal menyimpan banner", Toast.LENGTH_SHORT).show();
                                });
                    }
                },
                () -> {}
        );
    }

    private void loadBannerData() {
        if (bannerId == null) return;

        db.collection("banners").document(bannerId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        binding.etJudulBanner.setText(document.getString("judulBanner"));
                        binding.etDeskripsi.setText(document.getString("deskripsi"));
                        binding.etLinkPromo.setText(document.getString("linkPromo"));
                        String savedImageUrl = document.getString("gambarBannerUrl");
                        if (savedImageUrl != null && !savedImageUrl.isEmpty()) {
                            imageUrl = savedImageUrl;
                            Glide.with(this).load(savedImageUrl).into(binding.ivPreview);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memuat data banner", Toast.LENGTH_SHORT).show();
                });
    }
}