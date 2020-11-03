package com.ljm.ljmtest.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.minew.beaconset.ConnectService;
import com.minew.beaconset.ConnectionState;
import com.minew.beaconset.MinewBeacon;
import com.minew.beaconset.MinewBeaconConnection;
import com.minew.beaconset.MinewBeaconConnectionListener;
import com.minew.beaconset.a.c;
import com.minew.device.baseblelibaray.BaseBleManager;
import com.minew.device.baseblelibaray.a.a;
import com.minew.device.baseblelibaray.a.b;
import com.minew.device.baseblelibaray.a.f;
import com.minew.device.baseblelibaray.a.g;
import com.minew.device.baseblelibaray.a.i;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CustomConnectService extends Service{
    private BaseBleManager mBaseBleManager;
    private MinewBeacon mBeacon;
    private final String md5 = "AcCrEdItiSOK";
    public Handler mHandler = new Handler();
    public int CONNECT_STATE = 0;
    public int CONNECT_SUC = 1;
    public int CONNECT_FAIL = -1;
    public int CONNECTTING = 2;
    private int connectCount;
    private static final int CONNECT_COUNT = 3;
    private boolean mIsconnected;
    private boolean isWriting;
    Runnable connectRunnable = new Runnable() {
        public void run() {
            CustomConnectService.this.connectCount = CustomConnectService.this.connectCount + 1;
            Log.e("tag", "try again");
            CustomConnectService.this.mBaseBleManager.a(CustomConnectService.this, CustomConnectService.this.mBeacon.getMacAddress());
            if (CustomConnectService.this.connectCount < 3) {
                CustomConnectService.this.mHandler.postDelayed(CustomConnectService.this.connectRunnable, 5000L);
            }

        }
    };

    public CustomConnectService() {
    }

    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        this.initManager();
    }

    private void initManager() {
        this.mBaseBleManager = BaseBleManager.a();
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    public IBinder onBind(Intent var1) {
        return new CustomConnectService.ConnectBinder();
    }

    public boolean onUnbind(Intent var1) {
        return super.onUnbind(var1);
    }

    public void connect(final MinewBeacon var1) {
        this.mBeacon = var1;
        this.mIsconnected = false;
        this.mHandler.post(new Runnable() {
            public void run() {
                CustomConnectService.this.mBaseBleManager.a(CustomConnectService.this, var1.getMacAddress());
            }
        });
        this.mHandler.postDelayed(this.connectRunnable, 5000L);
        this.CONNECT_STATE = this.CONNECTTING;
    }

    public void disConnect(MinewBeacon var1) {
        this.mBaseBleManager.a(var1.getMacAddress());
        this.mHandler.removeCallbacks(this.connectRunnable);
    }

    @Subscribe
    public void onConnectionStateChangedEvent(f var1) {
        BluetoothGatt var2 = var1.a();
        int var3 = var1.b();
        int var4 = var1.c();
        CustomMinewBeaconConnection var5;
        CustomMinewBeaconConnectionListener var6;
        if (var3 == 0) {
            if (var4 == 2) {
                this.CONNECT_STATE = this.CONNECT_SUC;
                var2.discoverServices();
                this.mHandler.removeCallbacks(this.connectRunnable);
            } else if (var4 == 0) {
                if (this.connectCount < 3 && !this.mIsconnected) {
                    if (this.connectCount < 3 && this.CONNECT_STATE == this.CONNECT_SUC) {
                        this.mHandler.post(this.connectRunnable);
                    }
                } else {
                    var5 = (CustomMinewBeaconConnection)CustomMinewBeaconConnection.minewBeaconConnections.get(var2.getDevice().getAddress());
                    var6 = var5.getMinewBeaconConnectionListener();
                    var5.state = ConnectionState.BeaconStatus_Disconnect;
                    if (var6 != null && !this.isWriting) {
                        var6.onChangeState(var5, ConnectionState.BeaconStatus_Disconnect);
                    }

                    MinewBeaconConnection.minewBeaconConnections.remove(var2.getDevice().getAddress());
                    this.mHandler.removeCallbacks(this.connectRunnable);
                    this.connectCount = 0;
                }

                this.CONNECT_STATE = this.CONNECT_FAIL;
            }
        } else {
            var2.disconnect();
            var2.close();
            if (this.connectCount < 3 && !this.mIsconnected) {
                if (this.connectCount < 3 && this.CONNECT_STATE == this.CONNECT_SUC) {
                    this.mHandler.post(this.connectRunnable);
                }
            } else {
                var5 = (CustomMinewBeaconConnection)CustomMinewBeaconConnection.minewBeaconConnections.get(var2.getDevice().getAddress());
                var6 = var5.getMinewBeaconConnectionListener();
                var5.state = ConnectionState.BeaconStatus_ConnectFailed;
                if (var6 != null && !this.isWriting) {
                    var6.onChangeState(var5, ConnectionState.BeaconStatus_ConnectFailed);
                }

                CustomMinewBeaconConnection.minewBeaconConnections.remove(var2.getDevice().getAddress());
                this.mHandler.removeCallbacks(this.connectRunnable);
                this.connectCount = 0;
            }

            this.CONNECT_STATE = this.CONNECT_FAIL;
        }

    }

    @Subscribe
    public void onServiceDiscoveredEvent(i var1) {
        BluetoothGatt var2 = var1.a();
        int var3 = var1.b();
        if (var3 == 0) {
            this.write_AcCrEdItiSOK(var2);
        }

    }

    @Subscribe
    public void onCharacteristicReadEvent(b var1) {
        BluetoothGatt var2 = var1.a();
        BluetoothGattCharacteristic var3 = var1.b();
        int var4 = var1.c();
        if (var4 == 0) {
            CustomMinewBeaconConnection var5 = (CustomMinewBeaconConnection)CustomMinewBeaconConnection.minewBeaconConnections.get(var2.getDevice().getAddress());
            CustomMinewBeaconConnectionListener var6 = var5.getMinewBeaconConnectionListener();
            if (var3.getUuid().equals(com.minew.beaconset.a.b.b)) {
                byte var7 = var3.getValue()[0];
                Log.v("tag", "ProximityManager onCharacteristicRead  batteryValue=" + var7);
                var5.setting.setBattery(var7);
                var5.setting.setMacAddress(var2.getDevice().getAddress());
            }

            String var12;
            if (var3.getUuid().equals(com.minew.beaconset.a.b.f)) {
                var12 = var3.getStringValue(0);
                var5.setting.manufacture = var12;
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.g)) {
                var12 = var3.getStringValue(0);
                var5.setting.model = var12;
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.h)) {
                var12 = var3.getStringValue(0);
                var5.setting.SN = var12;
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.i)) {
                var12 = var3.getStringValue(0);
                var5.setting.hardware = var12;
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.j)) {
                var12 = var3.getStringValue(0);
                var5.setting.firmware = var12;
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.k)) {
                var12 = var3.getStringValue(0);
                var5.setting.software = var12;
            }

            String var8;
            byte[] var13;
            if (var3.getUuid().equals(com.minew.beaconset.a.b.l)) {
                var13 = var3.getValue();
                var8 = c.b(var13);
                var5.setting.systemId = var8.toString().trim();
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.m)) {
                var13 = var3.getValue();
                var8 = c.b(var13);
                var5.setting.certData = var8;
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.o)) {
                var13 = var3.getValue();
                var8 = c.b(var13);
                var5.setting.setUuid(var8);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.p)) {
                var13 = var3.getValue();
                var8 = c.b(var13);
                var5.setting.setMajor(Integer.parseInt(var8, 16));
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.q)) {
                var13 = var3.getValue();
                var8 = c.b(var13);
                var5.setting.setMinor(Integer.parseInt(var8, 16));
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.r)) {
                var13 = var3.getValue();
                int[] var14 = new int[var3.getValue().length];
                StringBuffer var9 = new StringBuffer();
                StringBuffer var10 = new StringBuffer();

                for(int var11 = 0; var11 < var13.length; ++var11) {
                    if (var13[var11] < 0) {
                        var14[var11] = Integer.valueOf(Integer.toBinaryString(var13[var11]).substring(24), 2);
                        var10.append(var14[var11]);
                    } else {
                        var14[var11] = var13[var11];
                        var10.append(var14[var11]);
                    }

                    if (var13.length == 1) {
                        var9.append(var13[var11]);
                    } else if (var11 == 0) {
                        var9.append(var13[var11]);
                    } else if (var11 == var13.length - 1) {
                        var9.append(var13[var11]);
                    } else {
                        var9.append(var13[var11]);
                    }
                }

                var5.setting.setCalibratedTxPower(Integer.parseInt(var9.toString()));
            }

            int var15;
            if (var3.getUuid().equals(com.minew.beaconset.a.b.s)) {
                var15 = var3.getIntValue(17, 0);
                var5.setting.setTxPower(var15);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.u)) {
                var15 = var3.getIntValue(17, 0);
                var5.setting.setBroadcastInterval(var15);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.v)) {
                var13 = var3.getValue();
                var8 = c.b(var13);
                var5.setting.setDeviceId(Integer.parseInt(var8, 16) + "");
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.w)) {
                var12 = var3.getStringValue(0);
                var5.setting.setName(var12);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.x)) {
                this.mIsconnected = true;
                this.connectCount = 0;
                var15 = var3.getIntValue(17, 0);
                var5.setting.setMode(var15);
                Log.e("tag", var5.setting.toString());
                var5.setting.setConnected(true);
                if (var6 != null) {
                    var5.state = ConnectionState.BeaconStatus_Connected;
                    var6.onChangeState(var5, ConnectionState.BeaconStatus_Connected);
                }
            }
        }

    }

    @Subscribe
    public void onCharacteristicWriteEvent(com.minew.device.baseblelibaray.a.c var1) {
        Log.v("tag", "CharacteristicWrite");
        final BluetoothGatt var2 = var1.a();
        BluetoothGattCharacteristic var3 = var1.b();
        int var4 = var1.c();
        final CustomMinewBeaconConnection var5 = (CustomMinewBeaconConnection)CustomMinewBeaconConnection.minewBeaconConnections.get(var2.getDevice().getAddress());
        final CustomMinewBeaconConnectionListener var6 = var5.getMinewBeaconConnectionListener();
        if (var4 == 0) {
            if (var3.getUuid().equals(com.minew.beaconset.a.b.y) && var6 != null) {
                this.isWriting = true;
                this.mHandler.postDelayed(new Runnable() {
                    public void run() {
                        CustomConnectService.this.isWriting = false;
                        ConnectionState var1 = var5.state;
                        switch(var1) {
                            case BeaconStatus_Connected:
                                var6.onWriteSettings(var5, false);
                                break;
                            case BeaconStatus_ConnectFailed:
                            case BeaconStatus_Disconnect:
                                var6.onWriteSettings(var5, true);
                        }

                    }
                }, 6000L);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.d)) {
                this.mHandler.postDelayed(new Runnable() {
                    public void run() {
                        CustomConnectService.this.readAllDevcieData(var2);
                    }
                }, 500L);
            }
        } else {
            if (var3.getUuid().equals(com.minew.beaconset.a.b.y) && var6 != null) {
                var6.onWriteSettings(var5, false);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.o) && var6 != null) {
                var6.onWriteSettings(var5, false);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.p) && var6 != null) {
                var6.onWriteSettings(var5, false);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.q) && var6 != null) {
                var6.onWriteSettings(var5, false);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.r) && var6 != null) {
                var6.onWriteSettings(var5, false);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.u) && var6 != null) {
                var6.onWriteSettings(var5, false);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.s) && var6 != null) {
                var6.onWriteSettings(var5, false);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.t) && var6 != null) {
                var6.onWriteSettings(var5, false);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.v) && var6 != null) {
                var6.onWriteSettings(var5, false);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.w) && var6 != null) {
                var6.onWriteSettings(var5, false);
            }

            if (var3.getUuid().equals(com.minew.beaconset.a.b.x) && var6 != null) {
                var6.onWriteSettings(var5, false);
            }
        }

    }

    @Subscribe
    public void onCharacteristicChangedEvent(a var1) {
        Log.v("tag", "CharacteristicChanged");
        BluetoothGatt var2 = var1.a();
        BluetoothGattCharacteristic var3 = var1.b();
        String var4 = var2.getDevice().getAddress();
    }

    @Subscribe
    public void onReadRemoteRssiEvent(g var1) {
        Log.v("tag", "ReadRemoteRssi");
        int var2 = var1.a();
        if (var2 == 0) {
        }

    }

    private void write_AcCrEdItiSOK(BluetoothGatt var1) {
        BaseBleManager.a().a(var1.getDevice().getAddress(), com.minew.beaconset.a.b.c, com.minew.beaconset.a.b.d, "AcCrEdItiSOK".getBytes());
    }

    private void readAllDevcieData(BluetoothGatt var1) {
        BaseBleManager.a().a(var1.getDevice().getAddress(), com.minew.beaconset.a.b.a, com.minew.beaconset.a.b.b);
        BluetoothGattService var2 = var1.getService(com.minew.beaconset.a.b.e);

        for(int var3 = 0; var3 < var2.getCharacteristics().size(); ++var3) {
            BluetoothGattCharacteristic var4 = (BluetoothGattCharacteristic)var2.getCharacteristics().get(var3);
            BaseBleManager.a().a(var1.getDevice().getAddress(), com.minew.beaconset.a.b.e, var4.getUuid());
        }

        BluetoothGattService var6 = var1.getService(com.minew.beaconset.a.b.n);

        for(int var7 = 0; var7 < var6.getCharacteristics().size(); ++var7) {
            if (var7 != 5 && var7 != 9 && var7 != 11) {
                BluetoothGattCharacteristic var5 = (BluetoothGattCharacteristic)var6.getCharacteristics().get(var7);
                BaseBleManager.a().a(var1.getDevice().getAddress(), com.minew.beaconset.a.b.n, var5.getUuid());
            }
        }

    }

    public class ConnectBinder extends Binder {
        public ConnectBinder() {
        }

        public CustomConnectService a() {
            return CustomConnectService.this;
        }
    }
}
