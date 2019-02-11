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

package com.dbeginc.dbshopping.authentication.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.dbeginc.common.utils.RequestState
import com.dbeginc.dbshopping.DBShopping
import com.dbeginc.dbshopping.LaunchActivity
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.databinding.FragmentLoginBinding
import com.dbeginc.dbshopping.utils.extensions.getValue
import com.dbeginc.dbshopping.utils.extensions.hideWithAnimation
import com.dbeginc.dbshopping.utils.extensions.showWithAnimation
import com.dbeginc.dbshopping.utils.extensions.snack
import com.dbeginc.dbshopping.utils.helper.ConstantHolder
import com.dbeginc.users.authentication.login.LoginView
import com.dbeginc.users.authentication.login.LoginViewModel
import com.dbeginc.users.viewmodels.UserProfileModel
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import org.json.JSONObject
import java.util.*

/**
 * Created by darel on 22.02.18.
 *
 * Login Fragment
 */
class LoginFragment : BaseFragment(), LoginView, FacebookCallback<LoginResult>, GraphRequest.GraphJSONObjectCallback {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var callbackManager: CallbackManager
    private val stateObserver = Observer<RequestState> { onStateChanged(it!!) }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)[LoginViewModel::class.java]

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestStateEvent()
                .observe(this, stateObserver)

        viewModel.getUser()
                .observe(this, Observer {
                    setUser(user = it!!)
                })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance()
                .registerCallback(callbackManager, this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_login,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        viewModel.presenter.bind(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.processingLoginAnimation.cancelAnimation()

        googleApiClient.stopAutoManage(activity!!)

        googleApiClient.disconnect()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == ConstantHolder.RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with user
                viewModel.loginGoogleUser(
                        result.signInAccount?.email!!,
                        result.signInAccount?.idToken!!
                )

            } else {
                // Google Sign In failed, update UI appropriately
                notifyUserOfIssue(result.status.status.statusMessage ?: getString(R.string.errorWhileDuringAuthentication))
            }
        }
        else callbackManager.onActivityResult(requestCode, resultCode, data)

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

        viewModel.loginFacebookUser(email, token)
    }

    override fun setupView() {
        hideLottieAnimation()

        binding.loginButton.setOnClickListener {
            // Save the email when user login with email and password
            viewModel.presenter.validateCredentials(
                    binding.loginEmail.getValue(),
                    binding.loginPassword.getValue(),
                    this
            )
        }

        binding.loginPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) binding.loginButton.callOnClick()
            return@setOnEditorActionListener true
        }

        binding.loginWithGoogleBtn.setOnClickListener { requestGoogleAccount() }

        binding.loginWithFacebookBtn.setOnClickListener { requestFaceBookAccount() }

        binding.goToSignUpScreenBtn.setOnClickListener { (activity as LaunchActivity).goToRegisterScreen() }

        binding.loginForgetPasswordBtn.setOnClickListener { (activity as LaunchActivity).goToForgotPasswordScreen() }
    }

    override fun credentialsInvalid() = binding.loginViewLayout.snack(resId = R.string.loginFailed)

    override fun onCredentialsValidated(email: String, password: String) {
        viewModel.loginUser(email, password)
    }

    override fun onStateChanged(state: RequestState) {
        when(state) {
            RequestState.LOADING -> onLoginProcessStarted()
            RequestState.COMPLETED -> binding.root.postDelayed(this::onLoginProcessCompleted, 500)
            RequestState.ERROR -> binding.root.postDelayed(this::onLoginProcessFailed, 500)
        }
    }

    private fun onLoginProcessStarted() {
        binding.loginButton.hideWithAnimation()

        showLottieAnimation()
    }

    private fun onLoginProcessCompleted() {
        hideLottieAnimation()

        binding.loginButton.showWithAnimation()
    }

    private fun onLoginProcessFailed() {
        hideLottieAnimation()

        binding.loginButton.showWithAnimation()

        credentialsInvalid()
    }

    private fun showLottieAnimation() {
        binding.processingLoginAnimation.playAnimation()

        binding.processingLoginAnimation.showWithAnimation()
    }

    private fun hideLottieAnimation() {
        binding.processingLoginAnimation.hideWithAnimation()

        binding.processingLoginAnimation.pauseAnimation()
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

    private fun setUser(user: UserProfileModel) {
        preferences.edit()
                .putBoolean(ConstantHolder.IS_USER_LOGGED, true)
                .apply()

        preferences.edit()
                .putString(ConstantHolder.USER_ID, user.uniqueId)
                .apply()

        DBShopping.memoryCache[user.uniqueId] = user

        (activity as? LaunchActivity)?.goToMainScreen()
    }

    private fun notifyUserOfIssue(message: String) = binding.loginViewLayout.snack(message)

}