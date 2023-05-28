package com.pkasemer.malai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pkasemer.malai.Database.DatabaseHelper;

import java.io.File;

public class ImageDisplayActivity extends AppCompatActivity {

    LinearLayout analyebtn, delete_sample_btn;
    String imagePath;


    Dialog track_dialog;

    TextView gametocytes_value, plasmodium_species_value,slideID,image_desc;
    ImageButton closebtn, backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);


        ImageView imageView = findViewById(R.id.image_view);
        analyebtn = findViewById(R.id.analyebtn);
        delete_sample_btn = findViewById(R.id.delete_sample_btn);
        backBtn = findViewById(R.id.backBtn);
        track_dialog = new Dialog(ImageDisplayActivity.this);
        track_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        track_dialog.setContentView(R.layout.results_dialog);
        ConstraintLayout dialog_background = track_dialog.findViewById(R.id.dialog_background);
        gametocytes_value = track_dialog.findViewById(R.id.gametocytes_value);
        plasmodium_species_value = track_dialog.findViewById(R.id.plasmodium_species_value);

        closebtn = track_dialog.findViewById(R.id.closebtn);
        slideID = findViewById(R.id.slideID);
        image_desc = findViewById(R.id.image_desc);

        // Get the image file path or URI from the intent
        imagePath = getIntent().getStringExtra("IMAGE_PATH");
        Integer image_id = getIntent().getIntExtra("IMAGE_ID",0);
        Integer slide_age = getIntent().getIntExtra("AGE",0);
        String slide_id = getIntent().getStringExtra("SLIDE_ID");
        String slide_gender = getIntent().getStringExtra("GENDER");
        String site_name = getIntent().getStringExtra("SITE_NAME");
        DatabaseHelper db = new DatabaseHelper(ImageDisplayActivity.this);

        slideID.setText("SlideID #: "+slide_id);
        image_desc.setText("Info: "+ slide_gender + " - "+ slide_age+ "Yrs" +", " + site_name );
        // Load the image into the ImageView using Glide or any other image loading library
        Glide.with(this)
                .load(imagePath)
                .into(imageView);


        analyebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog("imagePath");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        delete_sample_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the image file path from the intent
                db.deleteImageData(image_id);
                // Create a File object for the image file
                File imageFile = new File(imagePath);

                // Delete the image file
                if (imageFile.exists()) {
                    if (imageFile.delete()) {
                        // File deletion successful
                        Toast.makeText(ImageDisplayActivity.this, "Image deleted", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after deleting the image
                    } else {
                        // File deletion failed
                        Toast.makeText(ImageDisplayActivity.this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            }
        });
    }



    public void openDialog(String ImagePath) {
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                track_dialog.dismiss();
            }
        });

        plasmodium_species_value.setText("80%");
        gametocytes_value.setText("30%");
        track_dialog.show();
        track_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        track_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        track_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        track_dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}