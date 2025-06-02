package com.example.hercules77;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WinHistoryAdapter extends RecyclerView.Adapter<WinHistoryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onViewImage(String imageUrl);
        void onDelete(String id);
        void onVerify(String id, boolean currentStatus);
        void onDownload(String imageUrl);
        void onImageClicked(int position);
    }

    private List<WinHistory> list;
    private Context context;
    private OnItemClickListener listener;

    public WinHistoryAdapter(List<WinHistory> list, Context context, OnItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUser, txtJumlah, txtTanggal, txtStatus;
        Button btnView, btnDelete, btnVerify, btnDownload;
        ImageView imgBukti;

        public ViewHolder(View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtJumlah = itemView.findViewById(R.id.txtJumlahMenang);
            txtTanggal = itemView.findViewById(R.id.txtTanggalMenang);
            txtStatus = itemView.findViewById(R.id.txtStatusVerified);
            btnView = itemView.findViewById(R.id.btnViewImage);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnVerify = itemView.findViewById(R.id.btnVerify);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            imgBukti = itemView.findViewById(R.id.imgBukti);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_win_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WinHistory item = list.get(position);

        // Set User ID
        holder.txtUser.setText("User ID: " + item.getUserId());

        // Set jumlah menang (amount)
        holder.txtJumlah.setText("Jumlah Menang: " + item.getAmount());

        // Parse timestamp String ke long dengan aman
        String tanggal = "-";
        try {
            long timestamp = Long.parseLong(item.getTimestamp());
            if (timestamp > 0) {
                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                tanggal = dateFormat.format(new java.util.Date(timestamp));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        holder.txtTanggal.setText("Tanggal: " + tanggal);

        // Set status verified
        holder.txtStatus.setText(item.isVerified() ? "Status: Verified" : "Status: Not Verified");

        // Tombol-tombol aksi
        holder.btnView.setOnClickListener(v -> listener.onViewImage(item.getImageUrl()));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item.getId()));

        holder.btnVerify.setText(item.isVerified() ? "Unverify" : "Verify");
        holder.btnVerify.setOnClickListener(v -> listener.onVerify(item.getId(), item.isVerified()));

        holder.btnDownload.setOnClickListener(v -> listener.onDownload(item.getImageUrl()));

        // Load gambar bukti dengan Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .into(holder.imgBukti);

        // Klik gambar untuk upload gambar baru
        holder.imgBukti.setOnClickListener(v -> listener.onImageClicked(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
