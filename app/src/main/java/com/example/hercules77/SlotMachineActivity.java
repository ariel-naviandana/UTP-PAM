package com.example.hercules77;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.hercules77.databinding.ActivitySlotMachineBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SlotMachineActivity extends AppCompatActivity {

    private ActivitySlotMachineBinding binding;
    private FirebaseFirestore db;
    private ArrayList<SlotMachine> slotMachineList;
    private SlotMachineAdapter adapter;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySlotMachineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        random = new Random();

        setupRecyclerView();

        binding.btnAddSlotMachine.setOnClickListener(view -> {
            startActivity(new Intent(this, SlotMachineFormActivity.class));
        });
    }

    private void setupRecyclerView() {
        slotMachineList = new ArrayList<>();
        adapter = new SlotMachineAdapter(slotMachineList, db, this::simpanHasilPermainan);
        binding.recyclerViewSlotMachines.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewSlotMachines.setAdapter(adapter);
        loadSlotMachines();
    }

    private void loadSlotMachines() {
        db.collection("slot_machines").get().addOnSuccessListener(queryDocumentSnapshots -> {
            slotMachineList.clear();
            for (var document : queryDocumentSnapshots) {
                SlotMachine slotMachine = document.toObject(SlotMachine.class);
                slotMachine.setId(document.getId());
                slotMachineList.add(slotMachine);
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Gagal memuat mesin slot", Toast.LENGTH_SHORT).show()
        );
    }

    private void simpanHasilPermainan(SlotMachine slotMachine) {
        String result = random.nextBoolean() ? "WIN" : "LOSE";

        Map<String, Object> history = new HashMap<>();
        history.put("game", "Slot Machine");
        history.put("slotMachineId", slotMachine.getId());
        history.put("namaMesin", slotMachine.getNamaMesin());
        history.put("tipe", slotMachine.getTipe());
        history.put("gambarUrl", slotMachine.getGambarUrl());
        history.put("status", result);
        history.put("timestamp", System.currentTimeMillis());

        db.collection("histories").add(history)
                .addOnSuccessListener(docRef -> Toast.makeText(this, "Hasil disimpan: " + result, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal menyimpan hasil", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSlotMachines();
    }
}
