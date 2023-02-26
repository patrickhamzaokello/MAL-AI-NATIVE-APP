package com.pkasemer.malai.MalUtils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class NetworkUtil {

    private static final String SERVER_URL = "https://xyzobide.kakebeshop.com/mwonyaaAPI/Requests/endpoints/ping.php";


    public static boolean hasNetwork(Context context) {
        boolean isConnected = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null && activeNetwork.isConnected()) {
            isConnected = isServerReachable();
        }
        return isConnected;
    }

    private static boolean isServerReachable() {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setConnectTimeout(10 * 1000);          // 10 s.
            urlc.connect();
            if (urlc.getResponseCode() == 200) {        // 200 OK
                return true;
            } else {
                return false;
            }
        } catch (MalformedURLException e1) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

}
