package com.example.hercules77;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hercules77.databinding.ActivityEventBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity {
    private ActivityEventBinding binding;
    private FirebaseFirestore db;
    private ArrayList<Event> eventList;
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventFormActivity.class);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        eventList = new ArrayList<>();
        adapter = new EventAdapter(eventList, db);
        binding.recyclerViewEvent.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewEvent.setAdapter(adapter);
        loadEvents();
    }

    private void loadEvents() {
        db.collection("events").get().addOnSuccessListener(queryDocumentSnapshots -> {
            eventList.clear();
            for (var document : queryDocumentSnapshots) {
                Event event = document.toObject(Event.class);
                event.setId(document.getId());
                eventList.add(event);
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal memuat event", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }
}