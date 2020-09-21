package com.ljm.ljmtest.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.ljm.ljmtest.R

class BLEActivity : AppCompatActivity(), BLEPresenter.BLEActivityAction{
    private lateinit var presenter:BLEPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        presenter = BLEPresenter(this, this)
        presenter.onCreate(intent)
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            777 -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED){

                    presenter.startScan()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            778 -> {
                if (resultCode == Activity.RESULT_OK){

                    presenter.startScan()
                }
            }
        }
    }

    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    override fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN), 777)
    }

    override fun finishActivity() {
        finish()
    }

    override fun askBluetoothOn() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(intent, 778)
    }
}