package com.example.hercules77;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.hercules77.databinding.ActivityEventFormBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventFormActivity extends AppCompatActivity {

    private ActivityEventFormBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imageUri;
    private String imageUrl;
    private String eventId;
    private boolean isCloudinaryInitialized = false;
    private FirebaseFirestore db;
    private final Calendar calendar = Calendar.getInstance();
    private Date tanggalMulai, tanggalAkhir;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        initCloudinary();
        binding.etTanggalMulai.setOnClickListener(v -> showDatePicker(true));
        binding.etTanggalAkhir.setOnClickListener(v -> showDatePicker(false));

        binding.btnBack.setOnClickListener(v-> finish());
        binding.btnSave.setOnClickListener(v-> saveEvent());
        binding.btnUploadImage.setOnClickListener(v-> openImagePicker());

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                Glide.with(this).load(imageUri).into(binding.ivPreview);
            }
        });
    }

    private void saveEvent() {
        String judulEvent = binding.etNamaEvent.getText().toString().trim();
        String deskripsi = binding.etDeskripsi.getText().toString().trim();

        if (judulEvent.isEmpty() || deskripsi.isEmpty()) {
            Toast.makeText(this, "Judul event dan deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tanggalMulai == null || tanggalAkhir == null) {
            Toast.makeText(this, "Tanggal mulai dan akhir wajib dipilih", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImageToCloudinary(
                () -> {
                    Map<String, Object> eventData = new HashMap<>();
                    eventData.put("judulEvent", judulEvent);
                    eventData.put("deskripsi", deskripsi);
                    eventData.put("tanggalMulai", tanggalMulai);
                    eventData.put("tanggalAkhir", tanggalAkhir);
                    eventData.put("bannerUrl", imageUrl != null ? imageUrl : "");

                    if (eventId != null) {
                        eventData.put("id", eventId); // pastikan ID disimpan
                        db.collection("events").document(eventId)
                                .set(eventData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Event berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Gagal memperbarui event", Toast.LENGTH_SHORT).show());
                    } else {
                        db.collection("events").add(eventData)
                                .addOnSuccessListener(documentReference -> {
                                    String generatedId = documentReference.getId();
                                    eventData.put("id", generatedId);
                                    db.collection("events").document(generatedId)
                                            .set(eventData)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "Event berhasil disimpan", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Gagal menyimpan event", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Gagal menyimpan event", Toast.LENGTH_SHORT).show();
                                });
                    }
                },
                () -> {
                    Toast.makeText(this, "Upload gambar gagal", Toast.LENGTH_SHORT).show();
                }
        );
    }


    private void openImagePicker(){
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
                        Toast.makeText(EventFormActivity.this, "Gambar berhasil diunggah", Toast.LENGTH_SHORT).show();
                        onSuccess.run();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        binding.btnUploadImage.setEnabled(true);
                        binding.btnUploadImage.setText("Unggah Gambar");
                        Toast.makeText(EventFormActivity.this, "Gagal mengunggah gambar: " + error.getDescription(), Toast.LENGTH_SHORT).show();
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

    private void showDatePicker(boolean isMulai) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, y, m, d) -> {
            calendar.set(y, m, d);
            Date selectedDate = calendar.getTime();
            String formatted = sdf.format(selectedDate);

            if (isMulai) {
                tanggalMulai = selectedDate;
                binding.etTanggalMulai.setText(formatted);
            } else {
                tanggalAkhir = selectedDate;
                binding.etTanggalAkhir.setText(formatted);
            }
        }, year, month, day).show();
    }
}