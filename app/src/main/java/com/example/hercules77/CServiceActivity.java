package com.example.hercules77;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CServiceActivity extends AppCompatActivity {

    Button btn_whatsapp, btn_website, btn_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cservice);

        btn_whatsapp = findViewById(R.id.btn_whatsapp);
        btn_website = findViewById(R.id.btn_website);
        btn_email = findViewById(R.id.btn_email);

        btn_website.setOnClickListener(v -> {
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
            websiteIntent.setData(Uri.parse("https://s.id/bersamastopjudol"));
            startActivity(websiteIntent);
        });

        btn_whatsapp.setOnClickListener(v -> {
            Intent messageIntent = new Intent(Intent.ACTION_VIEW);
            messageIntent.setData(Uri.parse("https://api.whatsapp.com/send/?phone=%2B6281110015080&text&type=phone_number&app_absent=0"));
            startActivity(messageIntent);
        });

        btn_email.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:antijudol77@gmail.com"));
            startActivity(emailIntent);
        });
    }
}