package com.ljm.ljmtest

import android.Manifest
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.ljm.ljmtest.common.LjmUtil
import com.ljm.ljmtest.data.BluetoothData
import com.ljm.ljmtest.util.PreferenceManager
import java.util.*
import kotlin.collections.ArrayList

class MainPresenter constructor(var c:Context, var action:MainActivityAction) : Presenter{
//    private lateinit var locationManager: LocationManager

    companion object {
        private const val interval = 1000L
        private const val distance = 1.0f

        const val REQ_CODE_BT = 700
        const val REQ_CODE_BT_DISCOVERABLE = 701

        const val JOB_ID_NETWORK = 100
    }

    private val bluetoothDataArray: ArrayList<BluetoothData> = ArrayList()
    private val prefManager:PreferenceManager = PreferenceManager.getInstance(c)

    private lateinit var bluetoothLeScanner:BluetoothLeScanner
    private lateinit var bluetoothGatt:BluetoothGatt

    override fun onCreate(intent: Intent?) {
//        locationManager = c.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        c.registerReceiver(btReceiver, filter)

        if(prefManager.getUUID() == ""){
            val uuid = UUID.randomUUID().toString()
            LjmUtil.D("uuid : $uuid" )
            prefManager.saveUUID(uuid)
        }
    }

    override fun onResume() {
        if(ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            action.requestPermission()
        }else{

            /*var isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            var isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if(isNetworkEnabled){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    interval,
                    distance, this)
            }else if(isGPSEnabled){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    interval,
                    distance, this)
            }else{

                action.showToast("location provider not enabled...")
            }*/

            /*val locationTestWorkRequest = OneTimeWorkRequestBuilder<LocationWorker>().build()
            WorkManager.getInstance(c).enqueue(locationTestWorkRequest)*/

           /* val locationTestPeriodicRequest = PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(c).enqueue(locationTestPeriodicRequest)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                val scheduler = c.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                val jobServiceComponent = ComponentName(c, NetworkJob::class.java)
                val jobInfo = JobInfo.Builder(JOB_ID_NETWORK, jobServiceComponent)
                    .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                    .build()

                scheduler.schedule(jobInfo)
            }else{

            }*/
        }
    }

    override fun onPause() {

    }

    override fun onDestroy() {
        c.unregisterReceiver(btReceiver)
    }

    fun onClick(id: Int) {
        when(id){
            R.id.bluetooth_search ->{
                val btAdapter = BluetoothAdapter.getDefaultAdapter()

                if (btAdapter == null){
                    action.showToast("블루투스를 지원하지 않는 기기 입니다.")
                    return
                }

                bluetoothDataArray.clear()
                action.activateBluetooth()
            }
            R.id.bluetooth_discoverable -> {

                action.activateBluetoothDiscoverable()
            }

            R.id.bluetooth_advertise -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                    val advertiser:BluetoothLeAdvertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
                    val advertiseSettings = AdvertiseSettings.Builder()
                        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                        .setConnectable(false)
                        .setTimeout(10000)
                        .build()

                    val parcelUuid = ParcelUuid(UUID.fromString(prefManager.getUUID()))
                    LjmUtil.D("UUID : ${prefManager.getUUID()}")
                    val advertiseData = AdvertiseData.Builder()
                        .addServiceUuid(parcelUuid)
//                        .addServiceData(parcelUuid, "abcd".toByteArray(Charset.forName("UTF-8")))
//                        .setIncludeDeviceName(true)
                        /*.addServiceUuid(parcelUuid)
                        .addServiceData(parcelUuid, "0xff".toByteArray(Charset.forName("UTF-8")))*/
                        .build()

                    val advertiseCallback = object :AdvertiseCallback(){
                        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                            LjmUtil.D("advertise onStartSuccess()")
                            action.showToast("advertise start success!")
                            super.onStartSuccess(settingsInEffect)
                        }

                        override fun onStartFailure(errorCode: Int) {
                            LjmUtil.D("advertise onStartFailure() -> $errorCode")
                            super.onStartFailure(errorCode)
                        }
                    }

                    advertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback)

                }else{

                    action.showToast("지원되지 않는 기능입니다.")
                }
            }
            R.id.bluetooth_discovery ->{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    if(BluetoothAdapter.getDefaultAdapter().isEnabled){

                        bluetoothDataArray.clear()
                        bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
                        bluetoothLeScanner.startScan(scanCallback)
                        Handler(Looper.getMainLooper()).postDelayed({
                            bluetoothLeScanner.stopScan(scanCallback)
                            action.showToast("BLE scan stopped...")
                        }, 60000)
                    }else{

                        action.showToast("블루투스를 켜주세요.")
                    }
                }
            }
            R.id.bluetooth_temperature -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    if(BluetoothAdapter.getDefaultAdapter().isEnabled){

                        LjmUtil.D("온도계 찾기 시작!")
                        bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
                        bluetoothLeScanner.startScan(thermoScanCallback)
                        Handler(Looper.getMainLooper()).postDelayed({
                            bluetoothLeScanner.stopScan(thermoScanCallback)
                            action.showToast("BLE scan stopped...")
                        }, 300000)
                    }else{

                        action.showToast("블루투스를 켜주세요.")
                    }
                }
            }
        }

    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQ_CODE_BT -> {
                if(resultCode == Activity.RESULT_OK){
                    action.showToast("Result OK!")
                    val btAdapter = BluetoothAdapter.getDefaultAdapter()
                    val searchStart = btAdapter.startDiscovery()
                    LjmUtil.D("searchStart : $searchStart")
                }
            }
            REQ_CODE_BT_DISCOVERABLE -> {
                if (resultCode != Activity.RESULT_CANCELED){
                    LjmUtil.D("mode time : $resultCode")
                    action.showToast("discoverable mode activated!")
                }
            }
        }
    }

    private val btReceiver = object : BroadcastReceiver(){
        override fun onReceive(c: Context?, intent: Intent?) {
            when(intent!!.action!!){
                BluetoothDevice.ACTION_FOUND ->{
                    val device:BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, 0)

                    LjmUtil.D("Name : ${device.name} address : ${device.address} rssi : $rssi")
                    action.showToast("Name : ${device.name} address : ${device.address} rssi : $rssi")

                    val name = if(device.name == null) "null" else device.name

                    val data = BluetoothData(name, device.address, rssi, "")

                    if(!bluetoothDataArray.contains(data)){
                        bluetoothDataArray.add(data)
                        action.refreshBluetoothDataList(bluetoothDataArray)
                    }

                }
            }
        }

    }

    private val scanCallback:ScanCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ScanCallback(){
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            val device = result!!.device
//            val deviceName = if(device.name != null) device.name else "null"
            val deviceName = if(device.name != null) device.name else "null"
            val uuid:String = if(result.scanRecord!!.serviceUuids != null) result.scanRecord!!.serviceUuids[0].toString() else "null"
            val data = BluetoothData(deviceName, device.address, result.rssi.toShort(), uuid)
            Handler(Looper.getMainLooper()).post {
                if(!bluetoothDataArray.contains(data)){
                    bluetoothDataArray.add(data)
                    action.refreshBluetoothDataList(bluetoothDataArray)
                }
            }
            super.onScanResult(callbackType, result)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
        }
    }
    //온도계 전용 callback
    private val thermoScanCallback:ScanCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ScanCallback(){

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {

            val device = result!!.device

            if(device.name == "THERMOCARE"){
                action.showToast("device found!\ndevice name : ${device.name}" +
                        "\ndevice uuid: ${result.scanRecord!!.serviceUuids}\ndevice address: ${device.address}")
                LjmUtil.D("device found!\ndevice name : ${device.name}" +
                        "\ndevice uuid: ${result.scanRecord!!.serviceUuids}\ndevice address: ${device.address}")
                bluetoothLeScanner.stopScan(this)

                bluetoothGatt = device.connectGatt(c, true, bluetoothGattCallback)

            }else{

                LjmUtil.D("검색은 됐으나 온도계가 아님 -> ${device.name}")
            }

            super.onScanResult(callbackType, result)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
        }
    }

    var bluetoothGattCallback:BluetoothGattCallback = object: BluetoothGattCallback(){

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when(newState){
                BluetoothProfile.STATE_CONNECTED -> {
                    LjmUtil.D("온도계와 연결됨")
                    val discovered = gatt!!.discoverServices()
                    LjmUtil.D("서비스 찾기 시도 성공함? $discovered")
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    LjmUtil.D("온도계와 연결해제됨")
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            when(status){
                BluetoothGatt.GATT_SUCCESS ->{
                    LjmUtil.D("onServiceDiscovered received: $status")
                    val gattServices = gatt!!.services
                    gattServices.forEach { gattService ->

                        val serviceUUID = gattService.uuid.toString()
                        if(serviceUUID.contains(LjmUtil.BLE_THERMOCARE_TEMPERATURE_SERVICE)){
                            LjmUtil.D("service uuid : ${gattService.uuid}")

                            val gattCharacteristics = gattService.characteristics

                            gattCharacteristics.forEach{ gattCharacteristic ->
                                val uuid:String = gattCharacteristic.uuid.toString()

                                if(uuid.contains(LjmUtil.THERMOCARE_CHARACTERISTIC_TEMPERATURE_MEASUREMENT)){

                                    gatt.setCharacteristicNotification(gattCharacteristic, true)
                                    gatt.readCharacteristic(gattCharacteristic)
                                    return
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            LjmUtil.D("onCharacteristicRead(), status : $status, uuid : ${characteristic!!.uuid}")
            when(status){
                BluetoothGatt.GATT_SUCCESS -> {
                    val data: ByteArray? = characteristic.value
                    if(data?.isNotEmpty() == true){
                        val hexStringData = data.joinToString(separator = " ") {
                            String.format("%02X", it)
                        }
                        LjmUtil.D("characteristic.uuid -> ${characteristic.uuid}, value -> $hexStringData")
                    }
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {

            LjmUtil.D("onCharacteristicChanged()")
            val data: ByteArray? = characteristic!!.value
            if(data?.isNotEmpty() == true){
                val hexStringData = data.joinToString(separator = " ") {
                    String.format("%02X", it)
                }
                LjmUtil.D("characteristic.uuid -> ${characteristic.uuid}, value -> $hexStringData")
            }
        }
    }

    interface MainActivityAction{
        fun showToast(msg:String)
        fun requestPermission()
        fun setInfo(latitude:Double, longitude:Double)

        fun activateBluetooth()
        fun activateBluetoothDiscoverable()

        fun refreshBluetoothDataList(dataArray:ArrayList<BluetoothData>)
        fun sendBroadcast(action:String)
    }
}
