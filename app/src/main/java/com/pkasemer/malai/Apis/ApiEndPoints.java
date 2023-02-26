package com.pkasemer.malai.Apis;

import com.pkasemer.malai.Models.UserAuth;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface ApiEndPoints {




    //post user third part auth details
    @POST("userAuth.php")
    @Headers("Cache-Control: no-cache")
    Call<UserAuth> postAuth(
            @Body UserAuth userAuth
    );


}