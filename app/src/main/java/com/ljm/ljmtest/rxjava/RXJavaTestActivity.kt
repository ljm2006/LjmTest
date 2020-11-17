package com.ljm.ljmtest.rxjava

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ljm.ljmtest.R
import com.ljm.ljmtest.common.LjmUtil
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.functions.Consumer


class RXJavaTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_java_test)

        //Flowable, Observable -> rx java의 소스형태

        Flowable.fromArray("Hello Rx java!","My name is ljm").subscribe {
            LjmUtil.D(it)
        }

        Observable.just("hello world!").subscribe {
            LjmUtil.D(it)
        }

        Observable.just("하얀 늑대는 100의 데미지를 입었다.")
            .map { "화살공격으로 인하여 " + it }
            .map { it + "(25% 추가로 데미지를 받음)" }
            .subscribe{
                LjmUtil.D(it)
            }
    }
}