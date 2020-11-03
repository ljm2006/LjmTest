package com.ljm.ljmtest.bluetooth

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.ljm.ljmtest.R
import com.minew.beaconset.MinewBeacon

class BeaconSettingActivity : AppCompatActivity(), BeaconSettingPresenter.BeaconSettingAction {
    private lateinit var presenter:BeaconSettingPresenter
    private lateinit var listView:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon_setting)

        listView = findViewById(R.id.listView)

        presenter = BeaconSettingPresenter(this, this)
        presenter.onCreate(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            showToast("권한 설정 완료!")
        }
    }

    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun onClick(v: View){
        presenter.onClick(v.id)
    }

    override fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN), 1000)
    }

    override fun listBeacon(list: MutableList<MinewBeacon>) {
        listView.adapter = BeaconListAdapter(presenter, list)
    }

    private class BeaconHolder constructor(v:View) : RecyclerView.ViewHolder(v){
        val item = v
        val beaconName:TextView = v.findViewById(R.id.beaconName)
        val major:TextView = v.findViewById(R.id.major)
        val minor:TextView = v.findViewById(R.id.minor)
    }

    private class BeaconListAdapter constructor(var presenter: BeaconSettingPresenter,var beaconList:List<MinewBeacon>) : RecyclerView.Adapter<BeaconHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeaconHolder {
            val v:View = LayoutInflater.from(parent.context).inflate(R.layout.item_beacon_list, parent, false)
            return BeaconHolder(v)
        }

        override fun onBindViewHolder(holder: BeaconHolder, position: Int) {
            val beacon = beaconList[position]
            holder.beaconName.text = beacon.name
            holder.major.text = beacon.major
            holder.minor.text = beacon.minor
            holder.item.setOnClickListener {
                presenter.connectBeacon(beacon)
            }
        }

        override fun getItemCount(): Int {
            return beaconList.size
        }

    }
}