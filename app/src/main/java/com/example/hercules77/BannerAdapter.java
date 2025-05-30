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

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private ArrayList<Banner> bannerList;
    private FirebaseFirestore db;
    private Context context;

    public BannerAdapter(ArrayList<Banner> bannerList, FirebaseFirestore db) {
        this.bannerList = bannerList;
        this.db = db;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        this.context = parent.getContext();
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Banner banner = bannerList.get(position);
        holder.tvJudulBanner.setText(banner.getJudulBanner());
        holder.tvDeskripsi.setText(banner.getDeskripsi());

        if (banner.getGambarBannerUrl() != null && !banner.getGambarBannerUrl().isEmpty()) {
            Glide.with(context)
                    .load(banner.getGambarBannerUrl())
                    .placeholder(R.drawable.ic_banner_placeholder)
                    .error(R.drawable.ic_banner_placeholder)
                    .into(holder.ivGambar);
        } else {
            holder.ivGambar.setImageResource(R.drawable.ic_banner_placeholder);
        }

        holder.btnEditBanner.setOnClickListener(v -> {
            Intent intent = new Intent(context, BannerFormActivity.class);
            intent.putExtra("BANNER_ID", banner.getId());
            context.startActivity(intent);
        });

        holder.btnDeleteBanner.setOnClickListener(v -> {
            showDeleteConfirmationDialog(context, banner, position);
        });

        holder.btnDownloadBanner.setOnClickListener(v -> {
            if (banner.getGambarBannerUrl() == null || banner.getGambarBannerUrl().isEmpty()) {
                Toast.makeText(context, "Tidak ada gambar untuk diunduh", Toast.LENGTH_SHORT).show();
                return;
            }
            downloadImage(context, banner.getGambarBannerUrl(), banner.getJudulBanner());
        });
    }

    private void showDeleteConfirmationDialog(Context context, Banner banner, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Konfirmasi Penghapusan")
                .setMessage("Apakah Anda benar-benar ingin menghapus banner \"" + banner.getJudulBanner() + "\"? Tindakan ini tidak dapat dibatalkan.")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    db.collection("banners").document(banner.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Banner \"" + banner.getJudulBanner() + "\" berhasil dihapus", Toast.LENGTH_SHORT).show();
                                bannerList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, bannerList.size());
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Gagal menghapus banner: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void downloadImage(Context context, String imageUrl, String judulBanner) {
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
                            File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Banners");
                            if (!directory.exists() && !directory.mkdirs()) {
                                Toast.makeText(context, "Gagal membuat direktori penyimpanan", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String filename = "banner_" + judulBanner.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ".jpg";
                            File file = new File(directory, filename);

                            try (FileOutputStream out = new FileOutputStream(file)) {
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            }

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
        return bannerList.size();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudulBanner, tvDeskripsi;
        ImageView ivGambar;
        View btnEditBanner, btnDeleteBanner, btnDownloadBanner;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudulBanner = itemView.findViewById(R.id.tvJudulBanner);
            tvDeskripsi = itemView.findViewById(R.id.tvDeskripsi);
            ivGambar = itemView.findViewById(R.id.ivGambar);
            btnEditBanner = itemView.findViewById(R.id.btnEditBanner);
            btnDeleteBanner = itemView.findViewById(R.id.btnDeleteBanner);
            btnDownloadBanner = itemView.findViewById(R.id.btnDownloadBanner);
        }
    }
}