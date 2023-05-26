package com.pkasemer.malai.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.pkasemer.malai.CropperActivity;
import com.pkasemer.malai.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AnalyzeFragment extends Fragment {
    private static final int DESIRED_WIDTH = 628;
    private static final int DESIRED_HEIGHT = 480;
    private static final int REQUEST_CROP_REQUEST = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    LinearLayout camera_take, gallery_take, analyebtn;

    View view;
    private String[] PERMISSIONS;

    String path;
    Uri imageUri;
    ImageView captureImage;

    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;

    ActivityResultLauncher<String> mGetContent;

    public AnalyzeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_analyze, container, false);
        gallery_take = view.findViewById(R.id.gallery_take);
        camera_take = view.findViewById(R.id.Camera);
        captureImage = view.findViewById(R.id.my_avator);
        analyebtn = view.findViewById(R.id.analyebtn);


        camera_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                imageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

            }
        });

        analyebtn.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.inactivebtn)));
        analyebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Missing Trained Model", Toast.LENGTH_SHORT).show();
            }
        });


        gallery_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent = new Intent(getContext(), CropperActivity.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 101);

            }
        });

        return view;
    }


    private Uri getOutputMediaFileUri(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getString(R.string.mal_images_path));

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return FileProvider.getUriForFile(requireActivity(), getString(R.string.fileprovider), mediaFile);


    }

    private void saveCroppedImageToFolder(Uri croppedUri) {
        try {
            File mediaStorageDir = new File(getContext().getFilesDir(), "Mal_Images");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("MyApp", "Failed to create directory");
                    return;
                }
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File outputFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".jpg");

            InputStream inputStream = getContext().getContentResolver().openInputStream(croppedUri);
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
                getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            }
            MediaScannerConnection.scanFile(getContext(), new String[]{outputFile.getAbsolutePath()}, null, null);

            // Image saved successfully
            Toast.makeText(getContext(), "Image saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any errors that may occur during image saving
            Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (imageUri != null) {
                Intent intent = new Intent(getContext(), CropperActivity.class);
                intent.putExtra("DATA", imageUri.toString());
                startActivityForResult(intent, 101);
            } else {
                Toast.makeText(getContext(), "Image capture failed", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == -1 && requestCode == 101) {
            String result = data.getStringExtra("RESULT");
            if (result != null) {
                Uri croppedUri = Uri.parse(result);
                captureImage.setImageURI(croppedUri);
                analyebtn.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.activebtn)));
                saveCroppedImageToFolder(croppedUri);
            }


        } else {
            Toast.makeText(getContext(), "Error saving image", Toast.LENGTH_SHORT).show();
        }

    }


}