package mass.Ranger.Device;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import mass.Ranger.Util.Utils;

import java.util.ArrayList;
import java.util.List;

public class WiFiScanner {
    private final String                        TAG              = WiFiScanner.class.getName();
    private final List<WiFiScannerEventHandler> handlers         = new ArrayList<WiFiScannerEventHandler>();
    /*
    A systemic wifi scan finished event handler
    */
    private final BroadcastReceiver             wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getResultCode() == Activity.RESULT_OK) {
                WiFiStamps wifiStamps = new WiFiStamps();
                long endMillis = System.currentTimeMillis();
                List<ScanResult> results = wifiManager.getScanResults();

                if (results == null || results.size() == 0) {
                    //Logger.w(TAG, "WiFi scan return null");
                    return;
                }
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String connectedBSSID = null;
                try {
                    if (wifiInfo != null) {
                        connectedBSSID = wifiInfo.getBSSID().trim();
                        if (connectedBSSID.startsWith("\"")) {
                            connectedBSSID = connectedBSSID.replace('"', ' ').trim();
                        }
                    }
                } catch (Exception ignored) {
                }
                for (ScanResult result : results) {
                    AccessPointReading accessPointReading = new AccessPointReading();
                    accessPointReading.setFrequencyInMHz(result.frequency);
                    accessPointReading.setCapabilities(result.capabilities);
                    accessPointReading.setBssid(result.BSSID);
                    accessPointReading.setRSSI(result.level);
                    accessPointReading.setSsid(result.SSID);
                    accessPointReading.setTimestamps((startMillis / 2 + endMillis / 2) * Utils.TicksInMs);
                    if (result.BSSID.equals(connectedBSSID)) {
                        accessPointReading.setConnected(1);
                    }
                    wifiStamps.add(accessPointReading);
                    wifiStamps.setStartTimeMillis(startMillis);
                    wifiStamps.setEndTimeTimeMillis(endMillis);
                }
                
                for (WiFiScannerEventHandler handler : handlers) {
                    handler.onWiFiScanFinished(wifiStamps);
                }
                // TODO sleep for a while and start again so that we won't waste so much power
                startInternal();
            }
        }
    };
    private long        startMillis;
    private WifiManager wifiManager;

    public WiFiScanner(Context context) {
        this.wifiManager = (WifiManager) context.getSystemService(Activity.WIFI_SERVICE);
    }

    private void startInternal() {
        startMillis = System.currentTimeMillis();
        wifiManager.startScan();
    }

    public void start(Context context) {
        if (!this.wifiManager.isWifiEnabled()) {
            this.wifiManager.setWifiEnabled(true);
        }
        try {
            context.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        } catch (Exception ex) {
            //Logger.e(TAG, "register failed", ex);
        }
        startInternal();
    }

    public void stop(Context context) {
        try {
            context.unregisterReceiver(wifiScanReceiver);
        } catch (Exception ex) {
            //Logger.e(TAG, "stop failed", ex);
        }
    }

    public void registerWiFiHandler(WiFiScannerEventHandler handler) {
        handlers.add(handler);
    }

    public void removeWiFiHandler(WiFiScannerEventHandler handler) {
        handlers.remove(handler);
    }

    protected interface WiFiScannerEventHandler {
        public void onWiFiScanFinished(WiFiStamps data);
    }
}
