package com.example.hercules77;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.hercules77.databinding.ActivitySlotMachineFormBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SlotMachineFormActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySlotMachineFormBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imageUri;
    private String imageUrl;
    private String slotMachineId;
    private boolean isCloudinaryInitialized = false;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySlotMachineFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        initCloudinary();
        setupTipeSpinner();

        binding.btnBack.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
        binding.btnUploadImage.setOnClickListener(this);

        slotMachineId = getIntent().getStringExtra("SLOT_MACHINE_ID");
        if (slotMachineId != null) {
            binding.title.setText("Edit Mesin Slot");
            loadSlotMachineData();
        } else {
            binding.title.setText("Tambah Mesin Slot");
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

    private void setupTipeSpinner() {
        String[] tipeOptions = {"Klasik", "Video", "Progresif", "3D"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipeOptions);
        binding.spinnerTipe.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnBack) {
            finish();
        } else if (id == R.id.btnSave) {
            saveSlotMachine();
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
                        Toast.makeText(SlotMachineFormActivity.this, "Gambar berhasil diunggah", Toast.LENGTH_SHORT).show();
                        onSuccess.run();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        binding.btnUploadImage.setEnabled(true);
                        binding.btnUploadImage.setText("Unggah Gambar");
                        Toast.makeText(SlotMachineFormActivity.this, "Gagal mengunggah gambar: " + error.getDescription(), Toast.LENGTH_SHORT).show();
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

    private void saveSlotMachine() {
        String namaMesin = binding.etNamaMesin.getText().toString().trim();
        String deskripsi = binding.etDeskripsi.getText().toString().trim();
        String tipe = binding.spinnerTipe.getSelectedItem().toString();

        if (namaMesin.isEmpty() || deskripsi.isEmpty()) {
            Toast.makeText(this, "Nama mesin dan deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImageToCloudinary(
                () -> {
                    Map<String, Object> slotMachineData = new HashMap<>();
                    slotMachineData.put("namaMesin", namaMesin);
                    slotMachineData.put("deskripsi", deskripsi);
                    slotMachineData.put("tipe", tipe);
                    slotMachineData.put("gambarUrl", imageUrl != null ? imageUrl : "");

                    if (slotMachineId != null) {
                        db.collection("slot_machines").document(slotMachineId)
                                .set(slotMachineData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Mesin slot berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Gagal memperbarui mesin slot", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        db.collection("slot_machines").add(slotMachineData)
                                .addOnSuccessListener(documentReference -> {
                                    slotMachineData.put("id", documentReference.getId());
                                    db.collection("slot_machines").document(documentReference.getId())
                                            .set(slotMachineData)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "Mesin slot berhasil disimpan", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Gagal menyimpan mesin slot", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Gagal menyimpan mesin slot", Toast.LENGTH_SHORT).show();
                                });
                    }
                },
                () -> {}
        );
    }

    private void loadSlotMachineData() {
        if (slotMachineId == null) return;

        db.collection("slot_machines").document(slotMachineId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        binding.etNamaMesin.setText(document.getString("namaMesin"));
                        binding.etDeskripsi.setText(document.getString("deskripsi"));
                        String tipe = document.getString("tipe");
                        binding.spinnerTipe.setSelection(getTipeIndex(tipe));
                        String savedImageUrl = document.getString("gambarUrl");
                        if (savedImageUrl != null && !savedImageUrl.isEmpty()) {
                            imageUrl = savedImageUrl;
                            Glide.with(this).load(savedImageUrl).into(binding.ivPreview);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memuat data mesin slot", Toast.LENGTH_SHORT).show();
                });
    }

    private int getTipeIndex(String tipe) {
        String[] tipeOptions = {"Klasik", "Video", "Progresif", "3D"};
        for (int i = 0; i < tipeOptions.length; i++) {
            if (tipeOptions[i].equals(tipe)) {
                return i;
            }
        }
        return 0;
    }
}