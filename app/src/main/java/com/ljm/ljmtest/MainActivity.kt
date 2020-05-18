package com.ljm.ljmtest

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), MainPresenter.MainActivityAction {
    lateinit var presenter: MainPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter(this, this)
        presenter.onCreate(intent)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1000 && permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            showToast("권한 설정 완료.")
        }
    }

    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1000)
    }

    override fun setInfo(latitude: Double, longitude: Double) {
        val info = "latitude : $latitude\nlongitude : $longitude"
        val textInfo:TextView = findViewById(R.id.text_info)
        textInfo.text = info
    }
}
