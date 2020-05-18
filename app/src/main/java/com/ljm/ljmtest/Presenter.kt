package com.ljm.ljmtest

import android.content.Intent

interface Presenter {
    fun onCreate(intent: Intent?)
    fun onResume()
    fun onPause()
    fun onDestroy()
}