package com.pkasemer.malai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pkasemer.malai.HelperClasses.SharedPrefManager;
import com.pkasemer.malai.HttpRequests.RequestHandler;
import com.pkasemer.malai.HttpRequests.URLs;
import com.pkasemer.malai.Models.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginMaterial extends AppCompatActivity {
    TextView logoText, sloganText;
    ImageView image;
    Button callSignUp,forgotPassword;
    LinearLayout loginbtn;
    TextInputLayout username_layout, password_layout;
    TextInputEditText inputTextUsername, inputTextPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_material);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.primary_color));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_color));
        //if the user is already logged in we will directly start the profile activity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        //Hooks
        callSignUp = findViewById(R.id.signup_screen);
        loginbtn = findViewById(R.id.login_btn);
        logoText = findViewById(R.id.login_welcomeback);
        sloganText = findViewById(R.id.login_subtext);

        username_layout = findViewById(R.id.login_username);
        password_layout = findViewById(R.id.login_password);
        forgotPassword = findViewById(R.id.forgotPassword);

        inputTextUsername = findViewById(R.id.inputTextUsername);
        inputTextPassword = findViewById(R.id.inputTextPassword);


        //if user presses on login
        //calling the method login
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        //if user presses on not registered
        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open register screen
                Intent intent = new Intent(LoginMaterial.this, RegisterMaterial.class);
                startActivity(intent);
            }
        });



    }

    private void userLogin() {
        //first getting the values
        final String username = inputTextUsername.getText().toString().trim();
        final String password = inputTextPassword.getText().toString().trim();

        //validating inputs
        if (TextUtils.isEmpty(username)) {
            inputTextUsername.setError("Please enter your username");
            inputTextUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            inputTextPassword.setError("Please enter your password");
            inputTextPassword.requestFocus();
            return;
        }

        //if everything is fine

        class UserLogin extends AsyncTask<Void, Void, String> {

            ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if(s.isEmpty()){
                    //show network error
                    progressBar.setVisibility(View.GONE);
                    showErrorAlert();
                    return;
                }

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
//                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object
                        UserModel userModel = new UserModel(
                                userJson.getString("id"),
                                userJson.getString("full_name"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("phone"),
                                userJson.getString("password"),
                                userJson.getString("signUpDate"),
                                userJson.getString("profilePic"),
                                userJson.getString("status"),
                                userJson.getString("mwRole")
                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(userModel);

                        //starting the RootActivity activity
                        Intent intent = new Intent(LoginMaterial.this, MainActivity.class);
                        LoginMaterial.this.startActivity(intent);
                        progressBar.setVisibility(View.GONE);
                        finish();

                    } else {
                        //Invalid Username or Password
                        progressBar.setVisibility(View.GONE);
                        showInvalidUser();
                        return;
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_LOGIN, params);
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }

    private void showErrorAlert() {
        new SweetAlertDialog(
                this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Something went wrong!")
                .show();
    }

    private void showInvalidUser() {

        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Wrong Info")
                .setContentText("Invalid User or Password")
                .show();
    }
}