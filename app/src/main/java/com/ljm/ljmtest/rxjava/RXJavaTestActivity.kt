package com.ljm.ljmtest.rxjava

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ljm.ljmtest.R
import com.ljm.ljmtest.common.LjmUtil
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableEmitter
import io.reactivex.rxjava3.core.FlowableOnSubscribe
import io.reactivex.rxjava3.functions.Consumer


class RXJavaTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_java_test)

        Flowable.fromArray("Hello Rx java!","My name is ljm").subscribe {
            LjmUtil.D(it)
        }
    }
}