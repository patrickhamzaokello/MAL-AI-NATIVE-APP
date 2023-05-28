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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CapturedImageDetails extends AppCompatActivity {

    LinearLayout savebtn, retakebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captured_image_details);

        ImageView imageView = findViewById(R.id.captured_image);
        savebtn = findViewById(R.id.savebtn);
        retakebtn = findViewById(R.id.retakebtn);

        String imagePath = getIntent().getStringExtra("CAPTURED_IMAGE_PATH");
        Log.e("imagepaths", "onCreate: "+ imagePath );


        // Load the image into the ImageView using Glide or any other image loading library
        Glide.with(this)
                .load(imagePath)
                .into(imageView);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCroppedImageToFolder(Uri.parse(imagePath));
            }
        });

        retakebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close the current activity and navigate back to the previous screen
                finish();
            }
        });
    }

    private void saveCroppedImageToFolder(Uri imageUri) {
        try {
            // Create a file object from the image URI
            File sourceFile = new File(imageUri.getPath());

            // Get the destination folder path in internal storage
            File folder = new File(getExternalFilesDir(null), "Mal_Images");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Create a new file in the destination folder
            File destinationFile = new File(folder, "cropped_image.jpg");

            // Create input and output streams
            FileInputStream inputStream = new FileInputStream(sourceFile);
            FileOutputStream outputStream = new FileOutputStream(destinationFile);

            // Copy the file contents from input stream to output stream
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Close the streams
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            // Scan the newly saved image file so that it appears in the gallery
            MediaScannerConnection.scanFile(this, new String[]{destinationFile.getPath()}, null, null);
            Log.e("imagepathss", "saveCroppedImageToFolder: "+  destinationFile.getPath());

            Toast.makeText(this, "Image saved to app's internal storage", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }






}