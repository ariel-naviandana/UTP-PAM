package com.example.hercules77;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtEmail, txtStatus;
        Button btnToggleStatus, btnDownloadPhoto;
        ImageView imgPhoto;

        public UserViewHolder(View view) {
            super(view);
            txtUsername = view.findViewById(R.id.txtUsername);
            txtEmail = view.findViewById(R.id.txtEmail);
            txtStatus = view.findViewById(R.id.txtStatus);
            btnToggleStatus = view.findViewById(R.id.btnToggleStatus);
            btnDownloadPhoto = view.findViewById(R.id.btnDownloadPhoto);
            imgPhoto = view.findViewById(R.id.imgPhoto);
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.txtUsername.setText("Username: " + user.getUsername());
        holder.txtEmail.setText("Email: " + user.getEmail());
        holder.txtStatus.setText("Status: " + user.getStatus());

        holder.btnToggleStatus.setText(
                user.getStatus() != null && user.getStatus().equalsIgnoreCase("aktif") ? "Blokir Akun" : "Aktifkan Akun"
        );

        if (user.getFotoProfilUrl() != null && !user.getFotoProfilUrl().isEmpty()) {
            Glide.with(context)
                    .load(user.getFotoProfilUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_user_icon) // placeholder default saat loading
                    .error(R.drawable.ic_user_icon)       // default saat gagal load
                    .into(holder.imgPhoto);
        } else {
            holder.imgPhoto.setImageResource(R.drawable.ic_user_icon);
        }

        holder.btnToggleStatus.setOnClickListener(v -> {
            String newStatus = user.getStatus() != null && user.getStatus().equalsIgnoreCase("aktif") ? "banned" : "aktif";
            String userId = user.getDocumentId();

            updateUserStatusInFirestore(userId, newStatus, position);
        });

        holder.btnDownloadPhoto.setOnClickListener(v -> {
            if (user.getFotoProfilUrl() != null && !user.getFotoProfilUrl().isEmpty()) {
                downloadImageToDCIM(context, user.getFotoProfilUrl(), user.getUsername() + "_profile.jpg");
            } else {
                showToast("Foto profil tidak tersedia");
            }
        });
    }

    // Mengunduh gambar ke DCIM/Hercules77
    private void downloadImageToDCIM(Context context, String imageUrl, String fileName) {
        showToast("Mengunduh foto, tunggu hingga proses selesai");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(imageUrl)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showToast("Gagal download foto");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    showToast("Download foto gagal: " + response.message());
                    return;
                }

                byte[] imageBytes = response.body().bytes();

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/Hercules77");

                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri == null) {
                    showToast("Gagal membuat file di MediaStore");
                    return;
                }

                try (OutputStream out = context.getContentResolver().openOutputStream(uri)) {
                    if (out != null) {
                        out.write(imageBytes);
                        out.flush();
                        showToast("Foto berhasil disimpan di DCIM/Hercules77");
                    } else {
                        showToast("Gagal membuka stream untuk menulis file");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast("Gagal menyimpan foto");
                }
            }
        });
    }

    // Mengupdate status user di Firestore
    private void updateUserStatusInFirestore(String userId, String newStatus, int position) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    userList.get(position).setStatus(newStatus);
                    notifyItemChanged(position);
                    showToast("Status user diperbarui ke " + newStatus);
                })
                .addOnFailureListener(e -> {
                    showToast("Gagal memperbarui status user");
                });
    }

    private void showToast(String message) {
        mainHandler.post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}