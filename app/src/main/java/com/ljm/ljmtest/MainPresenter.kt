package com.ljm.ljmtest

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ljm.ljmtest.common.LjmUtil
import com.ljm.ljmtest.location.LocationWorker
import java.util.concurrent.TimeUnit

class MainPresenter constructor(var c:Context, var action:MainActivityAction) : Presenter{
//    private lateinit var locationManager: LocationManager

    companion object {
        private const val interval = 1000L
        private const val distance = 1.0f

        const val REQ_CODE_BT = 700
        const val REQ_CODE_BT_DISCOVERABLE = 701
    }

    override fun onCreate(intent: Intent?) {
//        locationManager = c.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        c.registerReceiver(btReceiver, filter)
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

            /*val locationTestPeriodicRequest = PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(c).enqueue(locationTestPeriodicRequest)*/
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

                action.activateBluetooth()
            }
            R.id.bluetooth_discoverable -> {

                action.activateBluetoothDiscoverable()
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
                    LjmUtil.D("Name : ${device.name} address : ${device.address}")
                    action.showToast("Name : ${device.name} address : ${device.address}")
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
    }
}
