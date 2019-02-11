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

package com.dbeginc.dbshopping.settings


import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.RequestType
import com.dbeginc.dbshopping.BuildConfig
import com.dbeginc.dbshopping.DBShopping.Companion.currentUser
import com.dbeginc.dbshopping.LaunchActivity
import com.dbeginc.dbshopping.MainActivity
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.databinding.FragmentSettingsBinding
import com.dbeginc.dbshopping.utils.extensions.hideWithAnimation
import com.dbeginc.dbshopping.utils.extensions.setUserImage
import com.dbeginc.dbshopping.utils.extensions.showWithAnimation
import com.dbeginc.dbshopping.utils.extensions.snack
import com.dbeginc.dbshopping.utils.helper.ConstantHolder
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LOADING_PERIOD
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.USER_ID
import com.dbeginc.dbshopping.utils.helper.DBShoppingEngine
import com.dbeginc.dbshopping.utils.helper.MainNavigator
import com.dbeginc.dbshopping.utils.helper.PermissionCallback
import com.dbeginc.domain.entities.data.CommentType
import com.dbeginc.users.profile.profilesetting.ProfileSettingsViewModel
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import org.json.JSONObject
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : BaseFragment(), FacebookCallback<LoginResult>, GraphRequest.GraphJSONObjectCallback, DialogInterface.OnClickListener, PermissionCallback {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var viewModel: ProfileSettingsViewModel
    private val stateObserver = Observer<RequestState> { onStateChanged(it!!) }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[ProfileSettingsViewModel::class.java]

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestStateEvent()
                .observe(this, stateObserver)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance()
                .registerCallback(callbackManager, this)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ConstantHolder.WRITE_AND_READ_EXTERNAL_STORAGE
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            onPermissionGranted(CommentType.IMAGE)

        } else onPermissionDenied()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == ConstantHolder.RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with user
                viewModel.linkAccountWithGoogle(
                        result.signInAccount?.email!!,
                        result.signInAccount?.idToken!!
                )

            } else {
                // Google Sign In failed, update UI appropriately
                notifyUserOfIssue(result.status.statusMessage ?: getString(R.string.errorWhileDuringAuthentication))
            }
        }
        else if (requestCode == ConstantHolder.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            binding.user?.let {
                it.avatar = Matisse.obtainResult(data).first().toString()

                setUserImage(binding.userAvatar, it.avatar)

                viewModel.updateUser(currentUser = it)
            }
        }
        else callbackManager.onActivityResult(requestCode, resultCode, data)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_settings,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.settingsToolbar)

        binding.settingsToolbar.setNavigationOnClickListener { MainNavigator.goToListsScreen(activity as MainActivity) }

        binding.settingsLoadingProgressBar.hideWithAnimation()

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestEmail()
                .requestProfile()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build()

        googleApiClient = GoogleApiClient.Builder(context!!)
                .enableAutoManage(activity!!, { if (!it.isSuccess) notifyUserOfIssue(it.errorMessage!!) })
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build()

        binding.linkAccountWithGoogleLabel.setOnClickListener { requestGoogleAccount() }

        binding.linkAccountWithFacebookLabel.setOnClickListener { requestFaceBookAccount() }

        binding.deleteAllYourListLabel.setOnClickListener {
            AlertDialog.Builder(context!!)
                    .setTitle(R.string.areYouSure)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.action_yes, this)
                    .create()
                    .show()
        }

        binding.requestFeatureLabel.setOnClickListener { openMailClient() }

        binding.userAvatar.setOnClickListener {
            if (hasWritePermission()) {
                Matisse.from(this)
                        .choose(MimeType.ofImage())
                        .maxSelectable(1)
                        .thumbnailScale(0.85f)
                        .showSingleMediaType(true)
                        .theme(R.style.Matisse_Zhihu)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED)
                        .imageEngine(DBShoppingEngine())
                        .capture(true)
                        .captureStrategy(CaptureStrategy(true, ConstantHolder.FILE_PROVIDER))
                        .forResult(ConstantHolder.REQUEST_IMAGE_CAPTURE)

            } else requestWriteAndReadPermission()
        }

        binding.user = currentUser

        binding.applicationVersionLabel.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        googleApiClient.stopAutoManage(activity!!)

        googleApiClient.disconnect()

    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        val userId = preferences.getString(USER_ID, currentUser?.uniqueId)

        viewModel.deleteAccount(userId)

    }

    override fun onSuccess(result: LoginResult) {
        onStateChanged(RequestState.LOADING)

        val infoRequest = GraphRequest.newMeRequest(result.accessToken, this)

        infoRequest.parameters = Bundle().apply { putString("fields", "id,email") }

        infoRequest.executeAsync()
    }

    override fun onCancel() { return }

    override fun onError(error: FacebookException?) =
            notifyUserOfIssue(error?.localizedMessage ?: getString(R.string.errorWhileDuringAuthentication))

    override fun onCompleted(jsonObject: JSONObject, response: GraphResponse) {
        val email = jsonObject["email"] as String
        val token = response.request.accessToken.token

        viewModel.linkAccountWithFacebook(email, token)
    }

    private fun onStateChanged(state: RequestState) {
        when(state) {
            RequestState.LOADING -> binding.settingsLoadingProgressBar.showWithAnimation()
            RequestState.COMPLETED -> binding.settingsLayout.postDelayed(this::onRequestCompleted, LOADING_PERIOD)
            RequestState.ERROR -> binding.settingsLayout.postDelayed(this::onRequestFailed, LOADING_PERIOD)
        }
    }

    private fun onRequestCompleted() {
        binding.settingsLoadingProgressBar.hideWithAnimation()

        if (viewModel.getLastRequest() == RequestType.DELETE) {
            preferences.edit()
                    .putBoolean(ConstantHolder.IS_USER_LOGGED, false)
                    .apply()

            startActivity(Intent(context, LaunchActivity::class.java).putExtra(ConstantHolder.SHOULD_SHOW_REGISTER, true))

            activity?.finish()
        }
    }

    private fun onRequestFailed() {
        binding.settingsLoadingProgressBar.hideWithAnimation()

        binding.settingsLayout.snack(resId = R.string.error_while_doing_settings)
    }

    private fun requestGoogleAccount() {
        if (googleApiClient.isConnected) googleApiClient.clearDefaultAccountAndReconnect()

        startActivityForResult(
                Auth.GoogleSignInApi.getSignInIntent(googleApiClient),
                ConstantHolder.RC_SIGN_IN
        )
    }

    private fun requestFaceBookAccount() {
        LoginManager.getInstance()
                .logInWithReadPermissions(
                        this,
                        Arrays.asList("public_profile", "email")
                )
    }

    private fun openMailClient() {
        val emailFeatureRequest = Intent()

        emailFeatureRequest.action = Intent.ACTION_SEND

        emailFeatureRequest.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
        emailFeatureRequest.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.request_features))

        emailFeatureRequest.type = "text/plain"

        startActivity(Intent.createChooser(
                emailFeatureRequest,
                resources.getText(R.string.send_with)
        ))
    }

    override fun onPermissionGranted(kind: CommentType) {
        if (kind == CommentType.IMAGE) {
            binding.userAvatar.callOnClick()
        }
    }

    override fun onPermissionDenied() {
        Snackbar.make(binding.settingsLayout, R.string.user_cant_use_feature_unless_accept_permission, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_retry, { binding.userAvatar.callOnClick() })
                .show()
    }

    private fun notifyUserOfIssue(errorMessage: String) = binding.settingsLayout.snack(errorMessage)

}