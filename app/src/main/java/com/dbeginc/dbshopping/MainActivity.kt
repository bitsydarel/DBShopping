/*
 *
 *  * Copyright (C) 2017 Darel Bitsy
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License
 *
 */

package com.dbeginc.dbshopping

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ActivityNotFoundException
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.transition.Fade
import android.support.v4.view.GravityCompat
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.widget.Toast
import com.dbeginc.common.utils.RequestState
import com.dbeginc.dbshopping.DBShopping.Companion.currentUser
import com.dbeginc.dbshopping.base.BaseActivity
import com.dbeginc.dbshopping.databinding.ActivityMainBinding
import com.dbeginc.dbshopping.databinding.MainNavHeaderBinding
import com.dbeginc.dbshopping.settings.SettingsFragment
import com.dbeginc.dbshopping.utils.extensions.getNetworkClient
import com.dbeginc.dbshopping.utils.extensions.snack
import com.dbeginc.dbshopping.utils.helper.CommentActionHandler
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.IS_USER_LOGGED
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.SHOULD_SHOW_REGISTER
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.USER_ID
import com.dbeginc.dbshopping.utils.helper.MainNavigator
import com.dbeginc.users.profile.UserViewModel
import com.dbeginc.users.viewmodels.UserProfileModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, CommentActionHandler {
    private lateinit var viewModel: UserViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var backgroundThread: ExecutorService
    private lateinit var playerMediaSourceFactory: ExtractorMediaSource.Factory
    private val playerHandler: Handler by lazy { Handler() }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.mainNavView.setNavigationItemSelectedListener(this)

        if (state == null) MainNavigator.goToListsScreen(this)
        else currentUser = state.getParcelable(USER_ID)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[UserViewModel::class.java]

        viewModel.loadUser(preferences.getString(USER_ID, ""))

        viewModel.getRequestStateEvent().observe(this, android.arch.lifecycle.Observer {
            onStateChanged(it!!)
        })

        viewModel.getUser().observe(this, Observer {
            setUser(it!!)
        })

        backgroundThread = Executors.newSingleThreadExecutor()

        val rendererFactory = DefaultRenderersFactory(applicationContext)

        val bandwidthMeter = DefaultBandwidthMeter() //Provides estimates of the currently available bandwidth.
        val trackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(trackSelectionFactory)

        val loadControl = DefaultLoadControl()

        exoPlayer = ExoPlayerFactory.newSimpleInstance(rendererFactory, trackSelector, loadControl)

        exoPlayer.addListener(object : Player.DefaultEventListener() {
            override fun onPlayerError(error: ExoPlaybackException) {
                logger.logError(error)
                binding.mainLayout.snack(error.unexpectedException.localizedMessage)
            }
        })

        val userAgent = Util.getUserAgent(applicationContext, applicationInfo.name)

        val networkDataSource = OkHttpDataSourceFactory(
                getNetworkClient(),
                userAgent,
                null
        )

        playerMediaSourceFactory = ExtractorMediaSource
                .Factory(DefaultDataSourceFactory(applicationContext, bandwidthMeter, networkDataSource))

        DBShopping.scheduleDataSync(this)

        DBShopping.scheduleUserAvailabilityCheck(this)

    }

    override fun onDestroy() {
        super.onDestroy()

        exoPlayer.release()

        backgroundThread.shutdown()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(USER_ID, currentUser)
    }

    override fun onBackPressed() {
        if (binding.mainLayout.isDrawerOpen(GravityCompat.START)) binding.mainLayout.closeDrawer(GravityCompat.START)
        else if (MainNavigator.onBackPressed(this))
        else super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.action_open_lists -> MainNavigator.goToListsScreen(this)
            R.id.action_send_comments -> showCommentsDialog()
            R.id.action_recommend_us -> shareTheApp()
            R.id.action_settings -> openSettings()
            R.id.action_logout_user -> {
                currentUser?.let {
                    AlertDialog.Builder(this)
                            .setMessage(R.string.action_logout)
                            .setIcon(R.drawable.ic_logout)
                            .setNegativeButton(android.R.string.no) { dialog, _ -> dialog?.cancel() }
                            .setPositiveButton(android.R.string.yes) { _, _ -> viewModel.logout(it.uniqueId) }
                            .create()
                            .show()
                }
            }
        }

        item.isChecked = true

        binding.mainLayout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        supportFragmentManager
                .findFragmentById(R.id.main_content)
                ?.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun pushPendingComment(action: () -> Unit) = backgroundThread.execute(action)

    override fun playRecordedVoice(recordedVoicePath: String) {
        exoPlayer.prepare(
                playerMediaSourceFactory.createMediaSource(
                        Uri.parse(recordedVoicePath),
                        playerHandler,
                        null
                )
        )

        exoPlayer.playWhenReady = true
    }

    override fun stopCurrentRecordedVoicePlaying() = exoPlayer.stop()

    fun openNavigationDrawer() = binding.mainLayout.openDrawer(GravityCompat.START)

    private fun showCommentsDialog() {
        val uri = Uri.parse("market://details?id=$packageName")

        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)

        try { startActivity(myAppLinkToMarket) }
        catch (error : ActivityNotFoundException) {
            Toast.makeText(
                    this,
                    R.string.cant_find_google_play_app,
                    Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun shareTheApp() {
        val shareIntent = Intent()

        shareIntent.action = Intent.ACTION_SEND

        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message, packageName))

        shareIntent.type = "text/plain"

        startActivity(Intent.createChooser(
                shareIntent,
                resources.getText(R.string.share_with)
        ))
    }

    private fun openSettings() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        val settingsFragment = SettingsFragment().apply {
            enterTransition = Fade(Fade.IN)
            exitTransition = Fade(Fade.OUT)
        }

        fragmentTransaction.replace(
                R.id.main_content,
                settingsFragment,
                SettingsFragment::class.java.simpleName
        )

        fragmentTransaction.commit()
    }

    private fun onStateChanged(state: RequestState) {
        when(state) {
            RequestState.LOADING -> return
            RequestState.COMPLETED -> onUserRequestCompleted()
            RequestState.ERROR -> return
        }
    }

    private fun onUserRequestCompleted() {
        preferences.edit().putBoolean(IS_USER_LOGGED, false).apply()

        startActivity(Intent(this, LaunchActivity::class.java).putExtra(SHOULD_SHOW_REGISTER, true))

        finish()
    }

    private fun setUser(user: UserProfileModel) {
        currentUser = user

        DataBindingUtil.bind<MainNavHeaderBinding>(binding.mainNavView.getHeaderView(0))
                .user = user
    }

}
