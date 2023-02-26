package com.pkasemer.malai.MalUtils;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import javax.annotation.Nullable;

public class NetworkStatusIntentService extends IntentService {

    public static final String EXTRA_NETWORK_STATUS = "network_status";
    public static final String ACTION_NETWORK_STATUS = "com.pkasemer.malai.NETWORK_STATUS_ACTION";

    public NetworkStatusIntentService() {
        super("NetworkStatusIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        boolean networkStatus = NetworkUtil.hasNetwork(this);
        Intent broadcastIntent = new Intent(ACTION_NETWORK_STATUS);
        broadcastIntent.putExtra(EXTRA_NETWORK_STATUS, networkStatus);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

    }
}
