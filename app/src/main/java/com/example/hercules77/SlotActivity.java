package com.example.hercules77;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hercules77.databinding.ActivitySlotBinding;
import java.util.Random;

public class SlotActivity extends AppCompatActivity {

    private ActivitySlotBinding binding;
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup view binding
        binding = ActivitySlotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Tombol kembali
        binding.btnBack.setOnClickListener(v -> finish());

        // Tombol submit
        binding.btnSubmit.setOnClickListener(v -> playSlot());
    }

    private void playSlot() {
        String betInput = binding.editBet.getText().toString().trim();

        if (betInput.isEmpty()) {
            Toast.makeText(this, "Masukkan nilai taruhan terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        int betAmount;
        try {
            betAmount = Integer.parseInt(betInput);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Taruhan tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate angka acak untuk slot
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);

        // Tampilkan angka ke UI
        binding.txtSlot1.setText(String.valueOf(num1));
        binding.txtSlot2.setText(String.valueOf(num2));
        binding.txtSlot3.setText(String.valueOf(num3));

        // Hitung kemenangan
        int winAmount = 0;
        if (num1 == num2 && num2 == num3) {
            winAmount = betAmount * 5;
        } else if (num1 == num2 || num2 == num3 || num1 == num3) {
            winAmount = betAmount * 3;
        }

        // Tampilkan hasil
        binding.txtYouWonLabel.setText(winAmount == 0 ? "Sorry" : "You Won:");
        binding.txtYouWonAmount.setText(winAmount == 0 ? "You Lose" : String.valueOf(winAmount));
    }
}
