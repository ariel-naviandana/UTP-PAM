package com.example.hercules77;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hercules77.databinding.ActivityEventBinding;

public class EventActivity extends AppCompatActivity {

    private ActivityEventBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventFormActivity.class);
            startActivity(intent);
        });

    }
}