package com.example.hercules77;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class CoinActivity extends AppCompatActivity {

    ImageView coinImage;
    Button flipButton;
    TextView resultText;
    EditText inputUang;
    Random random;
    int jumlahUang;
    String lastResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);

        coinImage = findViewById(R.id.coinImage);
        flipButton = findViewById(R.id.flipButton);
        resultText = findViewById(R.id.resultText);
        inputUang = findViewById(R.id.inputUang);
        random = new Random();

        flipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uangStr = inputUang.getText().toString();

                if (uangStr.isEmpty()) {
                    resultText.setText("Masukkan jumlah uang terlebih dahulu!");
                    resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    return;
                }

                jumlahUang = Integer.parseInt(uangStr);

                if (jumlahUang < 10000) {
                    resultText.setText("Minimal taruhan adalah 10.000!");
                    resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    return;
                }

                // Menampilkan pilihan HEAD / TAIL
                AlertDialog.Builder builder = new AlertDialog.Builder(CoinActivity.this);
                builder.setTitle("Pilih Tebakan");
                builder.setItems(new CharSequence[]{"HEAD", "TAIL"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tebakan = (i == 0) ? "HEAD" : "TAIL";
                        checkResult(tebakan);
                        inputUang.setText(""); // kosongkan input setelah pilihan
                    }
                });
                builder.show();
            }
        });
    }

    private void checkResult(String userGuess) {
        String actualResult;

        // AUTO-KALAH jika uang lebih dari 300.000 → hasil dipaksa jadi kebalikan dari pilihan
        if (jumlahUang > 3000000) {
            actualResult = userGuess.equals("HEAD") ? "TAIL" : "HEAD";
        } else {
            // MANIPULASI PROBABILITAS BERDASARKAN HASIL SEBELUMNYA
            double randomValue = Math.random();

            if (lastResult.equals("WIN")) {
                actualResult = (randomValue < 0.7) ? "HEAD" : "TAIL";
            } else if (lastResult.equals("LOSE")) {
                actualResult = (randomValue < 0.7) ? "TAIL" : "HEAD";
            } else {
                actualResult = (randomValue < 0.5) ? "HEAD" : "TAIL";
            }
        }

        // Tampilkan gambar sesuai hasil
        if (actualResult.equals("HEAD")) {
            coinImage.setImageResource(R.drawable.head);
        } else {
            coinImage.setImageResource(R.drawable.tail);
        }

        // Tentukan menang atau kalah
        if (userGuess.equals(actualResult)) {
            int totalMenang = jumlahUang * 2;
            resultText.setText("Anda Menang!\nHasil: " + actualResult + "\nUang Anda menjadi: " + totalMenang);
            resultText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            lastResult = "WIN";
        } else {
            resultText.setText("Anda Kalah!\nHasil: " + actualResult + "\nUang Anda hangus 😢");
            resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            lastResult = "LOSE";
        }
    }
}
