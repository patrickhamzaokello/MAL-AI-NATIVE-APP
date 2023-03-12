package com.pkasemer.malai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pkasemer.malai.MalUtils.NetworkStatusIntentService;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navView;
    NavController navController;
    private String[] PERMISSIONS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AskPermissions();
        setUpNavigation();
        // network register and start background serice for network status
        LocalBroadcastManager.getInstance(this).registerReceiver(new NetworkReceiver(), new IntentFilter(NetworkStatusIntentService.ACTION_NETWORK_STATUS));


        try {
            Intent networkIntent = new Intent(this, NetworkStatusIntentService.class);
            startService(networkIntent);
        } catch (IllegalStateException e) {
            // Handle the exception
            Log.e("root", "Unable to start service: " + e.getMessage());
        }
    }

    private void setUpNavigation() {
        navView = findViewById(R.id.bottomNav_view);
        navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void AskPermissions() {

        PERMISSIONS = new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        if(!hasPermissions(MainActivity.this, PERMISSIONS)){
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){


            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            } else {
            }
            if(grantResults[1]== PackageManager.PERMISSION_GRANTED){
            } else {
            }
            if(grantResults[2]== PackageManager.PERMISSION_GRANTED){
            } else {
            }
            if(grantResults[3]== PackageManager.PERMISSION_GRANTED){
            } else {
            }
            if(grantResults[4]== PackageManager.PERMISSION_GRANTED){
            } else {
            }
        }
    }

    private boolean hasPermissions(Context context, String... PERMISSIONS){
        if(context != null && PERMISSIONS != null){
            for (String permission: PERMISSIONS){
                if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }
}