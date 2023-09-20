package com.example.wificonnectchecksample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "'MainActivity'";

    private TextView tvWifiStatus;
    private TextView tvScan;

    private TextView tvConnectedWifi;
    private TextView tvConnectedWifi_OLD;
    private TextView tvConnectedWifi_OLD_V2;


    private Button btnScan;
    private Button btnRun;
    private Button btnConnect;

    private EditText editSsid;

    private EditText editPw;



    private String currentSSID = "";
    private String currentBSSID = "";

    private List<ScanResult> currentScanList;

    private String[] permission = new String[]{
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.CHANGE_WIFI_STATE,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
        android.Manifest.permission.ACCESS_WIFI_STATE,
        android.Manifest.permission.CHANGE_NETWORK_STATE,
        android.Manifest.permission.WRITE_SETTINGS,
    };

    WiFiStatusManager wiFiStateManager = null;

    WiFiScanManager wifiScanManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // permission for wifi
        checkPermission();

        initUiContents();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setWifiEnabled();
        registerWifiManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterWifiManager();
    }

    private void setWifiEnabled() {
        if (wiFiStateManager == null) {
            wiFiStateManager = new WiFiStatusManager(this);
        }
        if (!wiFiStateManager.isWifiEnabled()) {
            wiFiStateManager.setWifiEnabled();
        }

    }

    private void registerWifiManager() {
        if (wiFiStateManager != null) {
            wiFiStateManager.register();
        }
        if (wifiScanManager == null) {
            wifiScanManager = new WiFiScanManager(this);
            wifiScanManager.register(this);
        }
    }
    private void unregisterWifiManager() {
        if (wiFiStateManager != null) {
            wiFiStateManager.unregister();
        }

        if (wifiScanManager != null) {
            wifiScanManager.unregister();
            wifiScanManager = null;
        }
    }

    private void initUiContents() {
        tvWifiStatus = findViewById(R.id.tvWifiStatus);
        tvWifiStatus.setText("");

        tvScan = findViewById(R.id.scanList);
        tvScan.setText("");

        btnRun = findViewById(R.id.btnRun);
        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick - btnScan");
                run();
            }
        });

        btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick - btnScan");
                scan();
            }
        });

        btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick - btnConnect");
                connect();
            }
        });

        editSsid = findViewById(R.id.edit_ssid);
        editPw = findViewById(R.id.edit_pw);

        editSsid.setText("KH");
        editPw.setText("12345678");

        tvConnectedWifi = findViewById(R.id.tvConnectedWifi);
        tvConnectedWifi_OLD = findViewById(R.id.tvConnectedWifi_OLD);
        tvConnectedWifi_OLD_V2 = findViewById(R.id.tvConnectedWifi_OLD_V2);


    }

    private void run() {


        scanConnect();

        //getCurrentWifi();
    }

    private void scan() {
        tvScan.setText("");
        if (wifiScanManager != null) {
            wifiScanManager.startScan();
        }
    }

    private void scanConnect() {

    }

    private void connect() {
        final String ssid = editSsid.getText().toString();
        final String pw = editPw.getText().toString();
        WiFiConnectManager cm = new WiFiConnectManager(this);
        cm.connect(ssid, pw);
    }

    private void registerConnectedLog(String text) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss");
        String timeString = dateFormat.format(now);
        String log = String.format("[%s] %s\n", timeString, text);
        tvWifiStatus.append(log);
    }

    private void registerScanLog(String text) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss");
        String timeString = dateFormat.format(now);
        String log = String.format("[%s] %s\n", timeString, text);
        tvScan.append(log);
    }

    private void registerConnectLog(String text) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss");
        String timeString = dateFormat.format(now);
        String log = String.format("[%s] %s\n", timeString, text);
        tvWifiStatus.append(log);
    }

    private boolean checkPermission(){
        int result;
        List<String> listPermission = new ArrayList<>();
        for(String p : permission){
            result = ContextCompat.checkSelfPermission(MainActivity.this,p);
            if (result != PackageManager.PERMISSION_GRANTED){
                listPermission.add(p);
            }
        }
        if(!listPermission.isEmpty()){
            ActivityCompat.requestPermissions(MainActivity.this,listPermission.toArray(new String[listPermission.size()]),0);
            return false;
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String type = intent.getStringExtra("type");

        switch(type) {

            case "wifi_scanned_info": {
                currentScanList = (List<ScanResult>) intent.getSerializableExtra("result");
                for (ScanResult item : currentScanList) {
                    String ssid = item.SSID;
                    String bssid = item.BSSID;
                    this.registerScanLog("ssid: " + ssid + "[" + bssid + "]");
                }
            } break;

            case "wifi_connected_info": {
                String ssid = intent.getStringExtra("ssid");
                String bssid = intent.getStringExtra("bssid");
                if (!currentSSID.equals(ssid) || !currentBSSID.equals(bssid)) {
                    currentSSID = ssid;
                    currentBSSID = bssid;
                    tvConnectedWifi.setText(currentSSID);
                    if (!currentBSSID.isEmpty()) {
                        this.registerConnectedLog("ssid: " + ssid + "[" + bssid + "]");
                    }
                    else {
                        this.registerConnectedLog(ssid);
                    }
                }
            } break;

            case "wifi_status": {
                String result = intent.getStringExtra("result");
                this.registerConnectLog("Try to connect: " + result);
            } break;

        }
    }
}