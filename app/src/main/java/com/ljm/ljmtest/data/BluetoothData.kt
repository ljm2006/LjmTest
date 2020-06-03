package com.ljm.ljmtest.data

data class BluetoothData (var name:String, var address:String, var rssi:Short){
    override fun equals(other: Any?): Boolean {

        return name== (other as BluetoothData).name && address == (other as BluetoothData).address
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + address.hashCode()
        return result
    }
}