package com.pkasemer.malai.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pkasemer.malai.ImageDisplayActivity;
import com.pkasemer.malai.R;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<File> imageFiles;

    public ImageAdapter(Context context, ArrayList<File> imageFiles) {
        this.context = context;
        this.imageFiles = imageFiles;
    }

    @Override
    public int getCount() {
        return imageFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return imageFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            int imageSizeX = (int) context.getResources().getDimension(R.dimen.image_size_x); // Adjust image size as needed
            int imageSizeY = (int) context.getResources().getDimension(R.dimen.image_size_y); // Adjust image size as needed
            imageView.setLayoutParams(new GridView.LayoutParams(imageSizeX, imageSizeY));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        // Load image into ImageView
        File imageFile = imageFiles.get(position);
        Glide.with(context)
                .load(imageFile)
                .into(imageView);

        // Set click listener for the image
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage(imageFile);
            }
        });

        return imageView;
    }


    private void showImage(File imageFile) {
        Toast.makeText(context, "Image Clicked", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(context, ImageDisplayActivity.class);
        intent.putExtra("IMAGE_PATH", imageFile.getAbsolutePath());
        context.startActivity(intent);
    }
}
