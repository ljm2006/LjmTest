package com.ljm.ljmtest.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
            R.id.btn_album_open -> {
                openAlbum()
            }
            R.id.btn_file_chooser_open -> {
                a.openFileChooser()
            }
            R.id.btn_create_temp -> {
                a.createTempFileImageView()
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

    fun openAlbum(){
        val permissionCheck = ContextCompat.checkSelfPermission(c, Manifest.permission.CAMERA)
        val permissionCheck2 = ContextCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionCheck3 = ContextCompat.checkSelfPermission(c, Manifest.permission.READ_EXTERNAL_STORAGE)

        if(permissionCheck != PackageManager.PERMISSION_GRANTED &&
            permissionCheck2 != PackageManager.PERMISSION_GRANTED &&
            permissionCheck3 != PackageManager.PERMISSION_GRANTED){

            a.requestPermission(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 9001)
        }else{

            a.openAlbumActivity()
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
                6667 -> {
                    data?.data?.let {
                        val cursor = c.contentResolver.query(it, null, null, null, null)
                        if(cursor!!.moveToFirst()){
                            val count = cursor.columnCount
                            LjmUtil.D("columnIndex : $count")
                            val columnList = cursor.columnNames
                            LjmUtil.D("index : ${cursor.getColumnIndexOrThrow("_data")}")
                            val index = cursor.getColumnIndexOrThrow("_data")
                            val path = cursor.getString(index)
                            LjmUtil.D("data : $path")
                            a.loadImage(path)
                            /*for (element in columnList){
                                LjmUtil.D("name : $element")
                            }*/
                        }
                    }
                }
                6668 -> {
                    if(resultCode == Activity.RESULT_OK){

                        if(data!!.clipData != null){
                            val count = data.clipData!!.itemCount
                            LjmUtil.D("count : $count")
                        }else{

                            if(data.data != null){
                                val uri = data.data!!
                                val imgPath = data.data!!.path
                                LjmUtil.D("single img path : $imgPath")
                                a.loadImageFromUri(data.data!!)
                            }
                        }
                    }
                }
            }
        }
    }

    interface CameraAlbumAction{
        fun showToast(msg:String)
        fun requestPermission(permissions:Array<String>, reqCode:Int)
        fun openCameraActivity(file: File)
        fun openAlbumActivity()
        fun openFileChooser()
        fun loadImage(path:String)
        fun loadImageFromUri(uri:Uri)
        fun createTempFileImageView()
    }
}