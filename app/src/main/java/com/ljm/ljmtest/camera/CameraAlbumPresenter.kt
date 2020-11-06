package com.ljm.ljmtest.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.ljm.ljmtest.Presenter
import com.ljm.ljmtest.R
import com.ljm.ljmtest.common.LjmUtil
import java.io.File

class CameraAlbumPresenter constructor(var c: Context, var a:CameraAlbumAction) : Presenter {

    private lateinit var imgPath:String

    override fun onCreate(intent: Intent?) {

    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onDestroy() {

    }

    fun onClick(id: Int) {
        when(id){
            R.id.btn_camera_open -> {
                openCamera()
            }
        }
    }

    fun openCamera(){
        val permissionCheck = ContextCompat.checkSelfPermission(c, Manifest.permission.CAMERA)
        val permissionCheck2 = ContextCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permissionCheck != PackageManager.PERMISSION_GRANTED && permissionCheck2 != PackageManager.PERMISSION_GRANTED){

            a.requestPermission(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 9000)
        }else{
            deleteTempImageFile()

            val state = Environment.getExternalStorageState()
            if(TextUtils.equals(state, Environment.MEDIA_MOUNTED)){
                a.openCameraActivity(createImageFile())
            }

        }
    }

    fun createImageFile() : File{
        var file = File(c.getExternalFilesDir(null).toString(),"/temp/")
        if(!file.exists()){
            file.mkdir()
        }

        val imgName = "temp.jpeg"
        val imgFile = File("${c.getExternalFilesDir(null).toString()}/temp/", imgName)
        imgPath = imgFile.absolutePath
        return imgFile
    }

    fun deleteTempImageFile(){
        val imgName = "temp.jpeg"
        val imgFile = File("${c.getExternalFilesDir(null).toString()}/temp/", imgName)

        if(imgFile.exists()){

            val deleted = imgFile.delete()
            LjmUtil.D("deleted : $deleted")
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){

            when(requestCode){
                6666 -> {
                    a.loadImage(imgPath)
                }
            }
        }
    }

    interface CameraAlbumAction{
        fun showToast(msg:String)
        fun requestPermission(permissions:Array<String>, reqCode:Int)
        fun openCameraActivity(file: File)
        fun loadImage(path:String)
    }
}