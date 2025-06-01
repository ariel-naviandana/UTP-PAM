package com.example.hercules77;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

public class HomeAdminActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkStoragePermission();
        setContentView(R.layout.activity_home_admin);

        // Initialize CardViews
        CardView cardSlotMachine = findViewById(R.id.cardSlotMachine);
        CardView cardEvent = findViewById(R.id.cardEvent);
        CardView cardTips = findViewById(R.id.cardTips);
        CardView cardUser = findViewById(R.id.cardUser);
        CardView cardWinHistory = findViewById(R.id.cardWinHistory);
        CardView cardBanner = findViewById(R.id.cardBanner);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Set onClick listeners for navigation
        cardSlotMachine.setOnClickListener(v -> {
            Intent intent = new Intent(HomeAdminActivity.this, SlotMachineActivity.class);
            startActivity(intent);
        });

        cardEvent.setOnClickListener(v -> {
            Intent intent = new Intent(HomeAdminActivity.this, EventActivity.class);
            startActivity(intent);
        });

        cardTips.setOnClickListener(v -> {
            Intent intent = new Intent(HomeAdminActivity.this, TipsActivity.class);
            startActivity(intent);
        });

        cardUser.setOnClickListener(v -> {
            Intent intent = new Intent(HomeAdminActivity.this, UserActivity.class);
            startActivity(intent);
        });

        cardWinHistory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeAdminActivity.this, WinHistoryActivity.class);
            startActivity(intent);
        });

        cardBanner.setOnClickListener(v -> {
            Intent intent = new Intent(HomeAdminActivity.this, BannerActivity.class);
            startActivity(intent);
        });

        // Tombol Logout
        btnLogout.setOnClickListener(v -> {
            // Hapus login session
            getSharedPreferences("login_session", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            Intent intent = new Intent(HomeAdminActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Cek izin akses penyimpanan
    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Izin belum diberikan, minta izin
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        } else {
            // Izin sudah diberikan, bisa langsung lanjut
            Toast.makeText(this, "Izin penyimpanan sudah diberikan", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin penyimpanan diterima", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(this, "Izin penyimpanan ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

}