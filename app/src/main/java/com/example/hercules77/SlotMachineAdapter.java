package com.example.hercules77;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

    public SlotMachineAdapter(ArrayList<SlotMachine> slotMachineList, FirebaseFirestore db) {
        this.slotMachineList = slotMachineList;
        this.db = db;
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
                    .error(R.drawable.ic_slot)
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
            showDeleteConfirmationDialog(context, slotMachine, position);
        });

        holder.btnDownloadSlotMachine.setOnClickListener(v -> {
            if (slotMachine.getGambarUrl() == null || slotMachine.getGambarUrl().isEmpty()) {
                Toast.makeText(context, "Tidak ada gambar untuk diunduh", Toast.LENGTH_SHORT).show();
                return;
            }
            downloadImage(context, slotMachine.getGambarUrl(), slotMachine.getNamaMesin());
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

    private void downloadImage(Context context, String imageUrl, String namaMesin) {
        // Check storage permission for Android < 13
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Izin penyimpanan diperlukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        try {
                            // Use app-specific external storage (no permission needed for Android 10+)
                            File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "SlotMachines");
                            if (!directory.exists() && !directory.mkdirs()) {
                                Toast.makeText(context, "Gagal membuat direktori penyimpanan", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String filename = "slot_" + namaMesin.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ".jpg";
                            File file = new File(directory, filename);

                            try (FileOutputStream out = new FileOutputStream(file)) {
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            }

                            // Notify user and provide option to view/share
                            Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(fileUri, "image/*");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            if (intent.resolveActivity(context.getPackageManager()) != null) {
                                context.startActivity(Intent.createChooser(intent, "Buka Gambar"));
                            } else {
                                Toast.makeText(context, "Tidak ada aplikasi untuk membuka gambar", Toast.LENGTH_SHORT).show();
                            }

                            Toast.makeText(context, "Gambar disimpan di: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

                        } catch (IOException e) {
                            Toast.makeText(context, "Gagal menyimpan gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    public void onLoadFailed(@NonNull Exception e) {
                        Toast.makeText(context, "Gagal mengunduh gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        View btnEditSlotMachine, btnDeleteSlotMachine, btnDownloadSlotMachine;

        public SlotMachineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaMesin = itemView.findViewById(R.id.tvNamaMesin);
            tvTipe = itemView.findViewById(R.id.tvTipe);
            ivGambar = itemView.findViewById(R.id.ivGambar);
            btnEditSlotMachine = itemView.findViewById(R.id.btnEditSlotMachine);
            btnDeleteSlotMachine = itemView.findViewById(R.id.btnDeleteSlotMachine);
            btnDownloadSlotMachine = itemView.findViewById(R.id.btnDownloadSlotMachine);
        }
    }
}