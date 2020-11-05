package com.ljm.ljmtest.camera

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.ljm.ljmtest.BuildConfig
import com.ljm.ljmtest.R
import java.io.File

class CameraAndAlbumOpenActivity : AppCompatActivity(), CameraAlbumPresenter.CameraAlbumAction {
    private lateinit var presenter:CameraAlbumPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_album_open)

        presenter = CameraAlbumPresenter(this, this)
        presenter.onCreate(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){

            when(requestCode){
                6666 -> {

                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

    }

    fun onClick(v: View){
        presenter.onClick(v.id)
    }

    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun requestPermission(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    override fun openCameraActivity(file: File) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.resolveActivity(packageManager)?.let {

            file.let {
                val photoUri = FileProvider.getUriForFile(this, "com.ljm.ljmtest.camera.provider", it)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, 6666)
            }
        }
    }
}