package com.ljm.ljmtest.network

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NetworkJob : JobService() {

    companion object{
        class NetworkThread constructor(var c:Context) : Thread(){
            override fun run() {
                while (true){
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(c, "network activated!", Toast.LENGTH_SHORT).show()
                    }

                    try {

                        sleep(10000)
                    }catch (e:InterruptedException){

                        break
                    }
                }
            }
        }
    }

    private val networkThread = NetworkThread(this)

    override fun onStartJob(params: JobParameters?): Boolean {

        if(!networkThread.isAlive){
            networkThread.start()
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

}