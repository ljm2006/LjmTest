package com.ljm.ljmtest.bluetooth;

import com.google.gson.Gson;

public class CustomMinewBeaconSetting {
    private String uuid;
    private int major;
    private int minor;
    private String name;
    private int mode;
    private int broadcastInterval;
    private String deviceId;
    private String password;
    private int calibratedTxPower;
    private int txPower;
    private String macAddress;
    private int battery;
    private boolean connected;
    protected String manufacture;
    protected String SN;
    protected String model;
    protected String hardware;
    protected String software;
    protected String firmware;
    protected String systemId;
    protected String certData;
    public boolean uuidChange;
    public boolean majorChange;
    public boolean minorChange;
    public boolean calibratChange;
    public boolean txpowerChange;
    public boolean broadcasetintervalChange;
    public boolean deviceIdChange;
    public boolean nameChange;
    public boolean modeChange;
    public boolean passwordChange;
    private int uuidCount;
    private int majorCount;
    private int minorCount;
    private int calibratCount;
    private int txpowerCount;
    private int broadcasetintervalCount;
    private int deviceIdCount;
    private int nameCount;
    private int modeCount;
    private int passwordCount;

    public CustomMinewBeaconSetting() {
    }

    public void importJSON(String var1) {
        Gson var2 = new Gson();
        CustomMinewBeaconSetting var3 = (CustomMinewBeaconSetting)var2.fromJson(var1, this.getClass());
        this.uuid = var3.getUuid();
        this.major = var3.getMajor();
        this.minor = var3.getMinor();
        this.name = var3.getName();
        this.deviceId = var3.getDeviceId();
        this.battery = var3.getBattery();
        this.macAddress = var3.getMacAddress();
    }

    public String exportJSON() {
        Gson var1 = new Gson();
        String var2 = var1.toJson(this);
        return var2;
    }

    public String getUuid() {
        return this.uuid;
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public String getName() {
        return this.name;
    }

    public int getMode() {
        return this.mode;
    }

    public int getBroadcastInterval() {
        return this.broadcastInterval;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public String getPassword() {
        return this.password;
    }

    public int getCalibratedTxPower() {
        return this.calibratedTxPower;
    }

    public int getTxPower() {
        return this.txPower;
    }

    public String getMacAddress() {
        return this.macAddress;
    }

    public int getBattery() {
        return this.battery;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public String getManufacture() {
        return this.manufacture;
    }

    public String getSN() {
        return this.SN;
    }

    public String getModel() {
        return this.model;
    }

    public String getHardware() {
        return this.hardware;
    }

    public String getSoftware() {
        return this.software;
    }

    public String getFirmware() {
        return this.firmware;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public String getCertData() {
        return this.certData;
    }

    public void setBattery(int var1) {
        this.battery = var1;
    }

    public void setMacAddress(String var1) {
        this.macAddress = var1;
    }

    public void setUuid(String var1) {
        this.uuid = var1;
        ++this.uuidCount;
        if (this.uuidCount > 1) {
            this.uuidChange = true;
        }

    }

    public void setMajor(int var1) {
        this.major = var1;
        ++this.majorCount;
        if (this.majorCount > 1) {
            this.majorChange = true;
        }

    }

    public void setMinor(int var1) {
        this.minor = var1;
        ++this.minorCount;
        if (this.minorCount > 1) {
            this.minorChange = true;
        }

    }

    public void setName(String var1) {
        this.name = var1;
        ++this.nameCount;
        if (this.nameCount > 1) {
            this.nameChange = true;
        }

    }

    public void setMode(int var1) {
        this.mode = var1;
        ++this.modeCount;
        if (this.modeCount > 1) {
            this.modeChange = true;
        }

    }

    public void setBroadcastInterval(int var1) {
        this.broadcastInterval = var1;
        ++this.broadcasetintervalCount;
        if (this.broadcasetintervalCount > 1) {
            this.broadcasetintervalChange = true;
        }

    }

    public void setDeviceId(String var1) {
        this.deviceId = var1;
        ++this.deviceIdCount;
        if (this.deviceIdCount > 1) {
            this.deviceIdChange = true;
        }

    }

    public void setPassword(String var1) {
        this.password = var1;
        this.passwordChange = true;
    }

    public void setCalibratedTxPower(int var1) {
        this.calibratedTxPower = var1;
        ++this.calibratCount;
        if (this.calibratCount > 1) {
            this.calibratChange = true;
        }

    }

    public void setTxPower(int var1) {
        this.txPower = var1;
        ++this.txpowerCount;
        if (this.txpowerCount > 1) {
            this.txpowerChange = true;
        }

    }

    public void setConnected(boolean var1) {
        this.connected = var1;
    }
}
