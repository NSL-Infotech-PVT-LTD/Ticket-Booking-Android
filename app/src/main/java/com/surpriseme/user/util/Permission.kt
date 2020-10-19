package com.tournie.Util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build

object Permission {

    val REQUEST_LOCATION = 607
    val REQUEST_CAMERA_IMAGE = 608

    fun checkPermissionForLocation(
        activity: Activity,
        callBack: LocationCallBack
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activity.requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), REQUEST_LOCATION
            )
            return
        } else {
            callBack.onLocatioSuccess()
            //            getLastKnownLocation();
        }
    }

    fun checkPermissionForImageCamera(
        activity: Activity,
        callBack: GalleryCameraCallBack
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            activity.requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), REQUEST_CAMERA_IMAGE
            )
            return
        } else {
            callBack.onImagePermissionSuccess()
            //            getLastKnownLocation();
        }
    }

    interface LocationCallBack {
        fun onLocatioSuccess()
    }

    interface GalleryCameraCallBack {
        fun onImagePermissionSuccess()
    }
}