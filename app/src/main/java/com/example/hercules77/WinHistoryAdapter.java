package com.example.hercules77;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WinHistoryAdapter extends RecyclerView.Adapter<WinHistoryAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onViewImage(String imageUrl);
        void onDelete(String id);
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
        TextView txtUser, txtJumlah, txtTanggal;
        Button btnView, btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtJumlah = itemView.findViewById(R.id.txtJumlahMenang);
            txtTanggal = itemView.findViewById(R.id.txtTanggalMenang);
            btnView = itemView.findViewById(R.id.btnViewImage);
            btnDelete = itemView.findViewById(R.id.btnDelete);
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
        holder.txtUser.setText("User ID: " + item.getIdUser());
        holder.txtJumlah.setText("Jumlah Menang: " + item.getJumlahMenang());
        holder.txtTanggal.setText("Tanggal: " + item.getTanggalMenang());

        holder.btnView.setOnClickListener(v -> listener.onViewImage(item.getBuktiGambarUrl()));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item.getId()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

