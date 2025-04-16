package com.example.hercules77;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hercules77.databinding.ActivityHistoryBinding;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private ActivityHistoryBinding binding;
    private ArrayList<History> gameHistories = new ArrayList<>();
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        generateDummyData();

        adapter = new HistoryAdapter(this, gameHistories);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        binding.btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void generateDummyData() {
        gameHistories.add(new History(History.GameType.SLOT, "2025-03-05 12:30", false, 1500000));
        gameHistories.add(new History(History.GameType.COIN_FLIP, "2025-03-05 10:15", false, 1200000));
        gameHistories.add(new History(History.GameType.SLOT, "2025-03-04 18:00", false, 900000));
        gameHistories.add(new History(History.GameType.COIN_FLIP, "2025-03-03 16:45", false, 750000));
        gameHistories.add(new History(History.GameType.SLOT, "2025-03-02 15:00", false, 300000));
        gameHistories.add(new History(History.GameType.COIN_FLIP, "2025-03-02 13:30", false, 150000));
        gameHistories.add(new History(History.GameType.SLOT, "2025-03-01 12:00", false, 200000));
        gameHistories.add(new History(History.GameType.COIN_FLIP, "2025-03-01 10:30", false, 100000));
        gameHistories.add(new History(History.GameType.SLOT, "2025-02-07 14:15", false, 1200000));
        gameHistories.add(new History(History.GameType.COIN_FLIP, "2025-02-07 13:00", true, 850000));
        gameHistories.add(new History(History.GameType.SLOT, "2025-02-06 11:45", true, 650000));
        gameHistories.add(new History(History.GameType.COIN_FLIP, "2025-02-06 10:30", false, 200000));
        gameHistories.add(new History(History.GameType.SLOT, "2025-02-05 16:15", false, 400000));
        gameHistories.add(new History(History.GameType.COIN_FLIP, "2025-02-04 15:00", true, 1000000));
        gameHistories.add(new History(History.GameType.SLOT, "2025-02-03 13:45", false, 250000));
        gameHistories.add(new History(History.GameType.COIN_FLIP, "2025-02-03 09:15", true, 500000));
        gameHistories.add(new History(History.GameType.SLOT, "2025-02-02 14:30", true, 600000));
        gameHistories.add(new History(History.GameType.COIN_FLIP, "2025-02-02 11:00", false, 300000));
        gameHistories.add(new History(History.GameType.SLOT, "2025-02-01 12:45", true, 900000));
        gameHistories.add(new History(History.GameType.COIN_FLIP, "2025-02-01 10:30", false, 150000));
        gameHistories.add(new History(History.GameType.SLOT, "2025-01-23 16:30", true, 700000));
        gameHistories.add(new History(History.GameType.COIN_FLIP, "2025-01-22 14:00", true, 950000));
        gameHistories.add(new History(History.GameType.SLOT, "2025-01-21 11:15", true, 800000));
        gameHistories.add(new History(History.GameType.COIN_FLIP, "2025-01-21 09:30", true, 1200000));
        gameHistories.add(new History(History.GameType.SLOT, "2025-01-20 12:00", true, 500000));
        gameHistories.add(new History(History.GameType.COIN_FLIP, "2025-01-20 10:00", true, 750000));
    }
}
