package com.ljm.ljmtest.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager constructor(var c: Context) {

    private val pref:SharedPreferences = c.getSharedPreferences("Setting", Context.MODE_PRIVATE)

    companion object{

        private var manager:PreferenceManager? = null
        private const val KEY_UUID = "uuid"

        fun getInstance(c:Context): PreferenceManager =
            manager ?: synchronized(this){
                manager ?: PreferenceManager(c).also {
                    manager = it
                }
            }
    }

    fun saveUUID(uuid:String){
        val editor = pref.edit()
        editor.putString(KEY_UUID, uuid)
        editor.apply()
    }

    fun getUUID() : String{
        return pref.getString(KEY_UUID, "")!!
    }
}