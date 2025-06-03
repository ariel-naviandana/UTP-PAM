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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private ArrayList<Event> eventList;
    private FirebaseFirestore db;
    private Context context;

    public EventAdapter(ArrayList<Event> eventList, FirebaseFirestore db) {
        this.eventList = eventList;
        this.db = db;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        this.context = parent.getContext();
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.tvJudulEvent.setText(event.getJudulEvent());
        holder.tvDeskripsi.setText(event.getDeskripsi());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        holder.tvTanggalMulai.setText(sdf.format(event.getTanggalMulai()));
        holder.tvTanggalAkhir.setText(sdf.format(event.getTanggalAkhir()));
        if (event.getBannerUrl() != null && !event.getBannerUrl().isEmpty()) {
            Glide.with(context)
                    .load(event.getBannerUrl())
                    .placeholder(R.drawable.ic_banner_placeholder)
                    .error(R.drawable.ic_banner_placeholder)
                    .into(holder.ivGambar);
        } else {
            holder.ivGambar.setImageResource(R.drawable.ic_banner_placeholder);
        }

        holder.btnEditEvent.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventFormActivity.class);
            intent.putExtra("EVENT_ID", event.getId());
            context.startActivity(intent);
        });

        holder.btnDeleteEvent.setOnClickListener(v -> {
            showDeleteConfirmationDialog(context, event, position);
        });

        holder.btnDownloadEvent.setOnClickListener(v -> {
            if (event.getBannerUrl() == null || event.getBannerUrl().isEmpty()) {
                Toast.makeText(context, "Tidak ada gambar untuk diunduh", Toast.LENGTH_SHORT).show();
                return;
            }
            downloadImage(context, event.getBannerUrl(), event.getJudulEvent());
        });
    }

    private void showDeleteConfirmationDialog(Context context, Event event, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Konfirmasi Penghapusan")
                .setMessage("Apakah Anda benar-benar ingin menghapus mesin slot \"" + event.getJudulEvent() + "\"? Tindakan ini tidak dapat dibatalkan.")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    db.collection("slot_machines").document(event.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Mesin slot \"" + event.getJudulEvent() + "\" berhasil dihapus", Toast.LENGTH_SHORT).show();
                                eventList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, eventList.size());
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
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudulEvent, tvDeskripsi, tvTanggalMulai, tvTanggalAkhir;
        ImageView ivGambar;
        View btnEditEvent, btnDeleteEvent, btnDownloadEvent;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudulEvent = itemView.findViewById(R.id.tvJudulEvent);
            tvDeskripsi = itemView.findViewById(R.id.tvDeskripsi);
            tvTanggalMulai = itemView.findViewById(R.id.tvTanggalMulai);
            tvTanggalAkhir = itemView.findViewById(R.id.tvTanggalAkhir);
            ivGambar = itemView.findViewById(R.id.ivGambar);
            btnEditEvent = itemView.findViewById(R.id.btnEditEvent);
            btnDeleteEvent = itemView.findViewById(R.id.btnDeleteEvent);
            btnDownloadEvent = itemView.findViewById(R.id.btnDownloadEvent);
        }
    }
}
