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

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.dbeginc.common.BaseViewModel
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.addTo
import com.dbeginc.domain.Logger
import com.dbeginc.domain.ThreadProvider
import com.dbeginc.domain.entities.request.AccountRequestModel
import com.dbeginc.domain.entities.request.AuthRequestModel
import com.dbeginc.domain.entities.request.FacebookRequestModel
import com.dbeginc.domain.entities.request.GoogleRequestModel
import com.dbeginc.domain.entities.user.AuthenticationType
import com.dbeginc.domain.repositories.IUserRepo
import com.dbeginc.users.authentication.validator.AuthFormValidator
import com.dbeginc.users.viewmodels.UserProfileModel
import com.dbeginc.users.viewmodels.toUI
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by darel on 22.02.18.
 *
 * Login View Model
 */
class LoginViewModel @Inject constructor(private val userRepo: IUserRepo, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions = CompositeDisposable()
    override val requestState : MutableLiveData<RequestState> = MutableLiveData()
    private val _user : MutableLiveData<UserProfileModel> = MutableLiveData()
    // Rx Behavior relay that subscribe to request and notify view of new data
    private val userResponseListener = BehaviorRelay.create<UserProfileModel>()
    val presenter = LoginPresenter(AuthFormValidator())

    init {
        userResponseListener.subscribe(_user::postValue)
    }

    fun getUser() : LiveData<UserProfileModel> = _user

    fun loginUser(email: String, password: String) {
        userRepo.canUserLoginWithAccountProvider(AccountRequestModel(email, AuthenticationType.DEFAULT))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .flatMap { canLogin ->
                    if (canLogin) userRepo.loginUser(AuthRequestModel(email, password, ""))
                    else Single.error(Exception())
                }
                .map { user -> user.toUI() }
                .observeOn(threads.UI)
                .doAfterSuccess { requestState.postValue(RequestState.COMPLETED) }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)
                }
                .subscribe(userResponseListener)
                .addTo(subscriptions)
    }

    fun loginGoogleUser(email: String, token: String) {
        userRepo.canUserLoginWithAccountProvider(AccountRequestModel(email, AuthenticationType.GOOGLE))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .flatMap { canLogin ->
                    if (canLogin) userRepo.loginWithGoogle(GoogleRequestModel(email, token))
                    else Single.error(Exception())
                }
                .map { user -> user.toUI() }
                .observeOn(threads.UI)
                .doAfterSuccess { requestState.postValue(RequestState.COMPLETED) }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)
                }
                .subscribe(userResponseListener)
                .addTo(subscriptions)
    }

    fun loginFacebookUser(email: String, token: String) {
        userRepo.canUserLoginWithAccountProvider(AccountRequestModel(email, AuthenticationType.FACEBOOK))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .flatMap { canLogin ->
                    if (canLogin) userRepo.loginWithFacebook(FacebookRequestModel(email /*not important for login*/, token))
                    else Single.error(Exception())
                }
                .map { user -> user.toUI() }
                .observeOn(threads.UI)
                .doAfterSuccess { requestState.postValue(RequestState.COMPLETED) }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)
                }
                .subscribe(userResponseListener)
                .addTo(subscriptions)
    }
}