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

    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String HEADER_PRAGMA = "Pragma";
    private static Retrofit retrofit = null;

    private final static long CACHE_SIZE = 100 * 1024 * 1024; // 100MB Cache size

    private static OkHttpClient buildClient(Context context) {

        // Create Cache
        Cache cache = new Cache(context.getCacheDir(), CACHE_SIZE);

        return new OkHttpClient
                .Builder()
                .addInterceptor(offlineInterceptor(context))
                .addNetworkInterceptor(networkInterceptor()) // only used when network is on
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)) // used if network off or on
                .cache(cache)
                .build();
    }



    private static Interceptor offlineInterceptor(Context context){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Log.d("interceptor", "offlineInterceptor called");

                Request request = chain.request();
                //prevent caching when network is on. for that we use the networkinterceptor

                if (!NetworkReceiver.getNetworkStatus()) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();
                    request = request.newBuilder()
                            .removeHeader(HEADER_PRAGMA)
                            .removeHeader(HEADER_CACHE_CONTROL)
                            .cacheControl(cacheControl)
                            .build();

                }
                Log.e("intercept", "network: " + NetworkReceiver.getNetworkStatus() );
                return chain.proceed(request);
            }
        };
    }

    private static Interceptor networkInterceptor(){
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Log.d("interceptor", "networkinterceptor called");

                Response response =  chain.proceed(chain.request());
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(30, TimeUnit.MINUTES)
                        .build();
                return response.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }


    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(buildClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
//                    .baseUrl("http://192.168.0.130:8080/projects/MwonyaaApi/Requests/endpoints/")
//                    .baseUrl("https://shop.mwonyaa.com/Requests/endpoints/")
                    .baseUrl("https://xyzobide.kakebeshop.com/mwonyaaAPI/Requests/endpoints/")
                    .build();
        }
        return retrofit;
    }



}