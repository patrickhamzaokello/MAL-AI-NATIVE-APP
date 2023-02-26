package com.pkasemer.malai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.pkasemer.malai.Apis.ApiEndPoints;
import com.pkasemer.malai.Models.UserModel;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkStateChecker extends BroadcastReceiver {
    private Context context;
    private ApiEndPoints apiEndPoints;
    UserModel userModel;
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;


    }



}
