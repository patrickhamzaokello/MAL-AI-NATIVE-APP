package com.pkasemer.malai.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.pkasemer.malai.CaptureImageActivity;
import com.pkasemer.malai.CropperActivity;
import com.pkasemer.malai.MainActivity;
import com.pkasemer.malai.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;


public class AnalyzeFragment extends Fragment {
    private static final int DESIRED_WIDTH = 628;
    private static final int DESIRED_HEIGHT = 480;
    private static final int CROP_REQUEST = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    LinearLayout uploadImageBtn, submit_btn;

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
        submit_btn = view.findViewById(R.id.submit_btn);
        uploadImageBtn = view.findViewById(R.id.uploadImage);
        captureImage = view.findViewById(R.id.my_avator);


        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                imageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraIntent.putExtra("return-data", true);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

            }
        });


        submit_btn.setOnClickListener(new View.OnClickListener() {
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
                Environment.DIRECTORY_PICTURES), "Kasfa");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("Kasfa", "Failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        // Get the content:// URI for the file using FileProvider
        Uri fileUri = FileProvider.getUriForFile(requireActivity(), getString(R.string.fileprovider), mediaFile);

        return fileUri;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("URIk", "onActivityResult: " + requestCode);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (imageUri != null) {
                Intent intent = new Intent(getContext(), CropperActivity.class);
                intent.putExtra("DATA", imageUri.toString());
                startActivityForResult(intent, 101);
                Toast.makeText(getContext(), "Image Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Image capture failed", Toast.LENGTH_SHORT).show();
            }
        }
        if(resultCode==-1 && requestCode==101){
            String result = data.getStringExtra("RESULT");
            Uri resultUri = null;
            if(result != null){
                resultUri = Uri.parse(result);
                captureImage.setImageURI(resultUri);
            }


        }

    }




}