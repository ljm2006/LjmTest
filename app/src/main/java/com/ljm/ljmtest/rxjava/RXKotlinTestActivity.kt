package com.ljm.ljmtest.rxjava

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ljm.ljmtest.R
import com.ljm.ljmtest.common.LjmUtil
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.toObservable
import kotlinx.coroutines.*


class RXKotlinTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_java_test)

        Log("Hello world", "rxJava", "kotlin extension")
        changeViewText(findViewById(R.id.text), "Hello world")
        inputDamageLog("철수", "영희", 100)

        startFirstCoroutine()
        testMonad(100)
    }

    fun onClick(v:View){
        val num1Text = findViewById<EditText>(R.id.edit_num1).text.toString().replace(" ","")
        val num2Text = findViewById<EditText>(R.id.edit_num2).text.toString().replace(" ","")
        val num1 = if(num1Text == "") 0 else num1Text.toInt()
        val num2 = if(num2Text == "") 0 else num2Text.toInt()
        val calculator = RxCalculator(num1, num2)
        calculator.calculate(findViewById(R.id.text))
    }

    fun inputDamageLog(subject:String, target:String, damage:Int){
        val damageText = "${damage}피해를 주었다."
        Observable.just(damageText)
            .map {
                "${subject}가 ${target}에게 $it"
            }
            .map {
                "$it (25% 감소된 데미지.)"
            }
            .subscribe {
                LjmUtil.D(it)
            }
    }

    fun changeViewText(v: TextView, text:String){
        Observable.just(text).subscribe {
            v.text = it
        }

    }

    fun Log(vararg msg:String){
        msg.toObservable()
            .subscribe {
                LjmUtil.D(it)
            }
    }

    fun startFirstCoroutine(){
        Log("Coroutine start")
        val scope = CoroutineScope(Dispatchers.Default)

        val job = scope.launch {
            for(i in 0..10){

                delay(500)
                Log("Hello world~!")

                if(i == 10){
                    Log("Coroutine stop")
                }
            }
        }


    }

    fun testMonad(intData:Int){
        val maybeValue : Maybe<Int> = Maybe.just(intData)
        maybeValue.subscribeBy(
            onError = {it.printStackTrace()},
            onComplete = {Log("Test Complete!")},
            onSuccess = {Log("Test Data : $it")}
        )
    }
}