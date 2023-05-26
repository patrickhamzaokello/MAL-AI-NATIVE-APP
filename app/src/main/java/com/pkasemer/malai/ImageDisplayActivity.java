package com.pkasemer.malai;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);


        ImageView imageView = findViewById(R.id.image_view);

        // Get the image file path or URI from the intent
        String imagePath = getIntent().getStringExtra("IMAGE_PATH");

        // Load the image into the ImageView using Glide or any other image loading library
        Glide.with(this)
                .load(imagePath)
                .into(imageView);

        // Optionally, you can add additional functionality like pinch-to-zoom or swipe gestures
        // to allow users to interact with the image in the ImageDisplayActivity.
    }
}