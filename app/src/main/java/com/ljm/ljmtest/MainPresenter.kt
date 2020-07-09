package com.ljm.ljmtest

import android.Manifest
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.provider.Settings
import android.speech.tts.TextToSpeech
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.ljm.ljmtest.common.LjmUtil
import com.ljm.ljmtest.data.BluetoothData
import com.ljm.ljmtest.util.PreferenceManager
import java.util.*
import kotlin.collections.ArrayList

class MainPresenter constructor(var c:Context, var action:MainActivityAction) : Presenter, TextToSpeech.OnInitListener{
//    private lateinit var locationManager: LocationManager

    companion object {
        private const val interval = 1000L
        private const val distance = 1.0f

        const val REQ_CODE_BT = 700
        const val REQ_CODE_BT_DISCOVERABLE = 701

        const val JOB_ID_NETWORK = 100

        const val ACTION_TEMPERATURE_MEASURED = "com.ljm.ljmtest.ACTION_TEMPERATURE_MEASURED"
    }

    private val bluetoothDataArray: ArrayList<BluetoothData> = ArrayList()
    private val prefManager:PreferenceManager = PreferenceManager.getInstance(c)

    private lateinit var bluetoothLeScanner:BluetoothLeScanner
    private lateinit var bluetoothGatt:BluetoothGatt
    private lateinit var tts:TextToSpeech

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var oldScanning = false

    override fun onCreate(intent: Intent?) {
//        locationManager = c.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        c.registerReceiver(btReceiver, filter)

        if(prefManager.getUUID() == ""){
            val uuid = UUID.randomUUID().toString()
            LjmUtil.D("uuid : $uuid" )
            prefManager.saveUUID(uuid)
        }

        val bleFilter = IntentFilter()
        bleFilter.addAction(ACTION_TEMPERATURE_MEASURED)

        LjmUtil.D("bluetooth le systemFeature : ${c.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)}")

        tts = TextToSpeech(c, this)

        //device 정보 가져오기
        val deviceManufacturer = Build.MANUFACTURER;
        val deviceModel = Build.MODEL
        val deviceVersion = Build.VERSION.SDK_INT
        val deviceProductName = Settings.Secure.getString(c.contentResolver, "bluetooth_name")
        val deviceFullName = "$deviceModel($deviceProductName)"
        action.setDeviceInfo(deviceManufacturer, deviceFullName, deviceVersion.toString())
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
        if(::bluetoothGatt.isInitialized){

            bluetoothGatt.close()
        }
        c.unregisterReceiver(btReceiver)

        if(::tts.isInitialized){

            tts.stop()
            tts.shutdown()
        }
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
            R.id.bluetooth_discovery_old -> {
                bluetoothDataArray.clear()
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(!oldScanning){

                    Handler(Looper.getMainLooper()).postDelayed({
                        oldScanning = false
                        bluetoothAdapter.stopLeScan(leScanCallback)
                        action.showToast("old scan stopped because of timeout...")
                    }, 300000)
                    oldScanning = true
                    bluetoothAdapter.startLeScan(leScanCallback)
                }else{

                    oldScanning = false
                    bluetoothAdapter.stopLeScan(leScanCallback)
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

    //older ble scan callback
    private val leScanCallback = BluetoothAdapter.LeScanCallback{device, i, bytes ->
        Handler(Looper.getMainLooper()).post {
//            val deviceName = if(device.name != null) device.name else "null"
            val deviceName = if(device.name != null) device.name else "null"
            val uuid:String = "null"
            val data = BluetoothData(deviceName, device.address, 0.toShort(), uuid)
            Handler(Looper.getMainLooper()).post {
                if(!bluetoothDataArray.contains(data)){
                    bluetoothDataArray.add(data)
                    action.refreshBluetoothDataList(bluetoothDataArray)
                }
            }

            if(deviceName != "null"){
                if(deviceName == "THERMOCARE"){

                    Handler(Looper.getMainLooper()).post {
                        stopOldScan()
                        action.showToast("device found! scan stop!")
                    }
                }
            }
        }
    }

    private fun stopOldScan(){
        oldScanning = false
        bluetoothAdapter.stopLeScan(leScanCallback)
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
                                    /*LjmUtil.D("characteristic uuid : ${gattCharacteristic.uuid}")
                                    gatt.setCharacteristicNotification(gattCharacteristic, true)*/
                                    val descriptorConfig = gattCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                                    descriptorConfig.value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                                    val isConfigWrite = gatt.writeDescriptor(descriptorConfig)
                                    LjmUtil.D("config write is successful : $isConfigWrite")
                                    /*val isRead = gatt.readCharacteristic(gattCharacteristic)
                                    LjmUtil.D("read operation is successful : $isRead")*/
                                    val isSetCharacteristicNoti = gatt.setCharacteristicNotification(gattCharacteristic, true)
                                    LjmUtil.D("setting characteristic notification is successful : $isSetCharacteristicNoti")
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

                val hexDataSplit = hexStringData.split(" ")
                val temperatureHex = hexDataSplit[3] + hexDataSplit[2] + hexDataSplit[1]
                LjmUtil.D("temperature Hex : $temperatureHex")
                var temperatureData = (Integer.parseInt(temperatureHex, 16)).toDouble()
                LjmUtil.D("temperature Integer : $temperatureData")
                if(hexDataSplit[4] == "FF"){
                    temperatureData *= 0.1
                }

                LjmUtil.D("temperature : $temperatureData")
                val msg = String.format("%.1f도 입니다.", temperatureData)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null)
                }else{

                    tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
                }
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

        fun setDeviceInfo(manufacturer:String, model:String, version:String);
    }

    //TTS init
    override fun onInit(status: Int) {
        if(status != TextToSpeech.ERROR){
            action.showToast("TTS engine loaded...")
            tts.language = Locale.KOREAN
            tts.setPitch(1.0f)
            tts.setSpeechRate(1.0f)
        }

    }
}
