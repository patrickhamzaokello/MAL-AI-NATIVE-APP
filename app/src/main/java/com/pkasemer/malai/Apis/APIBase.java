package com.pkasemer.malai.Apis;

import android.content.Context;
import android.util.Log;

import com.pkasemer.malai.NetworkReceiver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class APIBase {

    private static Retrofit retrofit = null;

    private final static long CACHE_SIZE = 100 * 1024 * 1024; // 10MB Cache size

    private static OkHttpClient buildClient(Context context) {
        // Build interceptor
        final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = chain -> {
            Request request = chain.request();
                // If there is a network connection, set cache control for 30 seconds
                request = request.newBuilder()
                        .header("Cache-Control", "public, max-age=30")
                        .build();

            return chain.proceed(request);
        };

        // Create Cache
        Cache cache = new Cache(context.getCacheDir(), CACHE_SIZE);

        return new OkHttpClient
                .Builder()
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .cache(cache)
                .build();
    }



    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(buildClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://xyzobide.kakebeshop.com/mwonyaaAPI/Requests/endpoints/")
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getInferenceBase(Context context){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(buildClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://kasfa.mwonya.com/")
                    .build();
        }
        return retrofit;
    }

}