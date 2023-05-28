package com.pkasemer.malai;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pkasemer.malai.Database.DatabaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class CapturedImageDetails extends AppCompatActivity {

    LinearLayout savebtn, retakebtn;
    DatabaseHelper databaseHelper;
    String gender;
    TextInputEditText site_name, slide_age;
    TextInputLayout site_name_layout, slide_age_layout;

    String image_site_name;
    String image_slide_age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captured_image_details);

        ImageView imageView = findViewById(R.id.captured_image);
        savebtn = findViewById(R.id.savebtn);
        retakebtn = findViewById(R.id.retakebtn);

        String imagePath = getIntent().getStringExtra("CAPTURED_IMAGE_PATH");
        Log.e("imagepaths", "onCreate: " + imagePath);


        // Load the image into the ImageView using Glide or any other image loading library
        Glide.with(this)
                .load(imagePath)
                .into(imageView);

        // Inside onCreate() method of your activity
        RadioGroup genderRadioGroup = findViewById(R.id.genderRadioGroup);
        gender = genderRadioGroup.getCheckedRadioButtonId() == R.id.maleRadioButton ? "Male" : "Female";
        site_name_layout = findViewById(R.id.site);
        slide_age_layout = findViewById(R.id.age);

        site_name = findViewById(R.id.entersite);
        slide_age = findViewById(R.id.enterage);
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.maleRadioButton) {
                    gender = "Male";
                } else if (checkedId == R.id.femaleRadioButton) {
                    gender = "Female";
                }
            }
        });



        // Save the image data to the database
        databaseHelper = new DatabaseHelper(this);

        // Show a success message

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_site_name = site_name.getText().toString().trim();
                image_slide_age = slide_age.getText().toString().trim();



                //first we will do the validations

                if (TextUtils.isEmpty(image_site_name)) {
                    site_name_layout.setError("Enter Sample Site");
                    site_name_layout.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(image_slide_age)) {
                    slide_age_layout.setError("Enter Sample Age");
                    slide_age_layout.requestFocus();
                    return;
                }

                // Get other data values
                Log.e("logo", "onCreate: " +image_site_name+ " *** "+ image_slide_age);
                saveCroppedImageToFolder(Uri.parse(imagePath), image_site_name, Integer.parseInt(image_slide_age), gender);

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

    private void saveCroppedImageToFolder(Uri imageUri, String siteName, int age, String gender) {
        try {
            // Create a file object from the image URI
            File sourceFile = new File(imageUri.getPath());

            // Get the destination folder path in internal storage
            File folder = new File(getExternalFilesDir(null), "Mal_Images");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Generate a unique filename using timestamp and random number
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String slide_id_generated = "mal_" + timeStamp + "_" + new Random().nextInt(1000);
            String uniqueFileName = slide_id_generated + ".jpg";

            // Create a new file in the destination folder with the unique filename
            File destinationFile = new File(folder, uniqueFileName);

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

            databaseHelper.saveImageData(slide_id_generated, timeStamp, String.valueOf(destinationFile), siteName, age, gender);
            Toast.makeText(this, "Image saved to app's internal storage", Toast.LENGTH_SHORT).show();
            finish();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }


}