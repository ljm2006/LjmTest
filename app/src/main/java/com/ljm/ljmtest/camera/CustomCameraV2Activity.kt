package com.ljm.ljmtest.camera
/*Camera2 API 를 이용한 개발 예시*/

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ljm.ljmtest.R

class CustomCameraV2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_camera_v2)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(R.id.container, Camera2BasicFragment.newInstance())
                .commit()
        }
    }
}