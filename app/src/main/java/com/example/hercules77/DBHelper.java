package com.example.hercules77;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DBHelper {
    private final FirebaseFirestore db;
    private final Context context;

    public DBHelper(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Menambahkan data kemenangan ke Firestore
     */
    public void insertWinHistory(String idUser, int jumlahMenang, String tanggalMenang, String buktiGambarUrl) {
        Map<String, Object> history = new HashMap<>();
        history.put("idUser", idUser);
        history.put("jumlahMenang", jumlahMenang);
        history.put("tanggalMenang", tanggalMenang);
        history.put("buktiGambarUrl", buktiGambarUrl);
        history.put("status", "pending");
        history.put("isVerified", false);

        db.collection("histories")
                .add(history)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(context, "Riwayat kemenangan berhasil disimpan", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Gagal menyimpan riwayat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Memperbarui status verifikasi (true / false)
     */
    public void updateVerificationStatus(String historyId, boolean isVerified) {
        Map<String, Object> update = new HashMap<>();
        update.put("isVerified", isVerified);

        db.collection("histories")
                .document(historyId)
                .update(update)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Status verifikasi diperbarui", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Gagal memperbarui status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Menghapus data riwayat berdasarkan ID dokumen
     */
    public void deleteWinHistory(String historyId) {
        db.collection("histories")
                .document(historyId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Riwayat kemenangan berhasil dihapus", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Gagal menghapus riwayat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
