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

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.dbeginc.common.BaseViewModel
import com.dbeginc.common.utils.RequestState
import com.dbeginc.common.utils.addTo
import com.dbeginc.domain.Logger
import com.dbeginc.domain.ThreadProvider
import com.dbeginc.domain.entities.error.AccountAlreadyExistException
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
import io.reactivex.BackpressureStrategy
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by darel on 23.02.18.
 *
 * Register View Model
 */
class RegisterViewModel @Inject constructor(private val userRepo: IUserRepo, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    private val retryEvent = PublishSubject.create<Unit>()
    private val _registrationFailureListener = BehaviorRelay.create<Throwable>()
    private val _registrationResultListener = BehaviorRelay.create<UserProfileModel>()
    private val _registeredUser: MutableLiveData<UserProfileModel> = MutableLiveData()
    val presenter = RegisterPresenter(retryEvent, AuthFormValidator())

    init {
        _registrationResultListener.subscribe(_registeredUser::postValue)
    }

    fun getRegistrationFailureCause() : Throwable? = _registrationFailureListener.value

    fun getRegisteredUser() : LiveData<UserProfileModel> = _registeredUser

    fun registerUser(email: String, password: String, nickname: String) {
        userRepo.canUserRegisterWithAccountProvider(AccountRequestModel(email, AuthenticationType.DEFAULT))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .flatMap { canRegister ->
                    if (canRegister) userRepo.registerUser(AuthRequestModel(email, password, nickname))
                    else Single.error(AccountAlreadyExistException())
                }
                .map { domainUser -> domainUser.toUI() }
                .observeOn(threads.UI)
                .doOnError { error ->
                    requestState.postValue(RequestState.ERROR)
                    _registrationFailureListener.accept(error)
                    logger.logError(error)
                }
                .doAfterSuccess { requestState.postValue(RequestState.COMPLETED) }
                .subscribe(_registrationResultListener)
                .addTo(subscriptions)
    }

    fun registerGoogleUser(nickname: String, email: String, token: String) {
        userRepo.canUserRegisterWithAccountProvider(AccountRequestModel(email, AuthenticationType.GOOGLE))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .retryWhen { upstream -> upstream.flatMap { retryEvent.toFlowable(BackpressureStrategy.LATEST) } }
                .flatMap { canRegister ->
                    if (canRegister) userRepo.registerUserWithGoogle(GoogleRequestModel(nickname, token))
                    else Single.error(AccountAlreadyExistException())
                }
                .map { domainUser -> domainUser.toUI() }
                .observeOn(threads.UI)
                .doOnError { error ->
                    requestState.postValue(RequestState.ERROR)
                    _registrationFailureListener.accept(error)
                    logger.logError(error)
                }
                .doAfterSuccess { requestState.postValue(RequestState.COMPLETED) }
                .subscribe(_registrationResultListener)
                .addTo(subscriptions)
    }

    fun registerFacebookUser(nickname: String, email: String, token: String) {
        userRepo.canUserRegisterWithAccountProvider(AccountRequestModel(email, AuthenticationType.FACEBOOK))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .retryWhen { upstream -> upstream.flatMap { retryEvent.toFlowable(BackpressureStrategy.LATEST) } }
                .flatMap { canRegister ->
                    if (canRegister) userRepo.registerUserWithFacebook(FacebookRequestModel(nickname, token))
                    else Single.error(AccountAlreadyExistException())
                }
                .map { domainUser -> domainUser.toUI() }
                .observeOn(threads.UI)
                .doOnError { error ->
                    requestState.postValue(RequestState.ERROR)
                    _registrationFailureListener.accept(error)
                    logger.logError(error)
                }
                .doAfterSuccess { requestState.postValue(RequestState.COMPLETED) }
                .subscribe(_registrationResultListener)
                .addTo(subscriptions)
    }

}