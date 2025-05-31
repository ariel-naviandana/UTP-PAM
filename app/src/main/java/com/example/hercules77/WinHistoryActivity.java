package com.example.hercules77;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WinHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WinHistoryAdapter adapter;
    ArrayList<WinHistory> winHistoryList;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_history);

        recyclerView = findViewById(R.id.recyclerWinHistory);
        dbHelper = new DBHelper(this);

        loadWinHistories();
    }

    private void loadWinHistories() {
        winHistoryList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllWinHistory();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String idUser = cursor.getString(1);
                int jumlahMenang = cursor.getInt(2);
                String tanggalMenang = cursor.getString(3);
                String buktiGambarUrl = cursor.getString(4);

                winHistoryList.add(new WinHistory(id, idUser, jumlahMenang, tanggalMenang, buktiGambarUrl));
            } while (cursor.moveToNext());

            cursor.close();
        }

        adapter = new WinHistoryAdapter(winHistoryList, this, new WinHistoryAdapter.OnItemClickListener() {
            @Override
            public void onViewImage(String imageUrl) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(imageUrl), "image/*");
                startActivity(intent);
            }

            @Override
            public void onDelete(String id) {
                showDeleteConfirmation(id);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void showDeleteConfirmation(String id) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Riwayat")
                .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    dbHelper.deleteWinHistory(id);
                    loadWinHistories();
                    Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}