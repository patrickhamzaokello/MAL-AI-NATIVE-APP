package com.pkasemer.malai.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pkasemer.malai.CapturedImageDetails;
import com.pkasemer.malai.CropperActivity;
import com.pkasemer.malai.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class HomeFragment extends Fragment {
    private static final int DESIRED_WIDTH = 628;
    private static final int DESIRED_HEIGHT = 480;
    private static final int REQUEST_CROP_REQUEST = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    LinearLayout camera_take, gallery_take;

    View view;
    private String[] PERMISSIONS;

    String path;
    Uri imageUri;


    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;

    ActivityResultLauncher<String> mGetContent;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        gallery_take = view.findViewById(R.id.gallery_take);
        camera_take = view.findViewById(R.id.Camera);



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



        gallery_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if(result !=null){
                    Intent intent = new Intent(getContext(), CropperActivity.class);
                    intent.putExtra("DATA", result.toString());
                    startActivityForResult(intent, 101);
                }

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
            if (result != null && getContext() != null) {
                Uri croppedUri = Uri.parse(result);
                Intent intent = new Intent(getContext(), CapturedImageDetails.class);
                intent.putExtra("CAPTURED_IMAGE_PATH", croppedUri.toString());
                Log.e("imagepath", "onCreate: " + croppedUri);
                getContext().startActivity(intent);
            }


        } else {
            Toast.makeText(getContext(), "Error Capturing Image", Toast.LENGTH_SHORT).show();
        }

    }




}