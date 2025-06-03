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
import com.example.hercules77.databinding.ActivityTipsFormBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class TipsFormActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityTipsFormBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imageUri;
    private String imageUrl;
    private String tipsId;
    private boolean isCloudinaryInitialized = false;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTipsFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        initCloudinary();

        binding.btnBack.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
        binding.btnUploadImage.setOnClickListener(this);

        tipsId = getIntent().getStringExtra("TIPS_ID");
        if (tipsId != null) {
            binding.title.setText("Edit Tips & Trik");
            loadTipsData();
        } else {
            binding.title.setText("Tambah Tips & Trik Baru");
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
            saveTips();
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
                        binding.btnUploadImage.setText("Unggah Gambar Tips");
                        Toast.makeText(TipsFormActivity.this, "Gambar berhasil diunggah", Toast.LENGTH_SHORT).show();
                        onSuccess.run();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        binding.btnUploadImage.setEnabled(true);
                        binding.btnUploadImage.setText("Unggah Gambar Tips");
                        Toast.makeText(TipsFormActivity.this, "Gagal mengunggah gambar: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                        onError.run();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        binding.btnUploadImage.setEnabled(true);
                        binding.btnUploadImage.setText("Unggah Gambar Tips");
                        onError.run();
                    }
                })
                .dispatch();
    }

    private void saveTips() {
        String judul = binding.etJudulTips.getText() != null ? binding.etJudulTips.getText().toString().trim() : "";
        String konten = binding.etIsiKonten.getText() != null ? binding.etIsiKonten.getText().toString().trim() : "";
        String kategori = binding.etKategori.getText() != null ? binding.etKategori.getText().toString().trim() : "";

        if (judul.isEmpty() || konten.isEmpty() || kategori.isEmpty()) {
            Toast.makeText(this, "Judul, konten, dan kategori tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImageToCloudinary(
                () -> {
                    Map<String, Object> tipsData = new HashMap<>();
                    tipsData.put("judul", judul);
                    tipsData.put("konten", konten);
                    tipsData.put("kategori", kategori);
                    tipsData.put("gambarIlustrasiUrl", imageUrl != null ? imageUrl : "");

                    if (tipsId != null) {
                        tipsData.put("id", tipsId);
                        db.collection("tips").document(tipsId)
                                .set(tipsData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Tips berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Gagal memperbarui tips", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        db.collection("tips").add(tipsData)
                                .addOnSuccessListener(documentReference -> {
                                    tipsData.put("id", documentReference.getId());
                                    db.collection("tips").document(documentReference.getId())
                                            .set(tipsData)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "Tips berhasil disimpan", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Gagal menyimpan tips", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Gagal menyimpan tips", Toast.LENGTH_SHORT).show();
                                });
                    }
                },
                () -> {}
        );
    }

    private void loadTipsData() {
        if (tipsId == null) return;

        db.collection("tips").document(tipsId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        binding.etJudulTips.setText(document.getString("judul"));
                        binding.etIsiKonten.setText(document.getString("konten"));
                        binding.etKategori.setText(document.getString("kategori"));
                        String savedImageUrl = document.getString("gambarIlustrasiUrl");
                        if (savedImageUrl != null && !savedImageUrl.isEmpty()) {
                            imageUrl = savedImageUrl;
                            Glide.with(this).load(savedImageUrl).into(binding.ivPreview);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memuat data tips", Toast.LENGTH_SHORT).show();
                });
    }
}