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

package com.dbeginc.users.authentication.forgotpassword

import android.arch.lifecycle.MutableLiveData
import com.dbeginc.common.BaseViewModel
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.addTo
import com.dbeginc.domain.Logger
import com.dbeginc.domain.ThreadProvider
import com.dbeginc.domain.entities.request.AccountRequestModel
import com.dbeginc.domain.entities.user.AuthenticationType
import com.dbeginc.domain.repositories.IUserRepo
import com.dbeginc.users.authentication.validator.AuthFormValidator
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by darel on 25.02.18.
 *
 * Forgot Password ViewModel
 */
class ForgotPasswordViewModel @Inject constructor(private val userRepo: IUserRepo, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    val presenter = ForgotPasswordPresenter(AuthFormValidator())

    fun sendResetPasswordInstructions(email: String) {
        userRepo.sendResetPasswordInstructions(AccountRequestModel(email, AuthenticationType.DEFAULT))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .observeOn(threads.UI)
                .subscribe (
                        { requestState.postValue(RequestState.COMPLETED) },
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                )
                .addTo(subscriptions)
    }
}