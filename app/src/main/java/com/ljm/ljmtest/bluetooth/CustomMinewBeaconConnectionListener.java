package com.ljm.ljmtest.bluetooth;

import com.minew.beaconset.ConnectionState;

public interface CustomMinewBeaconConnectionListener {
    void onChangeState(CustomMinewBeaconConnection var1, ConnectionState var2);

    void onWriteSettings(CustomMinewBeaconConnection var1, boolean var2);
}
