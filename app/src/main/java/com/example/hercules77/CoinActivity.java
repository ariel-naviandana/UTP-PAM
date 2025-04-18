package com.example.hercules77;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class CoinActivity extends AppCompatActivity {

    ImageView coinImage;
    Button flipButton;
    TextView resultText;
    Random random;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);

        resultText = findViewById(R.id.resultText);
        coinImage = findViewById(R.id.coinImage);
        flipButton = findViewById(R.id.flipButton);
        random = new Random();

        flipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipCoin();
            }
        });
    }

    private void flipCoin() {
        showGuessDialog();
    }

    private void showGuessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Tebak Koin")
                .setMessage("Pilih salah satu:")
                .setPositiveButton("HEAD", (dialog, which) -> checkResult("HEAD"))
                .setNegativeButton("TAIL", (dialog, which) -> checkResult("TAIL"))
                .show();
    }

    private void checkResult(String userGuess) {
        int result = random.nextInt(2); // 0 = HEAD, 1 = TAIL
        String actualResult;

        if (result == 0) {
            actualResult = "HEAD";
            coinImage.setImageResource(R.drawable.head);
        } else {
            actualResult = "TAIL";
            coinImage.setImageResource(R.drawable.tail);
        }

        if (userGuess.equals(actualResult)) {
            resultText.setText("Anda Menang! Hasil: " + actualResult);
            resultText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            resultText.setText("Anda Kalah! Hasil: " + actualResult);
            resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
}