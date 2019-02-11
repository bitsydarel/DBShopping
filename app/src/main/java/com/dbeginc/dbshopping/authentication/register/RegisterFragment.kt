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

package com.dbeginc.dbshopping.authentication.register

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.dbeginc.common.utils.RequestState
import com.dbeginc.dbshopping.DBShopping.Companion.currentUser
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.databinding.FragmentRegisterBinding
import com.dbeginc.dbshopping.LaunchActivity
import com.dbeginc.dbshopping.utils.extensions.getValue
import com.dbeginc.dbshopping.utils.extensions.hideWithAnimation
import com.dbeginc.dbshopping.utils.extensions.showWithAnimation
import com.dbeginc.dbshopping.utils.extensions.snack
import com.dbeginc.dbshopping.utils.helper.ConstantHolder
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.IS_USER_LOGGED
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LOADING_PERIOD
import com.dbeginc.domain.entities.error.AccountAlreadyExistException
import com.dbeginc.users.authentication.register.RegisterView
import com.dbeginc.users.authentication.register.RegisterViewModel
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
 * Created by darel on 24.02.18.
 *
 * Register Fragment
 */
class RegisterFragment : BaseFragment(), RegisterView, FacebookCallback<LoginResult>, GraphRequest.GraphJSONObjectCallback {
    private lateinit var viewModel: RegisterViewModel
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var callbackManager: CallbackManager
    private val stateObserver = Observer<RequestState> { onStateChanged(it!!) }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)[RegisterViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestStateEvent().observe(this, stateObserver)

        viewModel.getRegisteredUser().observe(this, Observer {
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
                R.layout.fragment_register,
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
        binding.processingRegisterAnimation.cancelAnimation()

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
                viewModel.registerGoogleUser(
                        nickname = result.signInAccount?.displayName ?: "",
                        email = result.signInAccount?.email!!,
                        token = result.signInAccount?.idToken!!
                )

            } else {
                // Google Sign In failed, update UI appropriately
                notifyUserOfIssue(result.status.statusMessage ?: getString(R.string.errorWhileDuringRegistration))
            }

        } else callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSuccess(result: LoginResult) {
        onStateChanged(RequestState.LOADING)

        val infoRequest = GraphRequest.newMeRequest(result.accessToken, this)

        infoRequest.parameters = Bundle().apply { putString("fields", "id,name,email") }

        infoRequest.executeAsync()
    }

    override fun onCancel() { return }

    override fun onError(error: FacebookException?) =
            notifyUserOfIssue(error?.localizedMessage ?: getString(R.string.errorWhileDuringAuthentication))

    override fun onCompleted(jsonObject: JSONObject, response: GraphResponse) {
        val email = jsonObject["email"] as String
        val nickname = jsonObject["name"] as String
        val token = response.request.accessToken.token

        viewModel.registerFacebookUser(nickname, email, token)

    }

    override fun setupView() {
        hideLottieAnimation()

        binding.RegisterButton.setOnClickListener {
            // Save the email when user login with email and password
            viewModel.presenter.validateCredentials(
                    binding.RegisterNickname.getValue(),
                    binding.RegisterEmail.getValue(),
                    binding.RegisterPassword.getValue(),
                    this
            )
        }

        binding.RegisterPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) binding.RegisterButton.callOnClick()
            return@setOnEditorActionListener true
        }

        binding.RegisterNickname.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(text: Editable?) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.isNullOrBlank()) binding.RegisterNicknameContainer.error = null
            }
        })

        binding.RegisterEmail.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(text: Editable?) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.isNullOrBlank()) binding.RegisterEmailContainer.error = null
            }
        })

        binding.RegisterPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(text: Editable?) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.isNullOrBlank()) binding.RegisterPasswordContainer.error = null
            }
        })

        binding.RegisterWithGoogleBtn.setOnClickListener { requestGoogleAccount() }

        binding.RegisterWithFacebookBtn.setOnClickListener { requestFaceBookAccount() }

        binding.goToLoginScreenBtn.setOnClickListener { redirectUserToLoginPage() }
    }

    override fun nicknameDoesNotMatchTheRequirement() {
        binding.RegisterNicknameContainer.error = getString(R.string.NicknameErrorMessage)
    }

    override fun emailDoesNotMatchTheRequirement() {
        binding.RegisterEmailContainer.error = getString(R.string.emailErrorMessage)
    }

    override fun passwordDoesNotMatchTheRequirement() {
        binding.RegisterPasswordContainer.error = getString(R.string.PasswordErrorMessage)
    }

    override fun onCredentialsValidated(nickname: String, email: String, password: String) {
        viewModel.registerUser(
                nickname = nickname,
                email = email,
                password = password
        )
    }

    override fun onStateChanged(state: RequestState) {
        when(state) {
            RequestState.LOADING -> onRegistrationProcessStarted()
            RequestState.COMPLETED -> binding.root.postDelayed(this::onRegistrationProcessCompleted, LOADING_PERIOD)
            RequestState.ERROR -> binding.root.postDelayed(this::onRegistrationProcessFailed, LOADING_PERIOD)
        }
    }

    private fun onRegistrationProcessStarted() {
        binding.RegisterButton.hideWithAnimation()
        showLottieAnimation()
    }

    private fun onRegistrationProcessCompleted() {
        hideLottieAnimation()

        binding.RegisterButton.showWithAnimation()
    }

    private fun onRegistrationProcessFailed() {
        hideLottieAnimation()

        binding.RegisterButton.showWithAnimation()

        val failure = viewModel.getRegistrationFailureCause()

        when(failure) {
            is AccountAlreadyExistException ->  onAccountAlreadyExist()
            else -> notifyUserOfIssueAndSuggestToRetry(message = failure!!.localizedMessage)
        }
    }

    private fun notifyUserOfIssueAndSuggestToRetry(message: String) {
        Snackbar.make(binding.RegisterViewLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_retry, { viewModel.presenter.retryLoginRequest() })
                .show()
    }

    private fun onAccountAlreadyExist() {
        Snackbar.make(binding.RegisterViewLayout, R.string.emailAlreadyTaken, Snackbar.LENGTH_LONG)
                .setAction(R.string.loginLabel, { redirectUserToLoginPage() })
                .show()
    }

    private fun redirectUserToLoginPage() {
        (activity as LaunchActivity).goToLoginScreen()
    }

    private fun showLottieAnimation() {
        binding.processingRegisterAnimation.playAnimation()
        binding.processingRegisterAnimation.showWithAnimation()
    }

    private fun hideLottieAnimation() {
        binding.processingRegisterAnimation.hideWithAnimation()
        binding.processingRegisterAnimation.pauseAnimation()
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
        currentUser = user

        preferences.edit()
                .putBoolean(IS_USER_LOGGED, true)
                .apply()

        preferences.edit()
                .putString(ConstantHolder.USER_ID, user.uniqueId)
                .apply()

        (activity as LaunchActivity).goToMainScreen()
    }

    private fun notifyUserOfIssue(message: String = "", messageRes: Int = 0) {
        if (messageRes == 0) binding.RegisterViewLayout.snack(message = message)
        else binding.RegisterViewLayout.snack(resId = messageRes)
    }
}
