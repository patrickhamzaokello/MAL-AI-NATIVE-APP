package com.pkasemer.malai.ViewModel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pkasemer.malai.Apis.APIBase;
import com.pkasemer.malai.Apis.ApiEndPoints;
import com.pkasemer.malai.Models.InferenceModel;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InferenceViewModel extends ViewModel {
    private MutableLiveData<InferenceModel> inference_data;

    public InferenceViewModel() {
        inference_data = new MutableLiveData<>();
    }

    public MutableLiveData<InferenceModel> getInferenceDataObserver() {
        return inference_data;
    }

    public void makeApiCall(Context context, MultipartBody.Part body){
        ApiEndPoints apiEndPoints = APIBase.getInferenceBase(context).create(ApiEndPoints.class);
        Call<InferenceModel> callInferenceEndPoint = apiEndPoints.postImage(body);

        callInferenceEndPoint.enqueue(new Callback<InferenceModel>() {
            @Override
            public void onResponse(Call<InferenceModel> call, Response<InferenceModel> response) {
                InferenceModel inferenceModel = response.body();
                inference_data.postValue(inferenceModel);
            }

            @Override
            public void onFailure(Call<InferenceModel> call, Throwable t) {
                inference_data.postValue(null);
            }
        });

    }
}
