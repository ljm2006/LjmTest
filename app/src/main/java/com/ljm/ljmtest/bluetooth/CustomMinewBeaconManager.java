package com.ljm.ljmtest.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ljm.ljmtest.common.LjmUtil;
import com.minew.beaconset.BluetoothState;
import com.minew.beaconset.ConnectService;
import com.minew.beaconset.MinewBeacon;
import com.minew.beaconset.MinewBeaconManagerListener;
import com.minew.beaconset.a.c;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomMinewBeaconManager {
    public static List<MinewBeacon> scannedBeacons = new ArrayList();
    public static List<MinewBeacon> inRangeBeacons = new ArrayList();
    private ArrayList<MinewBeacon> mNewMinewBeaconList = new ArrayList();
    private static CustomMinewBeaconManager single;
    private static Context mContext;
    private MinewBeaconManagerListener mMinewBeaconManagerListener;
    private CustomConnectService mService;
    ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName var1, IBinder var2) {
            Log.d("ljm2006", "service connected!");
            CustomConnectService.ConnectBinder var3 = (CustomConnectService.ConnectBinder)var2;
            CustomMinewBeaconManager.this.mService = var3.a();
        }

        public void onServiceDisconnected(ComponentName var1) {
        }
    };
    public ScanCallback mScanCallback;
    Handler appearHandler = new Handler();
    Runnable appearRunnable = new Runnable() {
        public void run() {
            if (CustomMinewBeaconManager.this.mMinewBeaconManagerListener != null && CustomMinewBeaconManager.this.mNewMinewBeaconList.size() > 0) {
                CustomMinewBeaconManager.this.mMinewBeaconManagerListener.onAppearBeacons(CustomMinewBeaconManager.this.mNewMinewBeaconList);
            }

            CustomMinewBeaconManager.this.mNewMinewBeaconList.clear();
            CustomMinewBeaconManager.this.appearHandler.postDelayed(CustomMinewBeaconManager.this.appearRunnable, 3000L);
        }
    };
    Handler disappearHandler = new Handler();
    Runnable disappearRunnable = new Runnable() {
        public void run() {
            if (CustomMinewBeaconManager.this.mMinewBeaconManagerListener != null) {
                long var1 = System.currentTimeMillis();
                ArrayList var3 = new ArrayList();
                Iterator var4 = CustomMinewBeaconManager.scannedBeacons.iterator();

                while(var4.hasNext()) {
                    MinewBeacon var5 = (MinewBeacon)var4.next();
                    long var6 = var5.getAddTime();
                    if (var5.isInRange() && var1 - var6 > 10000L) {
                        var5.setInRange(false);
                        var3.add(var5);
                    }
                }

                if (var3.size() > 0) {
                    CustomMinewBeaconManager.this.mMinewBeaconManagerListener.onDisappearBeacons(var3);
                }
            }

            CustomMinewBeaconManager.this.disappearHandler.postDelayed(CustomMinewBeaconManager.this.disappearRunnable, 1000L);
        }
    };
    Handler rangeHandler = new Handler();
    Runnable rangeRunnable = new Runnable() {
        public void run() {
            if (CustomMinewBeaconManager.this.mMinewBeaconManagerListener != null) {
                ArrayList var1 = new ArrayList();
                Iterator var2 = CustomMinewBeaconManager.scannedBeacons.iterator();

                while(var2.hasNext()) {
                    MinewBeacon var3 = (MinewBeacon)var2.next();
                    if (System.currentTimeMillis() - var3.getAddTime() < 10000L) {
                        var1.add(var3);
                    }
                }

                CustomMinewBeaconManager.this.mMinewBeaconManagerListener.onRangeBeacons(var1);
            }

            CustomMinewBeaconManager.this.rangeHandler.postDelayed(CustomMinewBeaconManager.this.rangeRunnable, 1000L);
        }
    };
    public BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(BluetoothDevice var1, int var2, byte[] var3) {
            try {
                JSONObject var4 = c.a(var3);
                if (var4.getString("serviceData").startsWith("F0FF") || var4.getString("serviceData").startsWith("F1FF") || var4.getString("serviceData").startsWith("A5FD") || var4.getString("serviceData").startsWith("81AB")) {
                    CustomMinewBeaconManager.this.addDevice(var1, var2, var3);
                }
            } catch (Exception var5) {
                var5.printStackTrace();
            }

        }
    };

    public CustomMinewBeaconManager() {
    }

    public static CustomMinewBeaconManager getInstance(Context var0) {
        if (single == null) {
            Class var1 = CustomMinewBeaconManager.class;
            synchronized(CustomMinewBeaconManager.class) {
                if (single == null) {
                    single = new CustomMinewBeaconManager();
                    mContext = var0;
                }
            }
        }

        return single;
    }

    public void setMinewbeaconManagerListener(MinewBeaconManagerListener var1) {
        this.mMinewBeaconManagerListener = var1;
    }

    public MinewBeaconManagerListener getMinewBeaconManagerListener() {
        return this.mMinewBeaconManagerListener;
    }

    public BluetoothState checkBluetoothState() {
        if (!mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            return BluetoothState.BluetoothStateNotSupported;
        } else if (mContext != null) {
            BluetoothManager var1 = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter var2 = var1.getAdapter();
            return var2.isEnabled() ? BluetoothState.BluetoothStatePowerOn : BluetoothState.BluetoothStatePowerOff;
        } else {
            return BluetoothState.BluetoothStatePowerOff;
        }
    }

    public void startService() {
        Intent var1 = new Intent(mContext, CustomConnectService.class);
        mContext.bindService(var1, this.mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopService() {
        if (this.mService != null) {
            mContext.unbindService(this.mServiceConnection);
        }

    }

    public CustomConnectService getConnectService() {
        return this.mService;
    }

    public void startScan() {
        this.stopScan();
        BluetoothManager var1;
        BluetoothAdapter var2;
        if (Build.VERSION.SDK_INT < 21) {
            var1 = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            var2 = var1.getAdapter();
            if (var2 != null) {
                var2.startLeScan(this.mLeScanCallback);
            }
        } else {
            var1 = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            var2 = var1.getAdapter();
            BluetoothLeScanner var3 = var2.getBluetoothLeScanner();
            ArrayList var4 = new ArrayList();
            ScanSettings var5 = (new ScanSettings.Builder()).setScanMode(2).setReportDelay(0L).build();
            this.mScanCallback = new ScanCallback() {
                public void onScanResult(int var1, ScanResult var2) {
                    try {
                        JSONObject var3 = c.a(var2.getScanRecord().getBytes());
                        if (var3.getString("serviceData").startsWith("F0FF") || var3.getString("serviceData").startsWith("F1FF") || var3.getString("serviceData").startsWith("A5FD") || var3.getString("serviceData").startsWith("81AB")) {
                            CustomMinewBeaconManager.this.addDevice(var2.getDevice(), var2.getRssi(), var2.getScanRecord().getBytes());
                        }
                    } catch (Exception var4) {
                        var4.printStackTrace();
                    }

                }

                public void onBatchScanResults(List<ScanResult> var1) {
                    super.onBatchScanResults(var1);
                }

                public void onScanFailed(int var1) {
                    super.onScanFailed(var1);
                }
            };
            if (var3 != null) {
                var3.startScan(var4, var5, this.mScanCallback);
            }
        }

        this.appearHandler.post(this.appearRunnable);
        this.disappearHandler.post(this.disappearRunnable);
        this.rangeHandler.post(this.rangeRunnable);
    }

    public void stopScan() {
        BluetoothManager var1;
        BluetoothAdapter var2;
        if (Build.VERSION.SDK_INT < 21) {
            var1 = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            var2 = var1.getAdapter();
            if (var2 != null && this.mLeScanCallback != null) {
                var2.stopLeScan(this.mLeScanCallback);
            }
        } else {
            var1 = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            var2 = var1.getAdapter();
            BluetoothLeScanner var3 = var2.getBluetoothLeScanner();
            if (var3 != null && this.mScanCallback != null) {
                var3.stopScan(this.mScanCallback);
            }
        }

        this.appearHandler.removeCallbacks(this.appearRunnable);
        this.disappearHandler.removeCallbacks(this.disappearRunnable);
        this.rangeHandler.removeCallbacks(this.rangeRunnable);
    }

    private void addDevice(BluetoothDevice var1, int var2, byte[] var3) {
        boolean var4 = false;
        Iterator var5 = scannedBeacons.iterator();

        MinewBeacon var6;
        String var11;
        String var12;
        int var13;
        while(var5.hasNext()) {
            var6 = (MinewBeacon)var5.next();
            if (var1.getAddress().equals(var6.getMacAddress())) {
                var6.setMacAddress(var1.getAddress());
                var6.setRssi(var2);
                var6.setInRange(true);
                long var7 = System.currentTimeMillis();
                var6.setAddTime(var7);
                String var9 = var1.getName();
                if (var9 == null || "".equals(var9)) {
                    var9 = "N/A";
                }

                var6.setName(var9);
                JSONObject var10 = c.a(var3);
                var11 = var10.optString("manufacturerData");
                if (var11 != null && !"".equals(var11) && var11.length() > 48) {
                    var6.setUuid(this.formatUUID(var11.substring(8, 40)));
                    var6.setMajor(Integer.parseInt(var11.substring(40, 44), 16) + "");
                    var6.setMinor(Integer.parseInt(var11.substring(44, 48), 16) + "");
                }

                var6.setConnectable(true);
                var12 = var10.optString("txPowerLevel");
                if (var12 != null && !"".equals(var12)) {
                    var13 = Integer.parseInt(var12, 16);
                    var6.setTxpower(var13 + "");
                }

                String var17 = var10.optString("isConnected");
                if (var17 != null && !var17.equals("") && Integer.parseInt(var17, 16) == 0) {
                    var6.setConnectable(false);
                }

                var4 = true;
                break;
            }
        }

        if (!var4) {
            JSONObject var14 = c.a(var3);
            var6 = new MinewBeacon();
            var6.setMacAddress(var1.getAddress());
            var6.setRssi(var2);
            String var15 = var14.optString("serviceData");
            if (var15 != null && !"".equals(var15)) {
                var6.setBattery(this.getBattery(var15));
            }

            String var8 = var1.getName();
            if (var8 == null || "".equals(var8)) {
                var8 = "N/A";
            }

            var6.setName(var8);
            var6.setInRange(true);
            long var16 = System.currentTimeMillis();
            var6.setAddTime(var16);
            var11 = var14.optString("manufacturerData");
            if (var11 != null && !"".equals(var11) && var11.length() > 48) {
                var6.setUuid(this.formatUUID(var11.substring(8, 40)));
                var6.setMajor(Integer.parseInt(var11.substring(40, 44), 16) + "");
                var6.setMinor(Integer.parseInt(var11.substring(44, 48), 16) + "");
            }

            var12 = var14.optString("txPowerLevel");
            if (var12 != null && !"".equals(var12)) {
                var13 = Integer.parseInt(var12, 16);
                var6.setTxpower(var13 + "");
            }

            var6.setConnectable(true);
            scannedBeacons.add(var6);
            this.mNewMinewBeaconList.add(var6);
        }

    }

    private String getMajor(String var1) {
        if (var1.length() >= 6) {
            String var2 = var1.substring(6, 10);
            int var3 = Integer.parseInt(var2, 16);
            return var3 + "";
        } else {
            return "0";
        }
    }

    private String getMinor(String var1) {
        if (var1.length() >= 6) {
            String var2 = var1.substring(10, 14);
            int var3 = Integer.parseInt(var2, 16);
            return var3 + "";
        } else {
            return "0";
        }
    }

    private String formatUUID(String var1) {
        return var1.length() < 32 ? var1 : var1.substring(0, 8) + '-' + var1.substring(8, 12) + '-' + var1.substring(12, 16) + '-' + var1.substring(16, 20) + '-' + var1.substring(20, 32);
    }

    private int getBattery(String var1) {
        return var1.length() >= 6 ? Integer.parseInt(var1.substring(4, 6), 16) : 0;
    }
}
