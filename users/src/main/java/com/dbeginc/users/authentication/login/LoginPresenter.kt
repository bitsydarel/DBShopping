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

package com.dbeginc.users.authentication.login

import com.dbeginc.common.presenter.MVPVPresenter
import com.dbeginc.users.authentication.validator.AuthFormValidator
import io.reactivex.subjects.PublishSubject

/**
 * Created by darel on 22.02.18.
 *
 * Login Presenter
 */
class LoginPresenter(private val validator: AuthFormValidator) : MVPVPresenter<LoginView> {
    override fun bind(view: LoginView) = view.setupView()

    fun validateCredentials(email: String, password: String, view: LoginView) {
        if (!validator.verifyEmailValid(email) || !validator.verifyPasswordValid(password)) view.credentialsInvalid()
        else view.onCredentialsValidated(email, password)
    }
}