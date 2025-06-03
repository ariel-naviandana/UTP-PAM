package com.example.hercules77;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.TipsViewHolder> {
    private ArrayList<Tips> tipsList;
    private FirebaseFirestore db;
    private Context context;

    public TipsAdapter(ArrayList<Tips> tipsList, FirebaseFirestore db) {
        this.tipsList = tipsList;
        this.db = db;
    }

    @NonNull
    @Override
    public TipsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tips, parent, false);
        this.context = parent.getContext();
        return new TipsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsViewHolder holder, int position) {
        Tips tips = tipsList.get(position);
        holder.tvJudul.setText(tips.getJudul());
        holder.tvKategori.setText(tips.getKategori());
        holder.tvKonten.setText(tips.getKonten());

        if (tips.getGambarIlustrasiUrl() != null && !tips.getGambarIlustrasiUrl().isEmpty()) {
            Glide.with(context)
                    .load(tips.getGambarIlustrasiUrl())
                    .placeholder(R.drawable.ic_banner_placeholder)
                    .error(R.drawable.ic_banner_placeholder)
                    .into(holder.ivGambar);
        } else {
            holder.ivGambar.setImageResource(R.drawable.ic_banner_placeholder);
        }

        holder.btnDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(context, tips, position);
        });

        holder.btnDownload.setOnClickListener(v -> {
            if (tips.getGambarIlustrasiUrl() == null || tips.getGambarIlustrasiUrl().isEmpty()) {
                Toast.makeText(context, "Tidak ada gambar untuk diunduh", Toast.LENGTH_SHORT).show();
                return;
            }
            downloadImage(context, tips.getGambarIlustrasiUrl(), tips.getJudul());
        });

        holder.ivGambar.setOnClickListener(v -> {
            Intent intent = new Intent(context, ImgPreviewActivity.class);
            intent.putExtra("IMAGE_URL", tips.getGambarIlustrasiUrl());
            context.startActivity(intent);
        });
    }

    private void showDeleteConfirmationDialog(Context context, Tips tips, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Konfirmasi Penghapusan")
                .setMessage("Hapus tips \"" + tips.getJudul() + "\"? Tindakan ini tidak dapat dibatalkan.")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    db.collection("tips").document(tips.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Tips \"" + tips.getJudul() + "\" berhasil dihapus", Toast.LENGTH_SHORT).show();
                                tipsList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, tipsList.size());
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Gagal menghapus tips: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void downloadImage(Context context, String imageUrl, String judulTips) {
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
                        String filename = "tips_" + judulTips.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ".jpg";

                        OutputStream fos;
                        Uri imageUri;

                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TipsIlustrasi");

                                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                if (uri == null) {
                                    Toast.makeText(context, "Gagal membuat entri media", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                fos = context.getContentResolver().openOutputStream(uri);
                                imageUri = uri;
                            } else {
                                File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TipsIlustrasi");
                                if (!directory.exists() && !directory.mkdirs()) {
                                    Toast.makeText(context, "Gagal membuat folder", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                File file = new File(directory, filename);
                                fos = new FileOutputStream(file);
                                imageUri = Uri.fromFile(file);

                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri);
                                context.sendBroadcast(mediaScanIntent);
                            }

                            if (fos != null) {
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();

                                Toast.makeText(context, "Gambar berhasil diunduh", Toast.LENGTH_LONG).show();
                            }

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(imageUri, "image/*");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            if (intent.resolveActivity(context.getPackageManager()) != null) {
                                context.startActivity(Intent.createChooser(intent, "Lihat gambar dengan"));
                            }

                        } catch (IOException e) {
                            Toast.makeText(context, "Gagal menyimpan gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@NonNull Drawable placeholder) {
                    }
                });
    }

    @Override
    public int getItemCount() {
        return tipsList.size();
    }

    public static class TipsViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvKategori, tvKonten;
        ImageView ivGambar;
        Button btnDelete, btnDownload;

        public TipsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvJudulTips);
            tvKategori = itemView.findViewById(R.id.tvKategori);
            tvKonten = itemView.findViewById(R.id.tvKonten);
            ivGambar = itemView.findViewById(R.id.ivGambar);
            btnDelete = itemView.findViewById(R.id.btnDeleteTips);
            btnDownload = itemView.findViewById(R.id.btnDownloadTips);
        }
    }
}