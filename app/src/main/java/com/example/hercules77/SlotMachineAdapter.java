package com.example.hercules77;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SlotMachineAdapter extends RecyclerView.Adapter<SlotMachineAdapter.SlotMachineViewHolder> {
    private ArrayList<SlotMachine> slotMachineList;
    private FirebaseFirestore db;
    private Context context;
    private OnPlayListener onPlayListener;

    public interface OnPlayListener {
        void onPlay(SlotMachine slotMachine);
    }

    public SlotMachineAdapter(ArrayList<SlotMachine> slotMachineList, FirebaseFirestore db, OnPlayListener onPlayListener) {
        this.slotMachineList = slotMachineList;
        this.db = db;
        this.onPlayListener = onPlayListener;
    }

    @NonNull
    @Override
    public SlotMachineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot_machine, parent, false);
        this.context = parent.getContext();
        return new SlotMachineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotMachineViewHolder holder, int position) {
        SlotMachine slotMachine = slotMachineList.get(position);
        holder.tvNamaMesin.setText(slotMachine.getNamaMesin());
        holder.tvTipe.setText("Tipe: " + slotMachine.getTipe());

        if (slotMachine.getGambarUrl() != null && !slotMachine.getGambarUrl().isEmpty()) {
            Glide.with(context)
                    .load(slotMachine.getGambarUrl())
                    .placeholder(R.drawable.ic_slot)
                    .into(holder.ivGambar);
        } else {
            holder.ivGambar.setImageResource(R.drawable.ic_slot);
        }

        holder.btnEditSlotMachine.setOnClickListener(v -> {
            Intent intent = new Intent(context, SlotMachineFormActivity.class);
            intent.putExtra("SLOT_MACHINE_ID", slotMachine.getId());
            context.startActivity(intent);
        });

        holder.btnDeleteSlotMachine.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Mesin Slot")
                    .setMessage("Yakin ingin menghapus mesin \"" + slotMachine.getNamaMesin() + "\"?")
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        db.collection("slot_machines").document(slotMachine.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Berhasil dihapus", Toast.LENGTH_SHORT).show();
                                    slotMachineList.remove(position);
                                    notifyItemRemoved(position);
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Gagal menghapus", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        holder.btnDownloadSlotMachine.setOnClickListener(v -> {
            if (slotMachine.getGambarUrl() == null || slotMachine.getGambarUrl().isEmpty()) {
                Toast.makeText(context, "Tidak ada gambar untuk diunduh", Toast.LENGTH_SHORT).show();
                return;
            }
            downloadImage(slotMachine.getGambarUrl(), slotMachine.getNamaMesin());
        });

        holder.btnPlaySlotMachine.setOnClickListener(v -> {
            if (onPlayListener != null) {
                onPlayListener.onPlay(slotMachine);
            }
        });
    }

    private void downloadImage(String imageUrl, String namaMesin) {
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        try {
                            File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "SlotMachines");
                            if (!dir.exists()) dir.mkdirs();
                            File file = new File(dir, "slot_" + namaMesin + ".jpg");
                            try (FileOutputStream out = new FileOutputStream(file)) {
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            }

                            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "image/*");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            context.startActivity(Intent.createChooser(intent, "Lihat Gambar"));
                        } catch (IOException e) {
                            Toast.makeText(context, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@NonNull Drawable placeholder) {}
                });
    }

    @Override
    public int getItemCount() {
        return slotMachineList.size();
    }

    public static class SlotMachineViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaMesin, tvTipe;
        ImageView ivGambar;
        View btnEditSlotMachine, btnDeleteSlotMachine, btnDownloadSlotMachine, btnPlaySlotMachine;

        public SlotMachineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaMesin = itemView.findViewById(R.id.tvNamaMesin);
            tvTipe = itemView.findViewById(R.id.tvTipe);
            ivGambar = itemView.findViewById(R.id.ivGambar);
            btnEditSlotMachine = itemView.findViewById(R.id.btnEditSlotMachine);
            btnDeleteSlotMachine = itemView.findViewById(R.id.btnDeleteSlotMachine);
            btnDownloadSlotMachine = itemView.findViewById(R.id.btnDownloadSlotMachine);
            btnPlaySlotMachine = itemView.findViewById(R.id.btnPlaySlotMachine);
        }
    }
}
