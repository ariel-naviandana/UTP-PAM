package com.example.hercules77;

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
    }

    private void generateMenuData(){
        homeItems.add(new HomeItem("Coin Flip", R.drawable.ic_coin, "Coin Flip Gacor"));
        homeItems.add(new HomeItem("Slot", R.drawable.ic_slot, "Slot Gacor"));
        homeItems.add(new HomeItem("History", R.drawable.ic_back, "History Permainan"));
        homeItems.add(new HomeItem("Customer Service", R.drawable.ic_back, "Hubungi Kami"));
    }
}