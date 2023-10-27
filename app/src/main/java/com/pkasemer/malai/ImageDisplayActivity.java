package com.pkasemer.malai;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.pkasemer.malai.Database.DatabaseHelper;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageDisplayActivity extends AppCompatActivity {

    LinearLayout analyebtn, delete_sample_btn;
    String imagePath;


    Dialog track_dialog;
    protected Interpreter tflite;
    ImageView imageView;

    TextView gametocytes_value, plasmodium_species_value, slideID, image_desc;
    ImageButton closebtn, backBtn;

    private int imageSizeX;
    private int imageSizeY;
    private TensorImage inputImageBuffer;

    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;

    private static Bitmap img_process_bitmap;
    private List<String> labels;

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
        gametocytes_value = track_dialog.findViewById(R.id.gametocytes_value);
        plasmodium_species_value = track_dialog.findViewById(R.id.plasmodium_species_value);

        closebtn = track_dialog.findViewById(R.id.closebtn);
        slideID = findViewById(R.id.slideID);
        image_desc = findViewById(R.id.image_desc);

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

        try {
            tflite = new Interpreter(loadModelFile(this));

        } catch (IOException e) {
            e.printStackTrace();
        }


        analyebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                    openDialog("imagePath");
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
            int imageTensorIndex = 0;
            int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, weight, 3}
            imageSizeX = imageShape[1];
            imageSizeY = imageShape[2];

            DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

            int probabilityTensorIndex = 0;
            int[] probabilityShape = tflite.getOutputTensor(probabilityTensorIndex).shape();
            DataType probabilityDataType = tflite.getInputTensor(probabilityTensorIndex).dataType();


            inputImageBuffer = new TensorImage(imageDataType);
            outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);

            probabilityProcessor = new TensorProcessor.Builder().add(getPostProcessorNormalizedOP()).build();


            inputImageBuffer = loadImageFromPath(imagePath);
            tflite.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer().rewind());

            showResults(this);

        } else {
            Snackbar.make(view, "Please Choose Image", Snackbar.LENGTH_SHORT).show();
        }
    }


    private void showResults(Activity activity) {

        try {
            labels = FileUtil.loadLabels(this, "labels.txt");

            TensorBuffer tensorBuffer = probabilityProcessor.process(outputProbabilityBuffer);

            // Assuming the last dimension corresponds to class labels
            int height = tensorBuffer.getShape()[1];
            int width = tensorBuffer.getShape()[2];

            // Assuming you have a label mapping in the form of a List<String> called labels
// and a TensorBuffer containing predictions called tensorBuffer

            int batchSize = tensorBuffer.getShape()[0];
            int numClasses = tensorBuffer.getShape()[1];

            for (int batch = 0; batch < batchSize; batch++) {
                for (int classIndex = 0; classIndex < numClasses; classIndex++) {
                    // Access the value for the class prediction
                    float prediction = tensorBuffer.getFloatValue(classIndex);

                    // Get the label corresponding to the class index
                    String label = labels.get(classIndex);

                    // Log or print the label and the prediction
                    Log.d("TensorBuffer", "Batch: " + batch + ", Label: " + label + ", Prediction: " + prediction * 100);
                }
            }




        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private TensorImage loadImageFromPath(String imagePath) {
        // Load the image from the file path
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

        if (bitmap == null) {
            // Handle the case where the image couldn't be loaded from the provided path
            // You may want to return an error message or handle this case appropriately
            return null;
        }

        // Load the bitmap into a TensorImage
        inputImageBuffer.load(bitmap);

        // Create an image processor for TensorFlow
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());

        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(getPreProcessorNormalizedOP())
                .build();

        return imageProcessor.process(inputImageBuffer);
    }


    private TensorOperator getPreProcessorNormalizedOP() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }

    private TensorOperator getPostProcessorNormalizedOP() {
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }


    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("best_float32.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declareLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declareLength);
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