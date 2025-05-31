package com.example.hercules77;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2000ms = 2 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Pastikan layout splash tidak punya tombol

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("login_session", MODE_PRIVATE);
                String username = prefs.getString("username", null);

                if (username != null) {
                    // Sudah login
                    if (username.equals("admin")){
                        Intent intent = new Intent(SplashActivity.this, HomeAdminActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                } else {
                    // Belum login
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }

                finish(); // Selesai splash
            }
        }, SPLASH_DELAY);
    }
}
