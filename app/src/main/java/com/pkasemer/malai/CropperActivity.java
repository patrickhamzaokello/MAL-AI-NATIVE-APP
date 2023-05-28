package com.pkasemer.malai;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.util.UUID;

public class CropperActivity extends AppCompatActivity {

    String result;
    Uri fileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);
        readIntent();

        String dest_uri = new StringBuilder(UUID.randomUUID().toString()).append(".png").toString();

        UCrop.Options options = new UCrop.Options();
        options.setShowCropFrame(true);
        options.setShowCropGrid(true);
        options.setAllowedGestures(UCropActivity.ROTATE,UCropActivity.SCALE,UCropActivity.SCALE);


        UCrop.of(fileUri,Uri.fromFile(new File(getCacheDir(),dest_uri)))
                .withOptions(options)
                .withAspectRatio(640,480)
                .withMaxResultSize(640, 480)
                .start(CropperActivity.this);

    }

    private void readIntent() {
        Intent intent = getIntent();
        if (intent.getExtras() != null){
            result = intent.getStringExtra("DATA");
            fileUri = Uri.parse(result);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            final  Uri resultUri = UCrop.getOutput(data);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULT", resultUri+"");
            setResult(-1, returnIntent);
            finish();
        } else if (resultCode == UCrop.RESULT_ERROR) {
            finish();
        }
    }
}