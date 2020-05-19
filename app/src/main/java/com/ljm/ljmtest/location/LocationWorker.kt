package com.ljm.ljmtest.location

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ljm.ljmtest.R
import com.ljm.ljmtest.common.LjmUtil
import kotlin.concurrent.thread

class LocationWorker constructor(var c: Context, var workerParams:WorkerParameters) : Worker(c, workerParams), LocationListener {

    val thread = LocationCheckingThread(c)

    companion object {
        private const val interval = 1000L
        private const val distance = 1.0f

        class LocationCheckingThread constructor(var c: Context) : Thread(), LocationListener {
            override fun run() {
                if(ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    val locationManager = c.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    var isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                    var isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                    while (true){
                        LjmUtil.D("---Location Thread loop---")
                        Handler(Looper.getMainLooper()).post{


                            if(isNetworkEnabled){
                                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                    interval,
                                    distance, this)
                            }

                            if(isGPSEnabled){
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                    interval,
                                    distance, this)
                            }else{

                                LjmUtil.D("Location data not updated...")
                            }

                            val lastKnownLocation = locationManager.getLastKnownLocation(if (isNetworkEnabled) LocationManager.NETWORK_PROVIDER else LocationManager.NETWORK_PROVIDER)
                            lastKnownLocation?.let {

                                LjmUtil.D("last known location => latitude : ${lastKnownLocation.latitude}, longitude : ${lastKnownLocation.longitude}")
                            }
                        }

                        try {

                            LjmUtil.D("---Location Thread sleep 1minute---")
                            sleep(60000)
                        }catch (e:InterruptedException){

                            break
                        }
                    }
                }
            }

            override fun onLocationChanged(location: Location?) {
                location?.let {
                    LjmUtil.D("currentLocation => latitude:${it.latitude},longitude:${it.longitude}")

                    if(location.provider == LocationManager.GPS_PROVIDER){

                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(c, "currentLocation => latitude:${it.latitude},longitude:${it.longitude}", Toast.LENGTH_LONG).show()
                            val notification = NotificationCompat.Builder(c, "ljmTest")
                                .setContentTitle("좌표 수신 테스트(GPS)")
                                .setContentText("currentLocation => latitude:${it.latitude}\nlongitude:${it.longitude}")
                                .setVibrate(longArrayOf(100,500,100,500))
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .build()

                            val notificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            val randomId = (Math.random() * Int.MAX_VALUE-1).toInt()
                            notificationManager.notify(randomId, notification)
                        }
                    }else{

                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(c, "currentLocation => latitude:${it.latitude},longitude:${it.longitude}", Toast.LENGTH_LONG).show()
                            val notification = NotificationCompat.Builder(c, "ljmTest")
                                .setContentTitle("좌표 수신 테스트(Network)")
                                .setContentText("currentLocation => latitude:${it.latitude}\nlongitude:${it.longitude}")
                                .setVibrate(longArrayOf(100,500,100,500))
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .build()

                            val notificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            val randomId = (Math.random() * Int.MAX_VALUE-1).toInt()
                            notificationManager.notify(randomId, notification)
                        }
                    }
                }
            }

            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

            }

            override fun onProviderEnabled(p0: String?) {

            }

            override fun onProviderDisabled(p0: String?) {

            }
        }
    }

    override fun doWork(): Result {
        LjmUtil.D("doWork() -- LocationWorker")
        /*if(ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            val locationManager = c.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            var isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            val thread = Thread{

                while (true){
                    LjmUtil.D("---Location Thread loop---")
                    Handler(Looper.getMainLooper()).post{


                        if(isNetworkEnabled){
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                interval,
                                distance, this)
                        }

                        if(isGPSEnabled){
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                interval,
                                distance, this)
                        }else{

                            LjmUtil.D("Location data not updated...")
                        }

                        val lastKnownLocation = locationManager.getLastKnownLocation(if (isNetworkEnabled) LocationManager.NETWORK_PROVIDER else LocationManager.NETWORK_PROVIDER)
                        lastKnownLocation?.let {

                            LjmUtil.D("last known location => latitude : ${lastKnownLocation.latitude}, longitude : ${lastKnownLocation.longitude}")
                        }
                    }

                    try {

                        LjmUtil.D("---Location Thread sleep 1minute---")
                        Thread.sleep(60000)
                    }catch (e:InterruptedException){

                        break
                    }
                }

            }

            if(!thread.isAlive){
                LjmUtil.D("Location Thread start!")
                thread.start()
            }
        }*/

        if(!thread.isAlive){
            thread.start()
        }

        return Result.success()
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {

            LjmUtil.D("currentLocation => latitude:${it.latitude},longitude:${it.longitude}")

            if(location.provider == LocationManager.GPS_PROVIDER){

                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(c, "currentLocation => latitude:${it.latitude},longitude:${it.longitude}", Toast.LENGTH_LONG).show()
                    val notification = NotificationCompat.Builder(c, "ljmTest")
                        .setContentTitle("좌표 수신 테스트(GPS)")
                        .setContentText("currentLocation => latitude:${it.latitude}\nlongitude:${it.longitude}")
                        .setVibrate(longArrayOf(100,500,100,500))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build()

                    val notificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val randomId = (Math.random() * Int.MAX_VALUE-1).toInt()
                    notificationManager.notify(randomId, notification)
                }
            }else{

                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(c, "currentLocation => latitude:${it.latitude},longitude:${it.longitude}", Toast.LENGTH_LONG).show()
                    val notification = NotificationCompat.Builder(c, "ljmTest")
                        .setContentTitle("좌표 수신 테스트(Network)")
                        .setContentText("currentLocation => latitude:${it.latitude}\nlongitude:${it.longitude}")
                        .setVibrate(longArrayOf(100,500,100,500))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build()

                    val notificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val randomId = (Math.random() * Int.MAX_VALUE-1).toInt()
                    notificationManager.notify(randomId, notification)
                }
            }
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

    }

}