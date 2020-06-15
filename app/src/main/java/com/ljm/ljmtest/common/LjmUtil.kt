package com.ljm.ljmtest.common

import android.util.Log

class LjmUtil {
    companion object{

        const val BLE_THERMOCARE_BATTERY_SERVICE = "0000180f"
        const val BLE_THERMOCARE_TEMPERATURE_SERVICE = "00001809"
        const val BLE_THERMOCARE_ACCESS_PROFILE_SERVICE = "00001800"

        const val THERMOCARE_CHARACTERISTIC_TEMPERATURE_MEASUREMENT = "00002a1c"

        fun D(msg:String){
            Log.d("ljm2006", msg)
        }
    }

}