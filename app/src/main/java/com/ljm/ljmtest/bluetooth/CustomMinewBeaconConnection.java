package com.ljm.ljmtest.bluetooth;

import android.content.Context;
import android.util.Log;

import com.minew.beaconset.ConnectService;
import com.minew.beaconset.ConnectionState;
import com.minew.beaconset.MinewBeacon;
import com.minew.beaconset.MinewBeaconConnection;
import com.minew.beaconset.MinewBeaconConnectionListener;
import com.minew.beaconset.MinewBeaconManager;
import com.minew.beaconset.MinewBeaconSetting;
import com.minew.beaconset.a.a;
import com.minew.beaconset.a.b;
import com.minew.beaconset.a.c;
import com.minew.device.baseblelibaray.BaseBleManager;

import java.util.HashMap;

public class CustomMinewBeaconConnection {
    public static HashMap<String, CustomMinewBeaconConnection> minewBeaconConnections = new HashMap();
    private Context mContext;
    public ConnectionState state;
    public CustomMinewBeaconSetting setting = new CustomMinewBeaconSetting();
    public MinewBeacon mBeacon;
    private CustomMinewBeaconConnectionListener mMinewBeaconConnectionListener;
    private CustomConnectService mConnectService;

    private CustomMinewBeaconConnection() {
    }

    public CustomMinewBeaconConnection(Context var1, MinewBeacon var2) {
        this.mContext = var1;
        this.mBeacon = var2;
    }

    public void setMinewBeaconConnectionListener(CustomMinewBeaconConnectionListener var1) {
        this.mMinewBeaconConnectionListener = var1;
    }

    public CustomMinewBeaconConnectionListener getMinewBeaconConnectionListener() {
        return this.mMinewBeaconConnectionListener;
    }

    public void connect() {
        this.mConnectService = CustomMinewBeaconManager.getInstance(this.mContext).getConnectService();
        this.mConnectService.connect(this.mBeacon);
        minewBeaconConnections.put(this.mBeacon.getMacAddress(), this);
        this.state = ConnectionState.BeaconStatus_Connecting;
    }

    public void disconnect() {
        if (this.mConnectService != null) {
            this.mConnectService.disConnect(this.mBeacon);
            minewBeaconConnections.remove(this.mBeacon.getMacAddress());
        }

    }

    public void writeSetting(String var1) {
        String var2;
        if (this.setting.uuidChange) {
            var2 = a.b(this.setting.getUuid());
            BaseBleManager.a().a(this.setting.getMacAddress(), b.n, b.o, c.a(var2));
            Log.e("tag", "uuidchange");
        }

        if (this.setting.majorChange) {
            var2 = a.d(this.setting.getMajor() + "");
            var2 = a.b(var2);
            BaseBleManager.a().a(this.setting.getMacAddress(), b.n, b.p, c.a(var2));
            Log.e("tag", "majorchange");
        }

        if (this.setting.minorChange) {
            var2 = a.d(this.setting.getMinor() + "");
            var2 = a.b(var2);
            BaseBleManager.a().a(this.setting.getMacAddress(), b.n, b.q, c.a(var2));
            Log.e("tag", "minorchange");
        }

        if (this.setting.calibratChange) {
            int var4 = this.setting.getCalibratedTxPower();
            BaseBleManager.a().a(this.setting.getMacAddress(), b.n, b.r, new byte[]{(byte)var4});
            Log.e("tag", "calibratchange");
        }

        String var3;
        if (this.setting.txpowerChange) {
            var2 = a.a(this.setting.getTxPower() + "", 2);
            var3 = a.b(var2);
            BaseBleManager.a().a(this.setting.getMacAddress(), b.n, b.s, c.a(var3));
            Log.e("tag", "broadcastpowerchange");
        }

        if (this.setting.broadcasetintervalChange) {
            var2 = a.a(this.setting.getBroadcastInterval() + "", 2);
            var3 = a.b(var2);
            BaseBleManager.a().a(this.setting.getMacAddress(), b.n, b.u, c.a(var3));
            Log.e("tag", "broadcaseintervalchange");
        }

        if (this.setting.deviceIdChange) {
            var2 = a.c(this.setting.getDeviceId());
            var2 = a.b(var2);
            BaseBleManager.a().a(this.setting.getMacAddress(), b.n, b.v, c.a(var2));
            Log.e("tag", "deviceidchange");
        }

        if (this.setting.nameChange) {
            var2 = a.a(this.setting.getName());
            BaseBleManager.a().a(this.setting.getMacAddress(), b.n, b.w, c.a(var2));
            Log.e("tag", "nameChange");
        }

        if (this.setting.modeChange) {
            var2 = a.a(this.setting.getMode() + "", 2);
            var3 = a.b(var2);
            BaseBleManager.a().a(this.setting.getMacAddress(), b.n, b.x, c.a(var3));
            Log.e("tag", "modeChange");
        }

        if (this.setting.passwordChange) {
            var2 = a.a(this.setting.getPassword());
            BaseBleManager.a().a(this.setting.getMacAddress(), b.n, b.t, c.a(var2));
            Log.e("tag", "passwordChange");
        }

        Log.e("tag", "restart");
        var2 = a.a(var1);
        BaseBleManager.a().a(this.setting.getMacAddress(), b.n, b.y, c.a(var2));
    }
}
