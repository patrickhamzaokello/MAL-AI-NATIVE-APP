package com.pkasemer.malai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
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

public class RegisterMaterial extends AppCompatActivity {

    TextView logoText, sloganText;
    Button callLogIN;
    LinearLayout register_btn;
    TextInputLayout username_layout, password_layout;

    TextInputEditText inputTextFullname, inputTextUsername, inputTextEmail, inputTextPhone,inputTextPassword, inputTextConfirmPassword;
    RadioGroup radioGroupGender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_material);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.primary_color));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_color));
        //if the user is already logged in we will directly start the profile activity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        //Hooks

        //Hooks
        callLogIN = findViewById(R.id.login_screen);
        register_btn = findViewById(R.id.register_btn);
        logoText = findViewById(R.id.register_welcomeback);
        sloganText = findViewById(R.id.register_subtext);

        username_layout = findViewById(R.id.login_username);
        password_layout = findViewById(R.id.login_password);

        //get text from input boxes
        inputTextFullname = findViewById(R.id.inputTextFullname);
        inputTextUsername = findViewById(R.id.inputTextUsername);
        inputTextEmail = findViewById(R.id.inputTextEmail);
        inputTextPhone = findViewById(R.id.inputTextPhone);
        inputTextPassword = findViewById(R.id.inputTextPassword);
        inputTextConfirmPassword = findViewById(R.id.inputTextConfirmPassword);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        callLogIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on login
                //we will open the login screen
                Intent intent = new Intent(RegisterMaterial.this, LoginMaterial.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        final String full_name = inputTextFullname.getText().toString().trim();
        final String user_name = inputTextUsername.getText().toString().trim();
        final String user_email = inputTextEmail.getText().toString().trim();
        final String user_phone = inputTextPhone.getText().toString().trim();
        final String user_password = inputTextPassword.getText().toString().trim();
        final String confirm_password = inputTextConfirmPassword.getText().toString().trim();



        //first we will do the validations

        if (TextUtils.isEmpty(full_name)) {
            inputTextFullname.setError("Enter Full Name");
            inputTextFullname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(user_name)) {
            inputTextUsername.setError("Please enter username");
            inputTextUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(user_email)) {
            inputTextEmail.setError("Please enter your email");
            inputTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(user_email).matches()) {
            inputTextEmail.setError("Enter a valid email");
            inputTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(user_password)) {
            inputTextPassword.setError("Enter a password");
            inputTextPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(user_phone)) {
            inputTextPhone.setError("Enter a Phone Number");
            inputTextPhone.requestFocus();
            return;
        }

        if (!user_password.equals(confirm_password)) {
            inputTextPassword.setError("Password Does not Match");
            inputTextPassword.requestFocus();
            return;
        }


        //if it passes all the validations

        class RegisterUser extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("full_name", full_name);
                params.put("username", user_name);
                params.put("email", user_email);
                params.put("phone", user_phone);
                params.put("password", user_password);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion

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

                        //starting the profile activity
                        Intent intent = new Intent(RegisterMaterial.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        RegisterMaterial.this.startActivity(intent);
                        progressBar.setVisibility(View.GONE);
                        finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        showUserExists();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        RegisterUser ru = new RegisterUser();
        ru.execute();
    }


    private void showErrorAlert() {
        new SweetAlertDialog(
                this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Something went wrong!")
                .show();
    }

    private void showUserExists() {

        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("User already Exists")
                .setContentText("Try different Username and Email")
                .show();
    }


}