package com.example.hercules77;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import androidx.appcompat.app.AppCompatActivity;

public class EventActivity extends AppCompatActivity {

    private Button btnAddEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        btnAddEvent = findViewById(R.id.btnAddEvent);

        btnAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventFormActivity.class);
            startActivity(intent);
        });
    }
}