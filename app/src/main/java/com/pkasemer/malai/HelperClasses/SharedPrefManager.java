package com.pkasemer.malai.HelperClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.pkasemer.malai.Models.UserModel;
import com.pkasemer.malai.Authentication;


public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "mwonya_shared_pref";
    private static final String KEY_ID = "KEY_ID";
    private static final String KEY_FULL_NAME = "KEY_FULL_NAME";
    private static final String KEY_USERNAME = "KEY_USERNAME";
    private static final String KEY_EMAIL = "KEY_EMAIL";
    private static final String KEY_PHONE = "KEY_PHONE";
    private static final String KEY_PASSWORD = "KEY_PASSWORD";
    private static final String KEY_signUpDate = "KEY_signUpDate";
    private static final String KEY_profilePic = "KEY_profilePic";
    private static final String KEY_status = "KEY_status";
    private static final String KEY_mwRole = "KEY_mwRole";



    private static SharedPrefManager mInstance;
    private Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }




    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(UserModel userModel) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID, userModel.getId());
        editor.putString(KEY_FULL_NAME, userModel.getFull_name());
        editor.putString(KEY_USERNAME, userModel.getUsername());
        editor.putString(KEY_EMAIL, userModel.getEmail());
        editor.putString(KEY_PHONE, userModel.getPhone());
        editor.putString(KEY_profilePic, userModel.getProfilePic());
        editor.apply();
    }





    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }



    //this method will give the logged in user
    public UserModel getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new UserModel(
                sharedPreferences.getString(KEY_ID, null),
                sharedPreferences.getString(KEY_FULL_NAME, null),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_PHONE, null),
                sharedPreferences.getString(KEY_PASSWORD, null),
                sharedPreferences.getString(KEY_signUpDate, null),
                sharedPreferences.getString(KEY_profilePic, null),
                sharedPreferences.getString(KEY_status, null),
                sharedPreferences.getString(KEY_mwRole, null)
        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(mCtx, Authentication.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mCtx.startActivity(intent);
    }
}
