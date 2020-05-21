package com.ljm.ljmtest

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.ljm.ljmtest.data.BluetoothData
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

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
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

    fun onClick(v: View){
        presenter.onClick(v.id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_ADMIN), 1000)
    }

    override fun setInfo(latitude: Double, longitude: Double) {
        val info = "latitude : $latitude\nlongitude : $longitude"
        /*val textInfo:TextView = findViewById(R.id.text_info)
        textInfo.text = info*/
    }

    override fun activateBluetooth() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(intent, MainPresenter.REQ_CODE_BT)
    }

    override fun activateBluetoothDiscoverable() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }

        startActivityForResult(intent, MainPresenter.REQ_CODE_BT_DISCOVERABLE)
    }

    override fun refreshBluetoothDataList(dataArray: ArrayList<BluetoothData>) {
        val list: RecyclerView = findViewById(R.id.list)
        list.adapter = BTListAdapter(this, dataArray)
    }

    private class BTViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val name:TextView = v.findViewById(R.id.name)
        val address:TextView = v.findViewById(R.id.address)
        val rssi:TextView = v.findViewById(R.id.rssi)
    }

    private class BTListAdapter constructor(val c: Context, val dataArray: ArrayList<BluetoothData>) : RecyclerView.Adapter<BTViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BTViewHolder {
            val v:View = LayoutInflater.from(parent.context).inflate(R.layout.item_bluetooth_list, parent, false)
            return BTViewHolder(v)
        }

        override fun getItemCount(): Int {
            return dataArray.size
        }

        override fun onBindViewHolder(holder: BTViewHolder, position: Int) {
            val data:BluetoothData = dataArray[position]

            holder.name.text = data.name
            holder.address.text = data.address
            holder.rssi.text = data.rssi.toString()
        }

    }
}
