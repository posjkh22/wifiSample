package com.example.wificonnectchecksample;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import java.util.Timer;
import java.util.TimerTask;

public class WiFiStatusManager {

    private Context context;
    private  WiFiStatusCallback wifiStatusCallback;

    public WiFiStatusManager(Context context) {
        this.context = context;
        this.wifiStatusCallback = new WiFiStatusCallback(context);
    }

    public void register() {
        if (wifiStatusCallback != null) {
            wifiStatusCallback.register();
        }
    }
    public void unregister() {
        if (wifiStatusCallback != null) {
            wifiStatusCallback.unregister();
        }
    }

    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public void setWifiEnabled() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            Intent intent = new Intent(Settings.Panel.ACTION_WIFI);
            this.context.startActivity(intent);
        }
        else {
            WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
        }
    }

    public WifiInfo getCurrentWiFiInfo() {
        WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo();
    }
}
