package com.pkasemer.malai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CapturedImageDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captured_image_details);

        ImageView imageView = findViewById(R.id.captured_image);

        String imagePath = getIntent().getStringExtra("CAPTURED_IMAGE_PATH");
        Log.e("imagepaths", "onCreate: "+ imagePath );


        // Load the image into the ImageView using Glide or any other image loading library
        Glide.with(this)
                .load(imagePath)
                .into(imageView);
    }


    private void saveCroppedImageToFolder(Uri croppedUri) {
        try {
            File mediaStorageDir = new File(this.getFilesDir(), "Mal_Images");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("MyApp", "Failed to create directory");
                    return;
                }
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File outputFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".jpg");

            InputStream inputStream = getContentResolver().openInputStream(croppedUri);
            OutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            // Hide the image from the gallery
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, outputFile.getName());
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
                contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Mal_Images");
                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            }
            MediaScannerConnection.scanFile(this, new String[]{outputFile.getAbsolutePath()}, null, null);

            // Image saved successfully
            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any errors that may occur during image saving
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }
}