package com.ljm.ljmtest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ljm.ljmtest.location.LocationWorker
import java.util.concurrent.TimeUnit

class MainPresenter constructor(var c:Context, var action:MainActivityAction) : Presenter{
//    private lateinit var locationManager: LocationManager

    companion object {
        private const val interval = 1000L
        private const val distance = 1.0f

    }

    override fun onCreate(intent: Intent?) {
//        locationManager = c.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

            val locationTestPeriodicRequest = PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(c).enqueue(locationTestPeriodicRequest)
        }
    }

    override fun onPause() {

    }

    override fun onDestroy() {

    }

    interface MainActivityAction{
        fun showToast(msg:String)
        fun requestPermission()
        fun setInfo(latitude:Double, longitude:Double)
    }
}
