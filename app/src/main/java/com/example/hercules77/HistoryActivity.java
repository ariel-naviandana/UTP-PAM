package com.example.hercules77;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hercules77.databinding.ActivityHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.example.hercules77.History;

import com.google.firebase.Timestamp;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding binding;
    private ArrayList<History> gameHistories = new ArrayList<>();
    private HistoryAdapter adapter;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        adapter = new HistoryAdapter(this, gameHistories);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        binding.btnBack.setOnClickListener(v -> onBackPressed());

        fetchHistoryFromFirestore();
    }

    private void fetchHistoryFromFirestore() {
        db.collection("histories")
                .whereEqualTo("idUser", userId)
                .orderBy("tanggalMenang", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    gameHistories.clear(); // kosongkan sebelum isi ulang
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Timestamp tanggal = doc.getTimestamp("tanggalMenang");
                        int jumlah = doc.getLong("jumlahMenang").intValue();
                        boolean isVerified = doc.getBoolean("isVerified") != null && doc.getBoolean("isVerified");

                        // Deteksi tipe game dari jumlah kemenangan atau dari field "gameType" jika disimpan
                        History.GameType type = jumlah > 500000 ? History.GameType.SLOT : History.GameType.COIN_FLIP;

                        gameHistories.add(new History(type, tanggal, isVerified, jumlah)); // ✅ Parameter cocok
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal ambil data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
