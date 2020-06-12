package com.ljm.ljmtest.data

data class BluetoothData (var name:String, var address:String, var rssi:Short, var uuid:String){
    override fun equals(other: Any?): Boolean {

        return name== (other as BluetoothData).name && address == (other as BluetoothData).address && uuid == (other as BluetoothData).uuid
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + address.hashCode()
        return result
    }
}