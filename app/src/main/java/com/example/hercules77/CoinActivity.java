package com.example.hercules77;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CoinActivity extends AppCompatActivity {

    ImageView coinImage;
    Button flipButton;
    TextView resultText;
    EditText inputUang;
    Random random;
    int jumlahUang;
    String lastResult = "";

    // Firestore instance
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);

        coinImage = findViewById(R.id.coinImage);
        flipButton = findViewById(R.id.flipButton);
        resultText = findViewById(R.id.resultText);
        inputUang = findViewById(R.id.inputUang);
        random = new Random();

        // Init Firestore
        db = FirebaseFirestore.getInstance();

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

        // AUTO-KALAH jika uang lebih dari 30.000.000 (saya perbaiki typo sebelumnya)
        if (jumlahUang > 30000000) {
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
            resultText.setText("Anda Menang!\nHasil: " + actualResult + "\nUang yang Anda dapatkan: " + totalMenang);
            resultText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            lastResult = "WIN";

            // Simpan data menang ke Firestore
            saveResultToFirestore("win", totalMenang);
        } else {
            resultText.setText("Anda Kalah!\nHasil: " + actualResult + "\nUang Anda hangus 😢");
            resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            lastResult = "LOSE";

            // Simpan data kalah ke Firestore (amount = 0)
            saveResultToFirestore("lose", 0);
        }
    }

    private void saveResultToFirestore(String result, int amount) {
        // Siapkan data yang akan disimpan
        Map<String, Object> historyData = new HashMap<>();
        historyData.put("userId", "user123"); // Ganti dengan user ID sebenarnya jika ada sistem login
        historyData.put("gameType", "coin");
        historyData.put("result", result);
        historyData.put("amount", amount);
        historyData.put("imageUrl", ""); // kosongkan karena tidak ada gambar bukti di coin
        historyData.put("timestamp", System.currentTimeMillis());

        db.collection("histories")
                .add(historyData)
                .addOnSuccessListener(documentReference -> {
                    // Optional: feedback jika berhasil simpan
                    // Toast.makeText(CoinActivity.this, "Riwayat berhasil disimpan", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Optional: feedback jika gagal simpan
                    // Toast.makeText(CoinActivity.this, "Gagal simpan riwayat", Toast.LENGTH_SHORT).show();
                });
    }
}
