package com.ljm.ljmtest.camera

import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi
import java.lang.Long.signum

class CompareSizesByArea : Comparator<Size> {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun compare(lhs: Size, rhs: Size): Int {
        return signum(lhs.width.toLong() * lhs.height-rhs.width.toLong()*rhs.height)
    }
}