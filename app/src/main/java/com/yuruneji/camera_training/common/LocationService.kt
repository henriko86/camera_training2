package com.yuruneji.camera_training.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.concurrent.Executors

/**
 * @author toru
 * @version 1.0
 */
class LocationService(
    private val context: Context,
    private val intervalTime: Long = 60_000L,
    private val callback: (Location) -> Unit
) : DefaultLifecycleObserver {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private val locationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                callback.invoke(location)
            }
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        if (checkLocationPermission()) {
            val locationRequestBuild = LocationRequest.Builder(intervalTime)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            locationClient.requestLocationUpdates(locationRequestBuild, Executors.newSingleThreadExecutor(), locationCallback)
        }

        requestLocationPermission()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        locationClient.removeLocationUpdates(locationCallback)
    }

    private fun requestLocationPermission() {

        // 位置情報の権限があるか確認する
        val isAccept = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!isAccept) {
            // 権限が許可されていない場合はリクエストする
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}
