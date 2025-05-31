package com.example.hercules77;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
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
    private DBHelper db;

    public UserAdapter(Context context, List<User> userList, DBHelper db) {
        this.context = context;
        this.userList = userList;
        this.db = db;
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
    public UserAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserAdapter.UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.txtUsername.setText("Username: " + user.username);
        holder.txtEmail.setText("Email: " + user.email);
        holder.txtStatus.setText("Status: " + user.status);

        // Teks pada tombol sesuai status
        if (user.status.equalsIgnoreCase("aktif")) {
            holder.btnToggleStatus.setText("Blokir Akun");
        } else {
            holder.btnToggleStatus.setText("Aktifkan Akun");
        }

        // Menampilkan foto profil
        if (user.fotoProfilUrl != null && !user.fotoProfilUrl.isEmpty()) {
            Glide.with(context)
                    .load(user.fotoProfilUrl)
                    .circleCrop()
                    .into(holder.imgPhoto);
        }

        // Tombol untuk mengubah status
        holder.btnToggleStatus.setOnClickListener(v -> {
            String newStatus = user.status.equals("aktif") ? "banned" : "aktif";
            if (db.updateUserStatus(user.id, newStatus)) {
                user.status = newStatus;
                notifyItemChanged(position);
            }
        });

        // Tombol untuk mengunduh foto profile
        holder.btnDownloadPhoto.setOnClickListener(v -> {
            if (user.fotoProfilUrl != null && !user.fotoProfilUrl.isEmpty()) {
                downloadImageToDCIM(context, user.fotoProfilUrl, user.username + "_profile.jpg");
            }
        });
    }

    // Mengunduh foto profile ke DCIM
    private void downloadImageToDCIM(Context context, String imageUrl, String fileName) {
        OkHttpClient client = new OkHttpClient();

        ((UserActivity) context).runOnUiThread(() ->
                Toast.makeText(context, "Mengunduh foto, tunggu hingga proses selesai", Toast.LENGTH_SHORT).show()
        );

        Request request = new Request.Builder()
                .url(imageUrl)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                ((UserActivity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Gagal download foto", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    ((UserActivity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Download foto gagal: " + response.message(), Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                byte[] imageBytes = response.body().bytes();

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                // Tentukan folder di DCIM/Hercules77
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/Hercules77");

                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri == null) {
                    ((UserActivity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Gagal membuat file di MediaStore", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                try (OutputStream out = context.getContentResolver().openOutputStream(uri)) {
                    if (out != null) {
                        out.write(imageBytes);
                        out.flush();
                        ((UserActivity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Foto berhasil disimpan di DCIM/Hercules77", Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        ((UserActivity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Gagal membuka stream untuk menulis file", Toast.LENGTH_SHORT).show()
                        );
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    ((UserActivity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Gagal menyimpan foto", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}