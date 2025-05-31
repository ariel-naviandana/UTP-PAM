package com.example.hercules77;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.hercules77.databinding.ActivitySlotMachineBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class SlotMachineActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySlotMachineBinding binding;
    private FirebaseFirestore db;
    private ArrayList<SlotMachine> slotMachineList;
    private SlotMachineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySlotMachineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        setupRecyclerView();

        binding.btnAddSlotMachine.setOnClickListener(this);
    }

    private void setupRecyclerView() {
        slotMachineList = new ArrayList<>();
        adapter = new SlotMachineAdapter(slotMachineList, db);
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
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal memuat mesin slot", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAddSlotMachine) {
            Intent intent = new Intent(this, SlotMachineFormActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSlotMachines();
    }
}