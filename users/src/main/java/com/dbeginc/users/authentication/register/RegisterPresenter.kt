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

package com.dbeginc.users.authentication.register

import com.dbeginc.common.presenter.MVPVPresenter
import com.dbeginc.users.authentication.validator.AuthFormValidator
import io.reactivex.subjects.PublishSubject

/**
 * Created by darel on 23.02.18.
 *
 * Register Presenter
 */
class RegisterPresenter(private val retryBridge : PublishSubject<Unit>, private val validator: AuthFormValidator) : MVPVPresenter<RegisterView> {
    override fun bind(view: RegisterView) = view.setupView()

    fun retryLoginRequest() = retryBridge.onNext(Unit)

    fun validateCredentials(nickname: String, email: String, password: String, view: RegisterView) {
        if (!validator.verifyNickname(nickname)) view.nicknameDoesNotMatchTheRequirement()
        else if (!validator.verifyEmailValid(email)) view.emailDoesNotMatchTheRequirement()
        else if (!validator.verifyPasswordValid(password)) view.passwordDoesNotMatchTheRequirement()
        else view.onCredentialsValidated(nickname, email, password)
    }
}