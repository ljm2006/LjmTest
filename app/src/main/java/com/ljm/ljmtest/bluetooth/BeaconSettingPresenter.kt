package com.ljm.ljmtest.bluetooth

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.ljm.ljmtest.Presenter
import com.ljm.ljmtest.R
import com.ljm.ljmtest.common.LjmUtil
import com.minew.beaconset.*

class BeaconSettingPresenter constructor(var c: Context, var a: BeaconSettingAction) : Presenter {

    var beaconList = mutableListOf<MinewBeacon>()
    private lateinit var beaconManager:CustomMinewBeaconManager

    override fun onCreate(intent: Intent?) {
        if(ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            a.requestPermission()
        }
    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onDestroy() {

    }

    fun onClick(id: Int) {
        when(id){
            R.id.btn_start_scan -> {
                startScan()
            }
        }
    }

    fun startScan(){
        beaconList.clear()
        beaconManager = CustomMinewBeaconManager.getInstance(c)
        beaconManager.setMinewbeaconManagerListener(object : MinewBeaconManagerListener {
            override fun onUpdateBluetoothState(p0: BluetoothState?) {

            }

            override fun onRangeBeacons(p0: MutableList<MinewBeacon>?) {
                if (!p0.isNullOrEmpty()) {

                    for (beacon: MinewBeacon in p0) {

                        LjmUtil.D("beacon : ${beacon.name}, major : ${beacon.major}, minor : ${beacon.minor}")
                        val filtered = beaconList.filter {
                            it.name == beacon.name
                        }

                        if (filtered.isEmpty()) {

                            beaconList.add(beacon)
                        }

                    }
                }
            }

            override fun onAppearBeacons(p0: MutableList<MinewBeacon>?) {

            }

            override fun onDisappearBeacons(p0: MutableList<MinewBeacon>?) {

            }

        })

        beaconManager.startService()
        beaconManager.startScan()
        Handler(Looper.getMainLooper()).postDelayed({
            beaconManager.stopScan()
            a.listBeacon(beaconList)
        }, 5000)
    }

    fun connectBeacon(beacon: MinewBeacon){
        LjmUtil.D("connectBeacon()")
        val beaconConnection = CustomMinewBeaconConnection(c, beacon)
        beaconConnection.minewBeaconConnectionListener = object : CustomMinewBeaconConnectionListener{

            override fun onChangeState(conn: CustomMinewBeaconConnection?, state: ConnectionState?) {
                when(state!!){
                    ConnectionState.BeaconStatus_Connected -> {
                        LjmUtil.D("connected beacon major : ${beaconConnection.setting.major}")
                        var beaconSetting:CustomMinewBeaconSetting = conn!!.setting
                        val randomMajor = (Math.random() * 60000).toInt()
                        LjmUtil.D("major set : $randomMajor")
                        beaconSetting.major = randomMajor

                        conn.writeSetting("minew123")
                    }
                    ConnectionState.BeaconStatus_ConnectFailed -> {
                        LjmUtil.D("connection failed")
                    }
                    ConnectionState.BeaconStatus_Disconnect -> {
                        LjmUtil.D("disconnected")
                    }
                }
            }

            override fun onWriteSettings(var1: CustomMinewBeaconConnection?, success: Boolean) {
                if(success){

                    a.showToast("데이터 변경 성공!!")
                }else{

                    a.showToast("데이터 변경 실패 ㅠㅠ")
                }
            }

        }

        val connectService = beaconManager.connectService
        if(connectService != null){
            LjmUtil.D("connectService is not null!")
            beaconConnection.connect()
        }

    }

    interface BeaconSettingAction{
        fun showToast(msg: String)
        fun requestPermission()
        fun listBeacon(list: MutableList<MinewBeacon>)
    }
}