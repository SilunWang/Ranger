package mass.Ranger.Util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.*;

public class MyWifi extends Service {
    private WifiManager wifi;
    private BroadcastReceiver wifiReceiver;
    private Timer wifiTimer;
    private Timer wifiTimer2;
    private WifiManager.WifiLock wifiLock;

    private static List<ScanResult> results;
    private static final String TAG = "MYWIFI";
    private List<WifiBriefInfo> currentWifiInfoList;

    private long scantime, receivetime;
    private int rec_wifi_cnt = 0;
    private String rec_wifi = "";
    private int wifi_buffer = 30;
    private static int rcvcnt = 0;
    private static int stuckcnt = 0;
    private static long stuckTest_interval = 1000;
    private static long stuckTest_timeout = 5000;


    //-----------------

    private static ArrayList<LinkedList<WifiBriefInfo>> FingerPrints = new ArrayList<LinkedList<WifiBriefInfo>>();

    @Override
    public void onCreate() {
        super.onCreate();
        clearall();
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiLock = wifi.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY,
                "myLock");
        if (!wifi.isWifiEnabled()
                && wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
            wifi.setWifiEnabled(true);
        }
        wifiTimer = new Timer();
        wifiTimer.schedule(new stuckTestTask(), 5000, stuckTest_interval);
        wifiTimer2 = new Timer();
        scantime = new Date().getTime();
        wifi.startScan();
        wifiReceiver = new mywifiReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void onDestroy() {
        dump();
        if (wifiLock.isHeld())
            wifiLock.release();
        wifiTimer.cancel();
        wifiTimer2.cancel();
        unregisterReceiver(wifiReceiver);
        super.onDestroy();
    }

    public void dump() {
        if (rec_wifi_cnt != 0) {
            //DataFiles.record(rec_wifi, DataFiles.FileType.wifi);
        }
        rec_wifi_cnt = 0;
    }

    public void clearall() {
        rec_wifi = "";
        rec_wifi_cnt = 0;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        IBinder ret = new LocalBinder();
        return ret;
    }

    public class LocalBinder extends Binder {
        public void dump() {
            // DataFiles.record("wifi dump\r\n",DataFiles.FileType.log);
            if (rec_wifi_cnt != 0) {
                //DataFiles.record(rec_wifi, DataFiles.FileType.wifi);
            }
            rec_wifi_cnt = 0;
        }

        public void clearall() {
            // DataFiles.record("wifi clear\r\n",DataFiles.FileType.log);
            rec_wifi = "";
            rec_wifi_cnt = 0;
        }
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (stuckTest()) {
                        if (!wifi.isWifiEnabled()) {
                            if (wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
                                wifi.setWifiEnabled(true);
                            }
                        }

                        wifi.startScan();
                        scantime = new Date().getTime();
                    /*
					DataFiles.record(
							TAG + " WIFI GETS STUCK "
									+ String.format("%d\r\n", scantime),
							DataFiles.FileType.log);*/
                    }
                    break;
                case 2:
                    wifi.startScan();
                    scantime = new Date().getTime();
            }
            super.handleMessage(msg);
        }
    };

    public static boolean stuckTest() { // if true, wifi is stuck
        boolean r = false;
        if (rcvcnt != 0) {
            rcvcnt = 0;
            stuckcnt = 0;
        } else {
            stuckcnt++;
        }
        if (stuckcnt >= stuckTest_timeout / stuckTest_interval) {
            stuckcnt = 0;
            r = true;
        }
        return r;
    }

    private class stuckTestTask extends TimerTask {
        public void run() {
            try {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            } catch (Exception e) {
				/*
				DataFiles
						.record(TAG + " STUCKTESTTASK FAILED "
								+ String.format("%d\r\n", new Date().getTime()),
								DataFiles.FileType.log);*/
            }
        }
    }

    private class ScanTask extends TimerTask {
        public void run() {
            try {
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            } catch (Exception e) {
				/*
				DataFiles
						.record(TAG + " WIFISCANTASK FAILED "
								+ String.format("%d\r\n", new Date().getTime()),
								DataFiles.FileType.log);*/
            }
        }
    }

    public static WifiBriefInfo GetBestAp() {
        String mac = "None";
        int maxdB = -999;
        int freq = -1;
        if (results != null) {
            for (ScanResult rs : results) {
                if (rs.level > maxdB) {
                    mac = rs.BSSID;
                    maxdB = rs.level;
                    freq = rs.frequency;
                }
            }
        }
        WifiBriefInfo wifi;
        wifi = new WifiBriefInfo(0, mac, maxdB, freq);
        return wifi;
    }

    public static int GetApNum() {
        if (results != null)
            return results.size();
        else
            return 0;
    }

    //----------------------


    public static LinkedList<WifiBriefInfo> GetCurrFingerprint() {
        //if(!stuckTest()){
        Log.e("FPNUM::", "FPNUM " + GetFingerprintNum());
        return GetFingerprint(GetFingerprintNum() - 1);
        //} else {
        //	return null;
        //}
    }

    public static LinkedList<WifiBriefInfo> GetFingerprint(int index) {
        if ((index < FingerPrints.size()) && (index >= 0)) {
            Log.e("FP::", "Index:" + index);
            return FingerPrints.get(index);
        } else {
            Log.e("FP::", "NULL");
            return null;
        }
    }


    public static int GetFingerprintNum() {
        return FingerPrints.size();
    }

    private class mywifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            rcvcnt++;
            results = wifi.getScanResults();
            receivetime = new Date().getTime();

            currentWifiInfoList = new LinkedList<WifiBriefInfo>();

			/*
			DataFiles.record(TAG 
					+ String.format("%d\t", new Date().getTime()) 
					+ "MAC:" + rs.BSSID		+ "\t"
					+ "SSID:"+ rs.SSID  	+ "\t"
					+ "FR:"  + rs.frequency + "\t",
					DataFiles.FileType.log);
			
			DataFiles.record("\r\n",DataFiles.FileType.log);
			*/
            for (ScanResult rs : results) {
                currentWifiInfoList.add(new WifiBriefInfo(receivetime,
                        rs.BSSID, rs.level, rs.frequency));
            }
            Collections.sort(currentWifiInfoList, new WiFiMacComparable());

            if (currentWifiInfoList.size() != 0) {
                FingerPrints.add(new LinkedList<WifiBriefInfo>(currentWifiInfoList));

                rec_wifi_cnt++;
				/*
				 * rec_wifi += String.format("%d,%d,%2d,", scantime,
				 * receivetime, currentWifiInfoList.size()); for (WifiBriefInfo
				 * wifiInfo : currentWifiInfoList){ rec_wifi +=
				 * wifiInfo.bssid.replace(":", "") + String.format(",%2d,%4d,",
				 * -wifiInfo.rssi,wifiInfo.freq);
				 * 
				 * }
				 */

                rec_wifi += String.format("%d\t%d\t%2d", scantime, receivetime,
                        currentWifiInfoList.size());
                for (WifiBriefInfo wifiInfo : currentWifiInfoList) {
                    rec_wifi += "\t";
                    rec_wifi += wifiInfo.bssid.replace(":", "")
                            + String.format("\t%2d\t%4d", -wifiInfo.rssi,
                            wifiInfo.freq);
                }

                rec_wifi += "\r\n";
                if (rec_wifi_cnt >= wifi_buffer) {
                    //DataFiles.record(rec_wifi, DataFiles.FileType.wifi);
                    rec_wifi_cnt = 0;
                    rec_wifi = "";
                }
            } else {
				/*
				DataFiles
						.record(TAG + " NO RESULTS "
								+ String.format("%d\r\n", new Date().getTime()),
								DataFiles.FileType.wifi);*/
            }
            scantime = new Date().getTime();
            if (!wifi.isWifiEnabled()) {
                if (wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
                    wifi.setWifiEnabled(true);
                }
            }
            if (!wifi.startScan()) { // if failed, start later
                wifiTimer2.schedule(new ScanTask(), 700);
            }
        }
    }

    // Qiang0625: sort the WiFi info by Mac address
    public class WiFiMacComparable implements Comparator<WifiBriefInfo> {

        @Override
        public int compare(WifiBriefInfo arg0, WifiBriefInfo arg1) {
            int r = 0;
            if (arg0.freq > arg1.freq)
                r = 1;
            else if (arg0.freq < arg1.freq)
                r = -1;
            else {
                if (Long.parseLong(arg0.bssid.replace(":", ""), 16) > Long
                        .parseLong(arg1.bssid.replace(":", ""), 16))
                    r = 1;
                else if (Long.parseLong(arg0.bssid.replace(":", ""), 16) < Long
                        .parseLong(arg1.bssid.replace(":", ""), 16))
                    r = -1;
                else
                    r = 0;
            }
            return r;
        }
    }

    // Qiang0625: this comparator is not used
    public class WiFiLevelComparable implements Comparator<WifiBriefInfo> {

        @Override
        public int compare(WifiBriefInfo arg0, WifiBriefInfo arg1) {
            return (arg0.rssi > arg0.rssi ? 1 : (arg0.rssi == arg1.rssi ? 0
                    : -1));
        }

    }
}
