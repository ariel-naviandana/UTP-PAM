package com.example.hercules77;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class WinHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WinHistoryAdapter adapter;
    ArrayList<WinHistory> winHistoryList;
    FirebaseFirestore db;

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
                String idUser = doc.getString("idUser");
                Long jumlahMenang = doc.getLong("jumlahMenang");
                String tanggalMenang = doc.getString("tanggalMenang");
                String buktiGambarUrl = doc.getString("buktiGambarUrl");
                String status = doc.getString("status");
                Boolean isVerified = doc.getBoolean("isVerified");

                if (status == null) status = "";

                winHistoryList.add(new WinHistory(
                        id,
                        idUser != null ? idUser : "",
                        jumlahMenang != null ? jumlahMenang.intValue() : 0,
                        tanggalMenang != null ? tanggalMenang : "",
                        buktiGambarUrl != null ? buktiGambarUrl : "",
                        status,
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
                                loadWinHistories(); // refresh data
                            })
                            .addOnFailureListener(e -> Toast.makeText(WinHistoryActivity.this,
                                    "Gagal mengupdate status", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onDownload(String imageUrl) {
                    startDownload(imageUrl);
                }
            });

            recyclerView.setAdapter(adapter);

        }).addOnFailureListener(e ->
                Toast.makeText(this, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
        );
    }


    private void showDeleteConfirmation(String id) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Riwayat")
                .setMessage("Yakin ingin menghapus data ini?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    db.collection("histories").document(id).delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show();
                                loadWinHistories(); // reload
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void startDownload(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Downloading bukti gambar");
        request.setDescription("Mengunduh bukti screenshot");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedOverMetered(true);
        request.setAllowedOverRoaming(true);

        String fileName = "bukti_" + System.currentTimeMillis() + ".jpg";
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);

        Toast.makeText(this, "Download dimulai...", Toast.LENGTH_SHORT).show();
    }
}
