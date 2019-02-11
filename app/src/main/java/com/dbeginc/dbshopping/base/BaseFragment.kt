package com.dbeginc.dbshopping.base

import android.Manifest
import android.arch.lifecycle.ViewModelProvider
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.dbeginc.dbshopping.di.WithDependencies
import com.dbeginc.dbshopping.utils.helper.ConstantHolder
import dagger.android.support.DaggerFragment
import javax.inject.Inject

open class BaseFragment : DaggerFragment(), WithDependencies {
    @Inject protected lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject protected lateinit var preferences: SharedPreferences

    fun requestWriteAndReadPermission() {
        ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                ConstantHolder.WRITE_AND_READ_EXTERNAL_STORAGE
        )
    }

    fun requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(
                        Manifest.permission.RECORD_AUDIO
                ),
                ConstantHolder.RECORD_AUDIO
        )
    }

    fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                ),
                ConstantHolder.ACCESS_LOCATION
        )
    }

    fun hasWritePermission() : Boolean {
        return (ContextCompat.checkSelfPermission(activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
    }

    fun hasRecordAudioPermission() : Boolean {
        return (ContextCompat.checkSelfPermission(activity!!,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }

    fun hasLocationPermission() : Boolean {
        return (ContextCompat.checkSelfPermission(activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

}