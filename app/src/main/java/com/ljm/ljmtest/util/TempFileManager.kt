package com.ljm.ljmtest.util

import android.content.Context
import android.graphics.Bitmap
import com.ljm.ljmtest.common.LjmUtil
import java.io.File
import java.io.FileOutputStream

class TempFileManager {
    companion object{

        fun createBitmapImageTempFile(c: Context, bitmap: Bitmap, fileName:String){
            val dir_path = c.getExternalFilesDir(null).toString() + "/temp/"
            val dir = File(dir_path)

            if(!dir.exists()){
                dir.mkdirs()
            }

            val imgFileName = "$fileName.jpeg"
            val imgFile = File(dir_path, imgFileName)

            val outputStream = FileOutputStream(imgFile.absolutePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()

            LjmUtil.D("file exists : ${imgFile.exists()} -> dir : ${imgFile.absolutePath}")
        }
    }
}