package com.dbeginc.dbshopping.base

import android.arch.lifecycle.ViewModelProvider
import android.content.SharedPreferences
import com.dbeginc.domain.Logger
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

/**
 * Created by darel on 21.08.17.
 *
 * Base Activity
 */
open class BaseActivity : DaggerAppCompatActivity() {
    @Inject protected lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject protected lateinit var preferences: SharedPreferences
    @Inject protected lateinit var logger: Logger
}