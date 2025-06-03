package com.example.hercules77;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.hercules77.databinding.ActivityTipsBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class TipsActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityTipsBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Tips> tipsList;
    private TipsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTipsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        setupRecyclerView();

        binding.btnAddTips.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }

    private void setupRecyclerView() {
        tipsList = new ArrayList<>();
        adapter = new TipsAdapter(tipsList, db);
        binding.rvTips.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTips.setAdapter(adapter);
        loadTips();
    }

    private void loadTips() {
        db.collection("tips").get().addOnSuccessListener(queryDocumentSnapshots -> {
            tipsList.clear();
            for (var document : queryDocumentSnapshots) {
                Tips tips = document.toObject(Tips.class);
                tips.setId(document.getId());
                tipsList.add(tips);
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal memuat tips", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAddTips) {
            Intent intent = new Intent(this, TipsFormActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btnBack) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTips();
    }
}