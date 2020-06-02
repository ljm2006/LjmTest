package com.ljm.ljmtest

import android.Manifest
import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ljm.ljmtest.common.LjmUtil
import com.ljm.ljmtest.data.BluetoothData
import com.ljm.ljmtest.location.LocationWorker
import com.ljm.ljmtest.network.NetworkJob
import com.ljm.ljmtest.util.PreferenceManager
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainPresenter constructor(var c:Context, var action:MainActivityAction) : Presenter{
//    private lateinit var locationManager: LocationManager

    companion object {
        private const val interval = 1000L
        private const val distance = 1.0f

        const val REQ_CODE_BT = 700
        const val REQ_CODE_BT_DISCOVERABLE = 701

        const val JOB_ID_NETWORK = 100
    }

    private val bluetoothDataArray: ArrayList<BluetoothData> = ArrayList()
    private val prefManager:PreferenceManager = PreferenceManager.getInstance(c)

    override fun onCreate(intent: Intent?) {
//        locationManager = c.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        c.registerReceiver(btReceiver, filter)

        if(prefManager.getUUID() == ""){
            val uuid = UUID.randomUUID().toString()
            LjmUtil.D("uuid : $uuid" )
            prefManager.saveUUID(uuid)
        }
    }

    override fun onResume() {
        if(ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            action.requestPermission()
        }else{

            /*var isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            var isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if(isNetworkEnabled){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    interval,
                    distance, this)
            }else if(isGPSEnabled){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    interval,
                    distance, this)
            }else{

                action.showToast("location provider not enabled...")
            }*/

            /*val locationTestWorkRequest = OneTimeWorkRequestBuilder<LocationWorker>().build()
            WorkManager.getInstance(c).enqueue(locationTestWorkRequest)*/

           /* val locationTestPeriodicRequest = PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(c).enqueue(locationTestPeriodicRequest)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                val scheduler = c.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                val jobServiceComponent = ComponentName(c, NetworkJob::class.java)
                val jobInfo = JobInfo.Builder(JOB_ID_NETWORK, jobServiceComponent)
                    .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                    .build()

                scheduler.schedule(jobInfo)
            }else{

            }*/
        }
    }

    override fun onPause() {

    }

    override fun onDestroy() {
        c.unregisterReceiver(btReceiver)
    }

    fun onClick(id: Int) {
        when(id){
            R.id.bluetooth_search ->{
                val btAdapter = BluetoothAdapter.getDefaultAdapter()

                if (btAdapter == null){
                    action.showToast("블루투스를 지원하지 않는 기기 입니다.")
                    return
                }

                bluetoothDataArray.clear()
                action.activateBluetooth()
            }
            R.id.bluetooth_discoverable -> {

                action.activateBluetoothDiscoverable()
            }

            R.id.bluetooth_advertise -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                    val advertiser:BluetoothLeAdvertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
                    val advertiseSettings = AdvertiseSettings.Builder()
                        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                        .setConnectable(false)
                        .setTimeout(10000)
                        .build()

                    val parcelUuid = ParcelUuid(UUID.fromString(prefManager.getUUID()))

                    val advertiseData = AdvertiseData.Builder()
                        .setIncludeDeviceName(true)
                        /*.addServiceUuid(parcelUuid)
                        .addServiceData(parcelUuid, "0xff".toByteArray(Charset.forName("UTF-8")))*/
                        .build()

                    val advertiseCallback = object :AdvertiseCallback(){
                        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                            LjmUtil.D("advertise onStartSuccess()")
                            super.onStartSuccess(settingsInEffect)
                        }

                        override fun onStartFailure(errorCode: Int) {
                            LjmUtil.D("advertise onStartFailure() -> $errorCode")
                            super.onStartFailure(errorCode)
                        }
                    }

                    advertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback)

                }else{

                    action.showToast("지원되지 않는 기능입니다.")
                }
            }
        }

    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQ_CODE_BT -> {
                if(resultCode == Activity.RESULT_OK){
                    action.showToast("Result OK!")
                    val btAdapter = BluetoothAdapter.getDefaultAdapter()
                    val searchStart = btAdapter.startDiscovery()
                    LjmUtil.D("searchStart : $searchStart")
                }
            }
            REQ_CODE_BT_DISCOVERABLE -> {
                if (resultCode != Activity.RESULT_CANCELED){
                    LjmUtil.D("mode time : $resultCode")
                    action.showToast("discoverable mode activated!")
                }
            }
        }
    }

    private val btReceiver = object : BroadcastReceiver(){
        override fun onReceive(c: Context?, intent: Intent?) {
            when(intent!!.action!!){
                BluetoothDevice.ACTION_FOUND ->{
                    val device:BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, 0)

                    LjmUtil.D("Name : ${device.name} address : ${device.address} rssi : $rssi")
                    action.showToast("Name : ${device.name} address : ${device.address} rssi : $rssi")

                    val name = if(device.name == null) "null" else device.name

                    val data = BluetoothData(name, device.address, rssi)

                    if(!bluetoothDataArray.contains(data)){
                        bluetoothDataArray.add(data)
                        action.refreshBluetoothDataList(bluetoothDataArray)
                    }

                }
            }
        }

    }

    interface MainActivityAction{
        fun showToast(msg:String)
        fun requestPermission()
        fun setInfo(latitude:Double, longitude:Double)

        fun activateBluetooth()
        fun activateBluetoothDiscoverable()

        fun refreshBluetoothDataList(dataArray:ArrayList<BluetoothData>)
    }
}
