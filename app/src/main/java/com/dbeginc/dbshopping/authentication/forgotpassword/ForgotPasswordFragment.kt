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

package com.dbeginc.dbshopping.authentication.forgotpassword

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
import com.dbeginc.dbshopping.R
import com.dbeginc.dbshopping.base.BaseFragment
import com.dbeginc.dbshopping.databinding.FragmentForgotPasswordBinding
import com.dbeginc.dbshopping.LaunchActivity
import com.dbeginc.dbshopping.utils.extensions.getValue
import com.dbeginc.dbshopping.utils.extensions.hideWithAnimation
import com.dbeginc.dbshopping.utils.extensions.showWithAnimation
import com.dbeginc.dbshopping.utils.helper.ConstantHolder.LOADING_PERIOD
import com.dbeginc.users.authentication.forgotpassword.ForgotPasswordView
import com.dbeginc.users.authentication.forgotpassword.ForgotPasswordViewModel

/**
 * Forgot Password Fragment
 */
class ForgotPasswordFragment : BaseFragment(), ForgotPasswordView {
    private lateinit var viewModel: ForgotPasswordViewModel
    private lateinit var binding: FragmentForgotPasswordBinding
    private val stateObserver = Observer<RequestState> {
        onStateChanged(it!!)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)[ForgotPasswordViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestStateEvent()
                .observe(this, stateObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme_ForgotPassword)),
                R.layout.fragment_forgot_password,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.presenter.bind(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (binding.forgotPasswordAnimation.isAnimating) binding.forgotPasswordAnimation.cancelAnimation()

        if (binding.forgotPasswordLoadingAnimation.isAnimating) binding.forgotPasswordLoadingAnimation.cancelAnimation()
    }

    override fun setupView() {
        binding.forgotPasswordResetPassword.setOnClickListener {
            viewModel.presenter.validateEmail(
                    binding.forgotPasswordEmail.getValue(),
                    this
            )
        }

        binding.forgotPasswordEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) binding.forgotPasswordResetPassword.callOnClick()
            return@setOnEditorActionListener true
        }

        binding.forgotPasswordEmail.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(text: Editable?) {}
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.isNullOrBlank()) binding.forgotPasswordEmailContainer.error = null
            }
        })

        binding.forgotPasswordGoToLoginScreenBtn.setOnClickListener {
            (activity as LaunchActivity).goToLoginScreen()
        }

        binding.forgotPasswordAnimation.playAnimation()

        hideLottieAnimation()
    }

    override fun onEmailNotValid() {
        binding.forgotPasswordEmailContainer.error = getString(R.string.emailErrorMessage)
    }

    override fun onEmailValid(email: String) {
        binding.forgotPasswordEmailContainer.error = null

        viewModel.sendResetPasswordInstructions(email)
    }

    override fun onStateChanged(state: RequestState) {
        when(state) {
            RequestState.LOADING -> onSendInstructionsProcessStarted()
            RequestState.COMPLETED -> binding.root.postDelayed(this::onSendInstructionsProcessCompleted, LOADING_PERIOD)
            RequestState.ERROR -> binding.root.postDelayed(this::onSendInstructionsProcessFailed, LOADING_PERIOD)
        }
    }

    private fun onSendInstructionsProcessStarted() {
        binding.forgotPasswordResetPassword.hideWithAnimation()

        showLottieAnimation()
    }

    private fun onSendInstructionsProcessFailed() {
        hideLottieAnimation()
        binding.forgotPasswordResetPassword.showWithAnimation()
        notifyUserOfIssues()
    }

    private fun onSendInstructionsProcessCompleted() {
        hideLottieAnimation()
        binding.forgotPasswordResetPassword.showWithAnimation()
        notifyUserThatInstructionsWereSent()
    }

    private fun notifyUserOfIssues() {
        Snackbar.make(binding.forgotPasswordLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_retry, { binding.forgotPasswordResetPassword.callOnClick() })
                .show()
    }

    private fun notifyUserThatInstructionsWereSent() {
        Snackbar.make(binding.forgotPasswordLayout, R.string.reset_password_instructions_sent, Snackbar.LENGTH_LONG)
                .setAction(R.string.action_check_email, { openEmailClient() })
                .show()
    }

    private fun openEmailClient() {
        val emailClientRequest = Intent(Intent.ACTION_MAIN)

        emailClientRequest.addCategory(Intent.CATEGORY_APP_EMAIL)

        startActivity(Intent.createChooser(emailClientRequest, getString(R.string.action_check_email)))
    }

    private fun showLottieAnimation() {
        binding.forgotPasswordLoadingAnimation.playAnimation()
        binding.forgotPasswordLoadingAnimation.showWithAnimation()
    }

    private fun hideLottieAnimation() {
        binding.forgotPasswordLoadingAnimation.hideWithAnimation()
        binding.forgotPasswordLoadingAnimation.pauseAnimation()
    }

}
