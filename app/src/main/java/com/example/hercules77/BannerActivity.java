package com.example.hercules77;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.hercules77.databinding.ActivityBannerBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class BannerActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityBannerBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Banner> bannerList;
    private BannerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        setupRecyclerView();

        binding.btnAddBanner.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }

    private void setupRecyclerView() {
        bannerList = new ArrayList<>();
        adapter = new BannerAdapter(bannerList, db);
        binding.recyclerViewBanners.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewBanners.setAdapter(adapter);
        loadBanners();
    }

    private void loadBanners() {
        db.collection("banners").get().addOnSuccessListener(queryDocumentSnapshots -> {
            bannerList.clear();
            for (var document : queryDocumentSnapshots) {
                Banner banner = document.toObject(Banner.class);
                banner.setId(document.getId());
                bannerList.add(banner);
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal memuat banner", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAddBanner) {
            Intent intent = new Intent(this, BannerFormActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btnBack) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBanners();
    }
}