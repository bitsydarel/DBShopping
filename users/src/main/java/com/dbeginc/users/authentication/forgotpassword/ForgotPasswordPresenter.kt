package com.dbeginc.users.authentication.forgotpassword

import com.dbeginc.common.presenter.MVPVPresenter
import com.dbeginc.users.authentication.validator.AuthFormValidator

/**
 * Created by darel on 25.02.18.
 *
 * Forgot Password View
 */
class ForgotPasswordPresenter(private val validator: AuthFormValidator) : MVPVPresenter<ForgotPasswordView>{

    override fun bind(view: ForgotPasswordView) = view.setupView()

    fun validateEmail(email: String, view: ForgotPasswordView) {
        if (!validator.verifyEmailValid(email)) view.onEmailNotValid()
        else view.onEmailValid(email)
    }
}