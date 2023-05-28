package com.pkasemer.malai.Fragments;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pkasemer.malai.HelperClasses.SharedPrefManager;
import com.pkasemer.malai.LoginMaterial;
import com.pkasemer.malai.MalUtils.NetworkStatusIntentService;
import com.pkasemer.malai.Models.UserModel;
import com.pkasemer.malai.NetworkReceiver;
import com.pkasemer.malai.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SettingsFragment extends Fragment {
    TextView textViewUsername, textViewEmail, other_info;
    ImageView profile_image;
    ImageButton backbtn;
    private DrawableCrossFadeFactory factory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        if (!SharedPrefManager.getInstance(getContext()).isLoggedIn()) {
            startActivity(new Intent(getContext(), LoginMaterial.class));
        }
        factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

        textViewUsername = (TextView) view.findViewById(R.id.full_name);
        profile_image = (ImageView) view.findViewById(R.id.profile_image);
        textViewEmail = (TextView) view.findViewById(R.id.email_text);
        other_info = (TextView) view.findViewById(R.id.other_info);
        backbtn = view.findViewById(R.id.backbtn);


        UserModel userModel = SharedPrefManager.getInstance(getContext()).getUser();

        //setting the values to the textviews
        textViewUsername.setText(userModel.getFull_name());
        textViewEmail.setText(userModel.getEmail());
        other_info.setText(userModel.getUsername() + ", " + userModel.getPhone());

        Glide.with(getContext())
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background))
                .load(userModel.getProfilePic())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)     // cache both original & resized image
                .centerCrop()
                .transition(withCrossFade(factory))
                .into(profile_image);

        //when the user presses logout button
        //calling the logout method
        view.findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Logout!")
                        .setContentText("You will be required to Sign in next time")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.dismissWithAnimation();

                                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestIdToken(getString(R.string.default_web_client_id))
                                        .requestEmail()
                                        .build();
                                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);

                                GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(getContext());

                                if (gAccount != null) {
                                    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            SharedPrefManager.getInstance(getContext()).logout();
                                        }
                                    });
                                } else {
                                    SharedPrefManager.getInstance(getContext()).logout();
                                }

                            }
                        }).show();

            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // network register and start background serice for network status
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new NetworkReceiver(), new IntentFilter(NetworkStatusIntentService.ACTION_NETWORK_STATUS));
        Intent networkIntent = new Intent(getContext(), NetworkStatusIntentService.class);
        getContext().startService(networkIntent);



    }
}