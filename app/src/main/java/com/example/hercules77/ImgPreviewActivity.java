package com.example.hercules77;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class ImgPreviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_preview);

        ImageView imageView = findViewById(R.id.imageViewFull);
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_banner_placeholder)
                .into(imageView);

        imageView.setOnClickListener(v -> finish()); // Klik gambar untuk keluar, opsional
    }
}