package com.example.hercules77;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}