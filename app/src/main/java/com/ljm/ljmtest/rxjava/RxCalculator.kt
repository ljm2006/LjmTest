package com.ljm.ljmtest.rxjava

import android.widget.TextView
import com.ljm.ljmtest.common.LjmUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject

class RxCalculator constructor(val num1:Int, val num2:Int) {

    private val subjectAdd : Subject<Pair<Int,Int>> = PublishSubject.create()
    private var result = 0

    private var targetTextView : TextView? = null

    private val subjectCal : Subject<RxCalculator> = PublishSubject.create()

    init {
        subjectAdd.map { it.first + it.second }.subscribe {
            result = it
            LjmUtil.D("result : $result")
            if(targetTextView != null){

                targetTextView?.text = result.toString()
            }
        }

        //객체 생성하는 시점에서 구독 수행
        subjectCal.subscribe {
            with(it){
                calculateSubtraction()
            }
        }

        subjectCal.onNext(this)
    }

    fun calculate(resultTextView:TextView){
        targetTextView = resultTextView
        val pair = Pair(num1, num2)
        subjectAdd.onNext(pair)
    }

    inline fun calculateSubtraction() : Int{
        val result = num1 - num2
        LjmUtil.D("sub : $result")
        return result
    }
}