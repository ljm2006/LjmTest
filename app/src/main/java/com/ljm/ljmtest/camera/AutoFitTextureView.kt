package com.ljm.ljmtest.camera

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView
import android.view.View
import java.lang.IllegalArgumentException

class AutoFitTextureView constructor(context:Context, attrs: AttributeSet?) : TextureView(context, attrs) {

    private var ratioWidth = 0
    private var ratioHeight = 0

//    비율설정 함수
    fun setAspectRatio(width:Int, height:Int){

        if(width < 0 || height < 0){
            throw IllegalArgumentException("Size cannot be negative.")
        }

        ratioWidth = width
        ratioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        if(ratioWidth == 0 || ratioHeight == 0){

            setMeasuredDimension(width, height)
        }else{

            if(width < height * ratioWidth / ratioHeight){

                setMeasuredDimension(width, width * ratioHeight / ratioWidth)
            }else{

                setMeasuredDimension(height * ratioWidth/ratioHeight, height)
            }
        }
    }
}