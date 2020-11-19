package com.ljm.ljmtest.rxjava

import android.widget.TextView
import com.ljm.ljmtest.common.LjmUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject

class RxCalculator constructor(private val num1:Int, private val num2:Int) {

    private val subjectAdd : Subject<Pair<Int,Int>> = PublishSubject.create()
    private var result = 0

    private var targetTextView : TextView? = null

    init {
        subjectAdd.map { it.first + it.second }.subscribe {
            result = it
            LjmUtil.D("result : $result")
            if(targetTextView != null){

                targetTextView?.text = result.toString()
            }
        }
    }

    fun calculate(resultTextView:TextView){
        targetTextView = resultTextView
        val pair = Pair(num1, num2)
        subjectAdd.onNext(pair)
    }
}