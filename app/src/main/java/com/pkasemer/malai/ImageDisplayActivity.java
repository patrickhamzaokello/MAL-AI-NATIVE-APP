package com.pkasemer.malai;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.pkasemer.malai.Database.DatabaseHelper;
import com.pkasemer.malai.Models.InferenceModel;
import com.pkasemer.malai.ViewModel.InferenceViewModel;

import org.tensorflow.lite.Interpreter;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageDisplayActivity extends AppCompatActivity {

    LinearLayout analyebtn, delete_sample_btn;
    String imagePath;


    Dialog track_dialog;
    protected Interpreter tflite;
    ImageView imageView,image_result_view;

    TextView process_value, slideID, image_desc;
    ImageButton closebtn, backBtn;

    InferenceModel inference_back;
    InferenceViewModel inferenceViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);


        imageView = findViewById(R.id.image_view);
        analyebtn = findViewById(R.id.analyebtn);
        delete_sample_btn = findViewById(R.id.delete_sample_btn);
        backBtn = findViewById(R.id.backBtn);
        track_dialog = new Dialog(ImageDisplayActivity.this);
        track_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        track_dialog.setContentView(R.layout.results_dialog);
        ConstraintLayout dialog_background = track_dialog.findViewById(R.id.dialog_background);
        process_value = track_dialog.findViewById(R.id.process_value);
        image_result_view = track_dialog.findViewById(R.id.image_result_view);

        closebtn = track_dialog.findViewById(R.id.closebtn);
        slideID = findViewById(R.id.slideID);
        image_desc = findViewById(R.id.image_desc);

        inferenceViewModel = new ViewModelProvider(this).get(InferenceViewModel.class);


        inferenceViewModel.getInferenceDataObserver().observe(this, new Observer<InferenceModel>() {
            @Override
            public void onChanged(InferenceModel inferenceModel) {
                if(inferenceModel != null){
                    inference_back = inferenceModel;
                    Toast.makeText(ImageDisplayActivity.this, "Analysis Complete", Toast.LENGTH_SHORT).show();
                    Glide.with(ImageDisplayActivity.this)
                            .load(inference_back.getRotatedImage())
                            .into(image_result_view);
                    process_value.setText(inference_back.getTimeTaken().toString());
                } else {
                    Toast.makeText(ImageDisplayActivity.this, "Analysis Failed to Complete", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Get the image file path or URI from the intent
        imagePath = getIntent().getStringExtra("IMAGE_PATH");
        Integer image_id = getIntent().getIntExtra("IMAGE_ID", 0);
        Integer slide_age = getIntent().getIntExtra("AGE", 0);
        String slide_id = getIntent().getStringExtra("SLIDE_ID");
        String slide_gender = getIntent().getStringExtra("GENDER");
        String site_name = getIntent().getStringExtra("SITE_NAME");
        DatabaseHelper db = new DatabaseHelper(ImageDisplayActivity.this);

        slideID.setText("SlideID #: " + slide_id);
        image_desc.setText("Info: " + slide_gender + " - " + slide_age + "Yrs" + ", " + site_name);
        // Load the image into the ImageView using Glide or any other image loading library
        Glide.with(this)
                .load(imagePath)
                .into(imageView);

        analyebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProcessImage(view);
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


    private void ProcessImage(View view) {
        if (imageView.getDrawable() != null) {
            openDialog(imagePath);
        } else {
            Snackbar.make(view, "Please Choose Image", Snackbar.LENGTH_SHORT).show();
        }
    }


    public void openDialog(String ImagePath) {
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                track_dialog.dismiss();
            }
        });

        process_value.setText("..Processing Image");
        track_dialog.show();
        track_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        track_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        track_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        track_dialog.getWindow().setGravity(Gravity.BOTTOM);


        File file = new File(ImagePath);
//        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        inferenceViewModel.makeApiCall(getApplicationContext(),body);
    }


}