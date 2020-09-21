package com.ljm.ljmtest.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.ljm.ljmtest.Presenter
import com.ljm.ljmtest.common.LjmUtil
import com.ljm.ljmtest.data.BluetoothData
import java.lang.Exception

class BLEPresenter constructor(private val c: Context,private val act:BLEActivityAction) : Presenter {

    private var bluetoothAdapter:BluetoothAdapter? = null
    private lateinit var bleScanner: BluetoothLeScanner

    override fun onCreate(intent: Intent?) {

        if(ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(c, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(c, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED){

            act.requestPermission()
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter == null){
            act.showToast("unsupported ble device!")
            act.finishActivity()
            return
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if(!bluetoothAdapter!!.isEnabled){
                act.askBluetoothOn()
                return
            }
            startScan()
        }
    }

    override fun onResume() {

    }

    override fun onPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            stopScan("BLE Scan paused!")
        }
    }

    override fun onDestroy() {

    }

    fun startScan(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bleScanner = bluetoothAdapter!!.bluetoothLeScanner

            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setReportDelay(1000)
                .build()

            bleScanner.startScan(null, scanSettings ,scanCallBack)
            act.showToast("BLE Scan start!")

            Handler(Looper.getMainLooper()).postDelayed({

                stopScan("BLE Scan Timeout!")
            }, 10000)
        }
    }

    fun stopScan(msg:String){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bleScanner.stopScan(scanCallBack)
            act.showToast(msg)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private val scanCallBack:ScanCallback = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult?) {

//            LjmUtil.D("callbackType : $callbackType")
            /*when(callbackType){
                ScanSettings.CALLBACK_TYPE_ALL_MATCHES -> {
                    if(result != null){

                        val device = result.device
                        LjmUtil.D("scanResult -> name : ${device.name} address : ${device.address}")
                    }
                }
            }*/
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {

            results?.forEach {
                val device = it.device
                try {

                    LjmUtil.D("scanResult -> name : ${device.name} address : ${device.address}")
                    val data = BluetoothData(device.name, device.address, -1, device.uuids[0].toString())
                }catch (e:Exception){

                }
            }
        }

        override fun onScanFailed(errorCode: Int) {

        }
    }

    interface BLEActivityAction{
        fun showToast(msg:String)
        fun requestPermission()
        fun finishActivity()
        fun askBluetoothOn()
    }
}