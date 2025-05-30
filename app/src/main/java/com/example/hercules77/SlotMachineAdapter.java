package com.example.hercules77;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class SlotMachineAdapter extends RecyclerView.Adapter<SlotMachineAdapter.SlotMachineViewHolder> {
    private ArrayList<SlotMachine> slotMachineList;
    private FirebaseFirestore db;

    public SlotMachineAdapter(ArrayList<SlotMachine> slotMachineList, FirebaseFirestore db) {
        this.slotMachineList = slotMachineList;
        this.db = db;
    }

    @NonNull
    @Override
    public SlotMachineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot_machine, parent, false);
        return new SlotMachineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotMachineViewHolder holder, int position) {
        SlotMachine slotMachine = slotMachineList.get(position);
        holder.tvNamaMesin.setText(slotMachine.getNamaMesin());
        holder.tvTipe.setText("Tipe: " + slotMachine.getTipe());

        if (slotMachine.getGambarUrl() != null && !slotMachine.getGambarUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(slotMachine.getGambarUrl())
                    .placeholder(R.drawable.ic_slot)
                    .error(R.drawable.ic_slot)
                    .into(holder.ivGambar);
        } else {
            holder.ivGambar.setImageResource(R.drawable.ic_slot);
        }

        holder.btnEditSlotMachine.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SlotMachineFormActivity.class);
            intent.putExtra("SLOT_MACHINE_ID", slotMachine.getId());
            v.getContext().startActivity(intent);
        });

        holder.btnDeleteSlotMachine.setOnClickListener(v -> {
            showDeleteConfirmationDialog(v.getContext(), slotMachine, position);
        });
    }

    private void showDeleteConfirmationDialog(Context context, SlotMachine slotMachine, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Konfirmasi Penghapusan")
                .setMessage("Apakah Anda benar-benar ingin menghapus mesin slot \"" + slotMachine.getNamaMesin() + "\"? Tindakan ini tidak dapat dibatalkan.")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    db.collection("slot_machines").document(slotMachine.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Mesin slot \"" + slotMachine.getNamaMesin() + "\" berhasil dihapus", Toast.LENGTH_SHORT).show();
                                slotMachineList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, slotMachineList.size());
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Gagal menghapus mesin slot: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    @Override
    public int getItemCount() {
        return slotMachineList.size();
    }

    public static class SlotMachineViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaMesin, tvTipe;
        ImageView ivGambar;
        View btnEditSlotMachine, btnDeleteSlotMachine;

        public SlotMachineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaMesin = itemView.findViewById(R.id.tvNamaMesin);
            tvTipe = itemView.findViewById(R.id.tvTipe);
            ivGambar = itemView.findViewById(R.id.ivGambar);
            btnEditSlotMachine = itemView.findViewById(R.id.btnEditSlotMachine);
            btnDeleteSlotMachine = itemView.findViewById(R.id.btnDeleteSlotMachine);
        }
    }
}