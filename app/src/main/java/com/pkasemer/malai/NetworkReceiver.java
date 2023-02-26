package com.pkasemer.malai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pkasemer.malai.MalUtils.NetworkStatusIntentService;


public class NetworkReceiver extends BroadcastReceiver {

    private static boolean sNetworkStatus = true;

    public static boolean getNetworkStatus() {
        return sNetworkStatus;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(NetworkStatusIntentService.ACTION_NETWORK_STATUS)) {
            boolean networkStatus = intent.getBooleanExtra(NetworkStatusIntentService.EXTRA_NETWORK_STATUS, true);
            sNetworkStatus = networkStatus;
            Log.e("pii-", "NetworkReceiver: "+sNetworkStatus );
        }
    }
}