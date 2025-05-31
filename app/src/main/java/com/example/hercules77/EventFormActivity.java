package com.example.hercules77;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventFormActivity extends AppCompatActivity {

    private EditText etJudulEvent, etDeskripsiEvent;
    private Button btnUploadImage, btnSave;
    private ImageView ivPreview;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);

        etJudulEvent = findViewById(R.id.judulEvent);
        etDeskripsiEvent = findViewById(R.id.deskripsiEvent);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v->{
            String judulEvent = etJudulEvent.getText().toString();
            String deskripsiEvent = etDeskripsiEvent.getText().toString();
            Event event = new Event(judulEvent, deskripsiEvent,  null);
            db.collection("events").add(event);
            finish();
        });
    }
}