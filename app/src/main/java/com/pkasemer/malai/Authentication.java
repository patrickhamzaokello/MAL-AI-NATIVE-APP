package com.pkasemer.malai;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.pkasemer.malai.Apis.APIBase;
import com.pkasemer.malai.Apis.ApiEndPoints;
import com.pkasemer.malai.HelperClasses.SharedPrefManager;
import com.pkasemer.malai.Models.UserAuth;
import com.pkasemer.malai.Models.UserModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Authentication extends AppCompatActivity implements View.OnClickListener{

    LinearLayout login_screen, register_btn;
    GoogleSignInClient googleSignInClient;
    GoogleSignInOptions googleSignInOptions;
    ActivityResultLauncher<Intent> activityResultLauncher;
    FirebaseAuth auth;
    FirebaseDatabase database;
    private ApiEndPoints apiEndPoints;
    ProgressBar main_progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initial splash screen

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.primary_color));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_color));

        if (SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }

        login_screen = findViewById(R.id.login_screen);
        register_btn = findViewById(R.id.register_btn);
        main_progress = findViewById(R.id.main_progress);
        main_progress.setVisibility(View.GONE);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        login_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Authentication.this, LoginMaterial.class);
                startActivity(intent);
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Authentication.this, RegisterMaterial.class);
                startActivity(intent);
            }
        });
        apiEndPoints = APIBase.getClient(Authentication.this).create(ApiEndPoints.class);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(getString(R.string.firebase_database_url));

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                    auth.signInWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                assert user != null;
                                UserAuth userAuth = new UserAuth();
                                userAuth.setId(user.getUid());
                                userAuth.setUsername(user.getDisplayName());
                                userAuth.setFullName(user.getDisplayName());
                                userAuth.setEmail(user.getEmail());
                                userAuth.setProfilePic(String.valueOf(user.getPhotoUrl()));
                                userAuth.setPhone(user.getPhoneNumber() != null ? user.getPhoneNumber() : "Provide Phone number");
                                userAuth.setStatus("Google Account");
                                // post and register or retrieve user
                                save_Auth_Data_MwonyaDB(userAuth);


                            } else {
                                Toast.makeText(Authentication.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // user the intent
                } catch (ApiException e) {
                    e.printStackTrace();
                    Toast.makeText(Authentication.this, "Try again", Toast.LENGTH_SHORT).show();
                    main_progress.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                googleSignIn();
                break;
        }
    }

    private void googleSignIn() {
        main_progress.setVisibility(View.VISIBLE);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activityResultLauncher.launch(signInIntent);
    }

    private void save_Auth_Data_MwonyaDB(UserAuth userAuth) {
        postGoogleAccountInfo(userAuth).enqueue(new Callback<UserAuth>() {
            @Override
            public void onResponse(Call<UserAuth> call, Response<UserAuth> response) {

                try {
                    UserAuth user_auth_data = response.body();
                    if(!user_auth_data.getError() && user_auth_data.getId() != null){
                        UserModel userModel = new UserModel(
                                user_auth_data.getId(),
                                user_auth_data.getFullName(),
                                user_auth_data.getUsername(),
                                user_auth_data.getEmail(),
                                user_auth_data.getPhone(),
                                user_auth_data.getPassword(),
                                user_auth_data.getSignUpDate(),
                                user_auth_data.getProfilePic(),
                                user_auth_data.getStatus(),
                                user_auth_data.getMwRole()
                        );

                        Log.e("TAG", "onResponse: "+user_auth_data.getProfilePic());
                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(userModel);
                        //starting the RootActivity activity
                        Intent intent = new Intent(Authentication.this, MainActivity.class);
                        Authentication.this.startActivity(intent);
                        main_progress.setVisibility(View.GONE);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<UserAuth> call, Throwable t) {

                t.printStackTrace();
                Toast.makeText(Authentication.this, "something went wrong", Toast.LENGTH_SHORT).show();
                main_progress.setVisibility(View.GONE);
            }
        });
    }

    private Call<UserAuth> postGoogleAccountInfo(UserAuth userAuth) {
        return apiEndPoints.postAuth(userAuth);
    }
}