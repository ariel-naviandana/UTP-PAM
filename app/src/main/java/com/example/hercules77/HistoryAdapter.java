package com.example.hercules77;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hercules77.databinding.ItemHistoryBinding;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<History> historyList;
    private Context context;

    public HistoryAdapter(Context context, ArrayList<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHistoryBinding binding;

        public ViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(History history) {
            if (history.getGameType() == History.GameType.COIN_FLIP) {
                binding.imgIcon.setImageResource(R.drawable.ic_coin);
                binding.txtGameType.setText("Game: Coin Flip");
            } else {
                binding.imgIcon.setImageResource(R.drawable.ic_slot);
                binding.txtGameType.setText("Game: Slot");
            }

            binding.txtTimestamp.setText("Waktu: " + history.getTimestamp());

            String resultText = history.isWin()
                    ? "Menang! +Rp" + history.getAmount()
                    : "Kalah! -Rp" + history.getAmount();

            binding.txtResult.setText(resultText);

            int colorRes = history.isWin() ? android.R.color.holo_green_light : android.R.color.holo_red_light;
            binding.getRoot().setCardBackgroundColor(context.getResources().getColor(colorRes));
        }
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        holder.bind(historyList.get(position));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
}