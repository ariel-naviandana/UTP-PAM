package com.example.hercules77;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hercules77.databinding.ActivityHomeBinding;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private ArrayList<HomeItem> homeItems = new ArrayList<>();
    private HomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        generateMenuData();
        adapter = new HomeAdapter(this, homeItems);
        binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.homeRecyclerView.setAdapter(adapter);

        // Logout Button Click Listener
        binding.btnLogout.setOnClickListener(v -> {
            // TODO: lakukan logout di sini (misalnya hapus session/login status)
            // Contoh: jika pakai SharedPreferences untuk login state
            getSharedPreferences("login_session", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            // Pindah ke halaman Login
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // agar tidak bisa kembali ke Home
            startActivity(intent);
            finish();
        });
    }

    private void generateMenuData(){
        homeItems.add(new HomeItem("Coin Flip", R.drawable.ic_coin, "Coin Flip Gacor"));
        homeItems.add(new HomeItem("Slot", R.drawable.ic_slot, "Slot Gacor"));
        homeItems.add(new HomeItem("History", R.drawable.ic_history, "History Permainan"));
        homeItems.add(new HomeItem("Customer Service", R.drawable.ic_cs, "Hubungi Kami"));
    }
}