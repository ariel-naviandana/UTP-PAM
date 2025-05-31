package com.example.hercules77;

import okhttp3.*;

import java.io.File;

public class CloudinaryUploader {
    public static void uploadImage(File file, String uploadPreset, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                .addFormDataPart("upload_preset", uploadPreset)
                .build();

        Request request = new Request.Builder()
                .url("https://api.cloudinary.com/v1_1/dk7ayxsny/image/upload")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
