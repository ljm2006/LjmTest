package com.ljm.ljmtest.common

import android.util.Log

class LjmUtil {
    companion object{

        const val BLE_THERMOCARE_BATTERY_SERVICE = "0000180f"

        fun D(msg:String){
            Log.d("ljm2006", msg)
        }
    }

}