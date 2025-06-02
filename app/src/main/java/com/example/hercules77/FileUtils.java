package com.example.hercules77;

import android.content.Context;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    // Fungsi untuk mengambil file dari URI
    public static File getFileFromUri(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        File file = new File(context.getCacheDir(), "temp_image.jpg");
        OutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        inputStream.close();
        return file;
    }
}
