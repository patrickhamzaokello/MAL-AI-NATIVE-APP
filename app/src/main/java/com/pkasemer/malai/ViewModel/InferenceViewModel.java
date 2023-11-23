package com.pkasemer.malai.ViewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pkasemer.malai.Apis.APIBase;
import com.pkasemer.malai.Apis.ApiEndPoints;
import com.pkasemer.malai.Models.InferenceModel;
import com.pkasemer.malai.ProgressRequestBody;
import com.pkasemer.malai.ProgressRequestBodyOne;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InferenceViewModel extends ViewModel  {

    private final MutableLiveData<InferenceModel> inferenceData = new MutableLiveData<>();


    public MutableLiveData<InferenceModel> getInferenceDataObserver() {
        return inferenceData;
    }


    public void makeApiCall(Context context, File file, final ProgressRequestBody.UploadCallbacks listener) {
        // Create the custom ProgressRequestBody
        ProgressRequestBody fileBody = new ProgressRequestBody(file, listener);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
        // Create the API service
        ApiEndPoints apiEndPoints = APIBase.getInferenceBase(context).create(ApiEndPoints.class);

        // Create the API call
        Call<InferenceModel> call = apiEndPoints.postImage(filePart);

        // Execute the API call asynchronously
        call.enqueue(new Callback<InferenceModel>() {
            @Override
            public void onResponse(Call<InferenceModel> call, Response<InferenceModel> response) {
                if (response.isSuccessful()) {
                    InferenceModel inferenceModel = response.body();
                    inferenceData.postValue(inferenceModel);
                } else {
                    // Handle unsuccessful response
                    Log.e("InferenceViewModel", "API call failed with code: " + response.code());
                    inferenceData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<InferenceModel> call, Throwable t) {
                // Handle failure
                Log.e("InferenceViewModel", "API call failed", t);
                inferenceData.postValue(null);
            }
        });
    }


}