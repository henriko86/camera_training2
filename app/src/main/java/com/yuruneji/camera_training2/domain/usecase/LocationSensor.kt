package com.yuruneji.camera_training2.domain.usecase

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import timber.log.Timber
import java.util.concurrent.Executors

/**
 * @author toru
 * @version 1.0
 */
class LocationSensor(private val activity: Activity) {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private val _location: MutableLiveData<Location> = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    private val locationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                Timber.i("位置情報 start (${Thread.currentThread().name})")
                _location.postValue(location)
                Timber.i("位置情報 end (${Thread.currentThread().name})")
            }
        }
    }

    fun start(intervalTime: Long) {
        if (checkLocationPermission()) {
            val locationRequestBuild = LocationRequest.Builder(intervalTime)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            locationClient.requestLocationUpdates(locationRequestBuild, Executors.newSingleThreadExecutor(), locationCallback)
        }
    }

    fun stop() {
        locationClient.removeLocationUpdates(locationCallback)
    }

    fun requestLocationPermission() {

        // 位置情報の権限があるか確認する
        val isAccept = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!isAccept) {
            // 権限が許可されていない場合はリクエストする
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}
