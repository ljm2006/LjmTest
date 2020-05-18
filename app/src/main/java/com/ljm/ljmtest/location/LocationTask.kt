package com.ljm.ljmtest.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat

class LocationTask constructor(var c: Context) : AsyncTask<Unit,Unit,Unit>(), LocationListener {

    private lateinit var locationManager:LocationManager
    lateinit var locationCallback:LocationCallback

    companion object {
        private const val interval = 1000L
        private const val distance = 1.0f

    }

    override fun doInBackground(vararg p0: Unit?) {
        locationManager = c.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if(ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            locationCallback.onError("permission not granted...")
        }else{

            if(isNetworkEnabled){
                Handler(Looper.getMainLooper()).post {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, distance, this)
                }
            }else if(isGPSEnabled){
                Handler(Looper.getMainLooper()).post {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, distance, this)
                }
            }else{

                locationCallback.onError("location provider not enabled...")
            }
        }

    }

    override fun onLocationChanged(location: Location?) {

        location?.let {
            locationCallback.onLocationChanged(location)
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

    }

    interface LocationCallback{
        fun onLocationChanged(location:Location)
        fun onError(msg:String)
    }

}