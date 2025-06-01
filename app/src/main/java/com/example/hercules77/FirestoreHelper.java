package com.example.hercules77;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hercules77.WinHistory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference historyRef = db.collection("histories");

    public void updateStatus(String docId, String newStatus) {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("status", newStatus);

        historyRef.document(docId).update(updateMap)
                .addOnSuccessListener(unused -> Log.d("Firestore", "Status updated"))
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to update status", e));
    }

    public void deleteHistory(String docId) {
        historyRef.document(docId).delete()
                .addOnSuccessListener(unused -> Log.d("Firestore", "History deleted"))
                .addOnFailureListener(e -> Log.e("Firestore", "Delete failed", e));
    }

    public CollectionReference getHistoryReference() {
        return historyRef;
    }
}
