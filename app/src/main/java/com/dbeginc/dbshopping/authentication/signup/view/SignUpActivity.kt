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

package com.dbeginc.dbshopping.authentication.signup.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Base64
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.authentication.login.view.LoginActivity
import com.dbeginc.dbshopping.authentication.signup.SignUpContract
import com.dbeginc.dbshopping.base.BaseActivity
import com.dbeginc.dbshopping.base.LoadingDialog
import com.dbeginc.dbshopping.databinding.ActivitySignupBinding
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
class SignUpActivity : BaseActivity(), SignUpContract.SignUpView {
    @Inject lateinit var presenter: SignUpContract.SignUpPresenter
    private lateinit var binding: ActivitySignupBinding
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var progressDialog : LoadingDialog

    /********************************************************** Android Part Method **********************************************************/
    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectInAuthComponent(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        progressDialog = LoadingDialog()
        progressDialog.setMessage(getString(R.string.creatingUserProgress))
    }

    override fun onStart() {
        super.onStart()
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
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
                val emailAsUniqueId = Base64.encodeToString(result.signInAccount?.email?.toByteArray(), Base64.NO_WRAP)
                presenter.onUserSignUpWithGoogle(emailAsUniqueId, result.signInAccount?.idToken!!)

            } else {
                // Google Sign In failed, update UI appropriately
                displayErrorMessage(getString(R.string.errorWhileLoginWithGoogle))
            }
        }
    }

    /********************************************************** Sign Up View Part Method **********************************************************/

    override fun setupView() {
        binding.signUpButton.setOnClickListener { presenter.onUserSignUp() }

        binding.signUpWithGoogleBtn.setOnClickListener { presenter.getUserGoogleAccount() }

        binding.goToLoginScreenBtn.setOnClickListener { presenter.whenUserHasAccount() }

        presenter.onNicknameInputEvent()
        presenter.onEmailInputEvent()
        presenter.onPasswordInputEvent()
        presenter.onUserInputEvent()
    }

    override fun cleanState() = presenter.unBind()

    override fun getNickname(): String = binding.signUpNickname.getValue()

    override fun getEmail(): String = binding.signUpEmail.getValue()

    override fun getPassword(): String = binding.signUpPassword.getValue()

    override fun getNicknameInputEvent(): Flowable<String> = binding.signUpNickname.getInputEvent()

    override fun getEmailInputEvent(): Flowable<String> = binding.signUpEmail.getInputEvent()

    override fun getPasswordInputEvent(): Flowable<String> = binding.signUpPassword.getInputEvent()

    override fun displayNicknameInvalidMessage() {
        binding.signUpNicknameContainer.error = getString(R.string.nicknameErrorMessage)
    }

    override fun removeNicknameInvalidMessage() {
        binding.signUpNicknameContainer.isErrorEnabled = false
    }

    override fun displayEmailInvalidMessage() {
        binding.signUpEmailContainer.error = getString(R.string.EmailErrorMessage)
    }

    override fun removeEmailInvalidMessage() {
        binding.signUpEmailContainer.isErrorEnabled = false
    }

    override fun displayPasswordInvalidMessage() {
        binding.signUpPasswordContainer.error = getString(R.string.PasswordErrorMessage)
    }

    override fun removePasswordInvalidMessage() {
        binding.signUpPasswordContainer.isErrorEnabled = false
    }

    override fun activateSignUp() = binding.signUpButton.fadeIn()

    override fun disableSignUp() = binding.signUpButton.fadeOut()

    override fun displayUserAlreadyExist() {
        Snackbar.make(binding.signUpViewLayout, R.string.userAlreadyExistGoToLogin, Snackbar.LENGTH_LONG)
                .setAction(R.string.loginLabel, { goToLoginPage() })
                .setActionTextColor(Color.RED)
                .show()
    }

    override fun displaySignUpProgressMessage() {
        progressDialog.show(supportFragmentManager, LoadingDialog::class.java.simpleName)
    }

    override fun hideSignUpProgressMessage() = progressDialog.dismiss()

    override fun onSignUpSuccess() {
        val encodedEmail = Base64.encodeToString(FirebaseAuth.getInstance().currentUser?.email?.toByteArray(), Base64.NO_WRAP)
        presenter.loadUser(encodedEmail)
    }

    override fun setUser(user: UserModel) {
        Injector.userComponent = Injector.appComponent.plus(UserModule(user))
        binding.signUpViewLayout.snack("Got user with name ${user.name}, email: ${user.email}")
    }

    override fun requestGoogleAccount() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, ConstantHolder.RC_SIGN_IN)
    }

    override fun requestFacebookAccount() {
        displayErrorMessage("Login with facebook")
    }

    override fun displayErrorMessage(error: String) = binding.signUpViewLayout.snack(error)

    override fun goToHomePage() = Navigator.startActivity(this, Intent(this, HomeActivity::class.java))

    override fun goToLoginPage() = Navigator.startActivity(this, Intent(this, LoginActivity::class.java))

}
