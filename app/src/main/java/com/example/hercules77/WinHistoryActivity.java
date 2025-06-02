package com.example.hercules77;

import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WinHistoryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;
    private RecyclerView recyclerView;
    private WinHistoryAdapter adapter;
    private ArrayList<WinHistory> winHistoryList;
    private FirebaseFirestore db;
    private int selectedItemPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_history);

        recyclerView = findViewById(R.id.recyclerWinHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();

        loadWinHistories();
    }

    private void loadWinHistories() {
        winHistoryList = new ArrayList<>();
        CollectionReference historyRef = db.collection("histories");

        historyRef.get().addOnSuccessListener(querySnapshot -> {
            winHistoryList.clear();

            for (QueryDocumentSnapshot doc : querySnapshot) {
                String id = doc.getId();

                String userId = doc.getString("userId");  // Firestore pakai userId
                Long jumlahMenang = doc.getLong("amount"); // Firestore pakai amount

                String imageUrl = doc.getString("imageUrl");

                String status = doc.getString("result");  // misalnya ini status (win/lose)

                Boolean isVerified = doc.getBoolean("isVerified");  // field ini optional

                String tanggalMenang = "";
                Object tanggalObj = doc.get("timestamp");

                if (tanggalObj instanceof Number) {
                    long time = ((Number) tanggalObj).longValue();
                    Date date = new Date(time);
                    tanggalMenang = DateFormat.getDateInstance().format(date);
                } else if (tanggalObj instanceof String) {
                    tanggalMenang = (String) tanggalObj;
                }

                winHistoryList.add(new WinHistory(
                        id,
                        userId != null ? userId : "",
                        jumlahMenang != null ? jumlahMenang.intValue() : 0,
                        tanggalMenang,
                        imageUrl != null ? imageUrl : "",
                        status != null ? status : "",
                        isVerified != null ? isVerified : false
                ));
            }

            adapter = new WinHistoryAdapter(winHistoryList, this, new WinHistoryAdapter.OnItemClickListener() {
                @Override
                public void onViewImage(String imageUrl) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
                    startActivity(intent);
                }

                @Override
                public void onDelete(String id) {
                    showDeleteConfirmation(id);
                }

                @Override
                public void onVerify(String id, boolean currentStatus) {
                    boolean newStatus = !currentStatus;
                    db.collection("histories").document(id)
                            .update("isVerified", newStatus)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(WinHistoryActivity.this,
                                        newStatus ? "Data terverifikasi" : "Verifikasi dibatalkan",
                                        Toast.LENGTH_SHORT).show();
                                loadWinHistories();
                            })
                            .addOnFailureListener(e -> Toast.makeText(WinHistoryActivity.this,
                                    "Gagal mengupdate status", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onDownload(String imageUrl) {
                    startDownload(imageUrl);
                }

                @Override
                public void onImageClicked(int position) {
                    selectedItemPosition = position;
                    pickImageFromGallery();
                }
            });

            recyclerView.setAdapter(adapter);

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
        });
    }


    private void showDeleteConfirmation(String id) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Riwayat")
                .setMessage("Yakin ingin menghapus data ini?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    db.collection("histories").document(id).delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show();
                                loadWinHistories();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void startDownload(String url) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle("Downloading Image");
            request.setDescription("Downloading bukti gambar");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                    "image_" + System.currentTimeMillis() + ".jpg");
            DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            if (manager != null) manager.enqueue(request);
            Toast.makeText(this, "Download dimulai", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Gagal mulai download: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null && selectedItemPosition != -1) {
                String filePath = getRealPathFromUri(imageUri);
                if (filePath != null) {
                    File file = new File(filePath);
                    uploadImageToCloudinary(file);
                } else {
                    Toast.makeText(this, "Gagal mendapatkan path file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getRealPathFromUri(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private void uploadImageToCloudinary(File file) {
        String uploadPreset = "pam-project"; // TODO: Ganti dengan preset Cloudinary Anda

        CloudinaryUploader.uploadImage(file, uploadPreset, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(WinHistoryActivity.this, "Upload gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonString = response.body().string();
                    try {
                        JSONObject json = new JSONObject(jsonString);
                        String imageUrl = json.getString("secure_url");

                        if (selectedItemPosition != -1) {
                            WinHistory item = winHistoryList.get(selectedItemPosition);
                            db.collection("histories").document(item.getId())
                                    .update("buktiGambarUrl", imageUrl)
                                    .addOnSuccessListener(unused -> runOnUiThread(() -> {
                                        Toast.makeText(WinHistoryActivity.this, "Upload berhasil dan data diperbarui", Toast.LENGTH_SHORT).show();
                                        loadWinHistories();
                                    }))
                                    .addOnFailureListener(e -> runOnUiThread(() ->
                                            Toast.makeText(WinHistoryActivity.this, "Gagal update Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    ));
                        }
                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(WinHistoryActivity.this, "Parsing respons gagal", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(WinHistoryActivity.this, "Upload gagal, response tidak sukses", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
