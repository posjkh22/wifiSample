package com.example.wificonnectchecksample;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class WiFiConnectManagerOLD extends ConnectivityManager.NetworkCallback {

    private Context context;
    private NetworkRequest networkRequest;
    private ConnectivityManager connectivityManager;

    private String TAG = "WiFiConnectManager";

    public WiFiConnectManagerOLD(Context context, int flag){
        this.context = context;
        networkRequest =
                new NetworkRequest.Builder()
                        //.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .build();
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void connect(String ssid, String pw, boolean isHidden) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            final WifiNetworkSpecifier wifiNetworkSpecifier;
            wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                    .setSsid(ssid)
                    .setWpa2Passphrase(pw)
                    //.setIsHiddenSsid(isHidden)
                    .build();

            final NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .setNetworkSpecifier(wifiNetworkSpecifier)
                    .build();
            this.connectivityManager.requestNetwork(networkRequest, this);
        }
    }

    public void register() { this.connectivityManager.registerNetworkCallback(networkRequest, this);}

    public void unregister() {
        this.connectivityManager.unregisterNetworkCallback(this);
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        Log.d(TAG, "network connected");

        Intent intent = new Intent(this.context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("type", "connected_wifi");
        intent.putExtra("ssid", "Connected");
        intent.putExtra("bssid", "");
        startActivity(this.context, intent, null);
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            WifiInfo wifiInfo =  (WifiInfo) networkCapabilities.getTransportInfo();

            if (wifiInfo != null)
            {
                String ssid = wifiInfo.getSSID().replace("\"",  "" );
                String bssid = wifiInfo.getBSSID();
                Log.d(TAG, "ssid: " +ssid + "\n" + "bssid: "+ bssid);

                Intent intent = new Intent(this.context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);

                intent.putExtra("type", "connected_wifi");
                intent.putExtra("ssid", ssid);
                intent.putExtra("bssid", bssid);
                startActivity(this.context, intent, null);
            }
        }
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        Log.d(TAG, "network disconnected");

        Intent intent = new Intent(this.context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("type", "connected_wifi");
        intent.putExtra("ssid", "Disconnected");
        intent.putExtra("bssid", "");
        startActivity(this.context, intent, null);
    }
}
