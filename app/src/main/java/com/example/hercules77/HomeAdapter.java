package com.example.hercules77;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hercules77.databinding.ItemHomeBinding;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private ArrayList<HomeItem> homeItemsList;
    private Context context;

    public HomeAdapter(Context context, ArrayList<HomeItem> homeItemsList){
        this.homeItemsList = homeItemsList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHomeBinding binding;
        public ViewHolder(ItemHomeBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(HomeItem homeItem){
            switch (homeItem.getTitle()) {
                case "Coin Flip":
                    binding.imgIcon.setImageResource(homeItem.getIconResId());
                    binding.txtGameTitle.setText(homeItem.getTitle());
                    binding.txtDescription.setText(homeItem.getDescription());
                    break;
                case "Slot":
                    binding.imgIcon.setImageResource(homeItem.getIconResId());
                    binding.txtGameTitle.setText(homeItem.getTitle());
                    binding.txtDescription.setText(homeItem.getDescription());
                    break;
                case "History":
                    binding.imgIcon.setImageResource(homeItem.getIconResId());
                    binding.txtGameTitle.setText(homeItem.getTitle());
                    binding.txtDescription.setText(homeItem.getDescription());
                    break;
                case "Customer Service":
                    binding.imgIcon.setImageResource(homeItem.getIconResId());
                    binding.txtGameTitle.setText(homeItem.getTitle());
                    binding.txtDescription.setText(homeItem.getDescription());
                    break;
            }
        }
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemHomeBinding binding = ItemHomeBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {
        holder.bind(homeItemsList.get(position));

        holder.itemView.setOnClickListener(v -> {
            switch (homeItemsList.get(position).getTitle()) {
                case "Coin Flip":
                    context.startActivity(new Intent(context, CoinActivity.class));
                    break;
                case "Slot":
                    context.startActivity(new Intent(context, SlotActivity.class));
                    break;
                case "History":
                    context.startActivity(new Intent(context, HistoryActivity.class));
                    break;
                case "Customer Service":
                    context.startActivity(new Intent(context, CServiceActivity.class));
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeItemsList.size();
    }
}
