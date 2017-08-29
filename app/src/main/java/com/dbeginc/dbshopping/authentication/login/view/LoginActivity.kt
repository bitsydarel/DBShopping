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

package com.dbeginc.dbshopping.authentication.login.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Base64
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.authentication.login.LoginContract
import com.dbeginc.dbshopping.authentication.signup.view.SignUpActivity
import com.dbeginc.dbshopping.base.BaseActivity
import com.dbeginc.dbshopping.base.LoadingDialog
import com.dbeginc.dbshopping.databinding.ActivityLoginBinding
import com.dbeginc.dbshopping.di.user.module.UserModule
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.helper.Injector
import com.dbeginc.dbshopping.helper.Navigator
import com.dbeginc.dbshopping.helper.extensions.*
import com.dbeginc.dbshopping.home.view.HomeActivity
import com.dbeginc.dbshopping.viewmodels.UserModel
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.reactivex.Flowable
import javax.inject.Inject

/**
 * Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

class LoginActivity : BaseActivity() , LoginContract.LoginView {

    @Inject lateinit var presenter: LoginContract.LoginPresenter
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var progressDialog : LoadingDialog

    /********************************************************** Android Part Method **********************************************************/

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectInAuthComponent(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        progressDialog = LoadingDialog()
        progressDialog.setMessage(getString(R.string.loggingUserProgress))
    }

    override fun onStart() {
        super.onStart()
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestEmail()
                .requestProfile()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build()

        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, { displayErrorMessage(it.errorMessage!!) })
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build()
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        googleApiClient.stopAutoManage(this)
        googleApiClient.disconnect()
        cleanState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == ConstantHolder.RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val email : String = result.signInAccount?.email!!


                preferences.edit().putString(ConstantHolder.USER_EMAIL, email).apply()

                val emailAsUniqueId = Base64.encodeToString(email.toByteArray(), Base64.NO_WRAP)

                FirebaseAuth.getInstance()
                        .fetchProvidersForEmail(email)
                        .addOnCompleteListener {
                            task -> if (task.isSuccessful && isUserProvideGoogle(task.result.providers!!)) {
                            presenter.loginWithGoogle(emailAsUniqueId, result.signInAccount?.idToken!!)

                        } else { displayIncorrectLoginType() }}

            } else {
                // Google Sign In failed, update UI appropriately
                displayErrorMessage(getString(R.string.errorWhileLoginWithGoogle))
            }
        }
    }

    private fun displayIncorrectLoginType() {
        AlertDialog.Builder(this)
                .setTitle(R.string.incorrectLoginType)
                .setMessage(R.string.userExistButWithDifferentAccount)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show()
    }

    private fun isUserProvideGoogle(providers: List<String>) : Boolean = providers.contains(GoogleAuthProvider.PROVIDER_ID)

    /********************************************************** Login View Part Method **********************************************************/

    override fun setupView() {
        binding.loginButton.setOnClickListener { presenter.onUserLogin() }

        binding.loginWithGoogleBtn.setOnClickListener { presenter.getGoogleAccount() }

        binding.goToSignUpScreenBtn.setOnClickListener { presenter.whenUserHasNoAccount() }

        presenter.onUserEmailInputEvent()
        presenter.onUserPasswordInputEvent()
        presenter.onUserInputEvent()
    }

    override fun cleanState() = presenter.unBind()

    override fun getEmail(): String = binding.loginEmail.getValue()

    override fun getPassword(): String = binding.loginPassword.getValue()

    override fun getEmailInputEvent(): Flowable<String> = binding.loginEmail.getInputEvent()

    override fun getPasswordInputEvent(): Flowable<String> = binding.loginPassword.getInputEvent()

    override fun displayEmailInvalidMessage() {
        binding.loginEmailContainer.error = getString(R.string.EmailErrorMessage)
    }

    override fun removeEmailInvalidMessage() {
        binding.loginEmailContainer.isErrorEnabled = false
    }

    override fun displayPasswordInvalidMessage() {
        binding.loginPasswordContainer.error = getString(R.string.PasswordErrorMessage)
    }

    override fun removePasswordInvalidMessage() {
        binding.loginPasswordContainer.isErrorEnabled = false
    }

    override fun activateLogin() = binding.loginButton.fadeIn()

    override fun disableLogin() = binding.loginButton.fadeOut()

    override fun displayLoginInProgressMessage() {
        progressDialog.show(supportFragmentManager, LoadingDialog::class.java.simpleName)
    }

    override fun hideLoginInProgressMessage() = progressDialog.dismiss()

    override fun onLoginSuccess() {
        val encodedEmail = Base64.encodeToString(preferences.getString(ConstantHolder.USER_EMAIL, null).toByteArray(), Base64.NO_WRAP)
        presenter.loadUser(encodedEmail)
    }

    override fun setUser(user: UserModel) {
        Injector.userComponent = Injector.appComponent.plus(UserModule(user))
        binding.loginViewLayout.snack("Got user with name ${user.name}, email: ${user.email}")
    }

    override fun requestGoogleAccount() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, ConstantHolder.RC_SIGN_IN)
    }

    override fun requestFacebookAccount() {
        displayErrorMessage("Login with facebook")
    }

    override fun displayErrorMessage(error: String) = binding.loginViewLayout.snack(error)

    override fun goToHomePage() {
        Navigator.startActivity(this, Intent(this, HomeActivity::class.java))
    }

    override fun goToSignUpScreen() {
        Navigator.startActivity(this, Intent(this, SignUpActivity::class.java))
    }
}
